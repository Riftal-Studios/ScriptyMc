# ScriptyMc Python Client

## 1. Purpose

The ScriptyMc Python client is designed to provide a user-friendly interface for interacting with Minecraft through Python code. It serves as the primary interface between young programmers and the Minecraft game, abstracting away the complexity of REST API calls and providing an intuitive programming experience.

The key goals of this client are to:

- Provide a simple, Pythonic API for Minecraft interaction
- Abstract away HTTP/REST complexity
- Handle authentication and configuration automatically
- Provide robust error handling and user feedback
- Enable structured learning through examples

## 2. System Overview

The client library is structured in layers:

1. User Interface Layer:
   - Simple, intuitive methods for common operations
   - Kid-friendly function names and parameters
   - Automatic error handling and helpful messages

2. API Layer:
   - Handles REST API communication
   - Manages authentication
   - Validates inputs
   - Converts between Python and JSON data

3. Core Layer:
   - Configuration management
   - Exception handling
   - Base functionality

## 3. Supported Operations

### 3.1. Direct Operations

1. Block Placement

   ```python
   MinecraftScript.place_block(x: float, y: float, z: float, block_type: str) -> bool
   ```

   - Parameters:
     - x, y, z: Coordinates in the world
     - block_type: Type of block to place
   - Supported Block Types:
     - "STONE"
     - "DIRT"
     - "DIAMOND_BLOCK"
   - Returns: Success status

2. Entity Spawning

   ```python
   MinecraftScript.entities.spawn_entity(position: Position, entity_type: str) -> bool
   ```

   - Parameters:
     - position: Position object with coordinates
     - entity_type: Type of entity to spawn
   - Supported Entity Types: All Bukkit EntityType enum values

### 3.2. Composite Operations

1. Structure Building

   ```python
   MinecraftScript.structures.build(structure_type: str, position: Position, **kwargs) -> bool
   ```

   - Currently Supported Structures:
     - "floor": Creates a flat floor
       - Required kwargs: width: int, length: int

   Note: All composite operations are built using the direct operations above.

## 4. Architecture

### 4.1. Core Components

1. Client Interface (`MinecraftScript`):
   - Main entry point for users
   - Provides high-level methods
   - Manages API handlers

2. Configuration (`Configuration`):
   - Server connection settings
   - API key management
   - Default world settings
   - Timeout and debug options

3. API Handlers:
   - `BaseAPIHandler`: Common REST functionality
   - `BlockHandler`: Block placement and manipulation
   - `EntityHandler`: Entity spawning and control
   - `StructureHandler`: Pre-built structure creation

4. Models:
   - `Position`: Location representation
   - Clean conversion between Python and JSON

### 4.2. Security and Error Handling

1. API Key Management:
   - Multiple loading methods:
     - Environment variable (SCRIPTY_API_KEY)
     - api-key.txt file
     - Plugin directory
     - User's home directory
   - Secure storage recommendations
   - Clear error messages for missing keys
   - Automatic API key inclusion in requests
   - Header management

2. Input Validation:
   - Block type validation
   - Coordinate validation
   - World name validation

3. Error Handling:
   - Custom exception hierarchy:
     - `APIError`: Base exception
     - `AuthenticationError`: API key issues
     - `ConnectionError`: Network problems
     - `InvalidBlockError`: Invalid block types
   - User-friendly error messages
   - Detailed debugging information
   - Connection error handling

## 5. Protocol Implementation

### 5.1. Data Models

1. Position Model:

   ```python
   @dataclass
   class Position:
     x: float
     y: float
     z: float
     world: str = "world"
   ```

2. Request Models:
   - Block placement requests
   - Entity spawn requests
   - Structure building requests

## 6. Usage Examples

1. Basic Block Placement:

   ```python
   mc = MinecraftScript()
   mc.place_block(100, 64, 100, "STONE")
   ```

2. Structure Building:

   ```python
   start_pos = Position(100, 64, 100)
   mc.structures.build("floor", start_pos, width=5, length=5)
   ```

3. Configuration:

   ```python
   config = Configuration()
   config.server.port = 8080
   mc = MinecraftScript(config)
   ```

## 7. Error Handling

1. Exception Hierarchy:
   - `APIError`: Base exception
   - `AuthenticationError`: API key issues
   - `ConnectionError`: Network problems
   - `InvalidBlockError`: Invalid block types

2. User Feedback:
   - Clear error messages
   - Troubleshooting suggestions
   - Debug information when enabled

## 8. Future Enhancements

1. Extended Functionality:
   - More pre-built structures
   - Advanced building patterns
   - Entity AI control
   - Event handling

2. Educational Features:
   - Interactive tutorials
   - Code challenges
   - Achievement system
   - Visual block type browser

3. Development Tools:
   - Debugging utilities
   - Performance monitoring
   - Testing helpers
