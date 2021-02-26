package io.github.seanlego23.railroad.track;

import io.github.seanlego23.railroad.world.World;
import org.bukkit.Location;

public class TrackManager {
	private final World world;

	public TrackManager(World world) {
		this.world = world;
	}

	public ITrack getTrack(Location location) {
		ITrack track = this.getCachedTrack(location);
		if (track != null)
			return track;
		return null;
	}

	public ITrack getCachedTrack(Location location) {
		return null;
	}
}
