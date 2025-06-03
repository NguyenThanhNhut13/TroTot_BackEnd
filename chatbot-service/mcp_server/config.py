import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Get the Room Service API base URL from environment variables
ROOMSERVICE_API_BASE_URL = os.getenv(
    "ROOMSERVICE_API_BASE_URL", 
    "https://trotot-backend-api-gateway-1-0.onrender.com/"
)

# Remove trailing slash if present to avoid double slashes in URL construction
if ROOMSERVICE_API_BASE_URL.endswith('/'):
    ROOMSERVICE_API_BASE_URL = ROOMSERVICE_API_BASE_URL[:-1]