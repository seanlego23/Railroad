package io.github.seanlego23.railroad.event.users;

import io.github.seanlego23.railroad.stations.selection.ISelection;
import io.github.seanlego23.railroad.user.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserRemoveSelectionEvent extends UserRemoveEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public UserRemoveSelectionEvent(User user, ISelection selection) {
		super(user, selection);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public @NotNull ISelection getSelection() {
		return (ISelection) removable;
	}
}
