from dataclasses import dataclass
from typing import Optional
import os
from pathlib import Path

@dataclass
class ServerConfig:
  host: str = "localhost"
  port: int = 6060
  api_key: Optional[str] = None
  protocol: str = "http"

  def __post_init__(self):
    # Try to load an API key from the environment variable
    self.api_key = self.api_key or os.getenv("SCRIPTY_API_KEY")

    # If still no API key, try to load from api-key.txt
    if not self.api_key:
      self._load_api_key_from_file()

  def _load_api_key_from_file(self):
    """Attempt to load an API key from common locations"""
    possible_locations = [
      Path("api-key.txt"),
      Path("plugins/Scripty/api-key.txt"),
      Path.home() / ".scripty" / "api-key.txt"
    ]

    for path in possible_locations:
      if path.exists():
        try:
          content = path.read_text().strip()
          # Extract key from the "API Key: <key>" format
          if ":" in content:
            self.api_key = content.split(":", 1)[1].strip()
          else:
            self.api_key = content
          break
        except Exception:
          continue

  @property
  def base_url(self) -> str:
    return f"{self.protocol}://{self.host}:{self.port}"

  @property
  def headers(self) -> dict:
    if not self.api_key:
      raise ValueError(
          "API key not found. Please either:\n"
          "1. Set SCRIPTY_API_KEY environment variable\n"
          "2. Place api-key.txt in the current directory\n"
          "3. Place api-key.txt in plugins/Scripty/\n"
          "4. Place api-key.txt in ~/.scripty/"
      )
    return {
      "X-API-Key": self.api_key,
      "Content-Type": "application/json",
      "Accept": "application/json"
    }

class Configuration:
  def __init__(self):
    self.server = ServerConfig()
    self.default_world = "world"
    self.request_timeout = 10
    self.debug_mode = False
