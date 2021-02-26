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

import java.util.*;
import java.util.function.Consumer;

public class ButtonSelectionMethod implements ISelectionMethod {

	private static class ButtonIterator implements Iterator<Pair<IDestination, ITarget>> {
		private final ButtonSelectionMethod method;
		private final Iterator<Map.Entry<SelectionObject, IDestination>> iterator;
		private Pair<IDestination, ITarget> lastPair;

		public ButtonIterator(ButtonSelectionMethod method) {
			this.method = method;
			this.iterator = method.bindings.entrySet().iterator();
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public Pair<IDestination, ITarget> next() {
			Map.Entry<SelectionObject, IDestination> entry = this.iterator.next();
			this.lastPair = new Pair<>(entry.getValue(), new ButtonTarget(entry.getKey().getObject()));
			return this.lastPair;
		}

		@Override
		public void remove() {
			if (this.lastPair != null) {
				this.method.unbindDestination(this.lastPair.getFirst());
				this.lastPair = null;
			}
		}

		@Override
		public void forEachRemaining(Consumer<? super Pair<IDestination, ITarget>> action) {
			while (this.hasNext())
				action.accept(this.next());
		}
	}

	public static class ButtonTarget implements ITarget {
		private final SelectionObject selectionObject;

		private ButtonTarget(Metadatable object) {
			if (object instanceof Entity)
				this.selectionObject = new SelectionObject((Entity) object);
			else
				this.selectionObject = new SelectionObject((Block) object);
		}

		public ButtonTarget(SelectionObject object) throws InvalidSelectionException {
			if (!ButtonSelectionMethod.isButton(object.getKey()))
				throw new InvalidSelectionException(object.getName() + " is not of type Button.");
			this.selectionObject = object;
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
			return null;
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
			throw new InvalidDataTypeException("ButtonTarget class does not take any data.");
		}

		/**
		 * Gets the class types for the data this ITarget wants.
		 *
		 * @return The class types.
		 */
		@Override
		public Class<?>[] getDataTypes() {
			return null;
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

	private final Map<SelectionObject, IDestination> bindings;

	public ButtonSelectionMethod() {
		this.bindings = new HashMap<>();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isButton(NamespacedKey key) {
		return Material.ACACIA_BUTTON.getKey().equals(key) ||
				Material.BIRCH_BUTTON.getKey().equals(key) ||
				Material.CRIMSON_BUTTON.getKey().equals(key) ||
				Material.DARK_OAK_BUTTON.getKey().equals(key) ||
				Material.JUNGLE_BUTTON.getKey().equals(key) ||
				Material.OAK_BUTTON.getKey().equals(key) ||
				Material.POLISHED_BLACKSTONE_BUTTON.getKey().equals(key) ||
				Material.SPRUCE_BUTTON.getKey().equals(key) ||
				Material.STONE_BUTTON.getKey().equals(key) ||
				Material.WARPED_BUTTON.getKey().equals(key);
	}

	/**
	 * Gets the selection object type used to select destinations.
	 *
	 * @return The selection item type.
	 */
	@Override
	public NamespacedKey getSelectionObjectType() {
		return Material.STONE_BUTTON.getKey();
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
		if (!ButtonSelectionMethod.isButton(target.getSelectionObject().getKey()))
			throw new InvalidSelectionException(target.getSelectionObject().getName() + " is not a Button type.");
		if (this.bindings.containsKey(target.getSelectionObject()))
			return false;
		if (this.bindings.containsValue(dest))
			return false;
		this.bindings.put(target.getSelectionObject(), dest);
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
		for (Map.Entry<SelectionObject, IDestination> entry : this.bindings.entrySet())
			if (entry.getValue().equals(dest)) {
				this.bindings.remove(entry.getKey());
				break;
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
		if (!ButtonSelectionMethod.isButton(target.getSelectionObject().getKey()))
			return null;
		for (Map.Entry<SelectionObject, IDestination> entry : this.bindings.entrySet())
			if (entry.getKey().equals(target.getSelectionObject()))
				return entry.getValue();
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
		return new HashSet<>(Collections.singleton(this.bindings.get(object)));
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
		for (Map.Entry<SelectionObject, IDestination> entry : this.bindings.entrySet())
			if (entry.getValue().equals(dest))
				return new ButtonTarget(entry.getKey().getObject());
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
		return this.bindings.keySet();
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
		if (!ButtonSelectionMethod.isButton(location.getBlock().getType().getKey()))
			return false;
		for (SelectionObject selectionObject : this.getSelectionObjects())
			if (selectionObject.getLocation().equals(location))
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
		return this.isSelectionObjectBound(target.getSelectionObject());
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
		return this.bindings.containsValue(dest);
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
	 */
	@Override
	public ITarget getTarget(SelectionObject object, Object... data) throws InvalidSelectionException {
		return new ButtonTarget(object);
	}

	@NotNull
	@Override
	public Iterator<Pair<IDestination, ITarget>> iterator() {
		return new ButtonIterator(this);
	}

	@Override
	public void forEach(Consumer<? super Pair<IDestination, ITarget>> action) {
		for (Pair<IDestination, ITarget> pair : this)
			action.accept(pair);
	}
}
