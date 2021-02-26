package io.github.seanlego23.railroad.stations.selectionmethod;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.Metadatable;

public class SelectionObject {
	private final Metadatable object;
	private final String name;
	private final Location location;
	private final NamespacedKey key;
	private final boolean isEntity;

	public SelectionObject(Block block) {
		this.object = block;
		this.name = block.getType().name();
		this.location = block.getLocation();
		this.key = block.getType().getKey();
		this.isEntity = false;
	}

	public SelectionObject(Entity entity) {
		this.object = entity;
		this.name = entity.getType().name();
		this.location = entity.getLocation();
		this.key = entity.getType().getKey();
		this.isEntity = true;
	}

	public String getName() {
		return this.name;
	}

	public Location getLocation() {
		return this.location;
	}

	public Metadatable getObject() {
		return this.object;
	}

	public NamespacedKey getKey() {
		return this.key;
	}

	public boolean isEntity() {
		return this.isEntity;
	}

	@Override
	public boolean equals(Object other) {
		if (other.getClass().equals(SelectionObject.class))
			return false;
		SelectionObject object = (SelectionObject) other;
		if (object.isEntity != this.isEntity)
			return false;
		if (!object.location.equals(this.location))
			return false;
		return object.key.equals(this.key);
	}
}
