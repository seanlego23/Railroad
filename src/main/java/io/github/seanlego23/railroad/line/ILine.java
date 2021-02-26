package io.github.seanlego23.railroad.line;

import io.github.seanlego23.railroad.util.target.Removable;
import io.github.seanlego23.railroad.destinations.IDestination;
import io.github.seanlego23.railroad.track.ITrack;
import io.github.seanlego23.railroad.stations.IStation;
import io.github.seanlego23.railroad.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ILine extends Removable {

	@NotNull String getName();
	@NotNull LineType getTrackType();
	@NotNull List<IStation> getStations();
	@NotNull World getWorld();
	@Nullable IStation getStation(@NotNull String name);
	@NotNull List<ITrack> getRails();
	@NotNull List<IDestination> getDestinations();

	void addStation(@NotNull IStation station);
	boolean removeStation(@NotNull String name);
	void addDestination(@NotNull IDestination destination);
	boolean removeDestination(@NotNull String name);

	void setTrackType(@NotNull LineType lineType);
}
