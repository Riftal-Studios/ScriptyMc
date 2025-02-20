from typing import Optional

from .config import Configuration
from ..api.blocks import BlockHandler
from ..api.entities import EntityHandler
from ..api.structures import StructureHandler
from ..models.position import Position


class MinecraftScript:
  def __init__(self, config: Optional[Configuration] = None):
    self.config = config or Configuration()

    # Initialize API handlers
    self.blocks = BlockHandler(self.config)
    self.entities = EntityHandler(self.config)
    self.structures = StructureHandler(self.config)

  def place_block(self, x: float, y: float, z: float, block_type: str) -> bool:
    """User-friendly method to place a block"""
    position = Position(x, y, z, self.config.default_world)
    return self.blocks.place_block(position, block_type)

  def build_structure(self, structure_type: str, position: Position, **kwargs):
    """User-friendly method to build pre-defined structures"""
    return self.structures.build(structure_type, position, **kwargs)
