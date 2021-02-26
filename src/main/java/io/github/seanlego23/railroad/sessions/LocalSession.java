package io.github.seanlego23.railroad.sessions;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.stations.selectionmethod.SelectionObject;
import io.github.seanlego23.railroad.user.User;
import io.github.seanlego23.railroad.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocalSession {
	private final User user;
	private final World world;
	private SelectionObject selectionObject;
	private CuboidRegion region;

	public LocalSession(@NotNull World world, @NotNull User user) {
		this(world, user, null);
	}

	public LocalSession(@NotNull World world, @NotNull User user, @Nullable SelectionObject object) {
		this.world = world;
		this.user = user;
		this.selectionObject = object;
	}

	public @Nullable SelectionObject getSelectionObject() {
		return this.selectionObject;
	}

	public void setSelectionObject(@Nullable SelectionObject object) {
		this.selectionObject = object;
	}

	public void setRegionCorner1(Location loc) {
		//noinspection ConstantConditions
		if (((Railroad)Bukkit.getPluginManager().getPlugin("Railroad")).getWorldEditConnection() != null)
			return;
		if (this.region == null) {
			this.region = new CuboidRegion(new BukkitWorld(this.world.getBukkitWorld()), BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()), BlockVector3.ZERO);
			this.region.setPos2(null);
		} else {
			this.region.setPos1(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));
		}
	}

	public void setRegionCorner2(Location loc) {
		//noinspection ConstantConditions
		if (((Railroad)Bukkit.getPluginManager().getPlugin("Railroad")).getWorldEditConnection() != null)
			return;
		if (this.region == null) {
			this.region = new CuboidRegion(new BukkitWorld(this.world.getBukkitWorld()), BlockVector3.ZERO, BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));
			this.region.setPos2(null);
		} else {
			this.region.setPos1(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));
		}
	}

	@SuppressWarnings("ConstantConditions")
	public @Nullable Region getRegion() throws IncompleteRegionException {
		if (((Railroad)Bukkit.getPluginManager().getPlugin("Railroad")).getWorldEditConnection() != null)
			return ((Railroad)Bukkit.getPluginManager().getPlugin("Railroad")).getWorldEditConnection().getSelection(this.user.getPlayer());
		else {
			if (this.region.getPos1() == null || this.region.getPos1() == null)
				throw new IncompleteRegionException();
			return this.region.clone();
		}
	}

	public long getRegionVolume() {
		return this.region.getVolume();
	}

	public boolean regionIsDefined() {
		return this.region.getPos1() != null || this.region.getPos2() != null;
	}

	public World getWorld() {
		return this.world;
	}
}
