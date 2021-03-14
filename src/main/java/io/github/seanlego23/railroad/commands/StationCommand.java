package io.github.seanlego23.railroad.commands;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.destinations.IDestination;
import io.github.seanlego23.railroad.event.users.UserFailRemoveEvent;
import io.github.seanlego23.railroad.event.users.UserPreConfirmRemoveEvent;
import io.github.seanlego23.railroad.stations.IStation;
import io.github.seanlego23.railroad.stations.selection.ISelection;
import io.github.seanlego23.railroad.line.ILine;
import io.github.seanlego23.railroad.user.User;
import io.github.seanlego23.railroad.util.Items;
import io.github.seanlego23.railroad.world.World;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StationCommand{
	private final Railroad plugin;

	public StationCommand(Railroad plugin) {
		this.plugin = plugin;
	}

	@Nullable
	public List<String> onTabComplete(@NotNull User user, @NotNull String[] args) {
		List<String> possible = new ArrayList<>();
		if (args.length == 2) {
			possible.add("list");
			possible.add("nearest");
			possible.add("info");
			if (user.isManager())
				possible.addAll(Arrays.asList("create", "delete", "edit"));
		} else if (args.length == 3) {
			if (user.isManager() &&
					(args[1].equalsIgnoreCase("delete") ||
					args[1].equalsIgnoreCase("edit"))) {

				user.getWorld().getStationManager().getStations().forEach(station -> possible.add(station.getName()));
			} else if (user.isManager() && args[1].equalsIgnoreCase("create")) {
				user.getWorld().getLineManager().getTracks().forEach(track -> possible.add(track.getName()));
			} else if (args[1].equalsIgnoreCase("info")) {
				user.getWorld().getStationManager().getStations().forEach(station -> possible.add(station.getName()));
			}
		} else if (args.length == 4 && user.isManager() && args[1].equalsIgnoreCase("edit")) {
			IStation station = user.getWorld().getStationManager().getStation(args[2]);
			if (station != null) {
				possible.add("selection");
				possible.add("schedule");
				if (station.hasSchedules())
					possible.add("tickets");
			}
		} else if (args.length == 5 && user.isManager() && args[1].equalsIgnoreCase("edit")) {
			IStation station = user.getWorld().getStationManager().getStation(args[2]);
			if (station != null) {
				if (args[3].equalsIgnoreCase("selection")) {
					possible.addAll(Arrays.asList("add", "remove", "edit"));
				} else if (args[3].equalsIgnoreCase("schedule")) {
					possible.addAll(Arrays.asList("add", "remove", "edit", "view", "display"));
				} else if (station.hasSchedules() && args[3].equalsIgnoreCase("tickets")) {
					possible.add("material");
					possible.add("message");
				}
			}
		} else if (args.length == 6 && user.isManager() && args[1].equalsIgnoreCase("edit")) {
			IStation station = user.getWorld().getStationManager().getStation(args[2]);
			if (station != null) {
				if (args[3].equalsIgnoreCase("selection")) {
					if (args[4].equalsIgnoreCase("remove") || args[4].equalsIgnoreCase("edit"))
						station.getSelections().forEach(selection -> possible.add(selection.getName()));
				} else if (args[3].equalsIgnoreCase("schedule")) {
					if (args[4].equalsIgnoreCase("remove") ||
						args[4].equalsIgnoreCase("edit") ||
						args[4].equalsIgnoreCase("view") ||
						args[4].equalsIgnoreCase("display"))

						station.getSelections().forEach(selection -> possible.add(selection.getName()));
				} else if (station.hasSchedules() && args[3].equalsIgnoreCase("tickets")) {
					if (args[4].equalsIgnoreCase("material")) {
						possible.addAll(Items.getKeys());
						possible.add("none");
					}
				}
			}
		} else if (args.length == 7 && user.isManager() && args[1].equalsIgnoreCase("edit")) {
			IStation station = user.getWorld().getStationManager().getStation(args[2]);
			if (station != null) {
				if (args[3].equalsIgnoreCase("selection") && args[4].equalsIgnoreCase("edit")) {
					ISelection selection = station.getSelection(args[5]);
					if (selection != null)
						possible.addAll(Arrays.asList("destination", "description", "start"));
				} else if (args[3].equalsIgnoreCase("schedule")) {
					ISelection selection = station.getSelection(args[5]);
					if (selection != null) {
						if (args[4].equalsIgnoreCase("display")) {
							possible.add("add");
							possible.add("remove");
						} else if (args[4].equalsIgnoreCase("edit")) {
							selection.getDestinations().forEach(destination -> possible.add(destination.getName()));
						}
					}
				}
			}
		} else if (args.length == 8 && user.isManager() && args[1].equalsIgnoreCase("edit")) {
			IStation station = user.getWorld().getStationManager().getStation(args[2]);
			if (station != null) {
				if (args[3].equalsIgnoreCase("selection") && args[4].equalsIgnoreCase("edit")) {
					ISelection selection = station.getSelection(args[5]);
					if (selection != null && args[6].equalsIgnoreCase("destination")) {
						possible.addAll(Arrays.asList("add", "edit", "remove"));
					}
				} else if (args[3].equalsIgnoreCase("schedule")) {
					ISelection selection = station.getSelection(args[5]);
					if (selection != null) {
						if (args[4].equalsIgnoreCase("display") && args[6].equalsIgnoreCase("remove")) {
							possible.addAll(Arrays.asList("all", "last", "selected"));
						} else if (args[4].equalsIgnoreCase("edit")) {
							IDestination destination = selection.getDestination(args[6]);
							if (destination != null) {
								possible.add("0");
							}
						}
					}
				}
			}
		}
		return possible;
	}

	//TODO: Handle ?
	public boolean onCommand(@NotNull User user, @NotNull String[] args) {
		if (args.length == 2) {
			if (args[1].equalsIgnoreCase("list")) {
				StringBuilder message = new StringBuilder();
				message.append(ChatColor.GOLD).append("Stations: ");
				for (World world : this.plugin.getWorldManager().getWorlds()) {
					message.append(ChatColor.BLUE).append(world.getName()).append(ChatColor.WHITE).append(" ");
					List<IStation> stations = world.getStationManager().getStations();
					for (int i = 0; i < stations.size(); i++) {
						message.append(stations.get(i).getName());
						if (i != stations.size() - 1)
							message.append(",");
						message.append(" ");
					}
				}
				user.sendMessage(message.toString());
				return true;
			} else if (args[1].equalsIgnoreCase("nearest")) {
				List<IStation> stations = user.getWorld().getStationManager().getStations();
				IStation nearest = null;
				Location location = user.getLocation();
				Vector3 loc = Vector3.at(location.getX(), location.getY(), location.getZ());
				double distance = -1D;
				for (IStation station : stations) {
					if (station.getRegion().contains(BlockVector3.at(location.getX(), location.getY(), location.getZ()))) {
						user.sendMessage("You're inside of a station.");
						return true;
					}
					Vector3 center = station.getRegion().getCenter();
					double dis = center.distance(loc);
					if (distance == -1D || dis < distance) {
						distance = dis;
						nearest = station;
					}
				}
				if (nearest != null) {
					String message = "The nearest station is " + nearest.getName() + " station being " + ((long) distance) + " blocks away.";
					user.sendMessage(message);
					return true;
				}
			}
		} else if (args.length == 3) {
			if (args[1].equalsIgnoreCase("info")) {
				IStation station = user.getWorld().getStationManager().getStation(args[2]);
				if (station != null) {
					user.sendMessage(station.getChatInfo());
				}
			} else if (args[1].equalsIgnoreCase("delete") && user.isManager()) {
				IStation station = user.getWorld().getStationManager().getStation(args[2]);
				if (station != null) {
					UserPreConfirmRemoveEvent event = new UserPreConfirmRemoveEvent(user, station);
					Bukkit.getPluginManager().callEvent(event);

					if (!event.isCancelled()) {
						user.sendMessage("Are you sure you want to delete " + station.getName() + " station?");
						user.sendMessage("You have " + ChatColor.RED + "10" + ChatColor.WHITE + " seconds to decide.");
						user.sendMessage("Confirm by typing the command /rrr confirm");
						user.sendMessage("Cancel by typing the command /rrr cancel");
						user.setRemove(station);
						Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
							if (!user.isValid()) {
								UserFailRemoveEvent failEvent = new UserFailRemoveEvent(user, station, UserFailRemoveEvent.FailReason.LEFT);
								Bukkit.getPluginManager().callEvent(failEvent);
							} else if (user.checkRemove()) {
								UserFailRemoveEvent failEvent = new UserFailRemoveEvent(user, station, UserFailRemoveEvent.FailReason.TIME);
								Bukkit.getPluginManager().callEvent(failEvent);
								user.setRemove(null);
							}
						}, 200);
					} else {
						user.sendMessage("You were stopped from deleting " + station.getName() + " station.");
					}
					return true;
				}
			} else if (args[1].equalsIgnoreCase("list")) {
				org.bukkit.World world = Bukkit.getWorld(args[2]);
				if (world != null) {
					StringBuilder message = new StringBuilder();
					message.append(ChatColor.GOLD);
					message.append("Stations: ");
					message.append(ChatColor.WHITE);
					List<IStation> stations = this.plugin.getWorldManager().getWorld(world).getStationManager().getStations();
					for (int i = 0; i < stations.size(); i++) {
						message.append(stations.get(i));
						if (i != stations.size() - 1)
							message.append(", ");
					}
					user.sendMessage(message.toString());
					return true;
				}
			}
		} else if (args.length == 4) {
			if (args[1].equalsIgnoreCase("create")) {
				ILine track = user.getWorld().getLineManager().getTrack(args[2]);
				if (track != null && user.isManager(track.getWorld())) {
					for (IStation station : track.getStations())
						if (station.getName().equalsIgnoreCase(args[3])) {
							user.sendMessage(station.getName() + " station already exists.");
							return true;
						}
					if (!this.plugin.getSessionManager().getSession(user).getLocalSession(user.getWorld()).regionIsDefined()) {
						user.sendMessage(ChatColor.RED + "You must select a region for the station.");
						return true;
					}
					RegisteredServiceProvider<IStation> provider = this.plugin.getServer().getServicesManager().getRegistration(IStation.class);
					if (provider == null) {
						user.sendMessage("No station type has been registered!");
						IChatBaseComponent comp = IChatBaseComponent.ChatSerializer
								.a("{\"text\":\"Use default station type? \",\"extra\":[{\"text\":\"§9[Yes] \",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"§fUse the default station type.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/rr admin create station " + user.getName() + "\"}}{\"text\":\"§c[No]\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Don't use default station.");
						PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.SYSTEM, SystemUtils.b);
					}
				}
			}
		}
		return false;
	}
}
