package io.github.seanlego23.railroad.stations.selection;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.stations.IStation;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class BlockListener implements Listener {
	private final Railroad plugin;

	public BlockListener(Railroad plugin) {
		this.plugin = plugin;
	}

	private boolean isSelectionObject(Block block) {
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
	public void onBlockBurn(BlockBurnEvent event) {
		if (this.isSelectionObject(event.getBlock()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockDamage(BlockDamageEvent event) {
		if (this.isSelectionObject(event.getBlock()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockExplode(BlockExplodeEvent event) {
		if (this.isSelectionObject(event.getBlock())) {
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
	public void onBlockFade(BlockFadeEvent event) {
		if (this.isSelectionObject(event.getBlock()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		for (Block block : event.getBlocks()) {
			if (this.isSelectionObject(block)) {
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		for (Block block : event.getBlocks()) {
			if (this.isSelectionObject(block)) {
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockLeavesDecay(LeavesDecayEvent event) {
		if (this.isSelectionObject(event.getBlock()))
			event.setCancelled(true);
	}
}
