package io.github.seanlego23.railroad.event.users;

import io.github.seanlego23.railroad.destinations.IDestination;
import io.github.seanlego23.railroad.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserRemoveDestinationEvent extends UserRemoveEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public UserRemoveDestinationEvent(User user, IDestination destination) {
		super(user, destination);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public IDestination getDestination() {
		return (IDestination) removable;
	}
}
