package io.github.seanlego23.railroad.plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import io.github.seanlego23.railroad.Railroad;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

//Taken from Multiverse-Portals and modified
public class WorldEditConnection {

	private final Railroad connectingPlugin;

	private WorldEditPlugin worldEditPlugin;
	WorldEdit worldEdit;

	public WorldEditConnection(Railroad plugin) {
		this.connectingPlugin = plugin;
	}

	private WorldEditPlugin retrieveWorldEditPluginFromServer() {
		Plugin plugin = connectingPlugin.getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin == null) {
			plugin = connectingPlugin.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
		}

		if (plugin == null) {
			return null;
		} else if (plugin instanceof WorldEditPlugin) {
			return (WorldEditPlugin) plugin;
		} else {
			connectingPlugin.getLogger().warning("WorldEdit v" + plugin.getDescription().getVersion()
					+ " is incompatible with " + connectingPlugin.getDescription().getName() + " v"
					+ connectingPlugin.getDescription().getVersion());
			return null;
		}
	}

	/**
	 * Attempts to connect to the WorldEdit plugin.
	 *
	 * @return true if the WorldEdit plugin is available and able to be interfaced with.
	 */
	public boolean connect() {
		if (!isConnected()) {
			worldEditPlugin = retrieveWorldEditPluginFromServer();
			if (worldEditPlugin != null) {
				this.worldEdit = worldEditPlugin.getWorldEdit();
				connectingPlugin.getLogger().info(String.format("Found %s. Using it for selections.", worldEditPlugin.getName()));
				return true;
			}
		}
		return false;
	}

	public void disconnect() {
		worldEditPlugin = null;
		this.worldEdit = null;
	}

	/**
	 * Tests the connection to the WorldEdit plugin.
	 *
	 * @return true if current connected to the WorldEdit plugin.
	 */
	public boolean isConnected() {
		return worldEditPlugin != null;
	}

	//This was private
	public Region getSelection(Player player) throws IncompleteRegionException {
		if (!isConnected()) {
			throw new RuntimeException("WorldEdit connection is unavailable.");
		}
		return worldEdit.getSessionManager().get(new BukkitPlayer(worldEditPlugin, player)).getSelection(new BukkitWorld(player.getWorld()));
	}

	public String getWandItem() {
		if (!isConnected())
			throw new RuntimeException("WorldEdit connection is unavailable.");
		return worldEdit.getConfiguration().wandItem;
	}
}
