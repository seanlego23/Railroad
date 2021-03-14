package io.github.seanlego23.railroad.world;

import io.github.seanlego23.railroad.stations.StationManager;
import io.github.seanlego23.railroad.line.LineManager;
import io.github.seanlego23.railroad.track.TrackManager;
import org.jetbrains.annotations.NotNull;

public class World {
	private final org.bukkit.World bukkitWorld;
	private final String name;
	private final LineManager lineManager = new LineManager();
	private final StationManager stationManager = new StationManager();
	private final TrackManager trackManager = new TrackManager(this);
	private final WorldSettings settings;

	public World(@NotNull org.bukkit.World world) {
		this.bukkitWorld = world;
		this.name = world.getName();
		this.settings = new WorldSettings();
	}

	public @NotNull org.bukkit.World getBukkitWorld() {
		return this.bukkitWorld;
	}

	public @NotNull String getName() {
		return this.name;
	}

	public WorldSettings getSettings() {
		return this.settings;
	}

	public LineManager getLineManager() {
		return this.lineManager;
	}

	public StationManager getStationManager() {
		return this.stationManager;
	}

	public TrackManager getTrackManager() {
		return this.trackManager;
	}
}
