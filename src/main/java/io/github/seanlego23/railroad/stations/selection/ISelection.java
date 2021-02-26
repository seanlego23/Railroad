package io.github.seanlego23.railroad.stations.selection;

import io.github.seanlego23.railroad.util.target.Removable;
import io.github.seanlego23.railroad.destinations.IDestination;
import io.github.seanlego23.railroad.stations.IStation;
import io.github.seanlego23.railroad.stations.selectionmethod.SelectionObject;
import io.github.seanlego23.railroad.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Manages the selection process for which destination a player wants to go to.
 */
public interface ISelection extends Removable {

	@NotNull String getName();

	@Nullable String getDescription();

	void setDescription(@Nullable String description);

	@NotNull IStation getStationOwner();

	/**
	 * Gets the destinations associated with this selection.
	 * @return A Set of destinations
	 */
	@NotNull Set<IDestination> getDestinations();

	@Nullable IDestination getDestination(@NotNull String name);

	@NotNull Set<SelectionObject> getSelectionObjects();

	void select(@NotNull User user, @NotNull SelectionObject object);
}
