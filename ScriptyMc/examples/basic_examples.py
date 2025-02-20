from minecraft_script.src.core.client import MinecraftScript
from minecraft_script.src.models.position import Position

def simple_house_example():
  # Create client with default configuration
  mc = MinecraftScript()

  # Build a simple house
  start_pos = Position(100, 64, 100)

  try:
    # Build floor
    mc.structures.build("floor", start_pos, width=5, length=5)

    # Build walls
    mc.structures.build("walls", start_pos, width=5, length=5, height=4)

    # Add roof
    roof_pos = Position(start_pos.x, start_pos.y + 4, start_pos.z)
    mc.structures.build("roof", roof_pos, width=7, length=7)

    print("House built successfully!")

  except Exception as e:
    print(f"Error building house: {e}")

if __name__ == "__main__":
  simple_house_example()