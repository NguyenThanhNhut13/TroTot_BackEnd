import google.generativeai as genai
import requests
import os
import asyncio
from dotenv import load_dotenv
from loguru import logger # Để logging
from tenacity import retry, stop_after_attempt, wait_exponential, before_log
from fastapi import FastAPI
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "https://trotot-frontend.vercel.app",
        "http://localhost:3000",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Cấu hình Loguru cho Production
logger.remove()
logger.add(
    lambda msg: print(msg, end=""),
    level="INFO",
    format="{time:YYYY-MM-DD HH:mm:ss.SSS} | {level: <8} | {message}",
    colorize=False
)
logger.add(
    "chatbot_backend_debug.log",
    rotation="10 MB",
    compression="zip",
    level="DEBUG",
    format="{time:YYYY-MM-DD HH:mm:ss.SSS} | {level: <8} | {file}:{line} | {message}"
)

# Load environment variables from .env file
load_dotenv()

# Cấu hình Gemini API Key
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
if not GEMINI_API_KEY:
    logger.error("GEMINI_API_KEY environment variable is not set. Please set it for Gemini API access.")
    raise ValueError("GEMINI_API_KEY environment variable is not set.")
genai.configure(api_key=GEMINI_API_KEY)

# URL của MCP Server
MCP_SERVER_BASE_URL = os.getenv("MCP_SERVER_BASE_URL", "http://localhost:5001")
if MCP_SERVER_BASE_URL == "http://localhost:5001":
    logger.warning("MCP_SERVER_BASE_URL is set to default localhost. Ensure this is correct for your deployment.")

# --- Lấy định nghĩa Tool từ MCP Server ---
@retry(stop=stop_after_attempt(5), wait=wait_exponential(multiplier=1, min=2, max=10), before=before_log(logger, "INFO"))
def get_tool_definitions_from_mcp_server():
    """
    Attempt to load tool definitions from the MCP Server with retries.
    """
    logger.info(f"Attempting to load tool definitions from MCP Server at {MCP_SERVER_BASE_URL}/tool_definitions...")
    response = requests.get(f"{MCP_SERVER_BASE_URL}/tool_definitions", timeout=10)
    response.raise_for_status() # Raise HTTPError for bad responses (4xx or 5xx)
    logger.info("Successfully loaded tool definitions from MCP Server.")
    return response.json()

tool_definitions = []
try:
    tool_definitions = get_tool_definitions_from_mcp_server()
except Exception as e:
    logger.error(f"Failed to load tool definitions after multiple retries: {e}")
    # In a real production system, you might want to exit or enter a degraded mode
    # For now, we'll proceed with an empty tool_definitions list.

if not tool_definitions:
    logger.warning("No tool definitions loaded. Chatbot may not be able to interact with external services (roomservice).")

# Add this function after getting tool_definitions, before creating the model:
def fix_tool_definitions(tool_defs):
    """Fix tool definitions to be compatible with Gemini API."""
    type_map = {
        "object": "OBJECT",
        "string": "STRING",
        "number": "NUMBER",
        "integer": "NUMBER",
        "boolean": "BOOLEAN",
        "array": "ARRAY",
    }
    for tool in tool_defs:
        if "parameters" in tool:
            # Fix the type of parameters
            param_type = tool["parameters"].get("type")
            if isinstance(param_type, list):
                param_type = param_type[0]
            if param_type in type_map:
                tool["parameters"]["type"] = type_map[param_type]
            # Fix property types if they exist
            if "properties" in tool["parameters"]:
                for prop in tool["parameters"]["properties"].values():
                    prop_type = prop.get("type")
                    if isinstance(prop_type, list):
                        prop_type = prop_type[0]
                    if prop_type in type_map:
                        prop["type"] = type_map[prop_type]
                    # If property is array, fix its items type as well
                    if prop.get("type") == "ARRAY" and "items" in prop and "type" in prop["items"]:
                        items_type = prop["items"]["type"]
                        if isinstance(items_type, list):
                            items_type = items_type[0]
                        if items_type in type_map:
                            prop["items"]["type"] = type_map[items_type]
    return tool_defs

# Then use the fixed definitions
if tool_definitions:
    tool_definitions = fix_tool_definitions(tool_definitions)
    logger.info(f"Fixed tool definitions: {tool_definitions}")
else:
    logger.warning("No tool definitions to fix.")

# Initialize the model with fixed tool definitions
model = genai.GenerativeModel('models/gemini-2.0-flash', tools=tool_definitions)

# Bắt đầu session chat. Dùng async để tương thích với FastAPI nếu muốn
# tích hợp chatbot_backend vào một FastAPI app khác.
# Ở đây, chúng ta sẽ giữ nó đơn giản cho console app.
chat = model.start_chat(history=[])

@retry(stop=stop_after_attempt(3), wait=wait_exponential(multiplier=1, min=2, max=5), before=before_log(logger, "DEBUG"))
def call_mcp_tool(tool_name: str, tool_args: dict) -> dict:
    """
    Gửi yêu cầu gọi tool đến MCP Server và trả về kết quả, có retry logic.
    """
    endpoint = f"{MCP_SERVER_BASE_URL}/tools/{tool_name}"
    logger.info(f"Calling MCP Server endpoint: {endpoint} with args: {tool_args}")
    try:
        response = requests.post(endpoint, json=tool_args, timeout=30)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        logger.error(f"Error calling MCP Server tool '{tool_name}': {e}")
        # Re-raise exception for tenacity to catch
        raise

async def process_user_query_async(user_query: str):
    """
    Xử lý câu hỏi của người dùng: gửi cho Gemini, xử lý tool calls, và tạo phản hồi.
    """
    logger.info(f"User: {user_query}")
    try:
        response = chat.send_message(user_query)

        # Kiểm tra xem Gemini có muốn gọi tool không
        # Accessing content.parts[0] should be safe if response.candidates exists and is not empty
        if response.candidates and response.candidates[0].content.parts and \
           response.candidates[0].content.parts[0].function_call:

            tool_call = response.candidates[0].content.parts[0].function_call
            tool_name = tool_call.name
            # Convert proto MapComposite to dict if needed
            tool_args = dict(tool_call.args) if hasattr(tool_call.args, "items") else tool_call.args

            logger.info(f"Gemini wants to call tool: '{tool_name}' with arguments: {tool_args}")

            tool_response_data = {}
            try:
                # Gọi MCP Server để thực thi tool với retry
                tool_response_data = call_mcp_tool(tool_name, tool_args)
            except Exception as e:
                logger.error(f"Failed to execute tool '{tool_name}' after retries: {e}")
                tool_response_data = {"success": False, "error": f"Không thể thực hiện tìm kiếm phòng trọ lúc này: {e}"}
            if tool_response_data.get("success"):
                # Store the raw data for returning later
                raw_room_data = tool_response_data['data']
                
                # Send the tool result to Gemini and ask for a brief summary
                function_response_part = f"Kết quả tìm kiếm: {raw_room_data}. Hãy tóm tắt kết quả này trong 1 câu ngắn gọn."
                logger.info("Sending tool call result back to Gemini for summarization...")
                final_response_gemini = chat.send_message(function_response_part)
                summary_text = final_response_gemini.text
                logger.info(f"Bot summary: {summary_text}")
                
                # Return both the raw data and the summary
                return {
                    "summary": summary_text,
                    "raw_data": raw_room_data
                }
            else:
                error_message = tool_response_data.get("error", "Đã có lỗi xảy ra khi tìm phòng.")
                logger.error(f"Error from MCP Server: {error_message}")

                error_function_response_part = f"Lỗi khi tìm phòng: {error_message}"
                final_response_gemini = chat.send_message(error_function_response_part)
                final_text = final_response_gemini.text
                logger.info(f"Bot (Error): {final_text}")
                return final_text
        else:
            final_text = response.text
            logger.info(f"Gemini responded directly: {final_text}")
            return final_text

    except Exception as e:
        logger.error(f"An unexpected error occurred in chatbot: {e}", exc_info=True)
        return "Tôi xin lỗi, đã có lỗi nội bộ không mong muốn. Vui lòng thử lại."

# Add this class before your endpoints
class ChatRequest(BaseModel):
    message: str

@app.post("/chat", summary="Chat with the bot")
async def chat_endpoint(request: ChatRequest):
    """Endpoint to send messages to the chatbot and get responses."""
    user_message = request.message
    
    if not user_message:
        return JSONResponse(
            status_code=400,
            content={"error": "No message provided"}
        )
        
    response = await process_user_query_async(user_message)
    return {"response": response}

# --- Ví dụ sử dụng trong môi trường console ---
if __name__ == "__main__":
    logger.info("Chatbot is ready. Type 'exit' to quit.")
    while True:
        user_input = input("You: ")
        if user_input.lower() == 'exit':
            break
        # Chạy hàm async trong vòng lặp sự kiện
        asyncio.run(process_user_query_async(user_input))