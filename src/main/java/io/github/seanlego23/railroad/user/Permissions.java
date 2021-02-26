package io.github.seanlego23.railroad.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permissions {
	public enum perms {

		HELP("railroad.railroad"),
		STATION_LIST("railroad.station.list", HELP),
		WAND("railroad.manager.1", HELP, STATION_LIST),
		WORLD("railroad.manager.world.", WAND),
		WORLD_CONFIG("railroad.manager.2", WAND),
		CONFIG("railroad.manager.3", WORLD_CONFIG, WORLD),
		RELOAD("railroad.reload", HELP),
		ALL_CONFIG("railroad.conf.*", CONFIG),
		DEST_CREATE("railroad.destination.create", HELP),
		ALL("railroad.*", ALL_CONFIG, RELOAD);

		private final String name;
		private final List<perms> children;
		private final List<perms> parents = new ArrayList<>();

		perms(String name, perms ...children) {
			this.name = name;
			this.children = Arrays.asList(children);
			for (perms child : this.children)
				child.parents.add(this);
		}

		public @NotNull String getName() {
			return this.name;
		}

		public @Nullable List<perms> getChildren() {
			return this.children;
		}

		public @NotNull List<perms> getParents() {
			return this.parents;
		}
	}

	private final User user;

	public Permissions(User user) {
		this.user = user;
	}

	public boolean hasPermission(@NotNull perms permission, @Nullable String info) {
		if (this.user.isOp())
			return true;
		Player player = this.user.getPlayer();
		if (player.hasPermission(perms.ALL.getName()))
			return true;
		if (player.hasPermission(permission.getName() + (info == null ? "" : info)))
			return true;
		return this.checkParents(permission.getParents());
	}

	private boolean checkParents(@NotNull List<perms> parents) {
		if (parents.size() == 0)
			return false;
		Player player = this.user.getPlayer();
		boolean found = false;
		for (perms parent : parents) {
			if (player.hasPermission(parent.getName()))
				return true;
			found |= this.checkParents(parent.getParents());
		}
		return found;
	}
}
