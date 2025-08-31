FROM golang:1.25-alpine AS builder

WORKDIR /app

# Install protoc
RUN apk add --no-cache protobuf-dev

# Copy go.mod first
COPY go-server/go.mod ./

# Install protoc plugins
RUN go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
RUN go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest

# Copy proto file and generate Go code
COPY proto/calculator.proto .
RUN mkdir -p calculator
RUN protoc --go_out=./calculator --go_opt=paths=source_relative \
    --go-grpc_out=./calculator --go-grpc_opt=paths=source_relative \
    calculator.proto

# Copy source code
COPY go-server/main.go .

# Download dependencies and build
RUN go mod tidy
RUN go build -o calculator-server main.go

# Final stage
FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/

COPY --from=builder /app/calculator-server .

EXPOSE 50051

CMD ["./calculator-server"]