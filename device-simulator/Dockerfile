# Use a Python base image
FROM python:3.11-slim

# Set working directory
WORKDIR /app

# Copy requirement files and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application code
COPY simulator.py .

# Command to run the simulator.py script
CMD ["python", "simulator.py"]