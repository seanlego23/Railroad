package io.github.seanlego23.railroad.sessions;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.user.User;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
	private final Railroad plugin;
	private final Map<User, Session> sessionMap = new HashMap<>();

	public SessionManager(Railroad plugin) {
		this.plugin = plugin;
	}

	public Session createSession(User user) {
		if (this.sessionMap.containsKey(user))
			return this.sessionMap.get(user);
		Session newSession = new Session(user);
		this.sessionMap.put(user, newSession);
		return newSession;
	}

	public boolean removeSession(User user) {
		return this.sessionMap.remove(user) != null;
	}

	public Session getSession(User user) {
		return this.sessionMap.get(user);
	}

}
