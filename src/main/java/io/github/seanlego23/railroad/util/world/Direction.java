package io.github.seanlego23.railroad.util.world;

import org.bukkit.util.Vector;

public enum Direction {
	SOUTH(0, 1),
	NORTH(0, -1),
	EAST(1, 0),
	WEST(-1, 0);

	private Vector vector;

	Direction(int x, int z) {
		this.vector = new Vector(x, 0, z);
	}

	public Vector getVector() {
		return this.vector;
	}
}
