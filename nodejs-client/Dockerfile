FROM node:18-alpine

WORKDIR /app

# Copy package files
COPY nodejs-client/package*.json ./

# Install dependencies
RUN npm install

# Copy proto file (needed for proto-loader)
COPY proto/calculator.proto ./proto/

# Copy client code with original name
COPY nodejs-client/calculator-client.js .

# Set default server address for containerized environment
ENV GRPC_SERVER=go-server:50051

CMD ["npm", "start"]