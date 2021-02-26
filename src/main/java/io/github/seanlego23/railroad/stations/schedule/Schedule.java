package io.github.seanlego23.railroad.stations.schedule;

import io.github.seanlego23.railroad.util.target.Removable;
import io.github.seanlego23.railroad.destinations.IDestination;
import io.github.seanlego23.railroad.stations.selection.ISelection;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schedule implements Removable {
	private final Map<IDestination, Long> times;
	private final List<Sign> signs = new ArrayList<>();

	public Schedule(@NotNull ISelection selectionParent) {
		this.times = new HashMap<>();
		selectionParent.getDestinations().forEach(destination -> this.times.put(destination, null));
	}

	public @Nullable Long getTime(@NotNull IDestination destination) {
		return this.times.get(destination);
	}

	public boolean setTime(@NotNull IDestination destination, @NotNull Long time) {
		if (!this.times.containsKey(destination))
			return false;
		if (time < 0L || time > 24000L)
			throw new IllegalArgumentException("Time should be between 0 and 24000 ticks.");
		if (time == 24000L)
			time = 0L;
		this.times.put(destination, time);
		return true;
	}

	public boolean addDestination(@NotNull IDestination destination) {
		return this.addDestination(destination, null);
	}

	public boolean addDestination(@NotNull IDestination destination, @Nullable Long time) {
		if (!this.times.containsKey(destination))
			return false;
		return this.times.put(destination, time) == null;
	}

	public boolean removeDestination(@NotNull IDestination destination) {
		if (!this.times.containsKey(destination))
			return false;
		this.times.remove(destination);
		return true;
	}

	public void update(Long time) {

	}

	@Override
	public void remove() {

	}
}
