package io.github.seanlego23.railroad.user;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.util.target.Removable;
import io.github.seanlego23.railroad.world.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class User {
	private final Railroad plugin;
	private final Player user;
	private final String name;
	private final Permissions permissions;
	private Removable removing;
	private Date removeTime;
	private boolean valid = true;

	public User(@NotNull Railroad plugin, @NotNull Player player) {
		this.plugin = plugin;
		this.user = player;
		this.name = player.getName();
		this.permissions = new Permissions(this);
	}

	public @NotNull String getName() {
		return this.name;
	}

	public @NotNull Player getPlayer() {
		if (!this.valid)
			throw new InvalidatedUserException();
		return this.user;
	}

	public @NotNull World getWorld() {
		return this.plugin.getWorldManager().getWorld(this.getPlayer().getWorld());
	}

	public @NotNull Location getLocation() {
		return this.getPlayer().getLocation();
	}

	public boolean isOp() {
		return this.getPlayer().isOp();
	}

	public boolean hasPermission(@NotNull Permissions.perms permission) {
		return this.permissions.hasPermission(permission, null);
	}

	public boolean isManager() {
		return this.isManager(this.getWorld());
	}

	public boolean isManager(@NotNull World world) {
		return this.permissions.hasPermission(Permissions.perms.WORLD, world.getName());
	}

	public List<World> managerOf() {
		List<World> worldList = new ArrayList<>();
		for (World world : this.plugin.getWorldManager().getWorlds())
			if (this.isManager(world))
				worldList.add(world);
		return worldList;
	}

	public boolean isQueued() {
		return false;
	}

	public boolean isValid() {
		return this.valid;
	}

	public void invalidate() {
		this.valid = false;
	}

	public void sendItem(@NotNull ItemStack itemStack) {
		this.getPlayer().getInventory().addItem(itemStack);
	}

	public void sendMessage(String message) {
		this.getPlayer().sendMessage(message);
	}

	public void sendMessage(String[] messages) {
		this.getPlayer().sendMessage(messages);
	}

	public void sendMessage(UUID sender, String message) {
		this.getPlayer().sendMessage(sender, message);
	}

	public void sendMessage(UUID sender, String[] messages) {
		this.getPlayer().sendMessage(sender, messages);
	}

	public void setRemove(@Nullable Removable remove) {
		if (!this.valid)
			throw new InvalidatedUserException();
		if (remove != null) {
			this.removing = remove;
			this.removeTime = new Date(System.currentTimeMillis());
		} else {
			this.removing = null;
			this.removeTime = null;
		}
	}

	public boolean checkRemove() {
		if (!this.valid)
			throw new InvalidatedUserException();
		long timePassed = 0L;
		if (this.removeTime != null)
			timePassed = System.currentTimeMillis() - this.removeTime.getTime();
		return timePassed > 10000L;
	}

	public Removable getRemove() {
		if (!this.valid)
			throw new InvalidatedUserException();
		return this.removing;
	}
}
