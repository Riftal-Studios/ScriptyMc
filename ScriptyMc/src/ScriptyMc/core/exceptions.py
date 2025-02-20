class MinecraftScriptException(Exception):
  """Base exception for MinecraftScript"""
  pass

class ConnectionError(MinecraftScriptException):
  """Raised when connection to server fails"""
  pass

class InvalidBlockError(MinecraftScriptException):
  """Raised when invalid block type is specified"""
  pass

class InvalidEntityError(MinecraftScriptException):
  """Raised when invalid entity type is specified"""
  pass