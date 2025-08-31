FROM python:3.11-slim

WORKDIR /app

# Copy requirements and install dependencies
COPY python-client/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy proto file and generate Python code
COPY proto/calculator.proto ./proto/
RUN python -m grpc_tools.protoc -I./proto --python_out=. --grpc_python_out=. ./proto/calculator.proto

# Copy client code
COPY python-client/calculator_client.py .

# Set default server address for containerized environment
ENV GRPC_SERVER=java-server:50051

CMD ["python", "calculator_client.py"]