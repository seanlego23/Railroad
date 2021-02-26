package io.github.seanlego23.railroad.connection;

import io.github.seanlego23.railroad.track.ITrack;
import io.github.seanlego23.railroad.track.ITrackStop;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ITrack ITrack} connects to two {@link ITrackStop ITrackStop's} on either end.
 * An instance of this class is owned by both the ITrack object and the two ITrackStop
 * objects, any of the three are easily accessible to the others.
 */
public class Connection {

	private final ITrack track;
	private final ITrackStop stopStart;
	private final ITrackStop stopEnd;

	public Connection(@NotNull ITrack track, @NotNull ITrackStop stopStart, @NotNull ITrackStop stopEnd) {
		this.track = track;
		this.stopStart = stopStart;
		this.stopEnd = stopEnd;
	}

	public ITrack getTrack() {
		return this.track;
	}

	public ITrackStop getStopAtStart() {
		return this.stopStart;
	}

	public ITrackStop getStopAtEnd() {
		return this.stopEnd;
	}
}
