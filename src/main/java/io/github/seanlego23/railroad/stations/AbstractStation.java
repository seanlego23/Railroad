package io.github.seanlego23.railroad.stations;

import com.sk89q.worldedit.regions.Region;
import io.github.seanlego23.railroad.line.ILine;


public abstract class AbstractStation implements IStation {
	private final String name;
	private Region region;
	private final ILine track;

	public AbstractStation(String name, Region region, ILine track) {
		this.name = name;
		this.region = region;
		this.track = track;
	}

	/*
	If two stations have the same name and region, then they are effectively the same.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof IStation))
			return false;
		if (!this.getClass().equals(o.getClass()))
			return false;

		IStation other = (IStation) o;
		return this.getName().equals(other.getName()) &&
				this.getRegion().equals(other.getRegion());
	}

	@Override
	public void remove() {

	}
}
