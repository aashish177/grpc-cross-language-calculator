import grpc
import calculator_pb2
import calculator_pb2_grpc
import sys
import os

def run_calculator_operations(server_host='localhost:50051'):
    # Create a gRPC channel
    with grpc.insecure_channel(server_host) as channel:
        # Create a stub (client)
        stub = calculator_pb2_grpc.CalculatorStub(channel)
        
        print("=== Python Calculator Client ===")
        print(f"Connected to server at {server_host}")
        
        # Test different operations
        test_cases = [
            (10.5, 5.2, "addition"),
            (15.8, 3.3, "subtraction"), 
            (7.5, 4.0, "multiplication"),
            (20.0, 4.0, "division"),
            (10.0, 0.0, "division")  # Test division by zero
        ]
        
        for a, b, operation in test_cases:
            try:
                request = calculator_pb2.CalculationRequest(a=a, b=b)
                
                if operation == "addition":
                    response = stub.Add(request)
                elif operation == "subtraction":
                    response = stub.Subtract(request)
                elif operation == "multiplication":
                    response = stub.Multiply(request)
                elif operation == "division":
                    response = stub.Divide(request)
                
                print(f"\n{operation.upper()}:")
                print(f"Request: a={a}, b={b}")
                print(f"Response: {response.message}")
                print(f"Result: {response.result}")
                
            except grpc.RpcError as e:
                print(f"RPC failed for {operation}: {e}")

def interactive_mode(server_host='localhost:50051'):
    with grpc.insecure_channel(server_host) as channel:
        stub = calculator_pb2_grpc.CalculatorStub(channel)
        
        print("\n=== Interactive Calculator Mode ===")
        print("Commands: add, subtract, multiply, divide, quit")
        
        while True:
            operation = input("\nEnter operation (or 'quit'): ").strip().lower()
            
            if operation == 'quit':
                break
                
            if operation not in ['add', 'subtract', 'multiply', 'divide']:
                print("Invalid operation. Use: add, subtract, multiply, divide, quit")
                continue
                
            try:
                a = float(input("Enter first number: "))
                b = float(input("Enter second number: "))
                
                request = calculator_pb2.CalculationRequest(a=a, b=b)
                
                if operation == 'add':
                    response = stub.Add(request)
                elif operation == 'subtract':
                    response = stub.Subtract(request)
                elif operation == 'multiply':
                    response = stub.Multiply(request)
                elif operation == 'divide':
                    response = stub.Divide(request)
                
                print(f"Result: {response.message}")
                
            except ValueError:
                print("Please enter valid numbers")
            except grpc.RpcError as e:
                print(f"RPC failed: {e}")

if __name__ == '__main__':
    server_address = os.getenv('GRPC_SERVER', 'localhost:50051')
    
    if len(sys.argv) > 1 and sys.argv[1] == '--interactive':
        interactive_mode(server_address)
    else:
        run_calculator_operations(server_address)