package io.github.seanlego23.railroad.queue;

public enum TaskPriority {
	PLUGIN_LOW,
	USER,
	PLUGIN_HIGH,
	//USER_PRIORITY upgraded USER if taking forever plus actual priority.
}
