from abc import ABC, abstractmethod
import requests
from ..core.config import Configuration
from ..core.exceptions import ConnectionError

class BaseAPIHandler(ABC):
  def __init__(self, config: Configuration):
    self.config = config

  def _make_request(self, endpoint: str, method: str = "GET", **kwargs) -> dict:
    url = f"{self.config.server.base_url}/api/{endpoint}"

    try:
      response = requests.request(
          method=method,
          url=url,
          timeout=self.config.request_timeout,
          **kwargs
      )
      response.raise_for_status()
      return response.json()
    except requests.exceptions.RequestException as e:
      raise ConnectionError(f"Failed to connect to server: {e}")

  @abstractmethod
  def validate(self, *args, **kwargs):
    """Validate input parameters"""
    pass