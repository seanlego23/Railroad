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

	public enum Shape {
		NORTH_SOUTH,
		EAST_WEST,
		ASCENDING_EAST,
		ASCENDING_WEST,
		ASCENDING_NORTH,
		ASCENDING_SOUTH,
		SOUTH_EAST,
		SOUTH_WEST,
		NORTH_WEST,
		NORTH_EAST;

		public static Shape getShape(org.bukkit.block.data.Rail.Shape shape) {
			switch (shape) {
				case NORTH_SOUTH:
					return NORTH_SOUTH;
				case EAST_WEST:
					return EAST_WEST;
				case ASCENDING_EAST:
					return ASCENDING_EAST;
				case ASCENDING_WEST:
					return ASCENDING_WEST;
				case ASCENDING_NORTH:
					return ASCENDING_NORTH;
				case ASCENDING_SOUTH:
					return ASCENDING_SOUTH;
				case SOUTH_EAST:
					return SOUTH_EAST;
				case SOUTH_WEST:
					return SOUTH_WEST;
				case NORTH_WEST:
					return NORTH_WEST;
				default:
					return NORTH_EAST;
			}
		}
	}

	private final Location location;
	private final RailType railType;
	private final Shape defaultShape;
	private final Direction startDirection;
	private Shape currentShape;

	public Rail(@NotNull Location location, @NotNull RailType railType, @NotNull Direction startDirection) {
		this.location = location;
		this.railType = railType;
		World world = location.getWorld();
		if (world == null)
			throw new NullPointerException();
		org.bukkit.block.data.Rail railData = (org.bukkit.block.data.Rail) world.getBlockAt(location).getBlockData();
		Shape shape = Shape.getShape(railData.getShape());
		this.startDirection = startDirection;
		this.currentShape = shape;
		this.defaultShape = shape;
	}

	public Location getLocation() {
		return this.location;
	}

	public RailType getRailType() {
		return this.railType;
	}

	public Shape getCurrentShape() {
		return this.currentShape;
	}

	public Shape getDefaultShape() {
		return this.defaultShape;
	}

	public Shape setCurrentShape(Shape shape) {
		Shape old = this.currentShape;
		this.currentShape = shape;
		return old;
	}

	public Shape setCurrentToDefaultShape() {
		return this.setCurrentShape(this.defaultShape);
	}

	public Direction getStartDirection() {
		return this.startDirection;
	}

	public Direction getOppositeDirection() {
		switch (this.defaultShape) {
			case SOUTH_EAST:
			case NORTH_EAST:
				return (this.startDirection == Direction.SOUTH || this.startDirection == Direction.NORTH)
						? Direction.EAST : this.startDirection;
			case SOUTH_WEST:
			case NORTH_WEST:
				return (this.startDirection == Direction.SOUTH || this.startDirection == Direction.NORTH)
						? Direction.WEST : this.startDirection;
			case NORTH_SOUTH:
			case ASCENDING_NORTH:
			case ASCENDING_SOUTH:
				return (this.startDirection == Direction.NORTH) ? Direction.SOUTH : this.startDirection;
			default:
				return (this.startDirection == Direction.EAST) ? Direction.WEST : this.startDirection;
		}
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
