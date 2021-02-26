package io.github.seanlego23.railroad.world;

import com.sun.istack.internal.NotNull;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorldSettings {
	private boolean enabled;
	private final Set<NamespacedKey> stationSelectionBlacklist;
	private FileConfiguration config;

	public WorldSettings() {
		this.stationSelectionBlacklist = new HashSet<>();
	}

	public void init(@NotNull FileConfiguration worldConfig) {
		this.config = worldConfig;
		this.enabled = worldConfig.getBoolean("enabled", false);
		List<String> blacklist = worldConfig.getStringList("station.item-selection-blacklist");
		for (String item : blacklist)
			this.stationSelectionBlacklist.add(this.stringToKey(item));
	}

	private @NotNull NamespacedKey stringToKey(@NotNull String str) {
		NamespacedKey key;
		Pattern pattern = Pattern.compile("minecraft:.*");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches())
			key = NamespacedKey.minecraft(str.substring(10));
		else {
			Pattern customPattern = Pattern.compile("[a-z0-9._-]+(:)[a-z0-9/._-]+");
			Matcher customMatcher = customPattern.matcher(str);
			if (customMatcher.matches() && customMatcher.end() <= 256)
				//noinspection deprecation
				key = new NamespacedKey(customMatcher.group(0), customMatcher.group(2));
			else
				key = NamespacedKey.minecraft(str);
		}
		return key;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public @NotNull List<NamespacedKey> getBlacklist() {
		return new ArrayList<>(stationSelectionBlacklist);
	}

	public boolean addItemToBlacklist(@NotNull NamespacedKey key) {
		return this.stationSelectionBlacklist.add(key);
	}

	public boolean removeItemFromBlacklist(@NotNull NamespacedKey key) {
		return this.stationSelectionBlacklist.remove(key);
	}

	public void reset() {
		this.stationSelectionBlacklist.clear();
	}

	public void save(@NotNull File file) throws IOException {
		List<String> blacklist = new ArrayList<>();
		this.stationSelectionBlacklist.forEach(key -> blacklist.add(key.toString()));
		this.config.set("enabled", this.enabled);
		this.config.set("station.item-selection-blacklist", blacklist);
		this.config.save(file);
	}
}
