FROM python:3.11-slim

WORKDIR /app

# Install necessary tools
RUN apt-get update && apt-get install -y --no-install-recommends \
    supervisor \
    && rm -rf /var/lib/apt/lists/*

# Create directories for both services
RUN mkdir -p /app/chatbot_backend /app/mcp_server

# Copy requirements first to leverage Docker cache
COPY chatbot_backend/requirements.txt /app/chatbot_backend/
COPY mcp_server/requirements.txt /app/mcp_server/

# Install all dependencies
RUN pip install --no-cache-dir -r /app/chatbot_backend/requirements.txt \
    && pip install --no-cache-dir -r /app/mcp_server/requirements.txt

# Copy application code
COPY chatbot_backend/ /app/chatbot_backend/
COPY mcp_server/ /app/mcp_server/

# Set environment variables
ENV PYTHONUNBUFFERED=1
ENV MCP_SERVER_PORT=5002  
ENV CHATBOT_PORT=5001     
ENV PORT=5001            
ENV ROOMSERVICE_API_BASE_URL="https://trotot-backend-api-gateway-1-0.onrender.com"
ENV MCP_SERVER_BASE_URL="http://localhost:5002" 

# Create supervisor configuration
RUN echo "[supervisord]\nnodaemon=true\n\n\
[program:mcp_server]\ncommand=uvicorn main:app --host 0.0.0.0 --port %(ENV_MCP_SERVER_PORT)s\ndirectory=/app/mcp_server\nautorestart=true\n\n\
[program:chatbot_backend]\ncommand=uvicorn chatbot_app:app --host 0.0.0.0 --port %(ENV_CHATBOT_PORT)s\ndirectory=/app/chatbot_backend\nautorestart=true\n" > /etc/supervisor/conf.d/supervisord.conf

# Expose the ports the apps run on
EXPOSE ${MCP_SERVER_PORT} ${CHATBOT_PORT}

# Start supervisor to manage both services
CMD ["supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]