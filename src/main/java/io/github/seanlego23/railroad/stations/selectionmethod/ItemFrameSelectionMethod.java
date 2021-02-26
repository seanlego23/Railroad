package io.github.seanlego23.railroad.stations.selectionmethod;

import io.github.seanlego23.railroad.util.Pair;
import io.github.seanlego23.railroad.destinations.IDestination;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class ItemFrameSelectionMethod implements ISelectionMethod {

	private static class ItemFrameIterator implements Iterator<Pair<IDestination, ITarget>> {
		private final ItemFrameSelectionMethod selectionMethod;
		private final Iterator<Map.Entry<Pair<SelectionObject, Material>, IDestination>> iterator;
		private Pair<IDestination, ITarget> lastPair;

		public ItemFrameIterator(ItemFrameSelectionMethod method) {
			this.selectionMethod = method;
			this.iterator = method.bindings.entrySet().iterator();
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public Pair<IDestination, ITarget> next() {
			Map.Entry<Pair<SelectionObject, Material>, IDestination> entry = iterator.next();
			return this.lastPair = new Pair<>(entry.getValue(), new ItemFrameTarget(entry.getKey().getFirst(), new Object[] {entry.getKey().getSecond()}));
		}

		@Override
		public void remove() {
			this.selectionMethod.unbindDestination(this.lastPair.getFirst());
		}

		@Override
		public void forEachRemaining(Consumer<? super Pair<IDestination, ITarget>> action) {
			while (this.hasNext())
				action.accept(this.next());
		}
	}

	public static class ItemFrameTarget implements ITarget {
		private final SelectionObject selectionObject;
		private Material item;

		private ItemFrameTarget(SelectionObject object, Object[] data) {
			this.selectionObject = object;
			this.item = (Material) data[0];
		}

		public ItemFrameTarget(SelectionObject object, Material item) throws InvalidSelectionException {
			this.selectionObject = object;
			this.item = item;
			if (!object.getKey().equals(EntityType.ITEM_FRAME.getKey()))
				throw new InvalidSelectionException(object.getName() + " is not of type ItemFrame.");
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
			return new Object[] {this.item};
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
		 * @throws NullPointerException     If data or any of the
		 *                                  objects in the array are null.
		 */
		@Override
		public void setData(Object... data) throws InvalidDataTypeException {
			if (data.length != 1)
				throw new InvalidDataTypeException("Given data does not specify to given types.");
			if (!(data[0] instanceof Material))
				throw new InvalidDataTypeException(data[0].getClass().getName() + " is not of type Material.");
			this.item = (Material) data[0];
		}

		/**
		 * Gets the class types for the data this ITarget wants.
		 *
		 * @return The class types.
		 */
		@Override
		public Class<?>[] getDataTypes() {
			return new Class[] {Material.class};
		}

		/**
		 * Returns whether this ITarget accepts entities as
		 * a target.
		 *
		 * @return Whether entities are acceptable.
		 */
		@Override
		public boolean acceptsEntity() {
			return true;
		}

		/**
		 * Returns whether this ITarget accepts blocks as a
		 * target.
		 *
		 * @return Whether blocks are acceptable.
		 */
		@Override
		public boolean acceptsBlock() {
			return false;
		}
	}

	private final Map<Pair<SelectionObject, Material>, IDestination> bindings;

	public ItemFrameSelectionMethod() {
		this.bindings = new HashMap<>();
	}

	/**
	 * Gets the selection object type used to select destinations.
	 *
	 * @return The selection item type.
	 */
	@Override
	public NamespacedKey getSelectionObjectType() {
		return EntityType.ITEM_FRAME.getKey();
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
		if (!target.getSelectionObject().getKey().equals(EntityType.ITEM_FRAME.getKey()))
			throw new InvalidSelectionException(target.getSelectionObject().getName() + " is not an Item Frame.");
		Object[] data = target.getData();
		if (data == null || data.length != 1)
			return false;
		if (!(data[0] instanceof Material))
			return false;
		if (this.isSelectionObjectBound(target.getSelectionObject()))
			return false;
		if (this.isDestinationBound(dest))
			return false;
		Material material = (Material) data[0];
		this.bindings.put(new Pair<>(target.getSelectionObject(), material), dest);
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
		for (Map.Entry<Pair<SelectionObject, Material>, IDestination> entry : this.bindings.entrySet())
			if (entry.getValue().equals(dest))
				this.bindings.remove(entry.getKey());
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
		if (!target.getSelectionObject().getKey().equals(EntityType.ITEM_FRAME.getKey()))
			return null;
		for (Map.Entry<Pair<SelectionObject, Material>, IDestination> entry : this.bindings.entrySet()) {
			if (entry.getKey().getFirst().equals(target.getSelectionObject()) &&
				entry.getKey().getSecond().equals(target.getData()[0]))
				return entry.getValue();
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
		if (!object.getKey().equals(EntityType.ITEM_FRAME.getKey()))
			return null;
		for (Map.Entry<Pair<SelectionObject, Material>, IDestination> entry : this.bindings.entrySet())
			if (entry.getKey().getFirst().equals(object))
				return new HashSet<>(Collections.singleton(entry.getValue()));
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
		for (Map.Entry<Pair<SelectionObject, Material>, IDestination> entry : this.bindings.entrySet())
			if (entry.getValue().equals(dest))
				return new ItemFrameTarget(entry.getKey().getFirst(), new Object[] {entry.getKey().getSecond()});
		return null;
	}

	/**
	 * Gets the selection objects associated with this selection
	 * method.
	 *
	 * @return A set of selection objects, or an empty set if
	 * there are none.
	 */
	@Override
	public Set<SelectionObject> getSelectionObjects() {
		Set<SelectionObject> selectionObjects = new HashSet<>();
		for (Pair<SelectionObject, Material> pair : this.bindings.keySet())
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
			return new ItemFrameTarget(object, (Material) null);
		else if (data.length == 1)
			return new ItemFrameTarget(object, (Material) data[0]);
		else
			throw new InvalidDataTypeException("Given data does not specify to given types.");
	}

	@NotNull
	@Override
	public Iterator<Pair<IDestination, ITarget>> iterator() {
		return new ItemFrameIterator(this);
	}

	@Override
	public void forEach(Consumer<? super Pair<IDestination, ITarget>> action) {
		for (Pair<IDestination, ITarget> pair : this)
			action.accept(pair);
	}
}
