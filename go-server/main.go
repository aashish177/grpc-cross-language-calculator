package main

import (
	"context"
	"log"
	"net"

	pb "go-calculator-server/calculator"
	"google.golang.org/grpc"
)

const (
	port = ":50051"
)

// server is used to implement pb.CalculatorServer.
type server struct {
	pb.UnimplementedCalculatorServer
}

// Add implements pb.CalculatorServer
func (s *server) Add(ctx context.Context, in *pb.CalculationRequest) (*pb.CalculationResponse, error) {
	result := in.GetA() + in.GetB()
	log.Printf("Add operation: %.2f + %.2f = %.2f", in.GetA(), in.GetB(), result)
	
	return &pb.CalculationResponse{
		Result:    result,
		Operation: "addition",
	}, nil
}

// Subtract implements pb.CalculatorServer
func (s *server) Subtract(ctx context.Context, in *pb.CalculationRequest) (*pb.CalculationResponse, error) {
	result := in.GetA() - in.GetB()
	log.Printf("Subtract operation: %.2f - %.2f = %.2f", in.GetA(), in.GetB(), result)
	
	return &pb.CalculationResponse{
		Result:    result,
		Operation: "subtraction",
	}, nil
}

// Multiply implements pb.CalculatorServer
func (s *server) Multiply(ctx context.Context, in *pb.CalculationRequest) (*pb.CalculationResponse, error) {
	result := in.GetA() * in.GetB()
	log.Printf("Multiply operation: %.2f * %.2f = %.2f", in.GetA(), in.GetB(), result)
	
	return &pb.CalculationResponse{
		Result:    result,
		Operation: "multiplication",
	}, nil
}

// Divide implements pb.CalculatorServer
func (s *server) Divide(ctx context.Context, in *pb.CalculationRequest) (*pb.CalculationResponse, error) {
	if in.GetB() == 0 {
		log.Printf("Division by zero attempted: %.2f / %.2f", in.GetA(), in.GetB())
		return &pb.CalculationResponse{
			Result:    0,
			Operation: "division_error",
		}, nil
	}
	
	result := in.GetA() / in.GetB()
	log.Printf("Divide operation: %.2f / %.2f = %.2f", in.GetA(), in.GetB(), result)
	
	return &pb.CalculationResponse{
		Result:    result,
		Operation: "division",
	}, nil
}

func main() {
	lis, err := net.Listen("tcp", port)
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	
	s := grpc.NewServer()
	pb.RegisterCalculatorServer(s, &server{})
	
	log.Printf("Go Calculator server listening at %v", lis.Addr())
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}