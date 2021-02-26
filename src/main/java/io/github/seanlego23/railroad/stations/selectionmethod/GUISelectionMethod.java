package io.github.seanlego23.railroad.stations.selectionmethod;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import io.github.seanlego23.railroad.util.Pair;
import io.github.seanlego23.railroad.destinations.IDestination;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author seanlego23
 *
 * GUISelection is a selection method where players can click on a chosen type of block or entity inside a station
 */
public class GUISelectionMethod implements ISelectionMethod {

	private static class GUIIterator implements Iterator<Pair<IDestination, ITarget>> {
		private final GUISelectionMethod selection;
		private final Iterator<Map.Entry<Pair<SelectionObject, Set<Integer>>,List<Pair<IDestination, Object[]>>>> iterator;
		private Pair<SelectionObject, Set<Integer>> currentObject;
		private List<Pair<IDestination, Object[]>> currentList;
		private int clIndex;
		private Pair<IDestination, ITarget> lastPair;

		public GUIIterator(GUISelectionMethod selection) {
			this.selection = selection;
			this.iterator = selection.bindings.entrySet().iterator();
			if (this.iterator.hasNext()) {
				Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Object[]>>> entry = this.iterator.next();
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
			Pair<IDestination, Object[]> destinationPair = this.currentList.get(this.clIndex);
			this.lastPair = new Pair<>(destinationPair.getFirst(), new GUITarget(this.currentObject.getFirst(), destinationPair.getSecond()));
			this.clIndex++;
			if (this.clIndex == this.currentList.size() && this.iterator.hasNext()) {
				Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Object[]>>> entry = this.iterator.next();
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
				this.selection.unbindDestination(this.lastPair.getFirst());
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

	@Override
	public @org.jetbrains.annotations.NotNull Iterator<Pair<IDestination, ITarget>> iterator() {
		return new GUIIterator(this);
	}

	@Override
	public void forEach(Consumer<? super Pair<IDestination, ITarget>> action) {
		for (Pair<IDestination, ITarget> pair : this)
			action.accept(pair);
	}

	public static class GUITarget implements ITarget {
		private final SelectionObject selectionObject;
		private Integer inventorySlot;
		private Material inventoryMaterial;

		private GUITarget(@NotNull SelectionObject object, @NotNull Object[] data) {
			this.selectionObject = object;
			this.inventorySlot = (Integer) data[0];
			this.inventoryMaterial = (Material) data[1];
		}

		public GUITarget(@NotNull SelectionObject object, @Nullable Integer inventorySlot, @Nullable Material inventoryMaterial) throws InvalidSelectionException {
			this.selectionObject = object;
			this.inventorySlot = inventorySlot;
			this.inventoryMaterial = inventoryMaterial;
			if (!ISelectionMethod.checkSelection(object.getObject()))
				throw new InvalidSelectionException(object.getName() + " is not a valid selection object.");
		}

		/**
		 * Gets the Location
		 *
		 * @return The Location
		 */
		@Override
		public Location getLocation() {
			return this.selectionObject.getLocation();
		}

		/**
		 * Gets the data
		 *
		 * @return The data
		 */
		@Override
		public Object[] getData() {
			return new Object[] {this.inventorySlot, this.inventoryMaterial};
		}

		/**
		 * Gets the object this ITarget targets.
		 *
		 * @return The object
		 */
		@Override
		public SelectionObject getSelectionObject() {
			return this.selectionObject;
		}

		/**
		 * Sets the data
		 * @param data the new data.
		 * @throws InvalidDataTypeException If the type of data given
		 *                                  doesn't match what the ITarget
		 *                                  wants.
		 * @throws NullPointerException If data or any of the
		 * 		                        objects in the array are null.
		 */
		@Override
		public void setData(@NotNull Object... data) throws InvalidDataTypeException {
			if (data.length > 2 || data.length < 1)
				throw new InvalidDataTypeException("Given data does not specify to given types. (See ISelectionMethod.ITarget.getDataTypes)");
			if (data[0] instanceof Integer)
				this.inventorySlot = (Integer) data[0];
			else if (data[0] == null)
				throw new NullPointerException("data[0] is null.");
			else
				throw new InvalidDataTypeException(data[0].getClass().getName() + " is not of type Integer.");

			if (data.length == 2) {
				if (data[1] instanceof Material)
					this.inventoryMaterial = (Material) data[1];
				else if (data[1] == null)
					throw new NullPointerException("data[1] is null.");
				else
					throw new InvalidDataTypeException(data[1].getClass().getName() + " is not of type Material.");
			}
		}

		/**
		 * Gets the class types for the data this ITarget wants.
		 * @return The class types.
		 */
		@Override
		public Class<?>[] getDataTypes() {
			return new Class<?>[] {Integer.class, Material.class};
		}

		/**
		 * Returns whether this ITarget accepts entities as a target.
		 *
		 * @return Whether entities are acceptable.
		 */
		@Override
		public boolean acceptsEntity() {
			return true;
		}

		/**
		 * Returns whether this ITarget accepts blocks as a target.
		 *
		 * @return Whether blocks are acceptable.
		 */
		@Override
		public boolean acceptsBlock() {
			return true;
		}
	}

	private NamespacedKey itemType;
	private final Map<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Object[]>>> bindings;

	/**
	 * Creates a new instance of the GUISelectionMethod class.
	 */
	public GUISelectionMethod() {
		this.itemType = null;
		this.bindings = new HashMap<>();
	}

	/**
	 * Gets the selection item type used to select destinations.
	 *
	 * @return The selection item type.
	 */
	@Override
	public NamespacedKey getSelectionObjectType() {
		return this.itemType;
	}

	/**
	 * Binds a destination to a target.
	 *
	 * @param dest   The destination to bind.
	 * @param target The target to bind.
	 * @return True if the destination was bounded, false if not.
	 * @throws InvalidSelectionException If the selection item specified by <i>target</i> does not equal the type returned by {@link ISelectionMethod#getSelectionObjectType() ISelectionMethod.getMethodItemType}.
	 */
	@Override
	public boolean bindDestination(IDestination dest, ITarget target) throws InvalidSelectionException {
		SelectionObject object = target.getSelectionObject();
		if (this.itemType == null) {
			if (!ISelectionMethod.checkSelection(object.getObject()))
				throw new InvalidSelectionException(object.getName() + " is not a valid selection object.");
			this.itemType = object.getKey();
		} else if (!itemType.equals(object.getKey()))
			throw new InvalidSelectionException(object.getObject().getClass() + " is not of type " + itemType.toString());
		if (this.isDestinationBound(dest))
			return false;
		Pair<SelectionObject, Set<Integer>> thisObjectPair = null;
		for (Pair<SelectionObject, Set<Integer>> pair : this.bindings.keySet()) {
			if (pair.getFirst().getLocation().equals(object.getLocation())) {
				thisObjectPair = pair;
				break;
			}
		}

		Object[] data = target.getData();
		if (thisObjectPair == null) {
			if (data == null) {
				try {
					target.setData(0, Material.STONE);
				} catch (InvalidDataTypeException e) {
					return false;
				}
			} else if (data.length == 1) {
				try {
					target.setData(data[0], Material.STONE);
				} catch (InvalidDataTypeException e) {
					return false;
				}
			} else if (data.length != 2)
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
					target.setData(finalSlot, Material.STONE);
				} catch (InvalidDataTypeException e) {
					return false;
				}
			} else if (data.length == 1 || data.length == 2) {
				for (Integer next : thisObjectPair.getSecond())
					if (data[0] == next)
						return false;
				if (data.length == 1) {
					try {
						target.setData(data[0], Material.STONE);
					} catch (InvalidDataTypeException e) {
						return false;
					}
				}
			} else
				return false;
		}

		if (thisObjectPair == null)
			this.bindings.put(new Pair<>(target.getSelectionObject(),new TreeSet<>(Collections.singletonList((Integer) target.getData()[0]))), new ArrayList<>(Collections.singletonList(new Pair<>(dest, target.getData()))));
		else {
			this.bindings.get(thisObjectPair).add(new Pair<>(dest, target.getData()));
			thisObjectPair.getSecond().add((Integer) target.getData()[0]);
		}

		return true;
	}

	/**
	 * Unbinds a destination to a target.
	 *
	 * @param dest The destination to unbind.
	 */
	@Override
	public void unbindDestination(IDestination dest) {
		for (Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Object[]>>> entry : this.bindings.entrySet()) {
			for (Pair<IDestination, Object[]> pair : entry.getValue()) {
				if (pair.getFirst().equals(dest)) {
					entry.getValue().remove(pair);
					Integer oldSlot = (Integer) pair.getSecond()[0];
					entry.getKey().getSecond().remove(oldSlot);
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
	 * @return The destination, or null if this target is not bound.
	 */
	@Override
	public IDestination getDestination(ITarget target) {
		if (!itemType.equals(target.getSelectionObject().getKey()))
			return null;
		for (Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Object[]>>> entry : this.bindings.entrySet()) {
			if (entry.getKey().getFirst().equals(target.getSelectionObject())) {
				for (Pair<IDestination, Object[]> pair : entry.getValue()) {
					Object[] data = pair.getSecond();
					Object[] targetData = target.getData();
					if (data[0].equals(targetData[0]) && data[1].equals(targetData[1]))
						return pair.getFirst();
				}
			}
		}
		return null;
	}

	/**
	 * Gets all of the destinations bound with this ISelectionMethod.
	 *
	 * @return A set of destinations.
	 */
	@Override
	public Set<IDestination> getDestinations(SelectionObject object) {
		if (this.itemType.equals(object.getKey()))
			return null;
		Set<IDestination> destinationSet = null;
		for (Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Object[]>>> entry : this.bindings.entrySet()) {
			if (object.equals(entry.getKey().getFirst())) {
				destinationSet = new HashSet<>();
				for (Pair<IDestination, Object[]> pair : entry.getValue())
					destinationSet.add(pair.getFirst());
				break;
			}
		}
		return destinationSet;
	}

	/**
	 * Gets the target bound to this destination.
	 *
	 * @param dest The destination a target is bound to.
	 * @return The target, or null if this destination is not bound.
	 */
	@Override
	public ITarget getTarget(IDestination dest) {
		for (Map.Entry<Pair<SelectionObject, Set<Integer>>, List<Pair<IDestination, Object[]>>> entry : this.bindings.entrySet()) {
			for (Pair<IDestination, Object[]> pair : entry.getValue()) {
				if (dest.equals(pair.getFirst()))
					return new GUITarget(entry.getKey().getFirst(), pair.getSecond());
			}
		}
		return null;
	}

	/**
	 * Gets the selection objects associated with this selection method.
	 *
	 * @return A set of selection objects, or null if there are none
	 */
	@Override
	public Set<SelectionObject> getSelectionObjects() {
		Set<SelectionObject> selectionObjects = new HashSet<>();
		for (Pair<SelectionObject, Set<Integer>> pair : this.bindings.keySet())
			selectionObjects.add(pair.getFirst());
		return selectionObjects;
	}

	/**
	 * Checks if <i>object</i> has destinations bound to it by this ISelectionMethod.
	 *
	 * @param object The selection object
	 * @return True if this selection object is bound, false if not.
	 */
	@Override
	public boolean isSelectionObjectBound(SelectionObject object) {
		if (!this.itemType.equals(object.getKey()))
			return false;
		return this.getSelectionObjects().contains(object);
	}

	/**
	 * Checks if the <i>location</i> has a selection object at it.
	 *
	 * @param location The location
	 * @return True if there is a selection object at the location, false if not.
	 */
	@Override
	public boolean hasSelectionObject(Location location) {
		if (!location.getBlock().getType().getKey().equals(this.itemType))
			return false;
		for (SelectionObject object : this.getSelectionObjects())
			if (object.getLocation().equals(location))
				return true;
		return false;
	}

	/**
	 * Checks if a destination is bound to a target.
	 *
	 * @param target The target a destination is bound to.
	 * @return True if the target is bound to a destination, false if not.
	 */
	@Override
	public boolean isTargetBound(ITarget target) {
		return this.getDestination(target) != null;
	}

	/**
	 * Checks if a target is bound to the destination.
	 *
	 * @param dest The destination a target is bound to.
	 * @return True if the destination is bound to a target, false if not.
	 */
	@Override
	public boolean isDestinationBound(IDestination dest) {
		return this.getTarget(dest) != null;
	}

	/**
	 * Gets the appropriate ITarget using an entity.
	 *
	 * <b>Note:</b> If the ITarget does not support whatever object <i>object</i>
	 * holds ({@link Entity Entity} or {@link Block Block}) as selections, then
	 * this will return null.
	 *
	 * @param object The {@link SelectionObject SelectionObject} used in conjunction with the <i>data</i>.
	 * @param data   Any data needed in conjunction with the <i>object</i>.
	 * @return Returns the appropriate ITarget, or null if the <b>Note</b> above is true.
	 * @throws InvalidSelectionException If the SelectionObject object is invalid (See Config Description).
	 * @throws InvalidDataTypeException  If the type of data given doesn't match what the ITarget wants.
	 */
	@Override
	public ITarget getTarget(SelectionObject object, Object... data) throws InvalidSelectionException, InvalidDataTypeException {
		if (data == null || data.length == 0) {
			return new GUITarget(object, null, null);
		} else if (data.length == 1) {
			return new GUITarget(object, (Integer) data[0], null);
		} else if (data.length == 2) {
			return new GUITarget(object, (Integer) data[0], (Material) data[1]);
		} else
			throw new InvalidDataTypeException("Given data does not specify to given types.");
	}
}
