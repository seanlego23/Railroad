package io.github.seanlego23.railroad.queue;

public enum Task {
	ADD_TRACK (TaskPriority.USER),
	ADD_STATION (TaskPriority.USER),
	ADD_SELECTION (TaskPriority.USER),
	ADD_SCHEDULE (TaskPriority.USER),
	ADD_RAIL (TaskPriority.USER),
	ADD_DESTINATION (TaskPriority.USER),
	REMOVE_TRACK (TaskPriority.USER),
	REMOVE_STATION (TaskPriority.USER),
	REMOVE_SELECTION (TaskPriority.USER),
	REMOVE_SCHEDULE (TaskPriority.USER),
	REMOVE_RAIL (TaskPriority.USER),
	REMOVE_DESTINATION (TaskPriority.USER),
	CHECK_STATION (TaskPriority.PLUGIN_HIGH),
	CHECK_SELECTION (TaskPriority.PLUGIN_HIGH),
	CHECK_SCHEDULE (TaskPriority.PLUGIN_LOW),
	CHECK_RAIL (TaskPriority.PLUGIN_LOW),
	CHECK_DESTINATION (TaskPriority.PLUGIN_LOW);

	private final TaskPriority priority;

	Task(TaskPriority priority) {
		this.priority = priority;
	}

	public TaskPriority getPriority() {
		return this.priority;
	}
}
