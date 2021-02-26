package io.github.seanlego23.railroad.event.users;

import io.github.seanlego23.railroad.util.target.Removable;
import io.github.seanlego23.railroad.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserPreConfirmRemoveEvent extends UserRemoveEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public UserPreConfirmRemoveEvent(User user, Removable removable) {
		super(user, removable);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
