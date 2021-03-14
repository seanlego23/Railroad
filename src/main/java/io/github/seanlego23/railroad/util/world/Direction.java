package io.github.seanlego23.railroad.util.world;

import org.bukkit.util.Vector;

public enum Direction {
	SOUTH(0, 0, 1),
	SOUTH_UP(0, 1, 1),
	SOUTH_DOWN(0, -1, 1),
	NORTH(0, 0, -1),
	NORTH_UP(0, 1, -1),
	NORTH_DOWN(0, -1, -1),
	EAST(1, 0, 0),
	EAST_UP(1, 1, 0),
	EAST_DOWN(1, -1, 0),
	WEST(-1, 0, 0),
	WEST_UP(-1, 1, 0),
	WEST_DOWN(-1, -1, 0),
	SOUTH_EAST(1, 0, 1),
	SOUTH_EAST_UP(1, 1, 1),
	SOUTH_EAST_DOWN(1, -1, 1),
	SOUTH_WEST(-1, 0, 1),
	SOUTH_WEST_UP(-1, 1, 1),
	SOUTH_WEST_DOWN(-1, -1, 1),
	NORTH_EAST(1, 0, -1),
	NORTH_EAST_UP(1, 1, -1),
	NORTH_EAST_DOWN(1, -1, -1),
	NORTH_WEST(-1, 0, -1),
	NORTH_WEST_UP(-1, 1, -1),
	NORTH_WEST_DOWN(-1, -1, -1),
	UP(0, 1,0),
	DOWN(0, -1,0),
	HERE(0,0,0);

	private final Vector vector;

	Direction(int x, int y, int z) {
		this.vector = new Vector(x, y, z);
	}

	public Vector getVector() {
		return this.vector.clone();
	}

	public Direction getOpposite() {
		switch (this) {
			case SOUTH:
				return NORTH;
			case SOUTH_UP:
				return NORTH_DOWN;
			case SOUTH_DOWN:
				return NORTH_UP;
			case NORTH:
				return SOUTH;
			case NORTH_UP:
				return SOUTH_DOWN;
			case NORTH_DOWN:
				return SOUTH_UP;
			case EAST:
				return WEST;
			case EAST_UP:
				return WEST_DOWN;
			case EAST_DOWN:
				return WEST_UP;
			case WEST:
				return EAST;
			case WEST_UP:
				return EAST_DOWN;
			case WEST_DOWN:
				return EAST_UP;
			case SOUTH_EAST:
				return NORTH_WEST;
			case SOUTH_EAST_UP:
				return NORTH_WEST_DOWN;
			case SOUTH_EAST_DOWN:
				return NORTH_WEST_UP;
			case SOUTH_WEST:
				return NORTH_EAST;
			case SOUTH_WEST_UP:
				return NORTH_EAST_DOWN;
			case SOUTH_WEST_DOWN:
				return NORTH_EAST_UP;
			case NORTH_EAST:
				return SOUTH_WEST;
			case NORTH_EAST_UP:
				return SOUTH_WEST_DOWN;
			case NORTH_EAST_DOWN:
				return SOUTH_WEST_UP;
			case NORTH_WEST:
				return SOUTH_EAST;
			case NORTH_WEST_UP:
				return SOUTH_EAST_DOWN;
			case NORTH_WEST_DOWN:
				return SOUTH_EAST_UP;
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			default:
				return this;
		}
	}

	public Direction getRegular() {
		switch (this) {
			case SOUTH:
			case SOUTH_UP:
			case SOUTH_DOWN:
				return SOUTH;
			case NORTH:
			case NORTH_UP:
			case NORTH_DOWN:
				return NORTH;
			case EAST:
			case EAST_UP:
			case EAST_DOWN:
				return EAST;
			case WEST:
			case WEST_UP:
			case WEST_DOWN:
				return WEST;
			case SOUTH_EAST:
			case SOUTH_EAST_UP:
			case SOUTH_EAST_DOWN:
				return SOUTH_EAST;
			case SOUTH_WEST:
			case SOUTH_WEST_UP:
			case SOUTH_WEST_DOWN:
				return SOUTH_WEST;
			case NORTH_EAST:
			case NORTH_EAST_UP:
			case NORTH_EAST_DOWN:
				return NORTH_EAST;
			case NORTH_WEST:
			case NORTH_WEST_UP:
			case NORTH_WEST_DOWN:
				return NORTH_WEST;
			default:
				return this;
		}
	}

	public boolean onZAxis() {
		switch (this) {
			case SOUTH:
			case SOUTH_UP:
			case SOUTH_DOWN:
			case NORTH:
			case NORTH_UP:
			case NORTH_DOWN:
				return true;
			default:
				return false;
		}
	}

	public boolean onXAxis() {
		switch (this) {
			case EAST:
			case EAST_UP:
			case EAST_DOWN:
			case WEST:
			case WEST_UP:
			case WEST_DOWN:
				return true;
			default:
				return false;
		}
	}

	public boolean onYAxis() {
		switch (this) {
			case UP:
			case DOWN:
				return true;
			default:
				return false;
		}
	}
}
