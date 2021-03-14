package io.github.seanlego23.railroad.sessions;

import io.github.seanlego23.railroad.stations.selectionmethod.SelectionObject;
import io.github.seanlego23.railroad.user.User;
import io.github.seanlego23.railroad.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Session {
	private final User user;
	private final Map<World, LocalSession> localSessionMap = new HashMap<>();

	public Session(@NotNull User user) {
		this.user = user;
	}

	public LocalSession getLocalSession(@NotNull World world) {
		return this.localSessionMap.get(world);
	}

	public LocalSession createLocalSession(@NotNull World world) {
		return this.createLocalSession(world, null);
	}

	public LocalSession createLocalSession(@NotNull World world, @Nullable SelectionObject object) {
		if (this.localSessionMap.containsKey(world))
			return this.localSessionMap.get(world);
		LocalSession newLS = new LocalSession(world, this.user, object);
		this.localSessionMap.put(world, newLS);
		return newLS;
	}
}
