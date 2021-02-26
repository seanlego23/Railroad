package io.github.seanlego23.railroad.event.users;

import io.github.seanlego23.railroad.line.ILine;
import io.github.seanlego23.railroad.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserRemoveTrackEvent extends UserRemoveEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public UserRemoveTrackEvent(User user, ILine track) {
		super(user, track);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public ILine getTrack() {
		return (ILine) removable;
	}
}
