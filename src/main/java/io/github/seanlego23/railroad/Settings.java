package io.github.seanlego23.railroad;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import io.github.seanlego23.railroad.stations.selectionmethod.*;
import io.github.seanlego23.railroad.util.Items;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Settings {
	private final Railroad plugin;
	private Set<NamespacedKey> mainStationSelectionBlacklist;
	private Class<? extends ISelectionMethod> defaultSelectionMethod;
	private Material binder;

	public Settings(@NotNull Railroad plugin) {
		this.plugin = plugin;
	}

	public void init(@NotNull FileConfiguration configuration) {
		List<String> mainBlacklist = configuration.getStringList("station.selection-blacklist");
		if (mainBlacklist.size() != 0) {
			this.mainStationSelectionBlacklist = new HashSet<>();
			for (String item : mainBlacklist) {
				this.mainStationSelectionBlacklist.add(this.stringToKey(item));
			}
		}

		String selectionMethod = configuration.getString("station.default-selection-method", "GUI");
		assert selectionMethod != null;
		if (selectionMethod.equalsIgnoreCase("GUI"))
			this.defaultSelectionMethod = GUISelectionMethod.class;
		else if (selectionMethod.equalsIgnoreCase("Lectern"))
			this.defaultSelectionMethod = LecternSelectionMethod.class;
		else if (selectionMethod.equalsIgnoreCase("Button"))
			this.defaultSelectionMethod = ButtonSelectionMethod.class;
		else if (selectionMethod.equalsIgnoreCase("Item_Frame") || selectionMethod.equalsIgnoreCase("Item Frame") || selectionMethod.equalsIgnoreCase("ItemFrame"))
			this.defaultSelectionMethod = ItemFrameSelectionMethod.class;
		else if (selectionMethod.equalsIgnoreCase("Custom"))
			this.defaultSelectionMethod = null;
		else
			this.defaultSelectionMethod = GUISelectionMethod.class;

		String bindingItem = configuration.getString("station.binding-item", "minecraft:wooden_pickaxe");
		NamespacedKey bindingKey = this.stringToKey(bindingItem);
		this.binder = Material.getMaterial(bindingKey.getKey().toUpperCase(Locale.ROOT));
		if (this.binder == null) {
			this.plugin.getLogger().warning(bindingKey.toString() + " is not a valid material.");
			this.binder = Material.WOODEN_PICKAXE;
		} else if (!Items.getKeys().contains(this.binder.getKey().toString())) {
			this.plugin.getLogger().warning(this.binder.getKey().toString() + " is not a valid material.");
			this.binder = Material.WOODEN_PICKAXE;
		}
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

	public @Nullable List<NamespacedKey> getMainBlacklist() {
		return new ArrayList<>(this.mainStationSelectionBlacklist);
	}

	public boolean removeItemFromMainBlacklist(@NotNull NamespacedKey key) {
		return this.mainStationSelectionBlacklist.remove(key);
	}

	public boolean addItemToMainBlacklist(@NotNull NamespacedKey key) {
		return this.mainStationSelectionBlacklist.add(key);
	}

	public @Nullable Class<? extends ISelectionMethod> getDefaultSelectionMethod() {
		return this.defaultSelectionMethod;
	}

	public void setDefaultSelectionMethod(@NotNull Class<? extends ISelectionMethod> clazz) {
		this.defaultSelectionMethod = clazz;
	}

	public @NotNull Material getBindingMaterial() {
		return this.binder;
	}

	public void setBindingMaterial(@NotNull Material material) {
		this.binder = material;
	}

	public void save(@NotNull FileConfiguration configuration) {
		List<String> items = new ArrayList<>();
		this.mainStationSelectionBlacklist.forEach(namespacedKey -> items.add(namespacedKey.toString()));
		configuration.set("station.selection-blacklist", items);

		if (this.defaultSelectionMethod == null)
			configuration.set("station.default-selection-method", "Custom");
		else if (this.defaultSelectionMethod.isAssignableFrom(GUISelectionMethod.class))
			configuration.set("station.default-selection-method", "GUI");
		else if (this.defaultSelectionMethod.isAssignableFrom(LecternSelectionMethod.class))
			configuration.set("station.default-selection-method", "Lectern");
		else if (this.defaultSelectionMethod.isAssignableFrom(ButtonSelectionMethod.class))
			configuration.set("station.default-selection-method", "Button");
		else if (this.defaultSelectionMethod.isAssignableFrom(ItemFrameSelectionMethod.class))
			configuration.set("station.default-selection-method", "Item_Frame");

		configuration.set("station.binding-item", this.binder.name().toLowerCase(Locale.ROOT));
	}

	public void reset() {
		this.mainStationSelectionBlacklist = null;
		this.defaultSelectionMethod = null;
		this.binder = null;
	}

}
