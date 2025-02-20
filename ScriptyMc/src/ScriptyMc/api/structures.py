import time

from .base import BaseAPIHandler
from ..models.position import Position


class StructureHandler(BaseAPIHandler):
  def build(self, structure_type: str, position: Position, **kwargs):
    method_name = f"_build_{structure_type}"
    if hasattr(self, method_name):
      return getattr(self, method_name)(position, **kwargs)
    raise ValueError(f"Unknown structure type: {structure_type}")

  def _build_floor(self, position: Position, width: int, length: int):
    for dx in range(width):
      for dz in range(length):
        pos = Position(
            position.x + dx,
            position.y,
            position.z + dz,
            position.world
        )
        self._make_request("block", method="POST", json={
          **pos.to_dict(),
          "material": "STONE"
        })
        time.sleep(0.1)  # Prevent server overload
