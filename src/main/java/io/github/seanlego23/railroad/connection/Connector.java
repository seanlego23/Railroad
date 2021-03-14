package io.github.seanlego23.railroad.connection;

import io.github.seanlego23.railroad.track.Rail;
import io.github.seanlego23.railroad.util.target.RailroadTarget;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public interface Connector extends RailroadTarget {
    boolean containsRail(Location location);
    @Nullable Rail getRail(Location location);
}
