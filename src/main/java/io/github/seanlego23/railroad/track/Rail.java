package io.github.seanlego23.railroad.track;

import io.github.seanlego23.railroad.util.world.Direction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class Rail {

	public enum RailType {
		NORMAL,
		POWERED,
		DETECTOR,
		ACTIVATOR
	}

	//UP, NORMAL, CURVED_LEFT, CURVED_RIGHT
	public enum RailShape {
		NORMAL,
		UP,
		CURVED_LEFT,
		CURVED_RIGHT;

		public boolean isCurved() {
			return this == CURVED_LEFT || this == CURVED_RIGHT;
		}

		public static RailShape getRailShape(@NotNull org.bukkit.block.data.Rail.Shape shape) {
			switch (shape) {
				case NORTH_SOUTH:
				case EAST_WEST:
					return NORMAL;
				case ASCENDING_EAST:
				case ASCENDING_WEST:
				case ASCENDING_NORTH:
				case ASCENDING_SOUTH:
					return UP;
				case SOUTH_EAST:
				case NORTH_WEST:
					return CURVED_RIGHT;
				default:
					return CURVED_LEFT;
			}
		}
	}

	private final Location location;
	private final RailType railType;
	private final RailShape defaultRailShape;
	private final Direction startDirection;
	private RailShape currentRailShape;

	public Rail(@NotNull Location location, @NotNull RailType railType, @NotNull Direction startDirection) {
		this.location = location;
		this.railType = railType;
		World world = location.getWorld();
		if (world == null)
			throw new NullPointerException();
		org.bukkit.block.data.Rail railData = (org.bukkit.block.data.Rail) world.getBlockAt(location).getBlockData();
		RailShape shape = RailShape.getRailShape(railData.getShape());
		this.startDirection = startDirection;
		if (shape.isCurved()) {
			 if (this.startDirection == Direction.EAST)
			 	if (shape == RailShape.CURVED_LEFT)
			 		shape = RailShape.CURVED_RIGHT;
			 	else
			 		shape = RailShape.CURVED_LEFT;
			else if (this.startDirection == Direction.WEST)
				if (shape == RailShape.CURVED_LEFT)
					shape = RailShape.CURVED_RIGHT;
				else
					shape = RailShape.CURVED_LEFT;
		}
		this.currentRailShape = shape;
		this.defaultRailShape = shape;
	}

	public Location getLocation() {
		return this.location;
	}

	public RailType getRailType() {
		return this.railType;
	}

	public RailShape getCurrentRailShape() {
		return this.currentRailShape;
	}

	public RailShape getDefaultRailShape() {
		return this.defaultRailShape;
	}

	public RailShape setCurrentRailShape(RailShape shape) {
		RailShape old = this.currentRailShape;
		this.currentRailShape = shape;
		return old;
	}

	public RailShape setCurrentToDefaultRailShape() {
		return this.setCurrentRailShape(this.defaultRailShape);
	}

	public Direction getStartDirection() {
		return this.startDirection;
	}

	public static boolean materialIsRail(@NotNull Material material) {
		switch (material) {
			case RAIL:
			case POWERED_RAIL:
			case DETECTOR_RAIL:
			case ACTIVATOR_RAIL:
				return true;
			default:
				return false;
		}
	}
}
