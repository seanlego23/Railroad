package io.github.seanlego23.railroad.track;

import io.github.seanlego23.railroad.util.target.IncorrectRUIDTypeException;
import io.github.seanlego23.railroad.util.target.RUID;
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

	public static ITrack getTrack(RUID ruid) throws IncorrectRUIDTypeException {
		if (ruid.getType() != RUID.IDType.TRACK)
			throw new IncorrectRUIDTypeException("RUID is not a ITrack RUID.");
		return null;
	}

	public ITrack getCachedTrack(Location location) {
		return null;
	}

	public Junction getJunction(Location location) {
		Junction junction = this.getCachedJunction(location);
		if (junction != null)
			return junction;
		return null;
	}

	public static Junction getJunction(RUID ruid) throws IncorrectRUIDTypeException {
		if (ruid.getType() != RUID.IDType.JUNCTION)
			throw new IncorrectRUIDTypeException("RUID is not a Junction RUID.");
		return null;
	}

	public Junction getCachedJunction(Location location) {
		return null;
	}

	private ITrack createTrack(Class<? extends ITrack> clazz) {
		return null;
	}

	private ITrack createDefaultTrack() {
		return null;
	}
}
