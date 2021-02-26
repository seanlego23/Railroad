package io.github.seanlego23.railroad.world;

import io.github.seanlego23.railroad.Railroad;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager {
	private final Railroad plugin;
	private final Map<org.bukkit.World, World> worlds;

	public WorldManager(@NotNull Railroad plugin) {
		this.plugin = plugin;
		this.worlds = new HashMap<>();
	}

	public boolean init(@NotNull List<org.bukkit.World> bukkitWorlds) {
		boolean worked = true;
		for (org.bukkit.World bukkitWorld : bukkitWorlds) {
			try {
				this.createWorld(bukkitWorld);
			} catch (IOException e) {
				this.plugin.getLogger().severe(e.getMessage());
				this.plugin.getLogger().severe("Could not create settings for " + bukkitWorld.getName());
				worked = false;
			}
		}
		return worked;
	}

	public World getWorld(@NotNull org.bukkit.World world) {
		World newWorld = this.worlds.get(world);
		if (newWorld == null) {
			try {
				return this.createWorld(world);
			} catch (IOException e) {
				this.plugin.getLogger().severe(e.getMessage());
				this.plugin.getLogger().severe("Could not create settings for " + world.getName());
				return null;
			}
		} else
			return newWorld;
	}

	public World getWorld(@NotNull String name) {
		org.bukkit.World world = Bukkit.getWorld(name);
		if (world == null)
			return null;
		return this.worlds.get(world);
	}

	public List<World> getWorlds() {
		return new ArrayList<>(this.worlds.values());
	}

	public World createWorld(@NotNull org.bukkit.World world) throws IOException {
		if (this.worlds.containsKey(world))
			return this.worlds.get(world);
		File worldDirectory = new File(this.plugin.getDataFolder() + "\\worlds", world.getName());
		File worldSettingsFile;
		if (worldDirectory.mkdirs()) {
			worldSettingsFile = new File(worldDirectory, "worldConfig.yml");
			InputStream worldSettingsDefault = this.plugin.getResource("worldConfig.yml");
			if (worldSettingsDefault != null && !worldSettingsFile.exists())
				Files.copy(worldSettingsDefault, worldSettingsFile.toPath());
			else if (worldSettingsDefault == null)
				throw new FileNotFoundException("Couldn't find resource worldConfig.yml.");
		} else
			throw new FileSystemException("Couldn't create settings directory for " + world.getName());
		FileConfiguration worldConfig = YamlConfiguration.loadConfiguration(worldSettingsFile);
		World newWorld = new World(world);
		newWorld.getSettings().init(worldConfig);
		this.worlds.put(world, newWorld);
		return newWorld;
	}

	public List<World> getEnabledWorlds() {
		List<World> enabledWorlds = new ArrayList<>();
		for (World world : this.worlds.values())
			if (world.getSettings().isEnabled())
				enabledWorlds.add(world);
		return enabledWorlds;
	}

	public void saveWorlds() {
		for (World world : this.worlds.values()) {
			File worldConfig = new File(this.plugin.getDataFolder() + "\\worlds", world.getName() + "\\worldConfig.yml");
			try {
				world.getSettings().save(worldConfig);
			} catch (IOException e) {
				this.plugin.getLogger().severe("Couldn't save world settings for " + world.getName());
			}
		}
	}
}
