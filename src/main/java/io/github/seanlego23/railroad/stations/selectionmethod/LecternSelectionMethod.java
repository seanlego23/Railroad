package io.github.seanlego23.railroad.stations.selectionmethod;

import io.github.seanlego23.railroad.util.Pair;
import io.github.seanlego23.railroad.destinations.IDestination;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.Metadatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class LecternSelectionMethod implements ISelectionMethod {

	private static class LecternIterator implements Iterator<Pair<IDestination, ITarget>> {
		private final LecternSelectionMethod method;
		private final Iterator<Map.Entry<Pair<SelectionObject, Set<Integer>>,List<Pair<IDestination, Integer>>>> iterator;
		private Pair<SelectionObject, Set<Integer>> currentObject;
		private List<Pair<IDestination, Integer>> currentList;
		private int clIndex;
		private Pair<IDestination, ITarget> lastPair;

		public LecternIterator(LecternSelectionMethod method) {
			this.method = method;
			this.iterator = method.bindings.entrySet().iterator();
			if (iterator.hasNext()) {
				Map.Entry<Pair<SelectionObject, Set<Integer>>,List<Pair<IDestination, Integer>>> entry = this.iterator.next();
				this.currentObject = entry.getKey();
				this.currentList = entry.getValue();
			}
			this.clIndex = 0;
		}

		@Override
		public boolean hasNext() {
			return this.currentList != null;
		}

		@Override
		public Pair<IDestination, ITarget> next() {
			if (this.currentList == null)
				throw new NoSuchElementException();
			Pair<IDestination, Integer> destinationPair = this.currentList.get(this.clIndex);
			this.lastPair = new Pair<>(destinationPair.getFirst(), new LecternTarget(this.currentObject.getFirst().getObject(), destinationPair.getSecond()));
			this.clIndex++;
			if (this.clIndex == this.currentList.size() && this.iterator.hasNext()) {
				Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Integer>>> entry = this.iterator.next();
				this.currentObject = entry.getKey();
				this.currentList = entry.getValue();
				this.clIndex = 0;
			} else if (this.clIndex == this.currentList.size())
				this.currentList = null;
			return this.lastPair;
		}

		@Override
		public void remove() {
			if (this.lastPair != null) {
				this.method.unbindDestination(this.lastPair.getFirst());
				this.clIndex--;
				this.lastPair = null;
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super Pair<IDestination, ITarget>> action) {
			while (this.hasNext())
				action.accept(this.next());
		}
	}

	private static class LecternTarget implements ITarget {

		private final SelectionObject selectionObject;
		private Integer lecternPage;

		private LecternTarget(@NotNull Metadatable object, @NotNull Integer lecternPage) {
			if (object instanceof Entity)
				this.selectionObject = new SelectionObject((Entity) object);
			else
				this.selectionObject = new SelectionObject((Block) object);
			this.lecternPage = lecternPage;
		}

		public LecternTarget(@NotNull SelectionObject object, @Nullable Integer lecternPage) throws InvalidSelectionException {
			this.selectionObject = object;
			this.lecternPage = lecternPage;
			if (!(object.getKey().equals(Material.LECTERN.getKey())))
				throw new InvalidSelectionException(object.getName() + " is not a Lectern.");
		}

		/**
		 * Gets the location
		 *
		 * @return The location
		 */
		@Override
		public Location getLocation() {
			return this.selectionObject.getLocation();
		}

		/**
		 * Gets the data
		 *
		 * <b>Note:</b> This method guarantees that it won't
		 * return null, but the elements of the list might be
		 * null.
		 *
		 * @return The data
		 */
		@Override
		public Object[] getData() {
			return new Object[] {this.lecternPage};
		}

		/**
		 * Gets the {@link SelectionObject SelectionObject}
		 * associated with this ITarget.
		 *
		 * @return The object
		 */
		@Override
		public SelectionObject getSelectionObject() {
			return this.selectionObject;
		}

		/**
		 * Sets the data
		 *
		 * @param data the new data.
		 * @throws InvalidDataTypeException If the type of
		 *                                  data given doesn't match what the ITarget wants
		 *                                  (See {@link ITarget#getDataTypes() getDataTypes}).
		 * @throws NullPointerException If data or any of the
		 *                              objects in the array are null.
		 */
		@Override
		public void setData(@NotNull Object... data) throws InvalidDataTypeException {
			if (data.length != 1)
				throw new InvalidDataTypeException("Given data does not specify to given types. (See ISelectionMethod.ITarget.getDataTypes)");
			if (data[0] instanceof Integer)
				this.lecternPage = (Integer) data[0];
			else
				throw new InvalidDataTypeException(data[0].getClass().getName() + " is not of type Integer.");
		}

		/**
		 * Gets the class types for the data this ITarget wants.
		 *
		 * @return The class types.
		 */
		@Override
		public Class<?>[] getDataTypes() {
			return new Class[] {Integer.class};
		}

		/**
		 * Returns whether this ITarget accepts entities as
		 * a target.
		 *
		 * @return Whether entities are acceptable.
		 */
		@Override
		public boolean acceptsEntity() {
			return false;
		}

		/**
		 * Returns whether this ITarget accepts blocks as a
		 * target.
		 *
		 * @return Whether blocks are acceptable.
		 */
		@Override
		public boolean acceptsBlock() {
			return true;
		}
	}

	private final Map<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Integer>>> bindings;

	public LecternSelectionMethod() {
		this.bindings = new HashMap<>();
	}

	/**
	 * Gets the selection object type used to select destinations.
	 *
	 * @return The selection item type.
	 */
	@Override
	public NamespacedKey getSelectionObjectType() {
		return Material.LECTERN.getKey();
	}

	/**
	 * Binds a destination to a target. Once a destination
	 * is bound in an ISelection, it cannot be bound
	 * again unless unbound via {@link ISelectionMethod#unbindDestination
	 * unbindDestination}.
	 *
	 * @param dest   The destination to bind.
	 * @param target The target to bind.
	 * @return True if the destination was bounded, false if
	 * not.
	 * @throws InvalidSelectionException If the selection
	 *                                   item specified by <i>target</i> does not equal the
	 *                                   type returned by {@link ISelectionMethod#getSelectionObjectType()
	 *                                   ISelectionMethod.getMethodItemType}.
	 */
	@Override
	public boolean bindDestination(IDestination dest, ITarget target) throws InvalidSelectionException {
		SelectionObject object = target.getSelectionObject();
		if (!object.getKey().equals(Material.LECTERN.getKey()))
			throw new InvalidSelectionException(object.getName() + " is not a Lectern.");

		if (this.isDestinationBound(dest))
			return false;

		Pair<SelectionObject, Set<Integer>> thisObjectPair = null;
		for (Pair<SelectionObject, Set<Integer>> pair : this.bindings.keySet()) {
			if (pair.getFirst().equals(object)) {
				thisObjectPair = pair;
				break;
			}
		}

		Object[] data = target.getData();
		if (thisObjectPair == null) {
			if (data == null) {
				try {
					target.setData(0);
				} catch (InvalidDataTypeException e) {
					return false;
				}
			} else if (data.length != 1)
				return false;
		} else {
			if (data == null) {
				Integer finalSlot = 0;
				for (Integer next : thisObjectPair.getSecond()) {
					if (next.equals(finalSlot))
						finalSlot++;
					else
						break;
				}
				try {
					target.setData(finalSlot);
				} catch (InvalidDataTypeException e) {
					return false;
				}
			} else if (data.length == 1) {
				for (Integer next : thisObjectPair.getSecond()) {
					if (next.equals(data[0]))
						return false;
				}
			} else
				return false;
		}

		if (thisObjectPair == null)
			this.bindings.put(new Pair<>(target.getSelectionObject(), new TreeSet<>(Collections.singleton(0))),new ArrayList<>(Collections.singletonList(new Pair<>(dest, (Integer)target.getData()[0]))));
		else {
			this.bindings.get(thisObjectPair).add(new Pair<>(dest, (Integer)target.getData()[0]));
			thisObjectPair.getSecond().add((Integer) target.getData()[0]);
		}
		return true;
	}

	/**
	 * Unbinds a destination to a target.
	 * <p>
	 * If it is the last destination bound to a selection
	 * object, the selection object will be removed from
	 * this ISelectionMethod.
	 *
	 * @param dest The destination to unbind.
	 */
	@Override
	public void unbindDestination(IDestination dest) {
		for (Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Integer>>> entry : this.bindings.entrySet()) {
			for (Pair<IDestination, Integer> pair : entry.getValue()) {
				if (pair.getFirst().equals(dest)) {
					entry.getValue().remove(pair);
					entry.getKey().getSecond().remove(pair.getSecond());
					if (entry.getValue().size() == 0) {
						this.bindings.remove(entry.getKey());
					}
					return;
				}
			}
		}
	}

	/**
	 * Gets the destination bound to this target.
	 *
	 * @param target The target a destination is bound to.
	 * @return The destination, or null if this target is not
	 * bound.
	 */
	@Override
	public IDestination getDestination(ITarget target) {
		if (!target.getSelectionObject().getKey().equals(Material.LECTERN.getKey()))
			return null;
		Object[] data = target.getData();
		if (data == null || data.length == 0)
			return null;
		if (!(data[0] instanceof Integer))
			return null;

		for (Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Integer>>> entry : this.bindings.entrySet()) {
			if (entry.getKey().getFirst().equals(target.getSelectionObject())) {
				for (Pair<IDestination, Integer> pair : entry.getValue()) {
					if (pair.getSecond().equals(data[0]))
						return pair.getFirst();
				}
			}
		}
		return null;
	}

	/**
	 * Gets all of the destinations bound with this
	 * ISelectionMethod.
	 *
	 * @param object The selection object.
	 * @return A set of destinations.
	 */
	@Override
	public Set<IDestination> getDestinations(SelectionObject object) {
		if (!object.getKey().equals(Material.LECTERN.getKey()))
			return null;
		Set<IDestination> destinationSet = new HashSet<>();
		for (Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Integer>>> entry : this.bindings.entrySet()) {
			if (entry.getKey().getFirst().equals(object)) {
				for (Pair<IDestination, Integer> pair : entry.getValue())
					destinationSet.add(pair.getFirst());
				return destinationSet;
			}
		}
		return null;
	}

	/**
	 * Gets the target bound to this destination.
	 *
	 * @param dest The destination a target is bound to.
	 * @return The target, or null if this destination is not
	 * bound.
	 */
	@Override
	public ITarget getTarget(IDestination dest) {
		for (Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Integer>>> entry : this.bindings.entrySet()) {
			for (Pair<IDestination, Integer> pair : entry.getValue()) {
				if (pair.getFirst().equals(dest)) {
					return new LecternTarget(entry.getKey().getFirst().getObject(), pair.getSecond());
				}
			}
		}
		return null;
	}

	/**
	 * Gets the selection objects associated with this selection
	 * method.
	 *
	 * @return A set of selection objects, or null if there
	 * are none
	 */
	@Override
	public Set<SelectionObject> getSelectionObjects() {
		Set<SelectionObject> selectionObjects = new HashSet<>();
		for (Pair<SelectionObject, Set<Integer>> pair : this.bindings.keySet())
			selectionObjects.add(pair.getFirst());
		return selectionObjects;
	}

	/**
	 * Checks if <i>object</i> has destinations bound to it by
	 * this ISelectionMethod.
	 *
	 * @param object The selection object
	 * @return True if this selection object is bound, false
	 * if not.
	 */
	@Override
	public boolean isSelectionObjectBound(SelectionObject object) {
		if (!object.getKey().equals(Material.LECTERN.getKey()))
			return false;
		return this.getSelectionObjects().contains(object);
	}

	/**
	 * Checks if the <i>location</i> has a selection object
	 * at it.
	 *
	 * @param location The location
	 * @return True if there is a selection object at the
	 * location, false if not.
	 */
	@Override
	public boolean hasSelectionObject(Location location) {
		if (!location.getBlock().getType().equals(Material.LECTERN))
			return false;
		for (SelectionObject object : this.getSelectionObjects())
			if (object.getLocation().equals(location))
				return true;
		return false;
	}

	/**
	 * Checks if a destination is bound to the target.
	 *
	 * @param target The target a destination is bound to.
	 * @return True if the target is bound to a destination,
	 * false if not.
	 */
	@Override
	public boolean isTargetBound(ITarget target) {
		return this.getDestination(target) != null;
	}

	/**
	 * Checks if a target is bound to the destination.
	 *
	 * @param dest The destination a target is bound to.
	 * @return True if the destination is bound to a target,
	 * false if not.
	 */
	@Override
	public boolean isDestinationBound(IDestination dest) {
		return this.getTarget(dest) != null;
	}

	/**
	 * Gets the appropriate ITarget for this selection method.
	 *
	 * <b>Note:</b> If the ITarget does not support whatever
	 * object <i>object</i> holds ({@link Entity Entity} or
	 * {@link Block Block}) as selections, then this will
	 * return null.
	 *
	 * @param object The {@link SelectionObject SelectionObject}
	 *               used in conjunction with the <i>data</i>.
	 * @param data   Any data needed in conjunction with the
	 *               <i>object</i>.
	 * @return Returns the appropriate ITarget, or null if
	 * the <b>Note</b> above is true.
	 * @throws InvalidSelectionException If the SelectionObject
	 *                                   object is invalid (See Config Description).
	 * @throws InvalidDataTypeException  If the type of data
	 *                                   given doesn't match what the ITarget wants.
	 */
	@Override
	public ITarget getTarget(SelectionObject object, Object... data) throws InvalidSelectionException, InvalidDataTypeException {
		if (data == null || data.length == 0)
			return new LecternTarget(object, null);
		else if (data.length == 1)
			return new LecternTarget(object, (Integer) data[0]);
		else
			throw new InvalidDataTypeException("Given data does not specify to given types.");
	}

	@NotNull
	@Override
	public Iterator<Pair<IDestination, ITarget>> iterator() {
		return new LecternIterator(this);
	}

	@Override
	public void forEach(Consumer<? super Pair<IDestination, ITarget>> action) {
		for (Pair<IDestination, ITarget> pair : this)
			action.accept(pair);
	}
}
