package io.github.seanlego23.railroad.util;

import org.bukkit.entity.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entities {

	private final static Map<String, EntityType> allowed = new HashMap<>();

	static {
		for (EntityType type : EntityType.values()) {
			Class<? extends Entity> clazz = type.getEntityClass();
			if (clazz != null &&
				!clazz.isAssignableFrom(Projectile.class) &&
				!clazz.isAssignableFrom(Item.class) &&
				!clazz.isAssignableFrom(ExperienceOrb.class) &&
				!clazz.isAssignableFrom(EvokerFangs.class) &&
				!clazz.isAssignableFrom(AreaEffectCloud.class) &&
				!clazz.isAssignableFrom(FallingBlock.class) &&
				!clazz.isAssignableFrom(TNTPrimed.class) &&
				!clazz.isAssignableFrom(LightningStrike.class) &&
				!clazz.isAssignableFrom(Player.class))
				allowed.put(type.getKey().toString(), type);
		}
	}

	public static @Nullable EntityType getEntityType(String key) {
		return allowed.get(key);
	}

	public static List<String> getKeys() {
		return new ArrayList<>(allowed.keySet());
	}
}
