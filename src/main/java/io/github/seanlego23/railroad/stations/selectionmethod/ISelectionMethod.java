package io.github.seanlego23.railroad.stations.selectionmethod;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import io.github.seanlego23.railroad.util.Pair;
import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.destinations.IDestination;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.metadata.Metadatable;

import java.util.Set;

/**
 * @author seanlego23
 *
 * Interface to different selection methods.
 *
 * A selection method is how a player can choose which
 * destination they would like to go to.
 */
public interface ISelectionMethod extends Iterable<Pair<IDestination, ISelectionMethod.ITarget>> {
//GUI, Lectern, Buttons, Item Frame

	/**
	 * @author seanlego23
	 *
	 * Interface for the selection target for a destination.
	 * The Target holds the info on what Minecraft object you
	 * would like to bind a destination to and any data
	 * relating to that object.
	 */
	interface ITarget {

		/**
		 * Gets the location
		 *
		 * @return The location
		 */
		@NotNull Location getLocation();

		/**
		 * Gets the data
		 *
		 * <b>Note:</b> This method guarantees that it won't
		 * return null, but the elements of the list might be
		 * null.
		 *
		 * @return The data
		 */
		@NotNull Object[] getData();

		/**
		 * Gets the {@link SelectionObject SelectionObject}
		 * associated with this ITarget.
		 *
		 * @return The object
		 */
		@NotNull SelectionObject getSelectionObject();

		/**
		 * Sets the data
		 *
		 * @param data the new data.
		 * @throws InvalidDataTypeException If the type of
		 * data given doesn't match what the ITarget wants
		 * (See {@link ITarget#getDataTypes() getDataTypes}).
		 * @throws NullPointerException If data or any of the
		 * objects in the array are null.
		 */
		void setData(@NotNull Object... data) throws InvalidDataTypeException;

		/**
		 * Gets the class types for the data this ITarget wants.
		 *
		 * @return The class types.
		 */
		@NotNull Class<?>[] getDataTypes();

		/**
		 * Returns whether this ITarget accepts entities as
		 * a target.
		 *
		 * @return Whether entities are acceptable.
		 */
		boolean acceptsEntity();

		/**
		 * Returns whether this ITarget accepts blocks as a
		 * target.
		 *
		 * @return Whether blocks are acceptable.
		 */
		boolean acceptsBlock();
	}

	/**
	 * Gets the selection object type used to select destinations.
	 * Returns null if the implementation doesn't have a specified
	 * selection object type until the first selection object is bound.
	 *
	 * @return The selection item type.
	 */
	@Nullable
	NamespacedKey getSelectionObjectType();

	//TODO: Consider adding InvalidDataTypeException
	/**
	 * Binds a destination to a target. Once a destination
	 * is bound in an ISelection, it cannot be bound
	 * again unless unbound via {@link ISelectionMethod#unbindDestination
	 * unbindDestination}.
	 *
	 * @param dest The destination to bind.
	 * @param target The target to bind.
	 * @return True if the destination was bounded, false if
	 * not.
	 * @throws InvalidSelectionException If the selection
	 * item specified by <i>target</i> does not equal the
	 * type returned by {@link ISelectionMethod#getSelectionObjectType()
	 * ISelectionMethod.getMethodItemType}.
	 */
	boolean bindDestination(@NotNull IDestination dest, @NotNull ITarget target) throws InvalidSelectionException;

	/**
	 * Unbinds a destination to a target.
	 *
	 * If it is the last destination bound to a selection
	 * object, the selection object will be removed from
	 * this ISelectionMethod.
	 *
	 * @param dest The destination to unbind.
	 */
	void unbindDestination(@NotNull IDestination dest);

	/**
	 * Gets the destination bound to this target.
	 *
	 * @param target The target a destination is bound to.
	 * @return The destination, or null if this target is not
	 * bound.
	 */
	@Nullable IDestination getDestination(@NotNull ITarget target);

	/**
	 * Gets all of the destinations bound with this
	 * ISelectionMethod.
	 *
	 * @param object The selection object.
	 * @return A set of destinations.
	 */
	@Nullable Set<IDestination> getDestinations(@NotNull SelectionObject object);

	/**
	 * Gets the target bound to this destination.
	 *
	 * @param dest The destination a target is bound to.
	 * @return The target, or null if this destination is not
	 * bound.
	 */
	@Nullable ITarget getTarget(@NotNull IDestination dest);

	/**
	 * Gets the selection objects associated with this selection
	 * method.
	 *
	 * @return A set of selection objects, or an empty set if
	 * there are none.
	 */
	@NotNull Set<SelectionObject> getSelectionObjects();

	/**
	 * Checks if <i>object</i> has destinations bound to it by
	 * this ISelectionMethod.
	 *
	 * @param object The selection object
	 * @return True if this selection object is bound, false
	 * if not.
	 */
	boolean isSelectionObjectBound(@NotNull SelectionObject object);

	/**
	 * Checks if the <i>location</i> has a selection object
	 * at it.
	 *
	 * @param location The location
	 * @return True if there is a selection object at the
	 * location, false if not.
	 */
	boolean hasSelectionObject(@NotNull Location location);

	/**
	 * Checks if a destination is bound to the target.
	 *
	 * @param target The target a destination is bound to.
	 * @return True if the target is bound to a destination,
	 * false if not.
	 */
	boolean isTargetBound(@NotNull ITarget target);

	/**
	 * Checks if a target is bound to the destination.
	 *
	 * @param dest The destination a target is bound to.
	 * @return True if the destination is bound to a target,
	 * false if not.
	 */
	boolean isDestinationBound(@NotNull IDestination dest);

	/**
	 * Gets the appropriate ITarget for this selection method.
	 *
	 * <b>Note:</b> If the ITarget does not support whatever
	 * object <i>object</i> holds ({@link Entity Entity} or
	 * {@link Block Block}) as selections, then this will
	 * return null.
	 *
	 * @param object The {@link SelectionObject SelectionObject}
	 * used in conjunction with the <i>data</i>.
	 * @param data Any data needed in conjunction with the
	 * <i>object</i>.
	 * @return Returns the appropriate ITarget, or null if
	 * the <b>Note</b> above is true.
	 * @throws InvalidSelectionException If the SelectionObject
	 * object is invalid (See Config Description).
	 * @throws InvalidDataTypeException If the type of data
	 * given doesn't match what the ITarget wants.
	 */
	@Nullable ITarget getTarget(@NotNull SelectionObject object, @Nullable Object... data) throws InvalidSelectionException, InvalidDataTypeException;

	/**
	 * Checks if <i>object</i> is a valid selection object.
	 *
	 * @param object Selection object (Must extend
	 * {@link Entity Entity} or {@link Block Block}).
	 * @return True if <i>object</i> is a valid selection
	 * object, false if not.
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	static boolean checkSelection(@NotNull Metadatable object) {
		World objectWorld;
		NamespacedKey objectKey;
		if (object instanceof Entity) {
			if (object instanceof Projectile)
				return false;
			if (object instanceof Item || object instanceof ExperienceOrb)
				return false;
			if (object instanceof EvokerFangs || object instanceof AreaEffectCloud || object instanceof FallingBlock || object instanceof TNTPrimed)
				return false;
			if (object instanceof LightningStrike || object instanceof Player)
				return false;
			objectKey = ((Entity) object).getType().getKey();
			objectWorld = ((Entity)object).getWorld();

		} else if (object instanceof Block) {
			Material material = ((Block) object).getType();
			if (material.isAir() || material == Material.BARRIER)
				return false;
			objectKey = material.getKey();
			objectWorld = ((Block)object).getWorld();
		} else
			return false;

		for (NamespacedKey key : Railroad.getPlugin(Railroad.class).getSettings().getMainBlacklist())
			if (key.equals(objectKey))
				return false;
		for (NamespacedKey key : Railroad.getPlugin(Railroad.class).getWorldManager().getWorld(objectWorld).getSettings().getBlacklist())
			if (key.equals(objectKey))
				return false;
		return true;
	}
}
