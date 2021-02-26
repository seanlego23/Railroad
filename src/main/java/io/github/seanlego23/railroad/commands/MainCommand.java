package io.github.seanlego23.railroad.commands;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.user.Permissions;
import io.github.seanlego23.railroad.user.User;
import io.github.seanlego23.railroad.user.UserNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCommand implements TabExecutor {
	private final Railroad plugin;
	private final RailroadCommand railroadCommand;
	private final StationCommand stationCommand;

	public MainCommand(Railroad plugin) {
		this.plugin = plugin;
		this.railroadCommand = new RailroadCommand(this.plugin);
		this.stationCommand = new StationCommand(this.plugin);
	}


	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (sender instanceof Player) {
			User user;
			try {
				user = this.plugin.getUserManager().getUser((Player) sender);
			} catch (UserNotFoundException e) {
				this.plugin.getLogger().severe(e.getMessage());
				return null;
			}
			if (user == null)
				return null;

			if (args.length == 1) {
				List<String> possible = new ArrayList<>();
				if (user.hasPermission(Permissions.perms.HELP))
					possible.add("help");
				if (user.hasPermission(Permissions.perms.WAND))
					possible.add("wand");
				if (user.hasPermission(Permissions.perms.RELOAD))
					possible.add("reload");
				if (user.hasPermission(Permissions.perms.WORLD_CONFIG))
					possible.add("conf");
				if (user.hasPermission(Permissions.perms.STATION_LIST))
					possible.add("station");
				if (!user.managerOf().isEmpty())
					possible.addAll(Arrays.asList("track", "destination", "rail"));
				return possible;
			} else if (args.length > 1) {
				if (args[0].equalsIgnoreCase("conf") && user.hasPermission(Permissions.perms.WORLD_CONFIG))
					return this.railroadCommand.onTabComplete(user, args);
				else if (args[0].equalsIgnoreCase("station") && user.hasPermission(Permissions.perms.STATION_LIST))
					return this.stationCommand.onTabComplete(user, args);
			}
		}
		return null;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender instanceof Player) {
			User user;
			try {
				user = this.plugin.getUserManager().getUser((Player) sender);
			} catch (UserNotFoundException e) {
				this.plugin.getLogger().severe(e.getMessage());
				throw new RuntimeException(e);
			}
			if (user == null)
				throw new NullPointerException("Player " + sender.getName() + " is not registered with Railroad plugin.");

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help") && user.hasPermission(Permissions.perms.HELP)) {
					this.plugin.help(sender);
					return true;
				} else if (args[0].equalsIgnoreCase("wand") && user.hasPermission(Permissions.perms.WAND)) {
					user.sendItem(new ItemStack(this.plugin.getSettings().getBindingMaterial(), 1));
					return true;
				} else if (args[0].equalsIgnoreCase("reload") && user.hasPermission(Permissions.perms.RELOAD)) {
					this.plugin.reload();
					return true;
				}
			} else if (args.length > 1) {
				if (args[0].equalsIgnoreCase("conf") && user.hasPermission(Permissions.perms.WORLD_CONFIG))
					return this.railroadCommand.onCommand(user, args);
				else if (args[0].equalsIgnoreCase("station") && user.hasPermission(Permissions.perms.STATION_LIST))
					return this.stationCommand.onCommand(user, args);
			}
		}
		return false;
	}
}
