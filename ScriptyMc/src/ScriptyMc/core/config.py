from dataclasses import dataclass
from typing import Optional

@dataclass
class ServerConfig:
  #todo load from env
  host: str = "localhost"
  port: int = 6060
  api_key: Optional[str] = None
  protocol: str = "http"

  @property
  def base_url(self) -> str:
    return f"{self.protocol}://{self.host}:{self.port}"

class Configuration:
  def __init__(self):
    self.server = ServerConfig()
    self.default_world = "world"
    self.request_timeout = 10
    self.debug_mode = False