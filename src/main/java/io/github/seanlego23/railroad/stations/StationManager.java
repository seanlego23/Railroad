package io.github.seanlego23.railroad.stations;

import io.github.seanlego23.railroad.Railroad;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StationManager {
	private Map<String, IStation> stations;

	public StationManager() {

	}

	public List<IStation> getStations() {
		return new ArrayList<>(this.stations.values());
	}

	public @Nullable IStation getStation(String name) {
		return this.stations.get(name);
	}

	public boolean addStation(IStation station) {
		if (this.stations.containsKey(station.getName()))
			return false;
		this.stations.put(station.getName(), station);
		return true;
	}
}
