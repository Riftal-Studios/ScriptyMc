# gRPC Design

## 1. Overview and Context

This document explores an approach to designing a gRPC API for the
ScriptyMcPlugin, addressing performance and scalability considerations for
real-world educational programming scenarios.

### 1.1 Motivation

The current REST API design, while functional, presents several challenges for
real-world educational programming scenarios:

1. **High Request Volume**: Simple building operations can require numerous
   individual REST calls, creating potential performance bottlenecks
2. **Latency Overhead**: Each HTTP request/response cycle adds latency, which
   can impact the responsiveness of student programs
3. **Serialization Costs**: JSON, while human-readable, introduces significant
   serialization overhead for high-frequency operations

### 1.2 Why gRPC?

gRPC offers several advantages that align well with ScriptyMc's requirements:

1. **Streaming Support**:
   - Bidirectional streaming enables efficient handling of multiple commands
   - Reduces network overhead for repetitive operations
   - Enables real-time feedback for student programs

2. **Performance Benefits**:
   - Protocol Buffers provide efficient binary serialization
   - Multiplexed connections reduce connection overhead
   - HTTP/2 transport layer offers improved performance

3. **Strong API Contract**:
   - Protocol Buffers provide strict API definitions
   - Better type safety across language boundaries
   - Excellent code generation support for both Java and Python

### 1.3 Command Pattern Strategy

We propose a hybrid approach to command implementation:

1. **Base Layer**:
   - Thin pass-through layer exposing core Minecraft API functionality
   - Direct mapping to Bukkit/Spigot APIs where appropriate
   - Enables advanced users to access full Minecraft capabilities

2. **Craft Layer**:
   - Higher-level abstractions built on the base layer
   - Intuitive crafting commands for world creation
   - Smart blueprints and recipes for complex structures
   - Progressive crafting from basic blocks to masterpieces

3. **Implementation Approach**:

   ```plaintext
   
   Craft Layer (high-level API)

    Examples:
    - world.setBlocks(x1, y1, z1, x2, y2, z2, material)   # Bulk operations
        ↓
   
   Base Layer (1:1 mapping to PaperMC/Minecraft APIs)
   
    Examples:
    - world.getBlockAt(Location)            # Direct Bukkit API
    - player.teleport(Location)             # Direct Bukkit API
    - world.spawnEntity(Location, Type)     # Direct Bukkit API
   ```

### 1.4 Technical Considerations

1. **Integration with PaperMC**:
   - Leveraging gRPC-Java as the official implementation
   - Integration with PaperMC's thread model
   - Proper handling of Minecraft's main thread constraints

2. **Client Implementation**:
   - Python gRPC client with crafting abstractions
   - Automatic command batching and optimization
   - Simplified error handling for student use

3. **Performance Optimization**:
   - Command batching and aggregation
   - Efficient use of streaming for bulk operations
   - Smart caching of frequently used structures

## 2. Introduction to gRPC Development for ScriptyMcPlugin

If you're new to gRPC development, this section will guide you through the
fundamentals and show you how to implement a gRPC-based API for ScriptyMcPlugin.
We'll start with core concepts and then walk through a complete implementation,
using practical examples that you'll need for the plugin.

### 2.1 What is gRPC and Core Concepts

gRPC is a high-performance, open-source Remote Procedure Call (RPC) framework
developed by Google. Unlike traditional REST APIs that use HTTP/JSON, gRPC uses
HTTP/2 and Protocol Buffers to achieve better performance and type safety. Let's
break down the key concepts you'll need to understand:

1. **Network Communication Model**: While REST APIs use HTTP/1.1 with a
   request-response pattern over multiple connections, gRPC works differently:

   - **HTTP/2 Transport**:
     - Uses a single, multiplexed connection for multiple requests
     - Supports full-duplex communication (both directions simultaneously)
     - Headers are compressed automatically
     - Enables streaming without connection overhead

   - **Connection Lifecycle**:

     ```plaintext
     Client                    Server
       |                         |
       |------ HTTP/2 CONNECT ---|> Connection established
       |                         |
       |--- gRPC Request --------|> Binary Protobuf message
       |                         |  
       |<-- gRPC Response -------|  Binary Protobuf message
       |                         |
       |        ... more requests/responses on same connection ...
       |                         |
       |------ Close ------------|> Connection terminated
     ```

   - **Call Types**:
     1. Unary RPC (like traditional REST):

        ```plaintext
        Client         Server
          | ----req---> |
          | <---res---- |
        ```

     2. Server Streaming:

        ```plaintext
        Client         Server
          | ----req---> |
          | <---res1--- |
          | <---res2--- |
          | <---res3--- |
        ```

     3. Client Streaming:

        ```plaintext
        Client         Server
          | ---req1---> |
          | ---req2---> |
          | ---req3---> |
          | <---res---- |
        ```

     4. Bidirectional Streaming:

        ```plaintext
        Client         Server
          | ---req1---> |
          | <---res1--- |
          | ---req2---> |
          | <---res2--- |
        ```

2. **Protocol Buffers (Protobuf)**: Protocol Buffers are at the heart of gRPC.
   Think of them as a language-agnostic way to define your API's structure -
   similar to how you might write a Java interface, but one that can be used
   across different programming languages. Here's what you need to know:

   - The Interface Definition Language (IDL) used to define service contracts
   - Defines message structures (like Java classes) and service methods (like
     interface methods) in `.proto` files
   - Language-agnostic, generates code for multiple languages
   - Messages are serialized into binary format (unlike JSON text in REST)
   - Example:

     ```protobuf
     syntax = "proto3";
     
     service CraftService {
         rpc SetBlock(SetBlockRequest) returns (Empty) {}
     }
     
     message SetBlockRequest {
         int32 x = 1;
         int32 y = 2;
         int32 z = 3;
         string block_type = 4;
     }
     
     message Empty {}
     ```

   In this example, we define a service called `CraftService` with a method
   `SetBlock`. The numbers after each field (1, 2, 3, 4) are field identifiers
   that Protobuf uses for serialization - they should never change once your API
   is in use.

3. **Service Definition**: Services in gRPC are similar to interfaces in Java or
   classes in Python. They define the methods that clients can call remotely.
   Each method type maps to a different network communication pattern:

   ```protobuf
   service GameService {
       // Unary RPC - single request/response (like REST)
       rpc SetBlock(SetBlockRequest) returns (Empty) {}
       
       // Server Streaming - useful for updates or large data
       rpc WatchPlayerMovement(PlayerRequest) returns (stream PlayerPosition) {}
       
       // Client Streaming - useful for uploading data
       rpc BuildStructure(stream BlockPlacement) returns (BuildResult) {}
       
       // Bidirectional Streaming - useful for real-time interaction
       rpc ChatChannel(stream ChatMessage) returns (stream ChatMessage) {}
   }
   ```

4. **Code Generation**: One of gRPC's most powerful features is its code
   generation. When you compile your `.proto` files, the Protobuf compiler
   generates:
   - Message classes - for the data structures you defined
   - Service base classes - for implementing your server
   - Client stubs - for making calls to your server This generated code handles
   all the networking and serialization details, letting you focus on your
   application logic.

5. **Error Handling and Status Codes**: Unlike REST's HTTP status codes, gRPC
   uses its own status codes that are more specific to RPC operations:

   ```plaintext
   OK                  =  0  // Success
   CANCELLED           =  1  // Operation was cancelled
   UNKNOWN             =  2  // Unknown error
   INVALID_ARGUMENT    =  3  // Client specified invalid argument
   DEADLINE_EXCEEDED   =  4  // Deadline expired before operation completed
   NOT_FOUND           =  5  // Some requested entity was not found
   ALREADY_EXISTS      =  6  // Entity already exists
   PERMISSION_DENIED   =  7  // Caller doesn't have permission
   RESOURCE_EXHAUSTED  =  8  // Resource has been exhausted
   FAILED_PRECONDITION =  9  // Operation rejected because system not in correct state
   ABORTED             = 10  // Operation was aborted
   OUT_OF_RANGE        = 11  // Operation was attempted past valid range
   UNIMPLEMENTED       = 12  // Operation not implemented or supported
   INTERNAL            = 13  // Internal error
   UNAVAILABLE         = 14  // Service currently unavailable
   DATA_LOSS           = 15  // Unrecoverable data loss or corruption
   UNAUTHENTICATED     = 16  // Request not authenticated
   ```

### 2.2 Implementation Steps

Now that we understand the basics, let's walk through implementing a gRPC
service for ScriptyMcPlugin. We'll build this step-by-step, explaining each part
along the way.

1. **Set Up Project Dependencies**: First, we need to add the necessary
   dependencies to our project. For a Minecraft plugin, we'll need both Java
   (server-side) and Python (client-side) dependencies:

   For Java (Server) - add to your `build.gradle`:

   ```gradle
   plugins {
       id 'java'
       id 'com.google.protobuf' version '0.9.4'
       // ... other plugins ...
   }

   protobuf {
       protoc {
           artifact = "com.google.protobuf:protoc:3.25.1"
       }
       plugins {
           grpc {
               artifact = 'io.grpc:protoc-gen-grpc-java:1.60.0'
           }
       }
       generateProtoTasks {
           all()*.plugins {
               grpc {}
           }
       }
   }

   dependencies {
       implementation 'io.grpc:grpc-netty-shaded:1.60.0'
       implementation 'io.grpc:grpc-protobuf:1.60.0'
       implementation 'io.grpc:grpc-stub:1.60.0'
       compileOnly 'javax.annotation:javax.annotation-api:1.3.2'
   }
   ```

   For Python (Client):

   ```bash
   pip install grpcio grpcio-tools
   ```

   The `grpcio` package provides the runtime for gRPC, while `grpcio-tools`
   includes the Protobuf compiler and gRPC plugin.

2. **Define the Protocol**: Next, we'll create our service definition in a
   `.proto` file. This is where we define our API contract - the methods clients
   can call and the data structures they'll use:

   Create `scripty_mc.proto`:

   ```protobuf
   syntax = "proto3";
   
   package scripty_mc;
   
   service CraftService {
       // Set a single block in the world
       rpc SetBlock(SetBlockRequest) returns (Empty) {}
       // Get information about a block at specific coordinates
       rpc GetBlock(GetBlockRequest) returns (GetBlockResponse) {}
   }
   
   message SetBlockRequest {
       int32 x = 1;  // X coordinate
       int32 y = 2;  // Y coordinate
       int32 z = 3;  // Z coordinate
       string block_type = 4;  // Minecraft material name (e.g., "STONE")
   }
   
   message GetBlockRequest {
       int32 x = 1;
       int32 y = 2;
       int32 z = 3;
   }
   
   message GetBlockResponse {
       string block_type = 1;  // The material name of the block
   }
   
   message Empty {}  // Used for methods that don't need to return data
   ```

   Notice how we've added comments to explain each field - this is good practice
   as these comments will appear in the generated code.

3. **Generate Code**: Now we'll use the Protobuf compiler to generate the code
   we need. You'll run these commands from your project root:

   For Java (this generates code in your source directory):

   ```bash
   protoc --java_out=./src/main/java \
          --grpc-java_out=./src/main/java \
          scripty_mc.proto
   ```

   For Python (this generates Python modules in your current directory):

   ```bash
   python -m grpc_tools.protoc \
          --python_out=. \
          --grpc_python_out=. \
          scripty_mc.proto
   ```

4. **Implement the Server**: Now comes the interesting part - implementing our
   gRPC service in Java. We'll create a class that extends the generated service
   base class:

   ```java
   public class CraftServiceImpl extends CraftServiceGrpc.CraftServiceImplBase {
       private final Plugin plugin;
       
       public CraftServiceImpl(Plugin plugin) {
           this.plugin = plugin;
       }
       
       @Override
       public void setBlock(SetBlockRequest request, 
                          StreamObserver<Empty> responseObserver) {
           // Important: Minecraft requires world modifications to happen on the main thread
           Bukkit.getScheduler().runTask(plugin, () -> {
               try {
                   // Get the default world - you might want to make this configurable
                   World world = Bukkit.getWorlds().get(0);
                   
                   // Get the block at the specified coordinates
                   Block block = world.getBlockAt(
                       request.getX(), 
                       request.getY(), 
                       request.getZ()
                   );
                   
                   // Convert the string block type to a Minecraft Material
                   Material material = Material.valueOf(request.getBlockType());
                   block.setType(material);
                   
                   // Send success response
                   responseObserver.onNext(Empty.getDefaultInstance());
                   responseObserver.onCompleted();
               } catch (Exception e) {
                   // Convert any errors into gRPC errors
                   responseObserver.onError(
                       Status.INTERNAL
                           .withDescription(e.getMessage())
                           .asRuntimeException()
                   );
               }
           });
       }
   }
   ```

   Note how we handle Minecraft's threading requirements and provide meaningful
   error messages.

5. **Start the gRPC Server**: We need a class to manage our gRPC server's
   lifecycle. This will integrate with our Minecraft plugin:

   ```java
   public class GrpcServer {
       private final Server server;
       
       public GrpcServer(Plugin plugin, int port) throws IOException {
           // Create the gRPC server
           server = ServerBuilder.forPort(port)
               .addService(new CraftServiceImpl(plugin))
               .build();
       }
       
       public void start() throws IOException {
           server.start();
           // You might want to log this
           System.out.println("gRPC server started on port " + server.getPort());
       }
       
       public void stop() {
           if (server != null) {
               server.shutdown();
           }
       }
   }
   ```

6. **Plugin Lifecycle Management**: The gRPC server must be properly managed
   within the plugin lifecycle:

    ```java
    public class ScriptyMcPlugin extends JavaPlugin {
        private static ScriptyMcPlugin instance;
        private GrpcServer grpcServer;

        @Override
        public void onEnable() {
            instance = this;
            try {
                grpcServer = new GrpcServer(this, 50051);
                grpcServer.start();
                getLogger().info("gRPC server started successfully");
            } catch (IOException e) {
                getLogger().severe("Failed to start gRPC server: " + e.getMessage());
                getServer().getPluginManager().disablePlugin(this);
            }
        }

        @Override
        public void onDisable() {
            if (grpcServer != null) {
                grpcServer.stop();
                getLogger().info("gRPC server stopped");
            }
        }

        public static ScriptyMcPlugin getInstance() {
            return instance;
        }
    }

    ```

    This implementation ensures that:

    - The gRPC server starts when the plugin is enabled
    - Proper error handling if the server fails to start
    - Clean shutdown when the plugin is disabled
    - Access to the plugin instance through a singleton pattern

7. **Implement the Client**: Finally, we'll create a Python client that makes it
   easy to interact with our server:

    ```python
    import grpc
    from scripty_mc_pb2 import SetBlockRequest
    from scripty_mc_pb2_grpc import CraftServiceStub

    class MinecraftClient:
        def __init__(self, host='localhost', port=50051):
            # Create a channel to our server
            self.channel = grpc.insecure_channel(f'{host}:{port}')
            # Create a stub (client)
            self.stub = CraftServiceStub(self.channel)
        
        def set_block(self, x: int, y: int, z: int, block_type: str) -> bool:
            """
            Place a block in the Minecraft world.
            
            Args:
                x, y, z: Coordinates for the block
                block_type: Minecraft material name (e.g., "STONE")
            
            Returns:
                bool: True if successful, False if an error occurred
            """
            request = SetBlockRequest(
                x=x, y=y, z=z, block_type=block_type
            )
            try:
                self.stub.SetBlock(request)
                return True
            except grpc.RpcError as e:
                print(f"Error: {e.details()}")
                return False
    ```

    Notice how we've added proper documentation and error handling to make the
    client easy to use.

### 2.3 Key Implementation Considerations

When implementing gRPC for a Minecraft plugin, there are several important
factors to consider:

1. **Thread Safety**: Minecraft's architecture has specific threading
   requirements that we must respect:
   - World modifications must happen on the main thread
   - Use `Bukkit.getScheduler().runTask()` for server-side operations
   - Be careful with long-running operations that might block the main thread

2. **Error Handling**: Proper error handling is crucial for a good user
   experience:
   - Use appropriate gRPC status codes for different types of errors
   - Provide meaningful error messages that help users understand what went
     wrong
   - Implement proper exception handling on both client and server sides

3. **Performance**: gRPC provides several features to help optimize performance:
   - HTTP/2 multiplexing allows multiple requests over a single connection
   - Consider implementing batch operations for multiple blocks
   - Use streaming for real-time updates or large data transfers
   - Take advantage of Protocol Buffers' efficient serialization

4. **Security**: Don't forget about security considerations:
   - Implement authentication using gRPC interceptors
   - Consider using TLS for secure communication
   - Validate all input parameters before executing commands
   - Implement rate limiting if needed

### 2.4 Testing the Implementation

Testing is crucial for ensuring your gRPC service works correctly. Here's how to
test both sides of the implementation:

1. **Server Testing**: Create unit tests for your service implementation:

   ```java
   /**
    * Test Helper Classes
    */
   public class TestStreamObserver<T> implements StreamObserver<T> {
       public List<T> received = new ArrayList<>();
       public Throwable error;
       public boolean completed = false;

       @Override
       public void onNext(T value) {
           received.add(value);
       }

       @Override
       public void onError(Throwable t) {
           error = t;
       }

       @Override
       public void onCompleted() {
           completed = true;
       }
   }

   /**
    * Example Test Case
    */
   @Test
   public void testSetBlock() {
       // Create an instance of our service
       CraftServiceImpl service = new CraftServiceImpl(plugin);
       
       // Create a test request
       SetBlockRequest request = SetBlockRequest.newBuilder()
           .setX(0).setY(64).setZ(0)
           .setBlockType("STONE")
           .build();
       
       // Create a test observer to capture the response
       TestStreamObserver<Empty> responseObserver = new TestStreamObserver<>();
       
       // Call the method
       service.setBlock(request, responseObserver);
       
       // Verify the results
       assertTrue(responseObserver.completed);
       assertNull(responseObserver.error);
   }
   ```

2. **Client Testing**: Test the client implementation to ensure it handles both
   success and failure cases:

   ```python
   def test_set_block():
       # Create a client instance
       client = MinecraftClient()
       
       # Test successful block placement
       success = client.set_block(0, 64, 0, "STONE")
       assert success
       
       # Test invalid block type
       success = client.set_block(0, 64, 0, "INVALID_BLOCK")
       assert not success
   ```

### 2.5 Next Steps

Now that you understand the basics of implementing gRPC in ScriptyMcPlugin, you
can explore more advanced features:

- Section 3: Learn about the pass-through layer implementation for advanced
  usage
- Section 4: Discover how to implement the craft layer for user-friendly
  abstractions
- Section 5: Understand API compatibility considerations

This introduction provides the foundation you need to start implementing gRPC in
ScriptyMcPlugin. The following sections will build upon these concepts to create
a robust and efficient communication layer between Python clients and the
Minecraft server.

### 2.6 Conclusion and Further Reading

This section serves as an introductory tutorial for users new to gRPC, focusing
on the craft layer for accessibility. It provides a practical example of setting
a block, ensuring users can quickly get started with ScriptyMcPlugin. For deeper
insights into implementation details, readers are directed to Section 3 for the
pass-through layer and Section 4 for the craft layer's architecture. The
document also references the RaspberryJuice API (Section 5.1) for additional
context, highlighting how ScriptyMcPlugin enhances educational programming in
Minecraft.

### 2.7 Key Citations

- [gRPC Official Documentation Overview](https://grpc.io/docs/)
- [Protocol Buffers Developer
  Guide](https://developers.google.com/protocol-buffers)
- [PaperMC Project Documentation](https://papermc.io/docs)

## 3. Pass-Through Layer

The Pass-Through Layer serves as an escape hatch in the ScriptyMcPlugin's gRPC
API, enabling remote clients to directly invoke low-level PaperMC APIs when
higher-level abstractions (provided by the Craft Layer) do not suffice. This
layer is designed to delegate arbitrary method calls to the underlying PaperMC
server, leveraging its extensive Bukkit-based API for advanced operations. While
the higher-level Craft Layer is intended for most educational use cases, the
Pass-Through Layer ensures flexibility for power users needing fine-grained
control.

### 3.1 Design Overview

The Pass-Through Layer exposes a generic gRPC endpoint that delegates requests
to PaperMC APIs via a unified client interface. Key design principles include:

- **Generic Invocation**: A single method, `call_mc`, allows clients to invoke
  any PaperMC API method by specifying the target instance, method name, and
  parameters.
- **Object Reference System**: Uses unique identifiers to reference PaperMC
  objects (e.g., worlds, blocks), enabling clients to manipulate specific
  instances across calls.
- **Type Flexibility**: Supports a range of parameter and return types, from
  primitives to complex objects, using Protocol Buffers (Protobuf) for
  serialization.
- **Client Signaling**: Integrates with the `MinecraftClient` class, where
  `call_mc` explicitly signals use of the pass-through API, distinct from
  higher-level methods like `set_block`.

This layer complements the Craft Layer by providing a direct conduit to PaperMC
functionality, such as `World.getBlockAt()` or `Server.getWorld()`, without
predefined abstractions.

### 3.2 Protobuf Definition

The gRPC service and message definitions enable the pass-through mechanism.
Below is the Protobuf schema tailored for the `call_mc` approach:

```protobuf
syntax = "proto3";

package scripty_mc;

service PassThroughService {
    rpc CallMc(PassThroughRequest) returns (PassThroughResponse);
}

message PassThroughRequest {
    ObjectReference instance = 1;    // The object to invoke the method on (e.g., "server")
    string method_name = 2;          // The PaperMC API method (e.g., "getWorld")
    repeated Parameter parameters = 3; // Method arguments
}

message PassThroughResponse {
    oneof value {
        int32 int_value = 1;
        double double_value = 2;
        string string_value = 3;
        Location location = 4;
        string material_value = 5;
        ObjectReference object_reference = 6;
        ListParameter list_value = 7;
    }
}

message Parameter {
    oneof value {
        int32 int_value = 1;
        double double_value = 2;
        string string_value = 3;
        Location location = 4;
        string material_value = 5;
        ObjectReference object_reference = 6;
        ListParameter list_value = 7;
    }
}

message ObjectReference {
    string class_name = 1;    // Fully qualified class (e.g., "org.bukkit.Server")
    string identifier = 2;    // Unique ID (e.g., "server", "world123")
}

message Location {
    string world_name = 1;
    double x = 2;
    double y = 3;
    double z = 4;
    float pitch = 5;
    float yaw = 6;
}

message ListParameter {
    repeated Parameter elements = 1;
}
```

This schema defines `CallMc` as the RPC method, aligning with the client-side
`call_mc` naming convention, and supports flexible inputs and outputs for
PaperMC API calls.

### 3.3 Server Implementation

The server-side implementation uses Java and integrates with PaperMC's plugin
system, leveraging reflection for dynamic method invocation. Below is an example
implementation:

```java
import io.grpc.stub.StreamObserver;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PassThroughServiceImpl extends PassThroughServiceGrpc.PassThroughServiceImplBase {
    private final Map<String, Object> objectMap = new HashMap<>();
    private final Map<String, Class<?>> classMap = new HashMap<>();
    private final Plugin plugin;

    public PassThroughServiceImpl(Plugin plugin) {
        this.plugin = plugin;
        objectMap.put("server", plugin.getServer());
        classMap.put("org.bukkit.Server", Server.class);
        classMap.put("org.bukkit.World", World.class);
        classMap.put("org.bukkit.block.Block", Block.class);
    }

    @Override
    public void callMc(PassThroughRequest request, StreamObserver<PassThroughResponse> responseObserver) {
        try {
            Object instance = getObject(request.getInstance().getIdentifier());
            Method method = getMethod(instance.getClass(), request.getMethodName(), request.getParametersCount());
            Object[] args = convertParameters(request.getParametersList());
            Object result = method.invoke(instance, args);
            PassThroughResponse response = convertResult(result);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private Object getObject(String identifier) {
        return objectMap.computeIfAbsent(identifier, k -> { throw new IllegalArgumentException("Unknown object: " + k); });
    }

    private Method getMethod(Class<?> clazz, String methodName, int paramCount) throws NoSuchMethodException {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(methodName) && m.getParameterCount() == paramCount) {
                return m; // Simplified; real impl should match parameter types
            }
        }
        throw new NoSuchMethodException(methodName);
    }

    private Object[] convertParameters(com.google.protobuf.ProtocolStringList parameters) {
        Object[] args = new Object[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            String param = parameters.get(i);
            // Simplified type conversion; expand for full Parameter support
            args[i] = param; // Assume string parameters for this example
        }
        return args;
    }

    private PassThroughResponse convertResult(Object result) {
        PassThroughResponse.Builder response = PassThroughResponse.newBuilder();
        if (result == null) return response.build();
        if (result instanceof Integer) {
            response.setIntValue((Integer) result);
        } else if (result instanceof String) {
            response.setStringValue((String) result);
        } else if (result instanceof World) {
            String id = UUID.randomUUID().toString();
            objectMap.put(id, result);
            response.setObjectReference(ObjectReference.newBuilder()
                .setClassName("org.bukkit.World")
                .setIdentifier(id)
                .build());
        } // Add more type conversions as needed
        return response.build();
    }
}
```

**Notes:**

- This simplified version assumes string parameters for brevity. A full
  implementation would handle the `Parameter` message's `oneof` types (e.g.,
  `Location`, `Material`) as shown in prior examples.
- The `objectMap` stores references to PaperMC objects, with identifiers
  returned to clients for subsequent calls.

### 3.4 Client Interface

The client-side interface integrates the Pass-Through Layer into the
`MinecraftClient` class, using `call_mc` to signal raw API usage:

```python
class MinecraftClient:
    def __init__(self, channel):
        self.craft = CraftServiceStub(channel)  # Hypothetical higher-level service
        self.passthrough = PassThroughServiceStub(channel)

    def set_block(self, x, y, z, material):
        """Set a block using the higher-level Craft API."""
        return self.craft.SetBlock(x=x, y=y, z=z, material=material)

    def call_mc(self, instance, method_name, parameters):
        """Call a raw Minecraft PaperMC API method."""
        request = PassThroughRequest(
            instance=ObjectReference(identifier=instance),
            method_name=method_name,
            parameters=parameters  # Simplified; full impl would use Parameter messages
        )
        return self.passthrough.CallMc(request)

# Usage
client = MinecraftClient(channel)

# Higher-level API
client.set_block(10, 20, 30, "STONE")

# Pass-through API via call_mc
world_ref = client.call_mc("server", "getWorld", ["world"])
```

**Notes:**

- `call_mc` explicitly invokes the `PassThroughService.CallMc` RPC, contrasting
  with higher-level methods like `set_block`.
- Parameters are shown as a simple list here; a full implementation would use
  Protobuf `Parameter` messages.

### 3.5 Example Workflow

Consider retrieving a block's type at coordinates (10, 20, 30):

1. **Higher-Level API (Craft Layer)**:

   ```python
   block_type = client.get_block(10, 20, 30)  # Hypothetical Craft method
   ```

2. **Pass-Through API (call_mc)**:

   ```python
   # Get World object
   world_ref = client.call_mc("server", "getWorld", ["world"])
   # Get Block object
   block_ref = client.call_mc(world_ref.identifier, "getBlockAt", [10, 20, 30])
   # Get block type
   block_type = client.call_mc(block_ref.identifier, "getType", [])
   ```

This demonstrates how `call_mc` enables step-by-step access to PaperMC APIs,
returning object references for chaining calls.

### 3.6 Integration with PaperMC

- **Thread Safety**: PaperMC operations must run on the main thread. The gRPC
  server should use PaperMC's scheduler (e.g.,
  `Bukkit.getScheduler().runTask()`) to ensure compliance.
- **Performance**: Reflection introduces overhead, mitigated by caching `Method`
  objects for common calls (e.g., `getBlockAt`).
- **Security**: Restrict accessible classes/methods (e.g., whitelist
  `org.bukkit.*`) to prevent unintended access to plugin internals.

### 3.7 Advantages and Trade-offs

**Advantages:**

- Provides unrestricted access to PaperMC APIs, ideal for advanced educational
  scenarios.
- `call_mc` clearly signals raw API usage, maintaining a user-friendly
  distinction from the Craft Layer.
- Flexible object reference system supports complex workflows.

**Trade-offs:**

- Requires knowledge of PaperMC APIs, less intuitive for beginners than the
  Craft Layer.
- Reflection-based approach adds complexity and potential performance costs.
- Manual parameter/result conversion increases client-side effort for advanced
  use.

## 4. Craft Layer

The Craft Layer provides high-level APIs that abstract the complexities of the
underlying Minecraft API, making it easier for users, particularly students, to
interact with the game world. This layer is built on top of the Base Layer,
which offers a direct mapping to the PaperMC/Minecraft APIs. The goal is to
create a system that allows for repeatable and extensible command creation,
comparable to (but not necessarily compatible with) the RaspberryJuice API.

### 4.1 Architecture and Approach

The Craft Layer employs the **Command Pattern** to encapsulate requests as
objects, ensuring a clean separation of concerns and easy extensibility. Each
high-level operation, such as setting a block or spawning an entity, is
represented by a concrete command class that implements a common `Command`
interface. These command objects are responsible for executing the operation by
interacting with the Base Layer.

The **gRPC service** defines methods that correspond to these high-level
operations. When a client invokes a gRPC method, the server-side implementation
creates the appropriate command object and executes it. This design ensures that
the API is both intuitive and efficient, leveraging gRPC's performance benefits
while maintaining a user-friendly interface.

The architecture is designed to be modular and repeatable:

- Each new command follows the same pattern: define a Protobuf message, create a
  command class, and implement the gRPC service method.
- The Base Layer handles direct interactions with Minecraft APIs, allowing the
  Craft Layer to focus on high-level abstractions.

### 4.2 Dispatch and Wiring

To bridge the gap between the exposed gRPC endpoints and the execution of
commands in the Craft Layer, a dispatch mechanism is implemented. This mechanism
involves a gRPC service that receives client requests, maps them to appropriate
command objects, and ensures their execution within Minecraft's constraints
(e.g., running on the main thread). Below, we outline the key components and
classes that handle this wiring.

#### 4.2.1 gRPC Service Implementation

The entry point for client requests is the `CraftServiceImpl` class, which
extends the auto-generated `CraftServiceGrpc.CraftServiceImplBase` from the
Protobuf definitions. This class overrides methods corresponding to each
high-level operation exposed by the gRPC API, such as `setBlock`. Each method is
responsible for:

- Receiving the incoming request.
- Extracting parameters from the request.
- Instantiating the appropriate command object.
- Triggering its execution.

Here's an example implementation of the `setBlock` method:

```java
public class CraftServiceImpl extends CraftServiceGrpc.CraftServiceImplBase {

    @Override
    public void setBlock(SetBlockRequest request, StreamObserver<Empty> responseObserver) {
        int x = request.getX();
        int y = request.getY();
        int z = request.getZ();
        String blockType = request.getBlockType();

        Command command = new SetBlockCommand(x, y, z, blockType);
        executeCommand(command, responseObserver);
    }

    // Additional methods for other commands follow a similar pattern
}
```

In this implementation, the `SetBlockRequest` (a Protobuf-generated class)
contains the coordinates and block type, which are used to create a
`SetBlockCommand` instance. The command is then passed to a helper method,
`executeCommand`, for execution.

#### 4.2.2 Command Execution Helper

To standardize command execution across all gRPC methods, a helper method
`executeCommand` is defined within `CraftServiceImpl`. This method ensures that:

- Commands are executed on Minecraft's main thread (a requirement due to the
  game's thread-safety constraints).
- Errors are caught and returned as gRPC status codes.
- Successful responses are sent back to the client.

Here's the implementation:

```java
private void executeCommand(Command command, StreamObserver<Empty> responseObserver) {
    Bukkit.getScheduler().runTask(ScriptyMcPlugin.getInstance(), () -> {
        try {
            command.execute();
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    });
}
```

- **Thread Scheduling**: The `Bukkit.getScheduler().runTask` call schedules the
  command execution on the main thread using PaperMC's scheduler, with
  `ScriptyMcPlugin.getInstance()` providing the plugin context.
- **Error Handling**: If an exception occurs during `command.execute()`, it's
  caught and converted into a gRPC `Status.INTERNAL` error, preserving the error
  message for the client.
- **Response**: On success, an empty Protobuf message (`Empty`) is sent, and the
  response stream is completed.

#### 4.2.3 Command Interface and Classes

The `Command` interface defines the contract for all command implementations,
ensuring consistency:

```java
public interface Command {
    void execute() throws Exception;
}
```

Each command, such as `SetBlockCommand`, implements this interface. For example:

```java
public class SetBlockCommand implements Command {
    private final int x, y, z;
    private final String blockType;

    public SetBlockCommand(int x, int y, int z, String blockType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockType = blockType;
    }

    @Override
    public void execute() throws Exception {
        Material material = Material.matchMaterial(blockType);
        if (material == null) {
            throw new IllegalArgumentException("Invalid block type: " + blockType);
        }
        World world = Bukkit.getWorlds().get(0); // Note: This is simplified, consider using world configuration
        world.getBlockAt(x, y, z).setType(material);
    }
}
```

This class validates the `blockType` and uses Minecraft's API to set the block,
throwing an exception if the material is invalid.

#### 4.2.4 Extending with New Commands

To add a new command, such as `setBlocks` for setting multiple blocks:

1. **Update Protobuf**: Define a new `SetBlocksRequest` message and add an RPC
   method to the `CraftService` in the `.proto` file.
2. **Create Command Class**: Implement `SetBlocksCommand` with the `Command`
   interface.
3. **Add gRPC Method**: Override the corresponding method in `CraftServiceImpl`.

Example for `setBlocks`:

```java
@Override
public void setBlocks(SetBlocksRequest request, StreamObserver<Empty> responseObserver) {
    int x1 = request.getX1();
    int y1 = request.getY1();
    int z1 = request.getZ1();
    int x2 = request.getX2();
    int y2 = request.getY2();
    int z2 = request.getZ2();
    String blockType = request.getBlockType();

    Command command = new SetBlocksCommand(x1, y1, z1, x2, y2, z2, blockType);
    executeCommand(command, responseObserver);
}
```

The `SetBlocksCommand` would then iterate over the specified region and set
blocks accordingly.

#### 4.2.5 Error Handling and Validation

Validation occurs at two levels:

- **gRPC Method**: Initial checks can be performed before creating the command
  (e.g., ensuring coordinates are within bounds).
- **Command Class**: Detailed validation (e.g., converting `blockType` to a
  `Material`) happens during `execute()`, with exceptions propagated to
  `executeCommand` for gRPC error reporting.

#### 4.2.6 Thread Management

Minecraft's API requires that world-modifying operations run on the main thread.
The use of `Bukkit.getScheduler().runTask` in `executeCommand` ensures this,
making the dispatch mechanism compliant with the game's threading model.

### 4.3 Basic Classes and Infrastructure

To support this architecture, the following key classes and infrastructure are
defined:

#### 4.3.1 Command Interface

The `Command` interface defines the contract for all command objects.

```java
public interface Command {
    void execute();
}
```

#### 4.3.2 Concrete Command Classes

Each high-level operation has its own command class that implements the
`Command` interface. These classes encapsulate the parameters and logic for the
operation, interacting with the Base Layer as needed.

#### 4.3.3 Base Layer

The `BaseLayer` class acts as a facade, providing static methods for interacting
with the Minecraft API. It is assumed to have methods corresponding to common
operations, such as setting a block. For example:

```java
public class BaseLayer {
    public static void setBlock(int x, int y, int z, Material material) {
        World world = getWorld(); // Assuming a method to get the current world
        Block block = world.getBlockAt(x, y, z);
        block.setType(material);
        // Optionally, set default BlockData for the material
    }
    // Other methods for interacting with Minecraft APIs
}
```

#### 4.3.4 gRPC Service Implementation

The gRPC service handles incoming requests by creating and executing the
corresponding command objects. Each gRPC method corresponds to a specific
command, and the service implementation acts as the invoker for that command.

```java
public class CraftServiceImpl extends CraftServiceGrpc.CraftServiceImplBase {
    // gRPC methods will create and execute Command objects
}
```

This infrastructure ensures that new commands can be added by:

1. Defining a Protobuf message and service method.
2. Creating a new command class that implements the `Command` interface.
3. Implementing the gRPC service method to create and execute the new command.

### 4.4 Implementing a Specific Command: `world.setBlock`

To illustrate how the Craft Layer implements a command from the RaspberryJuice
API, we will use the `world.setBlock(x, y, z, blockID)` operation. In
RaspberryJuice, this command sets a single block at the specified coordinates to
the given block ID.

Given that ScriptyMc is comparable to RaspberryJuice but not necessarily
compatible, we adapt this command to modern Minecraft by accepting a string
representing the block's material name instead of a numeric ID. This makes the
API more intuitive for users familiar with modern Minecraft while maintaining a
similar method signature.

#### 4.4.1 Protobuf Definition

The gRPC method for setting a block is defined as follows:

```protobuf
service CraftService {
    rpc SetBlock(SetBlockRequest) returns (Empty) {}
}

message SetBlockRequest {
    int32 x = 1;
    int32 y = 2;
    int32 z = 3;
    string block_type = 4;
}
```

Here, `block_type` is a string matching the `Material` enum names in Minecraft,
such as "STONE" or "ORANGE_WOOL".

#### 4.4.2 Command Implementation

The `SetBlockCommand` class handles the logic for setting the block:

```java
public class SetBlockCommand implements Command {
    private final int x, y, z;
    private final String blockType;

    public SetBlockCommand(int x, int y, int z, String blockType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockType = blockType;
    }

    @Override
    public void execute() throws Exception {
        Material material = Material.matchMaterial(blockType);
        if (material == null) {
            throw new IllegalArgumentException("Invalid block type: " + blockType);
        }
        World world = Bukkit.getWorlds().get(0); // Note: This is simplified, consider using world configuration
        world.getBlockAt(x, y, z).setType(material);
    }
}
```

The `execute` method converts the `blockType` string to a `Material` enum and
calls the `BaseLayer.setBlock` method to perform the operation.

#### 4.4.3 gRPC Service Handling

The gRPC service implementation for `SetBlock` is straightforward:

```java
@Override
public void setBlock(SetBlockRequest request, StreamObserver<Empty> responseObserver) {
    String blockType = request.getBlockType();
    // Optionally, validate blockType here
    Command command = new SetBlockCommand(request.getX(), request.getY(), request.getZ(), blockType);
    try {
        command.execute();
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    } catch (RuntimeException e) {
        responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
    }
}
```

This implementation ensures that:

- The block type is valid, with errors handled gracefully via gRPC status codes.
- The command follows the established infrastructure, interacting with the Base
  Layer to perform the operation.

### 4.5 Extending the API

To add new commands, such as `world.setBlocks` for setting multiple blocks, the
process is similar:

1. Define the Protobuf message and service method.
2. Create a new command class, e.g., `SetBlocksCommand`, that implements the
   `Command` interface and uses the Base Layer to perform the operation
   efficiently.
3. Implement the gRPC service method to create and execute the new command.

This modular approach allows the API to grow while maintaining consistency and
performance.

### 4.6 Conclusion

The Craft Layer provides a robust and user-friendly interface for interacting
with the Minecraft world, leveraging the Command Pattern and gRPC for efficient
and scalable operations. By abstracting the complexities of the underlying API
and providing intuitive commands, it ensures that users can focus on creativity
and learning rather than technical details. The infrastructure established in
this section allows for repeatable command creation, enabling the ScriptyMc API
to expand in a structured and maintainable way, comparable to the RaspberryJuice
API.

## 5. Appendices

### 5.1 MCPI / RaspberryJuice API

The Minecraft Pi Edition API (MCPI) and its implementation in RaspberryJuice
provide a foundational example of Minecraft programmatic control. RaspberryJuice
extends the original MCPI API with additional features while maintaining
compatibility with the base functionality.

#### 5.1.1 World Commands

```python
# Basic World Operations
world.getBlock(x, y, z)                                    # Returns block ID
world.getBlockWithData(x, y, z)                            # Returns block ID and metadata
world.setBlock(x, y, z, blockID[, data])                   # Sets single block
world.setBlocks(x1, y1, z1, x2, y2, z2, blockID[, data])   # Sets multiple blocks
world.getBlocks(x1, y1, z1, x2, y2, z2)                    # Gets blocks in region
world.getHeight(x, z)                                      # Gets highest non-air block
world.getPlayerIds()                                       # Lists all player entity IDs
```

#### 5.1.2 Player Commands

```python
# Position and Movement
player.getTile()                   # Get block position (integer coordinates)
player.setTile(x, y, z)            # Teleport to block position
player.getPos()                    # Get exact position (float coordinates)
player.setPos(x, y, z)             # Set exact position

# Orientation
player.getDirection()              # Get facing direction vector
player.setDirection(x, y, z)       # Set facing direction
player.getRotation()               # Get horizontal rotation (yaw)
player.setRotation(yaw)            # Set horizontal rotation
player.getPitch()                  # Get vertical rotation
player.setPitch(pitch)             # Set vertical rotation

# Entity Management
player.getEntities(typeID)         # Get nearby entities
player.removeEntities(typeID)      # Remove nearby entities
```

#### 5.1.3 Entity Commands

```python
# Entity Management
entity.getTile(id)                 # Get entity block position
entity.setTile(id, x, y, z)        # Set entity block position
entity.getPos(id)                  # Get entity exact position
entity.setPos(id, x, y, z)         # Set entity exact position

# Advanced Entity Orientation (Extra feature; requires modded libraries)
entity.getDirection(id)            # Get facing direction
entity.setDirection(id, x, y, z)   # Set facing direction
entity.getRotation(id)             # Get horizontal rotation (yaw)
entity.setRotation(id, yaw)        # Set horizontal rotation
entity.getPitch(id)                # Get vertical rotation (pitch)
entity.setPitch(id, pitch)         # Set vertical rotation

entity.getName(id)                 # Get player name from entity ID
entity.getEntities(id, typeID)     # Get entities near specified entity
entity.removeEntities(id, typeID)  # Remove entities near specified entity

# Entity Spawning
spawnEntity(x, y, z, entityType)   # Spawn new entity
getEntityTypes()                   # List available entity types
removeEntity(id)                   # Remove specific entity
```
