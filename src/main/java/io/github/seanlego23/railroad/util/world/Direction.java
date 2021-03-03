package io.github.seanlego23.railroad.util.world;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public enum Direction {
	SOUTH(0, 0, 1),
	NORTH(0, 0, -1),
	EAST(1, 0, 0),
	WEST(-1, 0, 0),
	UP(0, 1,0),
	DOWN(0, -1,0);

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
			case NORTH:
				return SOUTH;
			case EAST:
				return WEST;
			case WEST:
				return EAST;
			case UP:
				return DOWN;
			default:
				return UP;
		}
	}

	//If in between two directions:
	//UP and DOWN are top priority
	//otherwise SOUTH < EAST < NORTH < WEST < SOUTH
	//Essentially -pi < x <= pi
	public static Direction getFromVector(Vector vector) {
		Vector other = vector.clone().normalize();
		List<Direction> inBetween = new ArrayList<>();
		for (Direction direction : Direction.values()) {
			if (direction.getVector().angle(other) < (float)(Math.PI / 4))
				return direction;
			else if (direction.getVector().angle(other) == (float)(Math.PI / 4))
				inBetween.add(direction);
		}

		if (inBetween.size() == 1)
			return inBetween.get(0);

		Direction first = inBetween.get(0);
		Direction second = inBetween.get(1);
		if (first == UP || second == UP)
			return UP;
		else if (first == DOWN || second == DOWN)
			return DOWN;
		else if (first == WEST) {
			if (second == SOUTH)
				return second;
			else
				return first;
		} else if (first == SOUTH) {
			if (second == EAST)
				return second;
			else
				return first;
		} else if (first == EAST) {
			if (second == NORTH)
				return second;
			else
				return first;
		} else {
			if (second == WEST)
				return second;
			else
				return first;
		}
	}
}
