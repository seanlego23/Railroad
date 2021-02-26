package io.github.seanlego23.railroad.user;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.sessions.SessionManager;
import io.github.seanlego23.railroad.world.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {
	private final Railroad plugin;
	private final Map<UUID, User> users;

	public UserManager(Railroad plugin) {
		this.plugin = plugin;
		this.users = new HashMap<>();
	}

	public void addUser(@NotNull Player player) {
		if (!this.users.containsKey(player.getUniqueId())) {
			User user = new User(this.plugin, player);
			this.users.put(player.getUniqueId(), user);
			SessionManager sessionManager = this.plugin.getSessionManager();
			sessionManager.createSession(user);
			for (World world: this.plugin.getWorldManager().getWorlds())
				sessionManager.getSession(user).createLocalSession(world);
		}
	}

	public boolean remove(@NotNull Player player) {
		if (this.hasUser(player)) {
			User user = this.users.get(player.getUniqueId());
			this.plugin.getSessionManager().removeSession(user);
			user.invalidate();
			return this.users.remove(player.getUniqueId()) != null;
		}
		return false;
	}

	public boolean hasUser(@NotNull Player player) {
		return this.users.get(player.getUniqueId()) != null;
	}

	public @Nullable User getUser(@NotNull Player player) throws UserNotFoundException {
		User user = this.users.get(player.getUniqueId());
		if (user == null) {
			if (Bukkit.getOnlinePlayers().contains(player))
				throw new UserNotFoundException("Player " + player.getName() + " is not registered with the Railroad plugin.");
			if (!Arrays.asList(Bukkit.getOfflinePlayers()).contains(Bukkit.getOfflinePlayer(player.getUniqueId())))
				throw new UserNotFoundException("Player " + player.getName() + " does not exist.");
		}
		return user;
	}

	public @Nullable User getUser(@NotNull UUID uuid) throws UserNotFoundException {
		User user = this.users.get(uuid);
		if (user == null) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) {
				if (!Arrays.asList(Bukkit.getOfflinePlayers()).contains(Bukkit.getOfflinePlayer(uuid)))
					throw new UserNotFoundException("Player {" + uuid.toString() + "} does not exist.");
			} else if (Bukkit.getOnlinePlayers().contains(player))
				throw new UserNotFoundException("Player " + player.getName() + " is not registered with the Railroad plugin.");
		}
		return user;
	}

	public @Nullable User getUser(@NotNull String name) throws UserNotFoundException {
		Player player = Bukkit.getPlayer(name);
		if (player != null)
			return this.getUser(player);
		throw new UserNotFoundException("Player " + name + "could not be found.");
	}
}
