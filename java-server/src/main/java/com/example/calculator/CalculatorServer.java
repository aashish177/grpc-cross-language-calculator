package com.example.calculator;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;

// Import the generated classes
import com.example.calculator.CalculatorOuterClass.CalculationRequest;
import com.example.calculator.CalculatorOuterClass.CalculationResponse;
import com.example.calculator.CalculatorGrpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CalculatorServer {
    private static final Logger logger = Logger.getLogger(CalculatorServer.class.getName());

    private Server server;

    private void start() throws IOException {
        int port = 50051;
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new CalculatorImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                CalculatorServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final CalculatorServer server = new CalculatorServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class CalculatorImpl extends CalculatorGrpc.CalculatorImplBase {
        
        @Override
        public void add(CalculationRequest req, StreamObserver<CalculationResponse> responseObserver) {
            double result = req.getA() + req.getB();
            CalculationResponse reply = CalculationResponse.newBuilder()
                    .setResult(result)
                    .setOperation("addition")
                    .setMessage(String.format("%.2f + %.2f = %.2f", req.getA(), req.getB(), result))
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            logger.info("Add operation: " + req.getA() + " + " + req.getB() + " = " + result);
        }

        @Override
        public void subtract(CalculationRequest req, StreamObserver<CalculationResponse> responseObserver) {
            double result = req.getA() - req.getB();
            CalculationResponse reply = CalculationResponse.newBuilder()
                    .setResult(result)
                    .setOperation("subtraction")
                    .setMessage(String.format("%.2f - %.2f = %.2f", req.getA(), req.getB(), result))
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            logger.info("Subtract operation: " + req.getA() + " - " + req.getB() + " = " + result);
        }

        @Override
        public void multiply(CalculationRequest req, StreamObserver<CalculationResponse> responseObserver) {
            double result = req.getA() * req.getB();
            CalculationResponse reply = CalculationResponse.newBuilder()
                    .setResult(result)
                    .setOperation("multiplication")
                    .setMessage(String.format("%.2f * %.2f = %.2f", req.getA(), req.getB(), result))
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            logger.info("Multiply operation: " + req.getA() + " * " + req.getB() + " = " + result);
        }

        @Override
        public void divide(CalculationRequest req, StreamObserver<CalculationResponse> responseObserver) {
            if (req.getB() == 0) {
                CalculationResponse reply = CalculationResponse.newBuilder()
                        .setResult(0)
                        .setOperation("division")
                        .setMessage("Error: Division by zero!")
                        .build();
                responseObserver.onNext(reply);
            } else {
                double result = req.getA() / req.getB();
                CalculationResponse reply = CalculationResponse.newBuilder()
                        .setResult(result)
                        .setOperation("division")
                        .setMessage(String.format("%.2f / %.2f = %.2f", req.getA(), req.getB(), result))
                        .build();
                responseObserver.onNext(reply);
                logger.info("Divide operation: " + req.getA() + " / " + req.getB() + " = " + result);
            }
            responseObserver.onCompleted();
        }
    }
}