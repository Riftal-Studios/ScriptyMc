from abc import ABC, abstractmethod
import requests
from ..core.config import Configuration
from ..core.exceptions import ConnectionError

class APIError(Exception):
  """Base exception for API errors"""
  pass

class AuthenticationError(APIError):
  """Raised when API key authentication fails"""
  pass

class BaseAPIHandler(ABC):
  def __init__(self, config: Configuration):
    self.config = config

  def _make_request(self, endpoint: str, method: str = "GET", **kwargs) -> dict:
    url = f"{self.config.server.base_url}/api/{endpoint}"

    # Merge default headers with any provided headers
    headers = kwargs.pop('headers', {})
    headers.update(self.config.server.headers)

    try:
      response = requests.request(
          method=method,
          url=url,
          headers=headers,
          timeout=self.config.request_timeout,
          **kwargs
      )

      if response.status_code == 401:
        raise AuthenticationError("Invalid or missing API key")

      response.raise_for_status()
      return response.json()

    except requests.exceptions.RequestException as e:
      if isinstance(e, requests.exceptions.HTTPError):
        if response.status_code == 401:
          raise AuthenticationError("Invalid or missing API key")
        error_msg = f"HTTP {response.status_code}"
        try:
          error_data = response.json()
          if "error" in error_data:
            error_msg = error_data["error"]
        except Exception:
          error_msg = response.text
        raise APIError(f"Server error: {error_msg}")
      elif isinstance(e, requests.exceptions.Timeout):
        raise ConnectionError(f"Request timed out after {self.config.request_timeout} seconds")
      elif isinstance(e, requests.exceptions.ConnectionError):
        raise ConnectionError(f"Failed to connect to server at {url}")
      else:
        raise ConnectionError(f"Request failed: {str(e)}")

  @abstractmethod
  def validate(self, *args, **kwargs):
    """Validate input parameters"""
    pass
