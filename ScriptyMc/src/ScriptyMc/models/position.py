from dataclasses import dataclass

@dataclass
class Position:
  x: float
  y: float
  z: float
  world: str = "world"

  def to_dict(self):
    return {
      "x": self.x,
      "y": self.y,
      "z": self.z,
      "world": self.world
    }