package io.github.seanlego23.railroad.event.users;

import io.github.seanlego23.railroad.util.target.Removable;
import io.github.seanlego23.railroad.user.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserRemoveEvent extends Event implements Cancellable {
	protected final User user;
	protected final Removable removable;
	protected boolean cancelled = false;

	private static final HandlerList HANDLERS = new HandlerList();

	public UserRemoveEvent(User user, Removable removable) {
		this.user = user;
		this.removable = removable;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public User getUser() {
		return this.user;
	}

	public Removable getRemovable() {
		return this.removable;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
