package io.github.seanlego23.railroad.destinations;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import io.github.seanlego23.railroad.track.ITrackStop;
import io.github.seanlego23.railroad.util.target.RailroadTarget;
import io.github.seanlego23.railroad.track.ITrack;
import io.github.seanlego23.railroad.stations.IStation;
import org.bukkit.Location;

import java.util.Set;

public interface IDestination extends RailroadTarget, ITrackStop {
	@NotNull Location getLocation();
	@NotNull Set<IStation> getConnectedStations();
	@NotNull Set<ITrack> getConnectedRailways();
	@NotNull String getName();
	@Nullable String getDescription();
}
