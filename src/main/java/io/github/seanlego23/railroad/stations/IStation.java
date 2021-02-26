package io.github.seanlego23.railroad.stations;

import com.sk89q.worldedit.regions.Region;
import io.github.seanlego23.railroad.track.ITrackStop;
import io.github.seanlego23.railroad.util.target.RailroadTarget;
import io.github.seanlego23.railroad.util.target.Removable;
import io.github.seanlego23.railroad.stations.schedule.Schedule;
import io.github.seanlego23.railroad.stations.selection.ISelection;
import io.github.seanlego23.railroad.line.ILine;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

//Ticketing, Railway Que
public interface IStation extends RailroadTarget, ITrackStop {
	@NotNull String getName();
	@NotNull ILine getTrack();
	@NotNull Region getRegion();
	@NotNull World getWorld();
	@NotNull String[] getChatInfo();
	boolean hasSchedules();
	@Nullable Schedule getSchedule(String selectionName);
	@NotNull Map<String, Schedule> getSchedules();
	boolean createSchedule(String selectionName);
	boolean removeSchedule(String selectionName);
	@Nullable ISelection getSelection(String name);
	@NotNull Set<ISelection> getSelections();

	void setTicketingMaterial(@NotNull Material material);
	boolean hasTicketing();

	boolean equals(Object other);
}
