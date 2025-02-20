from typing import Union
from .base import BaseAPIHandler
from ..models.position import Position
from ..core.exceptions import InvalidBlockError

class BlockHandler(BaseAPIHandler):
  def validate_block_type(self, block_type: str) -> bool:
    # Add validation logic for block types
    valid_blocks = {"STONE", "DIRT", "DIAMOND_BLOCK"}  #todo Find better way to handle the list
    return block_type.upper() in valid_blocks

  def place_block(self, position: Position, block_type: str) -> bool:
    if not self.validate_block_type(block_type):
      raise InvalidBlockError(f"Invalid block type: {block_type}")

    data = {
      **position.to_dict(),
      "material": block_type.upper()
    }

    response = self._make_request("block", method="POST", json=data)
    return response.get("status") == 200