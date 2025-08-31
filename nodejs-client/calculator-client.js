const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const path = require('path');

// Load the protobuf
const PROTO_PATH = './proto/calculator.proto';

const packageDefinition = protoLoader.loadSync(PROTO_PATH, {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true
});

const calculator = grpc.loadPackageDefinition(packageDefinition).calculator;

// Get server address from environment variable or use default
const SERVER_ADDRESS = process.env.GRPC_SERVER || 'localhost:50051';

function runCalculatorOperations() {
    // Create a client
    const client = new calculator.Calculator(SERVER_ADDRESS, grpc.credentials.createInsecure());
    
    console.log('=== Node.js Calculator Client ===');
    console.log(`Connected to server at ${SERVER_ADDRESS}`);
    
    // Test cases
    const testCases = [
        { a: 15.5, b: 7.2, operation: 'Add' },
        { a: 22.8, b: 5.3, operation: 'Subtract' },
        { a: 6.5, b: 3.0, operation: 'Multiply' },
        { a: 25.0, b: 5.0, operation: 'Divide' },
        { a: 12.0, b: 0.0, operation: 'Divide' }  // Test division by zero
    ];
    
    let completed = 0;
    const total = testCases.length;
    
    testCases.forEach((testCase, index) => {
        const request = { a: testCase.a, b: testCase.b };
        
        // Call the appropriate method
        const methodName = testCase.operation.toLowerCase();
        
        client[methodName](request, (error, response) => {
            if (error) {
                console.error(`\nError in ${testCase.operation}:`, error);
            } else {
                console.log(`\n${testCase.operation.toUpperCase()}:`);
                console.log(`Request: a=${testCase.a}, b=${testCase.b}`);
                console.log(`Response: operation=${response.operation}, result=${response.result}`);
            }
            
            completed++;
            if (completed === total) {
                client.close();
                console.log('\n=== All operations completed ===');
            }
        });
    });
}

function interactiveMode() {
    const client = new calculator.Calculator(SERVER_ADDRESS, grpc.credentials.createInsecure());
    const readline = require('readline');
    
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });
    
    console.log('\n=== Interactive Calculator Mode ===');
    console.log('Commands: add, subtract, multiply, divide, quit');
    
    function promptUser() {
        rl.question('\nEnter operation (or \'quit\'): ', (operation) => {
            operation = operation.trim().toLowerCase();
            
            if (operation === 'quit') {
                client.close();
                rl.close();
                return;
            }
            
            if (!['add', 'subtract', 'multiply', 'divide'].includes(operation)) {
                console.log('Invalid operation. Use: add, subtract, multiply, divide, quit');
                promptUser();
                return;
            }
            
            rl.question('Enter first number: ', (aStr) => {
                rl.question('Enter second number: ', (bStr) => {
                    const a = parseFloat(aStr);
                    const b = parseFloat(bStr);
                    
                    if (isNaN(a) || isNaN(b)) {
                        console.log('Please enter valid numbers');
                        promptUser();
                        return;
                    }
                    
                    const request = { a: a, b: b };
                    
                    client[operation](request, (error, response) => {
                        if (error) {
                            console.error('RPC failed:', error);
                        } else {
                            console.log(`Result: ${response.operation} = ${response.result}`);
                        }
                        promptUser();
                    });
                });
            });
        });
    }
    
    promptUser();
}

// Check command line arguments
if (process.argv.includes('--interactive')) {
    interactiveMode();
} else {
    runCalculatorOperations();
}