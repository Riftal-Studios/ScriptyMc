# ScriptyMcPlugin

## 1. Purpose

ScriptyMc is an educational project designed to help children learn programming
by controlling Minecraft through Python code. It draws inspiration from projects
like MCPI (Minecraft Pi Edition API) and RaspberryJuice, but modernizes the
approach with a secure REST API and a more robust architecture.

The key goals of this project are to:

- Provide a simple, intuitive way for children to learn programming
- Create a secure and stable interface to Minecraft
- Enable programmatic control of the game world
- Support modern Minecraft versions and development practices

## 2. System Overview

The system consists of two main components:

1. Server Plugin (ScriptyMcPlugin):
   - A Bukkit/Spigot plugin written in Java
   - Provides a REST API for external interaction
   - Handles security and validation
   - Manages Minecraft world modifications

2. Python Client:
   - A user-friendly Python library
   - Abstracts the REST API complexity
   - Provides kid-friendly programming interfaces
   - Includes educational examples and tutorials

## 3. Protocol and Operations

### 3.1. Authentication

All endpoints require authentication using an API key passed via the `X-API-Key`
header.

### 3.2. REST Endpoints

1. Block Operations (`POST /api/block`)
   - Purpose: Place a single block in the world
   - Example Request:

     ```json
     {
       "world": "world",
       "x": 100,
       "y": 64,
       "z": 100,
       "material": "STONE"
     }
     ```

   - Required Parameters:

     ```json
     {
       "world": "string",
       "x": "float",
       "y": "float",
       "z": "float",
       "material": "string"
     }
     ```

   - Supported Materials: Currently limited to:
     - STONE
     - DIRT
     - DIAMOND_BLOCK

2. Entity Operations (`POST /api/spawn`)
   - Purpose: Spawn an entity in the world
   - Example Request:

     ```json
     {
       "entityType": "WOLF",
       "world": "world",
       "x": 100,
       "y": 64,
       "z": 100
     }
     ```

   - Required Parameters:

     ```json
     {
       "entityType": "string",
       "world": "string",
       "x": "float",
       "y": "float",
       "z": "float"
     }
     ```

   - Supported Entity Types: All valid Bukkit EntityType enum values

### 3.3. Response Format

All endpoints return responses in the following format:

```json
{
  "status": 200,
  "message": "Success message",
  "data": null  // Optional data field
}
```

## 4. Supported Operations

### 4.1. REST Endpoints

1. Block Operations (`POST /api/block`)
   - Purpose: Place a single block in the world
   - Required Parameters:

     ```json
     {
       "world": "string",
       "x": "float",
       "y": "float",
       "z": "float",
       "material": "string"
     }
     ```

   - Supported Materials: Currently limited to:
     - STONE
     - DIRT
     - DIAMOND_BLOCK

2. Entity Operations (`POST /api/spawn`)
   - Purpose: Spawn an entity in the world
   - Required Parameters:

     ```json
     {
       "entityType": "string",
       "world": "string",
       "x": "float",
       "y": "float",
       "z": "float"
     }
     ```

   - Supported Entity Types: All valid Bukkit EntityType enum values

### 4.2. Response Format

All endpoints return responses in the following format:

```json
{
  "status": 200,
  "message": "Success message",
  "data": null  // Optional data field
}
```

## 5. Code

### 5.1. Key Classes

1. Core Plugin:
   - `Scripty` (Main plugin class)
     - Extends JavaPlugin
     - Manages plugin lifecycle
     - Initializes REST server
     - Handles plugin configuration

2. REST Server:
   - `RestServer`
     - Manages HTTP server setup
     - Handles API key generation and validation
     - Registers endpoint handlers
     - Implements security filters

3. Request Handlers:
   - `BaseHandler`
     - Abstract base class for all handlers
     - Provides common JSON response handling
     - Manages request validation

   - `PlaceBlockHandler`
     - Handles block placement requests
     - Validates block materials
     - Ensures thread safety
     - Manages chunk loading

   - `SpawnEntityHandler`
     - Handles entity spawning
     - Validates entity types
     - Ensures thread safety

4. Models:
   - `ApiResponse`
     - Standard response format
     - Includes status, message, and optional data

   - `BlockRequest`
     - Block placement request model
     - Coordinates and material type

   - `SpawnRequest`
     - Entity spawning request model
     - Entity type and location

5. Utilities:
   - `JsonUtils`
     - JSON serialization/deserialization
     - Uses Gson for JSON processing

### 5.2. Security Features

1. API Key Authentication
   - Secure key generation using SecureRandom
   - Key storage in config.yml and api-key.txt
   - Required for all API requests

2. Thread Safety
   - All Minecraft operations run on main server thread
   - Prevents concurrency issues
   - Uses BukkitRunnable for scheduling

3. Input Validation
   - Material type validation
   - World name validation
   - Coordinate validation
   - Chunk loading checks

### 5.3. Error Handling

1. HTTP Status Codes
   - 200: Success
   - 400: Invalid request
   - 401: Unauthorized
   - 405: Method not allowed
   - 500: Internal server error

2. Detailed Error Messages
   - Human-readable error descriptions
   - Proper exception handling
   - Comprehensive logging
