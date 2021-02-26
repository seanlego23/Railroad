package io.github.seanlego23.railroad.stations.selection;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.sessions.LocalSession;
import io.github.seanlego23.railroad.stations.IStation;
import io.github.seanlego23.railroad.stations.selectionmethod.SelectionObject;
import io.github.seanlego23.railroad.user.User;
import io.github.seanlego23.railroad.user.UserNotFoundException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class SelectionListener implements Listener {
	private final Railroad plugin;

	public SelectionListener(Railroad plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		User user = null;
		try {
			user = this.plugin.getUserManager().getUser(event.getPlayer());
		} catch (UserNotFoundException e) {
			this.plugin.getLogger().severe(e.getMessage());
		}
		Block clickedBlock = event.getClickedBlock();
		ItemStack item = event.getItem();
		Action action = event.getAction();
		if (clickedBlock != null) {
			IStation currentStation = null;
			List<MetadataValue> metadataValues = clickedBlock.getMetadata("Station");
			if (!metadataValues.isEmpty()) {
				for (MetadataValue metadataValue : metadataValues) {
					if (metadataValue.getOwningPlugin() instanceof Railroad) {
						if (user == null) {
							event.setCancelled(true);
							event.getPlayer().sendMessage(ChatColor.RED + "Railroad plugin could not identify you. Try reconnecting or reloading plugin.");
							return;
						}
						currentStation = user.getWorld().getStationManager().getStation(metadataValue.asString());
						break;
					}
				}
			}
			if (currentStation != null)
				event.setCancelled(true);

			if (user != null) {
				if (item != null && item.getType().equals(this.plugin.getSettings().getBindingMaterial()) &&
						event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND) &&
						user.isManager(user.getWorld())) {

					if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.LEFT_CLICK_BLOCK))
						return;

					LocalSession localSession = this.plugin.getSessionManager().getSession(user).getLocalSession(user.getWorld());
					if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						if (currentStation != null) {
							localSession.setSelectionObject(new SelectionObject(clickedBlock));
						} else if (this.plugin.getWorldEditConnection().isConnected()) {
							Location loc = clickedBlock.getLocation();
							localSession.setRegionCorner2(loc);
							String message = ChatColor.DARK_PURPLE + "Second position set to ("
									+ loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
									+ ")";
							if (localSession.regionIsDefined()) {
								user.sendMessage(message + " (" + localSession.getRegionVolume() + ").");
							} else {
								user.sendMessage(message + ".");
							}
							event.setCancelled(true);
						}
					} else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
						if (!this.plugin.getWorldEditConnection().isConnected() && currentStation == null) {
							Location loc = clickedBlock.getLocation();
							localSession.setRegionCorner2(loc);
							String message = ChatColor.DARK_PURPLE + "First position set to ("
									+ loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
									+ ")";
							if (localSession.regionIsDefined()) {
								user.sendMessage(message + " (" + localSession.getRegionVolume() + ").");
							} else {
								user.sendMessage(message + ".");
							}
							event.setCancelled(true);
						}
					}
				} else if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND) && currentStation != null) {
					List<MetadataValue> selectionMetadata = clickedBlock.getMetadata("Selection");
					ISelection selection = null;
					for (MetadataValue metadataValue : selectionMetadata) {
						if (metadataValue.getOwningPlugin() instanceof Railroad) {
							selection = currentStation.getSelection(metadataValue.asString());
							break;
						}
					}
					if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && selection != null) {
						selection.select(user, new SelectionObject(clickedBlock));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		User user = null;
		try {
			user = this.plugin.getUserManager().getUser(event.getPlayer());
		} catch (UserNotFoundException e) {
			this.plugin.getLogger().severe(e.getMessage());
		}
		Entity clickedEntity = event.getRightClicked();
		ItemStack item = event.getHand().equals(EquipmentSlot.HAND) ? event.getPlayer().getInventory().getItemInMainHand() : event.getPlayer().getInventory().getItemInOffHand();

		List<MetadataValue> stationMetadata = clickedEntity.getMetadata("Station");
		IStation currentStation = null;
		if (!stationMetadata.isEmpty()) {
			for (MetadataValue stationMeta : stationMetadata) {
				if (stationMeta.getOwningPlugin() instanceof Railroad) {
					if (user == null) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "Railroad plugin could not identify you. Try reconnecting or reloading plugin.");
						return;
					}
					currentStation = user.getWorld().getStationManager().getStation(stationMeta.asString());
				}
			}
		}

		if (currentStation != null)
			event.setCancelled(true);

		if (user != null) {
			if (item.getType().equals(this.plugin.getSettings().getBindingMaterial()) &&
				user.isManager(user.getWorld())) {

				LocalSession localSession = this.plugin.getSessionManager().getSession(user).getLocalSession(user.getWorld());
				if (currentStation != null) {
					localSession.setSelectionObject(new SelectionObject(clickedEntity));
				}
			} else if (currentStation != null) {
				List<MetadataValue> selectionMetadata = clickedEntity.getMetadata("Selection");
				ISelection selection = null;
				for (MetadataValue metadataValue : selectionMetadata) {
					if (metadataValue.getOwningPlugin() instanceof Railroad) {
						selection = currentStation.getSelection(metadataValue.asString());
						break;
					}
				}
				if (selection != null) {
					selection.select(user, new SelectionObject(clickedEntity));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
			User user = null;
			try {
				user = this.plugin.getUserManager().getUser(event.getPlayer());
			} catch (UserNotFoundException e) {
				this.plugin.getLogger().severe(e.getMessage());
			}

			ItemStack item = event.getHand().equals(EquipmentSlot.HAND) ? event.getPlayer().getInventory().getItemInMainHand() : event.getPlayer().getInventory().getItemInOffHand();
			ArmorStand armorStand = (ArmorStand) event.getRightClicked();
			List<MetadataValue> stationMetadata = armorStand.getMetadata("Station");
			IStation currentStation = null;
			if (!stationMetadata.isEmpty()) {
				for (MetadataValue stationMeta : stationMetadata) {
					if (stationMeta.getOwningPlugin() instanceof Railroad) {
						if (user == null) {
							event.setCancelled(true);
							event.getPlayer().sendMessage(ChatColor.RED + "Railroad plugin could not identify you. Try reconnecting or reloading plugin.");
							return;
						}
						currentStation = user.getWorld().getStationManager().getStation(stationMeta.asString());
					}
				}
			}

			if (currentStation != null)
				event.setCancelled(true);

			if (user != null) {
				if (item.getType().equals(this.plugin.getSettings().getBindingMaterial()) &&
					user.isManager(user.getWorld())) {

					LocalSession localSession = this.plugin.getSessionManager().getSession(user).getLocalSession(user.getWorld());
					if (currentStation != null) {
						localSession.setSelectionObject(new SelectionObject(armorStand));
					}
				} else if (currentStation != null) {
					List<MetadataValue> selectionMetadata = armorStand.getMetadata("Selection");
					ISelection selection = null;
					for (MetadataValue metadataValue : selectionMetadata) {
						if (metadataValue.getOwningPlugin() instanceof Railroad) {
							selection = currentStation.getSelection(metadataValue.asString());
							break;
						}
					}
					if (selection != null) {
						selection.select(user, new SelectionObject(armorStand));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	//if armor stand is a selection object and user is manager, then ok.
	public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
		User user = null;
		try {
			user = this.plugin.getUserManager().getUser(event.getPlayer());
		} catch (UserNotFoundException e) {
			this.plugin.getLogger().severe(e.getMessage());
		}

		ArmorStand armorStand = event.getRightClicked();
		List<MetadataValue> stationMetadata = armorStand.getMetadata("Station");
		IStation currentStation = null;
		if (!stationMetadata.isEmpty()) {
			for (MetadataValue stationMeta : stationMetadata) {
				if (stationMeta.getOwningPlugin() instanceof Railroad) {
					if (user == null) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "Railroad plugin could not identify you. Try reconnecting or reloading plugin.");
						return;
					}
					currentStation = user.getWorld().getStationManager().getStation(stationMeta.asString());
				}
			}
		}

		if (currentStation == null)
			return;

		if (!user.isManager(this.plugin.getWorldManager().getWorld(armorStand.getWorld())))
			event.setCancelled(true);
	}
}
