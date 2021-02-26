package io.github.seanlego23.railroad.stations.selection;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.stations.IStation;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class EntityListener implements Listener {
	private final Railroad plugin;

	public EntityListener(Railroad plugin) {
		this.plugin = plugin;
	}

	private boolean isSelectionObject(Entity entity) {
		List<MetadataValue> stationMetaData = entity.getMetadata("Station");
		IStation currentStation = null;
		for (MetadataValue stationMeta : stationMetaData) {
			if (stationMeta.getOwningPlugin() instanceof Railroad) {
				currentStation = this.plugin.getWorldManager().getWorld(entity.getWorld()).getStationManager().getStation(stationMeta.asString());
				break;
			}
		}
		if (currentStation == null)
			return false;

		List<MetadataValue> selectionMetaData = entity.getMetadata("Selection");
		ISelection selection = null;
		for (MetadataValue selectionMeta : selectionMetaData) {
			if (selectionMeta.getOwningPlugin() instanceof Railroad) {
				selection = currentStation.getSelection(selectionMeta.asString());
				break;
			}
		}

		return selection != null;
	}

	private boolean isSelectionObject(org.bukkit.block.Block block) {
		List<MetadataValue> stationMetaData = block.getMetadata("Station");
		IStation currentStation = null;
		for (MetadataValue stationMeta : stationMetaData) {
			if (stationMeta.getOwningPlugin() instanceof Railroad) {
				currentStation = this.plugin.getWorldManager().getWorld(block.getWorld()).getStationManager().getStation(stationMeta.asString());
				break;
			}
		}
		if (currentStation == null)
			return false;

		List<MetadataValue> selectionMetaData = block.getMetadata("Selection");
		ISelection selection = null;
		for (MetadataValue selectionMeta : selectionMetaData) {
			if (selectionMeta.getOwningPlugin() instanceof Railroad) {
				selection = currentStation.getSelection(selectionMeta.asString());
				break;
			}
		}

		return selection != null;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityCombust(EntityCombustEvent event) {
		if (this.isSelectionObject(event.getEntity()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (this.isSelectionObject(event.getEntity()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity killedEntity = event.getEntity();
		if (this.isSelectionObject(killedEntity)) {
			Entity entity = killedEntity.getWorld().spawnEntity(killedEntity.getLocation(), event.getEntityType());

			if (entity instanceof LivingEntity)
				((LivingEntity) entity).setAI(false);
			if (entity instanceof Hanging)
				((Hanging) entity).setFacingDirection(killedEntity.getFacing(), false);
			else
				entity.setRotation(killedEntity.getLocation().getYaw(), killedEntity.getLocation().getPitch());


			List<MetadataValue> stationMetadata = killedEntity.getMetadata("Station");
			for (MetadataValue stationMeta : stationMetadata) {
				if (stationMeta.getOwningPlugin() instanceof Railroad) {
					entity.setMetadata("Station", stationMeta);
					break;
				}
			}

			List<MetadataValue> selectionMetadata = killedEntity.getMetadata("Selection");
			for (MetadataValue selectionMeta : selectionMetadata) {
				if (selectionMeta.getOwningPlugin() instanceof Railroad) {
					entity.setMetadata("Selection", selectionMeta);
					break;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (this.isSelectionObject(event.getEntity())) {
			event.setCancelled(true);
			return;
		}
		for (Block block : event.blockList()) {
			if (this.isSelectionObject(block)) {
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityTeleport(EntityTeleportEvent event) {
		if (this.isSelectionObject(event.getEntity()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onHangingBreak(HangingBreakEvent event) {
		if (this.isSelectionObject(event.getEntity()))
			event.setCancelled(true);
	}
}
