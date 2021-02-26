package io.github.seanlego23.railroad.event.users;

import io.github.seanlego23.railroad.stations.schedule.Schedule;
import io.github.seanlego23.railroad.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserRemoveScheduleEvent extends UserRemoveEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public UserRemoveScheduleEvent(User user, Schedule schedule) {
		super(user, schedule);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public @NotNull Schedule getSchedule() {
		return (Schedule) removable;
	}

}
