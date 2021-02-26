package io.github.seanlego23.railroad.plugin;

import io.github.seanlego23.railroad.Railroad;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultConnection {

	private final Railroad connectingPlugin;

	private Economy economy;

	public VaultConnection(Railroad plugin) {
		this.connectingPlugin = plugin;
	}

	public boolean connectEconomy() {
		if (this.isVaultInstalled() && !this.isEconomyConnected()) {
			RegisteredServiceProvider<Economy> rsp = this.connectingPlugin.getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp == null)
				return false;
			this.economy = rsp.getProvider();
			return true;
		}
		return false;
	}

	public boolean isVaultInstalled() {
		return this.connectingPlugin.getServer().getPluginManager().isPluginEnabled("Vault");
	}

	public boolean isEconomyConnected() {
		return this.economy != null;
	}

	public void disconnect() {
		this.economy = null;
	}
}
