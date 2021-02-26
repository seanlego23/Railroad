package io.github.seanlego23.railroad.track;

import io.github.seanlego23.railroad.connection.Connection;
import io.github.seanlego23.railroad.destinations.IDestination;
import io.github.seanlego23.railroad.util.target.RUID;
import io.github.seanlego23.railroad.util.target.RailroadTarget;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface ITrack extends RailroadTarget {
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

	@NotNull ITrack.Way getWay();
	void setWay(@NotNull ITrack.Way way);
	int getLength();
	int getWidth();
	int getDepth();
	boolean contains(Location location);
	@Nullable Rail getRail(Location location);
	@NotNull Location getStart();
	@NotNull Location getEnd();
	@NotNull List<Junction> getJunctions();
	@NotNull Set<IDestination> getConnectedDestinations();
	@Nullable Connection getConnection();
	@NotNull RUID getRUID();
}
