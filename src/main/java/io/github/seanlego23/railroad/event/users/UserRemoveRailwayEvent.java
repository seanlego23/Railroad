package io.github.seanlego23.railroad.event.users;

import io.github.seanlego23.railroad.track.ITrack;
import io.github.seanlego23.railroad.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserRemoveRailwayEvent extends UserRemoveEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public UserRemoveRailwayEvent(User user, ITrack railway) {
		super(user, railway);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public ITrack getRailway() {
		return (ITrack) removable;
	}

}
