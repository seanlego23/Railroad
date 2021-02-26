package io.github.seanlego23.railroad.stations.selection;

import io.github.seanlego23.railroad.destinations.IDestination;
import io.github.seanlego23.railroad.stations.selectionmethod.ISelectionMethod;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;

/**
 * AbstractSelection class should be extended by all ISelection implementations.
 */
public abstract class AbstractSelection implements ISelection {
	private ISelectionMethod selectionMethod;
	private Set<IDestination> destinations;
	private Set<Location> locations;

	@Override
	public void remove() {

	}
}
