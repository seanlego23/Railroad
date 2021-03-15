package io.github.seanlego23.railroad.track;

import io.github.seanlego23.railroad.connection.Connection;
import io.github.seanlego23.railroad.connection.Connector;
import io.github.seanlego23.railroad.connection.IllegalConnectionException;
import io.github.seanlego23.railroad.destinations.IDestination;
import io.github.seanlego23.railroad.world.World;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ITrack extends Connector {
	enum Way {
		/**
		 * One direction towards the start of the rail.
		 */
		ONE_WAY_TO,

		/**
		 * One direction towards the end of the rail.
		 */
		ONE_WAY_FROM,

		/**
		 * The rail is used in both directions.
		 */
		BOTH_WAYS
	}

	@NotNull World getWorld();
	@NotNull ITrack.Way getWay();
	void setWay(@NotNull ITrack.Way way);
	int getLength();
	int getWidth();
	int getDepth();
	boolean contains(Location location);
	@NotNull Rail getStart();
	@NotNull Rail getEnd();
	@NotNull Set<IDestination> getConnectedDestinations();
	@NotNull Connection getConnection();
	void connectTo(ITrackStop stop) throws IllegalConnectionException;

	//TODO: Finish after service provider is connected
	default ITrack split(Location location) {
		return null;
	}
}
