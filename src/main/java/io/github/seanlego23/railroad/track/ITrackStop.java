package io.github.seanlego23.railroad.track;

import io.github.seanlego23.railroad.util.target.RUID;
import org.jetbrains.annotations.NotNull;

/**
 * The ITrackStop interface is just a marker interface to denote that an ITrack
 * can connect to an class that implements this interface.
 */
public interface ITrackStop {
    @NotNull RUID getID();
}
