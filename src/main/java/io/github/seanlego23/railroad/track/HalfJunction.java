package io.github.seanlego23.railroad.track;

import io.github.seanlego23.railroad.util.target.RUID;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public final class HalfJunction {

    private final Location location;
    private final JunctionType junctionType;
    private final RUID firstConnector;
    private final RUID secondConnector;
    private final RUID thirdConnector;
    private final Set<RUID> firstTrackStops;
    private final Set<RUID> secondTrackStops;
    private final Set<RUID> thirdTrackStops;

    public HalfJunction(@NotNull Location location, @NotNull JunctionType junctionType,
                        @NotNull RUID first, @NotNull RUID second, @Nullable RUID third,
                        @NotNull Set<RUID> firstTS, @NotNull Set<RUID> secondTS, @NotNull Set<RUID> thirdTS) {
        this.location = location;
        this.junctionType = junctionType;
        this.firstConnector = first;
        this.secondConnector = second;
        this.thirdConnector = third;
        this.firstTrackStops = firstTS;
        this.secondTrackStops = secondTS;
        this.thirdTrackStops = thirdTS;
    }

    public Location getLocation() {
        return this.location.clone();
    }

    public JunctionType getJunctionType() {
        return (JunctionType) this.junctionType.clone();
    }

    public RUID getFirstConnector() {
        return this.firstConnector;
    }

    public RUID getSecondConnector() {
        return this.secondConnector;
    }

    public @Nullable RUID getThirdConnector() {
        return this.thirdConnector;
    }

    public Set<RUID> getFirstTrackStops() {
        return new HashSet<>(this.firstTrackStops);
    }

    public Set<RUID> getSecondTrackStops() {
        return new HashSet<>(this.secondTrackStops);
    }

    public Set<RUID> getThirdTrackStops() {
        return new HashSet<>(this.thirdTrackStops);
    }
}
