package io.github.seanlego23.railroad.event.users;

import io.github.seanlego23.railroad.stations.IStation;
import io.github.seanlego23.railroad.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserRemoveStationEvent extends UserRemoveEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public UserRemoveStationEvent(User user, IStation station) {
		super(user, station);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public IStation getStation() {
		return (IStation) removable;
	}
}
