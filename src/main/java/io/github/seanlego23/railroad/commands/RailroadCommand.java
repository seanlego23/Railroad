package io.github.seanlego23.railroad.commands;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.stations.selectionmethod.ButtonSelectionMethod;
import io.github.seanlego23.railroad.stations.selectionmethod.GUISelectionMethod;
import io.github.seanlego23.railroad.stations.selectionmethod.ItemFrameSelectionMethod;
import io.github.seanlego23.railroad.stations.selectionmethod.LecternSelectionMethod;
import io.github.seanlego23.railroad.user.Permissions;
import io.github.seanlego23.railroad.user.User;
import io.github.seanlego23.railroad.util.Entities;
import io.github.seanlego23.railroad.util.Items;
import io.github.seanlego23.railroad.world.World;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RailroadCommand {
	Railroad plugin;

	public RailroadCommand(Railroad plugin) {
		this.plugin = plugin;
	}

	@Nullable
	public List<String> onTabComplete(@NotNull User user, @NotNull String[] args) {
		final List<String> possible = new ArrayList<>();
		if (args.length == 2) {
			possible.add("world_blacklist");
			if (user.hasPermission(Permissions.perms.CONFIG))
				possible.add("blacklist");
			if (user.hasPermission(Permissions.perms.ALL_CONFIG))
				possible.addAll(Arrays.asList("enable", "disable", "wand", "def_selection_method"));
		} else if (args.length == 3) {
			if (args[1].equalsIgnoreCase("world_blacklist")) {
				user.managerOf().forEach(world -> possible.add(world.getName()));
			} else if (args[1].equalsIgnoreCase("blacklist") && user.hasPermission(Permissions.perms.CONFIG)) {
				possible.addAll(Arrays.asList("add", "remove"));
			} else if (user.hasPermission(Permissions.perms.ALL_CONFIG)) {
				if (args[1].equalsIgnoreCase("enable")) {
					this.plugin.getWorldManager().getWorlds().forEach(world -> possible.add(world.getName()));
				} else if (args[1].equalsIgnoreCase("disable")) {
					this.plugin.getWorldManager().getEnabledWorlds().forEach(world -> possible.add(world.getName()));
				} else if (args[1].equalsIgnoreCase("wand")) {
					possible.addAll(Items.getKeys());
					if (this.plugin.getWorldEditConnection().isConnected()) {
						String wandItem = this.plugin.getWorldEditConnection().getWandItem();
						if (wandItem.contains("minecraft:"))
							possible.remove(wandItem);
						else {
							Material worldEditWand = Material.getMaterial(wandItem.toLowerCase(Locale.ROOT));
							if (worldEditWand != null)
								possible.remove(worldEditWand.getKey().toString());
						}
					}
				} else if (args[1].equalsIgnoreCase("def_selection_method")) {
					possible.addAll(Arrays.asList("GUI", "Lectern", "Button", "Item_Frame", "Custom"));
				}
			}
		} else if (args.length == 4) {
			if (args[1].equalsIgnoreCase("world_blacklist")) {
				World world = this.plugin.getWorldManager().getWorld(args[2]);
				if (world != null && user.isManager(world))
					possible.addAll(Arrays.asList("add", "remove"));
			} else if (args[1].equalsIgnoreCase("blacklist") && user.hasPermission(Permissions.perms.CONFIG)) {
				if (args[2].equalsIgnoreCase("add")) {
					possible.addAll(Items.getKeys());
					possible.addAll(Entities.getKeys());
				} else if (args[2].equalsIgnoreCase("remove")) {
					this.plugin.getSettings().getMainBlacklist().forEach(item -> possible.add(item.toString()));
				}
			}
		} else if (args.length == 5) {
			World world = this.plugin.getWorldManager().getWorld(args[2]);
			if (world != null && user.isManager(world)) {
				if (args[3].equalsIgnoreCase("add")) {
					possible.addAll(Items.getKeys());
					possible.addAll(Entities.getKeys());
				} else if (args[3].equalsIgnoreCase("remove")) {
					world.getSettings().getBlacklist().forEach(item -> possible.add(item.toString()));
				}
			}
		}
		return possible;
	}

	public boolean onCommand(@NotNull User user, @NotNull String[] args) {
		if (args.length == 3) {
			if (args[1].equalsIgnoreCase("enable")) {
				World world = this.plugin.getWorldManager().getWorld(args[2]);
				if (world != null) {
					world.getSettings().setEnabled(true);
					return true;
				} else
					user.sendMessage(args[2] + " is not a world on this server.");
			} else if (args[1].equalsIgnoreCase("disable")) {
				World world = this.plugin.getWorldManager().getWorld(args[2]);
				if (world != null) {
					world.getSettings().setEnabled(false);
					return true;
				} else
					user.sendMessage(args[2] + " is not a world on this server.");
			} else if (args[1].equalsIgnoreCase("wand")) {
				if (!args[2].contains(":"))
					args[2] = "minecraft:" + args[2];
				if (!Items.getKeys().contains(args[2]))
					user.sendMessage(args[2] + " is not a valid wand material.");
				else {
					String wandItem = this.plugin.getWorldEditConnection().getWandItem();
					if (!wandItem.contains("minecraft:"))
						wandItem = "minecraft:" + wandItem;
					if (wandItem.equalsIgnoreCase(args[2]))
						user.sendMessage("The Railroad wand cannot be the same as the WorldEdit wand.");
					else {
						this.plugin.getSettings().setBindingMaterial(Material.getMaterial(args[2].substring(args[2].indexOf(":") + 1).toUpperCase(Locale.ROOT)));
						return true;
					}
				}
			} else if (args[1].equalsIgnoreCase("def_selection_method")) {
				if (args[2].equalsIgnoreCase("GUI"))
					this.plugin.getSettings().setDefaultSelectionMethod(GUISelectionMethod.class);
				else if (args[2].equalsIgnoreCase("Lectern"))
					this.plugin.getSettings().setDefaultSelectionMethod(LecternSelectionMethod.class);
				else if (args[2].equalsIgnoreCase("Button"))
					this.plugin.getSettings().setDefaultSelectionMethod(ButtonSelectionMethod.class);
				else if (args[2].equalsIgnoreCase("Item_Frame"))
					this.plugin.getSettings().setDefaultSelectionMethod(ItemFrameSelectionMethod.class);
				else if (args[2].equalsIgnoreCase("Custom"))
					this.plugin.getSettings().setDefaultSelectionMethod(null);
				else
					return false;
				return true;
			}
		} else if (args.length == 4 &&
				   args[1].equalsIgnoreCase("blacklist") &&
				   user.hasPermission(Permissions.perms.CONFIG)) {
			if (args[2].equalsIgnoreCase("add")) {
				List<String> possible = new ArrayList<>(Items.getKeys());
				possible.addAll(Entities.getKeys());
				if (possible.contains(args[3])) {
					this.plugin.getSettings().addItemToMainBlacklist(NamespacedKey.minecraft(args[3].substring(10).toLowerCase(Locale.ROOT)));
					return true;
				}
			} else if (args[2].equalsIgnoreCase("remove")) {
				if (args[3].startsWith("minecraft:"))
					return this.plugin.getSettings().removeItemFromMainBlacklist(NamespacedKey.minecraft(args[3].substring(10).toLowerCase(Locale.ROOT)));
			}
		} else if (args.length == 5 && args[1].equalsIgnoreCase("world_blacklist")) {
			World world = this.plugin.getWorldManager().getWorld(args[2]);
			if (world != null && user.isManager(world)) {
				if (args[3].equalsIgnoreCase("add")) {
					List<String> possible = new ArrayList<>(Items.getKeys());
					possible.addAll(Entities.getKeys());
					if (possible.contains(args[4]))
						return world.getSettings().addItemToBlacklist(NamespacedKey.minecraft(args[3].substring(10).toLowerCase(Locale.ROOT)));
				} else if (args[3].equalsIgnoreCase("remove")) {
					if (args[4].startsWith("minecraft:"))
						return world.getSettings().removeItemFromBlacklist(NamespacedKey.minecraft(args[4].substring(10).toLowerCase(Locale.ROOT)));
				}
			}
		}
		return false;
	}
}
