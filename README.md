# ScriptyMc - Learn Scripting with Minecraft! 🎮

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Python 3.8+](https://img.shields.io/badge/python-3.8+-blue.svg)](https://www.python.org/downloads/)
[![Minecraft](https://img.shields.io/badge/minecraft-1.20+-brightgreen.svg)](https://www.minecraft.net/)

ScriptyMc is an educational project that helps kids learn programming by controlling Minecraft through Python code. It consists of two main components:
- A Minecraft plugin (ScriptyPlugin) that provides a REST API
- A Python client library that makes it easy to interact with the game

## 🎯 Features

- 🏗️ Place blocks and build structures
- 🐾 Spawn and control entities
- 🏰 Create pre-built structures (houses, towers, etc.)
- 🔧 Simple, kid-friendly API
- 📚 Educational examples and tutorials
- 🛡️ Safe and secure execution

[//]: # (## 🚀 Quick Start)

[//]: # ()
[//]: # (### Server Setup &#40;Plugin&#41;)

[//]: # ()
[//]: # (1. Download the latest `ScriptyPlugin.jar` from the [releases page]&#40;https://github.com/yourusername/minecraft-script/releases&#41;)

[//]: # (2. Place it in your Bukkit/Spigot server's `plugins` folder)

[//]: # (3. Start/restart your server)

[//]: # (4. Configure the plugin in `plugins/ScriptyPlugin/config.yml`)

[//]: # ()
[//]: # (```yaml)

[//]: # (# plugins/ScriptyPlugin/config.yml)

[//]: # (server:)

[//]: # (  port: 8080)

[//]: # (  api-key: "your-secret-key"  # Optional)

[//]: # (security:)

[//]: # (  allowed-ips: ["127.0.0.1"])

[//]: # (  max-requests-per-second: 20)

[//]: # (```)

[//]: # ()
[//]: # (### Client Setup &#40;Python&#41;)

[//]: # ()
[//]: # (1. Install the Python client:)

[//]: # (```bash)

[//]: # (pip install scriptymc)

[//]: # (```)

[//]: # ()
[//]: # (2. Create your first script:)

[//]: # (```python)

[//]: # (from minecraft_script import KidScript)

[//]: # ()
[//]: # (# Create our minecraft helper)

[//]: # (mc = KidScript&#40;&#41;)

[//]: # ()
[//]: # (# Build a cool house!)

[//]: # (mc.build_house&#40;100, 64, 100, size=5&#41;)

[//]: # ()
[//]: # (# Add some pets)

[//]: # (mc.spawn_pet&#40;102, 64, 102, "WOLF"&#41;)

[//]: # (mc.spawn_pet&#40;104, 64, 104, "CAT"&#41;)

[//]: # (```)

[//]: # ()
[//]: # (## 🔧 Plugin &#40;Server-Side&#41;)

[//]: # ()
[//]: # (The ScriptyPlugin is a Bukkit/Spigot plugin written in Java that provides:)

[//]: # ()
[//]: # (- RESTful API endpoints for block manipulation)

[//]: # (- Entity spawning and control)

[//]: # (- Security features and rate limiting)

[//]: # (- Configuration options)

[//]: # ()
[//]: # (### API Endpoints)

[//]: # ()
[//]: # (```)

[//]: # (POST /api/block)

[//]: # (POST /api/spawn)

[//]: # (POST /api/structure)

[//]: # (GET  /api/status)

[//]: # (```)

[//]: # ()
[//]: # (### Plugin Dependencies)

[//]: # ()
[//]: # (- Java 21 or higher)

[//]: # (- Papermc 1.21.x+)

[//]: # ()
[//]: # (## 📚 Python Client)

[//]: # ()
[//]: # (The Python client library provides an easy-to-use interface for kids while maintaining extensibility for advanced users.)

[//]: # ()
[//]: # (### Basic Usage)

[//]: # ()
[//]: # (```python)

[//]: # (from minecraft_script import KidScript)

[//]: # ()
[//]: # (mc = KidScript&#40;&#41;)

[//]: # ()
[//]: # (# Simple commands)

[//]: # (mc.place_block&#40;100, 64, 100, "DIAMOND_BLOCK"&#41;)

[//]: # (mc.spawn_pet&#40;100, 65, 100, "WOLF"&#41;)

[//]: # ()
[//]: # (# Build structures)

[//]: # (mc.build_house&#40;100, 64, 100&#41;)

[//]: # (mc.build_tower&#40;120, 64, 120, height=10&#41;)

[//]: # (```)

[//]: # ()
[//]: # (### Advanced Usage)

[//]: # ()
[//]: # (```python)

[//]: # (from minecraft_script.core import ScriptyMc)

[//]: # (from minecraft_script.models import Position)

[//]: # (from minecraft_script.core.config import Configuration)

[//]: # ()
[//]: # (# Custom configuration)

[//]: # (config = Configuration&#40;&#41;)

[//]: # (config.server.port = 8081)

[//]: # ()
[//]: # (# Create client with custom config)

[//]: # (mc = ScriptyMc&#40;config&#41;)

[//]: # ()
[//]: # (# Use advanced features)

[//]: # (position = Position&#40;100, 64, 100, world="creative"&#41;)

[//]: # (mc.structures.build&#40;"castle", position, size=10, style="medieval"&#41;)

[//]: # (```)

[//]: # ()
[//]: # ([//]: # &#40;## 🎓 Learning Resources&#41;)
[//]: # ()
[//]: # ([//]: # &#40;&#41;)
[//]: # ([//]: # &#40;- [Getting Started Guide]&#40;docs/tutorials/getting-started.md&#41;&#41;)
[//]: # ()
[//]: # ([//]: # &#40;- [Basic Tutorials]&#40;docs/tutorials/basics/&#41;&#41;)
[//]: # ()
[//]: # ([//]: # &#40;- [Advanced Projects]&#40;docs/tutorials/advanced/&#41;&#41;)
[//]: # ()
[//]: # ([//]: # &#40;- [API Reference]&#40;docs/api-reference/&#41;&#41;)
[//]: # ()
[//]: # (## 🛠️ Development Setup)

[//]: # ()
[//]: # (### Plugin Development)

[//]: # ()
[//]: # (1. Clone the repository:)

[//]: # (```bash)

[//]: # (git clone https://github.com/yourusername/minecraft-script.git)

[//]: # (```)

[//]: # ()
[//]: # (2. Build the plugin:)

[//]: # (```bash)

[//]: # (cd minecraft-script/plugin)

[//]: # (mvn clean package)

[//]: # (```)

[//]: # ()
[//]: # (3. Find the jar in `target/ScriptyPlugin.jar`)

[//]: # ()
[//]: # (### Client Development)

[//]: # ()
[//]: # (1. Set up Python environment:)

[//]: # (```bash)

[//]: # (cd minecraft-script/client)

[//]: # (python -m venv venv)

[//]: # (source venv/bin/activate  # or venv\Scripts\activate on Windows)

[//]: # (pip install -e ".[dev]")

[//]: # (```)

[//]: # ()
[//]: # (2. Run tests:)

[//]: # (```bash)

[//]: # (pytest tests/)

[//]: # (```)

[//]: # ()
[//]: # ([//]: # &#40;## 🤝 Contributing&#41;)
[//]: # ()
[//]: # ([//]: # &#40;&#41;)
[//]: # ([//]: # &#40;Contributions are welcome! Please read our [Contributing Guidelines]&#40;CONTRIBUTING.md&#41; first.&#41;)
[//]: # ()
[//]: # ([//]: # &#40;&#41;)
[//]: # ([//]: # &#40;1. Fork the repository&#41;)
[//]: # ()
[//]: # ([//]: # &#40;2. Create your feature branch &#40;`git checkout -b feature/AmazingFeature`&#41;&#41;)
[//]: # ()
[//]: # ([//]: # &#40;3. Commit your changes &#40;`git commit -m 'Add some AmazingFeature'`&#41;&#41;)
[//]: # ()
[//]: # ([//]: # &#40;4. Push to the branch &#40;`git push origin feature/AmazingFeature`&#41;&#41;)
[//]: # ()
[//]: # ([//]: # &#40;5. Open a Pull Request&#41;)
[//]: # ()
[//]: # ([//]: # &#40;&#41;)
[//]: # ([//]: # &#40;## 📝 License&#41;)
[//]: # ()
[//]: # ([//]: # &#40;&#41;)
[//]: # ([//]: # &#40;This project is licensed under the MIT License - see the [LICENSE]&#40;LICENSE&#41; file for details.&#41;)
[//]: # ()
[//]: # (## 📞 Support)

[//]: # ()
[//]: # (- Create an [issue]&#40;https://github.com/Riftal-Studios/scriptymc/issues&#41;)

[//]: # (- Join our [Discord server]&#40;https://discord.gg/QTHtkUtpSv&#41;)

## 🔮 Future Plans

- [ ] Block pattern recognition and AI building assistance
- [ ] Visual programming interface
- [ ] More educational tutorials and examples
- [ ] Support for custom structures and entities
- [ ] Multiplayer collaboration features

## ⚠️ Note

This project is not affiliated with Mojang or Microsoft. Minecraft is a trademark of Mojang Studios.
