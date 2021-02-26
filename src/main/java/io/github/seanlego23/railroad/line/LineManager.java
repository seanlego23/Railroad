package io.github.seanlego23.railroad.line;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LineManager {
	private List<ILine> tracks = new ArrayList<>();

	public LineManager() {

	}

	public @Nullable ILine getTrack(String name) {
		for (ILine track : this.tracks)
			if (track.getName().equalsIgnoreCase(name))
				return track;
		return null;
	}

	public List<ILine> getTracks() {
		return new ArrayList<>(this.tracks);
	}

	public List<ILine> getTracksWorld(World world) {
		List<ILine> worldTracks = new ArrayList<>();
		for (ILine track : this.tracks)
			if (track.getWorld().equals(world))
				worldTracks.add(track);
		return worldTracks;
	}

}
