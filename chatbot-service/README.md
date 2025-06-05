# TroTot Chatbot Service

A conversational AI service for the TroTot room rental platform that helps users search for rooms through natural language queries.

## Overview

TroTot Chatbot Service uses Google's Gemini AI to understand user queries, extract search parameters, and provide summarized responses with room data.

## Architecture

- **Chatbot Backend**: Handles user queries using Gemini API.
- **MCP Server**: Middleware between the chatbot and the room service API.

Both services run in a single Docker container.

## Features

- Natural language room search
- AI-powered summarization of results
- REST API endpoints for integration

## Requirements

- Python 3.11+
- Docker
- Google Gemini API key

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| GEMINI_API_KEY | Google Gemini API key | (No default) |
| MCP_SERVER_BASE_URL | MCP server URL | http://localhost:5001 |
| ROOMSERVICE_API_BASE_URL | Room service API URL | https://trotot-backend-api-gateway-1-0.onrender.com |
| CHATBOT_PORT | Chatbot service port | 5002 |
| MCP_SERVER_PORT | MCP server port | 5001 |

## Setup

### Using Docker (Recommended)

```bash
git clone <repository-url>
cd TroTot_BackEnd/chatbot-service
echo "GEMINI_API_KEY=your_gemini_api_key" > .env
docker build -t trotot-chatbot .
docker run -p 5002:5002 -p 5001:5001 --env-file .env trotot-chatbot
```

### Running Services Separately

```bash
pip install -r requirements.txt
export GEMINI_API_KEY=your_gemini_api_key
python mcp_server/main.py
# In a new terminal
python chatbot/main.py
```

## API Endpoints

### Chatbot Backend (Port 5002)

- `POST /chat`:  
    **Request:**  
    ```json
    {
        "message": "I want to find a room in District 1 under 5 million VND"
    }
    ```

### MCP Server (Port 5001)

- `POST /mcp/search`:  
    **Request:**  
    ```json
    {
        "location": "District 1",
        "max_price": 5000000
    }
    ```
    **Response:**  
    ```json
    {
        "results": [ ... ]
    }
    ```

## Deployment to Render

1. Fork/clone this repository.
2. Create a new Web Service on Render.
3. Select "Build and deploy from a Docker image".
4. Connect your GitHub repository.
5. Set environment variables:
     - `PORT`: 5002
     - `GEMINI_API_KEY`: Your Gemini API key
6. The chatbot service will be accessible via the Render URL.

## Codebase Structure

- `chatbot_backend/`: Gemini-powered chatbot (FastAPI)
- `mcp_server/`: Middleware for tool operations
- `Dockerfile`: Containerization for both services

## License

NTN License.

## Contributing

Contributions are welcome! Please open issues or submit pull requests.
