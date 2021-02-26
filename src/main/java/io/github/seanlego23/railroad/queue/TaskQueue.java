package io.github.seanlego23.railroad.queue;

import io.github.seanlego23.railroad.Railroad;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class TaskQueue implements Queue<QueueElement> {
	private final Railroad plugin;

	public TaskQueue(Railroad plugin) {
		this.plugin = plugin;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@NotNull
	@Override
	public Iterator<QueueElement> iterator() {
		return null;
	}

	@NotNull
	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@NotNull
	@Override
	public <T> T[] toArray(@NotNull T[] a) {
		return null;
	}

	@Override
	public boolean add(QueueElement queueElement) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends QueueElement> c) {
		return false;
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean offer(QueueElement queueElement) {
		return false;
	}

	@Override
	public QueueElement remove() {
		return null;
	}

	@Override
	public QueueElement poll() {
		return null;
	}

	@Override
	public QueueElement element() {
		return null;
	}

	@Override
	public QueueElement peek() {
		return null;
	}
}

