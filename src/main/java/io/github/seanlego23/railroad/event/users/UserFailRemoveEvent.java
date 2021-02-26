package io.github.seanlego23.railroad.event.users;

import io.github.seanlego23.railroad.util.target.Removable;
import io.github.seanlego23.railroad.user.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserFailRemoveEvent extends Event {
	protected final User user;
	protected final Removable removable;
	protected final FailReason reason;

	public enum FailReason {
		CANCEL,
		TIME,
		LEFT,
		CUSTOM
	}

	private static final HandlerList HANDLERS = new HandlerList();

	public UserFailRemoveEvent(@NotNull User user, @NotNull Removable removable, @NotNull FailReason reason) {
		this.user = user;
		this.removable = removable;
		this.reason = reason;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public @NotNull User getUser() {
		return this.user;
	}

	public @NotNull Removable getRemovable() {
		return this.removable;
	}

	public @NotNull FailReason getReason() {
		return this.reason;
	}
}
