package io.github.seanlego23.railroad.line;

public enum LineType {
	/**
	 * One station connecting to all destinations.
	 */
	CENTRALIZED,

	/**
	 * Every destination is a station.
	 */
	CONTINUOUS,

	/**
	 * Multiple stations.
	 */
	SCATTER,

	/**
	 * Unknown.
	 */
	UNKNOWN,
}
