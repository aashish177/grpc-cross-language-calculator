# gRPC Cross-Language Calculator with Docker

A distributed calculator system demonstrating cross-language gRPC communication using protocol buffers. The project implements servers in Java and Go, clients in Python and Node.js, all containerized with Docker for seamless cross-language interoperability.

## Project Architecture

```
┌─────────────────┐    ┌─────────────────┐
│   Java Server   │    │   Go Server     │
│   (Port 50051)  │    │   (Port 50052)  │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          │    ┌─────────────────┐│
          │    │ Shared Proto    ││
          │    │ Definition      ││
          │    └─────────────────┘│
          │                      │
┌─────────┴───────┐    ┌─────────┴───────┐
│ Python Client   │    │ Node.js Client  │
│                 │    │                 │
└─────────────────┘    └─────────────────┘
```

## Features

- **Cross-Language Compatibility**: Any client can communicate with any server
- **Four Mathematical Operations**: Add, Subtract, Multiply, Divide
- **Error Handling**: Division by zero protection
- **Docker Containerization**: Each component runs in isolated containers
- **Network Communication**: Docker Compose orchestration with custom networking

## Project Structure

```
grpc-docker-project/
├── proto/
│   └── calculator.proto          # Shared protocol buffer definition
├── java-server/
│   ├── src/main/java/...
│   ├── build.gradle
│   └── Dockerfile
├── go-server/
│   ├── main.go
│   ├── go.mod
│   └── Dockerfile
├── python-client/
│   ├── calculator_client.py
│   ├── requirements.txt
│   └── Dockerfile
├── nodejs-client/
│   ├── calculator-client.js
│   ├── package.json
│   └── Dockerfile
└── docker-compose.yml
```

## Prerequisites

- Docker and Docker Compose
- Java 17+ (for local development)
- Go 1.23+ (for local development)
- Python 3.11+ (for local development)
- Node.js 18+ (for local development)
- Protocol Buffer Compiler (protoc)

## Quick Start

### 1. Clone and Navigate
```bash
git clone <your-repo>
cd grpc-project
```

### 2. Build All Services
```bash
docker-compose build
```

### 3. Start Servers
```bash
docker-compose up java-server go-server -d
```
NOTE: Wait few seconds for Java server to load

### 4. Test Native Language Pairs
```bash
# Python client → Java server
docker-compose run --rm python-client

# Node.js client → Go server
docker-compose run --rm nodejs-client
```

### 5. Test Cross-Language Compatibility
```bash
# Python client → Go server
docker-compose run --rm python-to-go

# Node.js client → Java server
docker-compose run --rm nodejs-to-java
```

### 6. View Server Logs
```bash
docker-compose logs java-server
docker-compose logs go-server
```

### 7. Cleanup
```bash
docker-compose down
```

## Local Development

### Java Server
```bash
cd java-server
./gradlew run
# Server runs on localhost:50051
```

### Go Server
```bash
cd go-server
go run main.go
# Server runs on localhost:50051
```

### Python Client
```bash
cd python-client
pip install -r requirements.txt
python -m grpc_tools.protoc -I../proto --python_out=. --grpc_python_out=. ../proto/calculator.proto
python calculator_client.py
```

### Node.js Client
```bash
cd nodejs-client
npm install
node calculator-client.js
```

## Protocol Buffer Definition

The shared `calculator.proto` defines:

```protobuf
service Calculator {
  rpc Add(CalculationRequest) returns (CalculationResponse);
  rpc Subtract(CalculationRequest) returns (CalculationResponse);
  rpc Multiply(CalculationRequest) returns (CalculationResponse);
  rpc Divide(CalculationRequest) returns (CalculationResponse);
}

message CalculationRequest {
  double a = 1;
  double b = 2;
}

message CalculationResponse {
  double result = 1;
  string operation = 2;
}
```

## Container Details

| Service | Base Image | Port | Purpose |
|---------|------------|------|---------|
| java-server | openjdk:17-jdk-slim | 50051 | Java gRPC calculator server |
| go-server | golang:1.23-alpine | 50051 | Go gRPC calculator server |
| python-client | python:3.11-slim | - | Python gRPC client |
| nodejs-client | node:18-alpine | - | Node.js gRPC client |

## Example Operations

### Addition Request
```json
{
  "a": 15.5,
  "b": 7.2
}
```

### Response
```json
{
  "result": 22.7,
  "operation": "addition"
}
```

## Testing

The system includes comprehensive testing scenarios:

1. **Same-Language Communication**
   - Python client ↔ Java server
   - Node.js client ↔ Go server

2. **Cross-Language Communication**
   - Python client ↔ Go server
   - Node.js client ↔ Java server

3. **Error Handling**
   - Division by zero protection
   - Network connectivity error handling

## Troubleshooting

### Common Issues

**Java Server Takes Time to Start**
- The Java server requires Gradle build time
- Wait 30-60 seconds after `docker-compose up` before testing clients

**gRPC Connection Refused**
- Ensure servers are fully started before running clients
- Check container logs: `docker-compose logs <service-name>`

**Proto Generation Errors**
- Verify protoc is installed and in PATH
- Check that proto file syntax is valid

### Health Checks

Check if servers are responding:
```bash
# Test Java server
curl -v telnet://localhost:50051

# Test Go server  
curl -v telnet://localhost:50052
```

## Development Notes

### Code Generation
- **Java**: Uses Gradle protobuf plugin for automatic generation
- **Go**: Manual protoc with go-grpc plugin
- **Python**: Runtime generation with grpc_tools.protoc
- **Node.js**: Dynamic loading with @grpc/proto-loader

### Build Systems
- **Java**: Gradle with protobuf plugin
- **Go**: Native go build with module dependencies
- **Python**: pip with requirements.txt
- **Node.js**: npm with package.json

## Performance Considerations

- Go server typically starts faster than Java server
- Python client has longer initial import time due to protobuf compilation
- Node.js client uses dynamic proto loading for flexibility
- All services are production-ready with proper error handling

## Contributing

1. Ensure all tests pass locally before containerizing
2. Test both same-language and cross-language scenarios
3. Verify Docker builds complete successfully
4. Update documentation for any protocol changes

## License

This project is for educational purposes demonstrating gRPC cross-language capabilities with Docker containerization.
