# Use Python base image
FROM python:3.9-slim

# Set working directory
WORKDIR /app

# Copy requirements file and install dependencies
COPY requirements.txt .
RUN pip install -r requirements.txt

# Copy model files
COPY models/ ./models/

# Copy source code
COPY src/ ./src/

# Expose port (FastAPI service)
EXPOSE 5000

# Command to run the application
CMD ["uvicorn", "src.main:app", "--host", "0.0.0.0", "--port", "5000"]