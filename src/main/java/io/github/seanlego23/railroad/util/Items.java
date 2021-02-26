package io.github.seanlego23.railroad.util;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Items {

	private final static Map<String, Material> allowed = new HashMap<>();

	static {
		for (Material material : Material.values())
			if (!material.isAir() && material.isItem())
				allowed.put(material.getKey().toString(), material);
	}

	public static @Nullable Material getMaterial(String key) {
		return allowed.get(key);
	}

	public static List<String> getKeys() {
		return new ArrayList<>(allowed.keySet());
	}
}
