package io.github.seanlego23.railroad;

import io.github.seanlego23.railroad.commands.MainCommand;
import io.github.seanlego23.railroad.plugin.VaultConnection;
import io.github.seanlego23.railroad.plugin.WorldEditConnection;
import io.github.seanlego23.railroad.sessions.SessionManager;
import io.github.seanlego23.railroad.stations.selection.SelectionListener;
import io.github.seanlego23.railroad.user.UserListener;
import io.github.seanlego23.railroad.user.UserManager;
import io.github.seanlego23.railroad.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public final class Railroad extends JavaPlugin {
	private final Settings settings;
	private FileConfiguration config;
	private @NotNull WorldEditConnection worldEditConnection = new WorldEditConnection(this);
	private @NotNull VaultConnection vaultConnection = new VaultConnection(this);
	private final UserManager userManager = new UserManager(this);
	private final SessionManager sessionManager = new SessionManager(this);
	private final WorldManager worldManager = new WorldManager(this);
	private boolean failedInit = false;

	public final static String version = "v1.0";

	public Railroad() {
		this.settings = new Settings(this);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		this.config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

		this.settings.init(this.config);
		if (!this.worldManager.init(Bukkit.getWorlds())) {
			getServer().getScheduler().runTaskLater(this, () -> Bukkit.getPluginManager().disablePlugin(this), 1);
			this.failedInit = true;
		}

		getCommand("railroad").setExecutor(new MainCommand(this));
		getServer().getPluginManager().registerEvents(new WorldEditPluginListener(), this);
		getServer().getPluginManager().registerEvents(new VaultPluginListener(), this);
		getServer().getPluginManager().registerEvents(new UserListener(this), this);
		getServer().getPluginManager().registerEvents(new SelectionListener(this), this);

	}

	@Override
	public void onDisable() {
		if (this.failedInit)
			getServer().broadcastMessage(ChatColor.RED + "Railroad plugin failed to initialize. Plugin was disabled.");

		this.settings.save(this.config);
		try {
			this.config.save(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			getLogger().severe("Couldn't save config.");
		}

		this.getWorldManager().saveWorlds();
	}

	public void reload() {
		getLogger().info("Railroad is reloading...");
		this.config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

		this.settings.reset();
		this.settings.init(this.config);
		this.worldManager.getWorlds().forEach(world -> {
			world.getSettings().reset();
			File worldConfigFile = new File(getDataFolder() + "\\worlds", world.getName() + "\\worldConfig.yml");
			world.getSettings().init(YamlConfiguration.loadConfiguration(worldConfigFile));
		});

		getLogger().info("Railroad reloaded.");
	}

	public void help(CommandSender sender) {
		//TODO: Do help
		sender.sendMessage("");
	}

	public Settings getSettings() {
		return this.settings;
	}

	public @NotNull UserManager getUserManager() {
		return this.userManager;
	}

	public @NotNull SessionManager getSessionManager() {
		return this.sessionManager;
	}

	public @NotNull WorldManager getWorldManager() {
		return this.worldManager;
	}

	public @NotNull WorldEditConnection getWorldEditConnection() {
		return this.worldEditConnection;
	}

	public @NotNull VaultConnection getVaultConnection() {
		return this.vaultConnection;
	}

	private class WorldEditPluginListener implements Listener {

		private WorldEditPluginListener() {
			if (Railroad.this.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
				this.connectWorldEdit();
			}
		}

		private boolean isPluginWorldEdit(@NotNull Plugin plugin) {
			return plugin.getName().equals("WorldEdit");
		}

		private boolean connectWorldEdit() {
			Railroad.this.worldEditConnection = new WorldEditConnection(Railroad.this);
			return Railroad.this.worldEditConnection.connect();
		}

		@EventHandler
		private void pluginEnabled(PluginEnableEvent event) {
			if (isPluginWorldEdit(event.getPlugin()))
				this.connectWorldEdit();
		}

		@EventHandler
		private void pluginDisableEvent(PluginDisableEvent event) {
			if (isPluginWorldEdit(event.getPlugin())) {
				Railroad.this.worldEditConnection.disconnect();
			}
		}
	}

	private class VaultPluginListener implements Listener {
		private VaultPluginListener() {
			if (Railroad.this.getServer().getPluginManager().getPlugin("Vault") != null)
				if (Railroad.this.getServer().getPluginManager().isPluginEnabled("Vault"))
					this.connectVault();
		}

		private boolean isPluginVault(@NotNull Plugin plugin) {
			return plugin.getName().equals("Vault");
		}

		private void connectVault() {
			Railroad.this.vaultConnection = new VaultConnection(Railroad.this);
			Railroad.this.vaultConnection.connectEconomy();
		}

		@EventHandler
		private void pluginEnabled(PluginEnableEvent event) {
			if (isPluginVault(event.getPlugin())) {
				this.connectVault();
			}
		}

		@EventHandler
		private void pluginDisableEvent(PluginDisableEvent event) {
			if (isPluginVault(event.getPlugin())) {
				Railroad.this.vaultConnection.disconnect();
				Railroad.this.vaultConnection = null;
			}
		}
	}
}
