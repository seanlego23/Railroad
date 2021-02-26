package io.github.seanlego23.railroad.queue;

import io.github.seanlego23.railroad.user.User;

public class QueueElement {
	private final User user;
	private final Task task;

	public QueueElement(User user, Task task) {
		this.user = user;
		this.task = task;
	}

}
