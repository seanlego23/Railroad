package io.github.seanlego23.railroad.track;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.RailroadException;
import io.github.seanlego23.railroad.connection.Connector;
import io.github.seanlego23.railroad.connection.IllegalConnectionException;
import io.github.seanlego23.railroad.destinations.IDestination;
import io.github.seanlego23.railroad.stations.IStation;
import io.github.seanlego23.railroad.util.target.IncorrectRUIDTypeException;
import io.github.seanlego23.railroad.util.target.RUID;
import io.github.seanlego23.railroad.util.world.Direction;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Specifies a junction between rails.
 */
public final class Junction implements Connector, ITrackStop {

	private final RUID ruid;
	private final Location location;
	private final RUID firstConnector;
	private final RUID secondConnector;
	private final RUID thirdConnector;
	private final RUID fourthConnector;
	private final Rail rail;
	private final JunctionType junctionType;
	private Set<RUID> firstTrackStops;
	private Set<RUID> secondTrackStops;
	private Set<RUID> thirdTrackStops;
	private Set<RUID> fourthTrackStops;
	private Rail.Shape currentShape;
	private Rail.Shape defaultShape;

	private static final long serialVersionUID = -420454754446389452L;
	private static Field ruidField;
	private static Field locField;
	private static Field railField;
	private static Field junctionTypeField;
	private static Field firstConnectorField;
	private static Field secondConnectorField;
	private static Field thirdConnectorField;
	private static Field fourthConnectorField;

	static {
		try {
			ruidField = Junction.class.getDeclaredField("ruid");
			locField = Junction.class.getDeclaredField("location");
			railField = Junction.class.getDeclaredField("rail");
			junctionTypeField = Junction.class.getDeclaredField("junctionType");
			firstConnectorField = Junction.class.getDeclaredField("firstConnector");
			secondConnectorField = Junction.class.getDeclaredField("secondConnector");
			thirdConnectorField = Junction.class.getDeclaredField("thirdConnector");
			fourthConnectorField = Junction.class.getDeclaredField("fourthConnector");
			ruidField.setAccessible(true);
			locField.setAccessible(true);
			railField.setAccessible(true);
			junctionTypeField.setAccessible(true);
			firstConnectorField.setAccessible(true);
			secondConnectorField.setAccessible(true);
			thirdConnectorField.setAccessible(true);
			fourthConnectorField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public Junction() {
		this.ruid = null;
		this.location = null;
		this.rail = null;
		this.junctionType = null;
		this.firstConnector = null;
		this.secondConnector = null;
		this.thirdConnector = null;
		this.fourthConnector = null;
	}

	public Junction(@NotNull Location location, @NotNull Connector firstConnector, @NotNull Connector secondConnector,
					@NotNull Connector thirdConnector) throws InvalidJunctionLocationException {
		this.ruid = new RUID(this);
		this.location = location;
		this.rail = new Rail(location, Rail.RailType.NORMAL, Direction.HERE);
		this.firstConnector = firstConnector.getID();
		this.secondConnector = secondConnector.getID();
		this.thirdConnector = thirdConnector.getID();
		this.fourthConnector = null;


		this.getStops(firstConnector, secondConnector, thirdConnector, null);
		this.junctionType = JunctionType.createJunctionType(location, firstConnector, secondConnector, thirdConnector,
				null);
		Rail.Shape shape = this.junctionType.centerShape(firstConnector.getID(), secondConnector.getID());
		if (shape == null)
			shape = this.junctionType.centerShape(firstConnector.getID(), thirdConnector.getID());
		this.defaultShape = shape;
		this.currentShape = shape;

		//set center rail
	}

	public Junction(@NotNull Location location, @NotNull Connector firstConnector, @NotNull Connector secondConnector,
					@NotNull Connector thirdConnector, @NotNull Connector fourthConnector)
			throws InvalidJunctionLocationException {
		this.ruid = new RUID(this);
		this.location = location;
		this.rail = new Rail(location, Rail.RailType.NORMAL, Direction.HERE);
		this.firstConnector = firstConnector.getID();
		this.secondConnector = secondConnector.getID();
		this.thirdConnector = thirdConnector.getID();
		this.fourthConnector = fourthConnector.getID();

		this.getStops(firstConnector, secondConnector, thirdConnector, fourthConnector);
		this.junctionType = JunctionType.createJunctionType(location, firstConnector, secondConnector, thirdConnector,
				fourthConnector);
		Rail.Shape shape = this.junctionType.centerShape(firstConnector.getID(), secondConnector.getID());
		if (shape == null) {
			shape = this.junctionType.centerShape(firstConnector.getID(), thirdConnector.getID());
			if (shape == null)
				shape = this.junctionType.centerShape(firstConnector.getID(), fourthConnector.getID());
		}
		this.defaultShape = shape;
		this.currentShape = shape;

		//set center rail
	}

	private void getStops(Connector first, Connector second, Connector third, Connector fourth) {
		this.firstTrackStops = new HashSet<>();
		this.secondTrackStops = new HashSet<>();
		this.thirdTrackStops = new HashSet<>();
		this.fourthTrackStops = new HashSet<>();

		if (first instanceof ITrack) {
			ITrackStop firstStopStart = ((ITrack) first).getConnection().getStopAtStart();
			ITrackStop firstStopEnd = ((ITrack) first).getConnection().getStopAtEnd();
			ITrackStop otherEnd = firstStopStart == null ? firstStopEnd : firstStopStart;

			if (otherEnd instanceof Junction) {
				this.firstTrackStops.addAll(((Junction) otherEnd).getFirstTrackStops());
				this.firstTrackStops.addAll(((Junction) otherEnd).getSecondTrackStops());
				this.firstTrackStops.addAll(((Junction) otherEnd).getThirdTrackStops());
				this.firstTrackStops.addAll(((Junction) otherEnd).getFourthTrackStops());
			} else if (otherEnd instanceof IDestination || otherEnd instanceof IStation)
				this.firstTrackStops.add(otherEnd.getID());
		} else if (first instanceof Junction) {
			//Need a better way to find out which side of the Junction this Junction is.
			this.firstTrackStops.addAll(((Junction) first).getFirstTrackStops());
		}
	}

	public Location getLocation() {
		return this.location;
	}

	public Rail.Shape getDefaultShape() {
		return this.defaultShape;
	}

	protected void setDefaultShape(Rail.Shape shape) {
		this.defaultShape = shape;
	}

	public RUID getFirstConnector() {
		return this.firstConnector;
	}

	public RUID getSecondConnector() {
		return this.secondConnector;
	}

	public RUID getThirdConnector() {
		return this.thirdConnector;
	}

	public @Nullable RUID getFourthConnector() {
		return this.fourthConnector;
	}

	public Set<RUID> getFirstTrackStops() {
		return new HashSet<>(this.firstTrackStops);
	}

	public Set<RUID> getSecondTrackStops() {
		return new HashSet<>(this.secondTrackStops);
	}

	public Set<RUID> getThirdTrackStops() {
		return new HashSet<>(this.thirdTrackStops);
	}

	public Set<RUID> getFourthTrackStops() {
		return new HashSet<>(this.fourthTrackStops);
	}

	public HalfJunction getInfoExcluding(RUID from) {
		List<RUID> connectors = new ArrayList<>(Arrays.asList(this.firstConnector, this.secondConnector,
				this.thirdConnector, this.fourthConnector));
		List<Set<RUID>> stops = new ArrayList<>(Arrays.asList(this.firstTrackStops, this.secondTrackStops,
				this.thirdTrackStops, this.fourthTrackStops));
		if (connectors.get(3) == null) {
			connectors.remove(3);
			stops.remove(3);
		}
		boolean found = false;
		for (int i = 0; i < connectors.size(); i++) {
			RUID connector = connectors.get(i);
			if (connector.equals(from)) {
				connectors.remove(connector);
				stops.remove(i);
				found = true;
				break;
			}
		}

		if (!found)
			return null;
		if (connectors.size() == 2)
			return new HalfJunction(this.location, this.junctionType,
									connectors.get(0), connectors.get(1), null,
									stops.get(0), stops.get(1), new HashSet<>());
		else
			return new HalfJunction(this.location, this.junctionType,
									connectors.get(0), connectors.get(1), connectors.get(2),
									stops.get(0), stops.get(1), stops.get(2));
	}

	protected boolean addTrackStop(int order, @NotNull ITrackStop stop) {
		return this.addTrackStop(order, stop.getID());
	}

	protected boolean addTrackStop(int order, @NotNull RUID id) {
		switch (order) {
			case 1:
				return this.firstTrackStops.add(id);
			case 2:
				return this.secondTrackStops.add(id);
			case 3:
				return this.thirdTrackStops.add(id);
			case 4:
				if (fourthConnector != null)
					return this.fourthTrackStops.add(id);
				else
					return false;
			default:
				return false;
		}
	}

	protected boolean removeTrackStop(int order, @NotNull ITrackStop stop) {
		return this.removeTrackStop(order, stop.getID());
	}

	protected boolean removeTrackStop(int order, @NotNull RUID id) {
		switch (order) {
			case 1:
				return this.firstTrackStops.remove(id);
			case 2:
				return this.secondTrackStops.remove(id);
			case 3:
				return this.thirdTrackStops.remove(id);
			case 4:
				if (fourthConnector != null)
					return this.fourthTrackStops.add(id);
				else
					return false;
			default:
				return false;
		}
	}

	@Override
	public boolean containsRail(Location location) {
		return location.equals(this.location);
	}

	@Override
	public @Nullable Rail getRail(Location location) {
		return location.equals(this.location) ? this.rail : null;
	}

	public void connectTo(Connector connector) throws IllegalConnectionException {
		if (!(connector instanceof Junction) && !(connector instanceof ITrack))
			throw new IllegalConnectionException("Junction's can only connect to another Junction or an ITrack.");
		if (connector instanceof Junction) {
			Junction other = (Junction) connector;

			boolean thisAlreadyConnect = false;
			if (this.firstConnector.equals(other.ruid) || this.secondConnector.equals(other.ruid))
				thisAlreadyConnect = true;
			if (!thisAlreadyConnect) {
				if (this.thirdConnector == null) {
					Connector firstConnector, secondConnector;
					try {
						firstConnector = this.firstConnector.getType() == RUID.IDType.TRACK ?
										 TrackManager.getTrack(this.firstConnector) :
										 TrackManager.getJunction(this.firstConnector);
						secondConnector = this.secondConnector.getType() == RUID.IDType.TRACK ?
										  TrackManager.getTrack(this.secondConnector) :
										  TrackManager.getJunction(this.secondConnector);
						if (firstConnector == null || secondConnector == null)
							throw new RailroadException("Connector/s that should exist, do not.");
					} catch (IncorrectRUIDTypeException e) {
						throw new IllegalConnectionException("Invalid RUID Connector in Junction " +
															 this.ruid.toString());
					}
					JunctionType newType;
					try {
						newType = JunctionType.createJunctionType(this.location, firstConnector, secondConnector, other,
								null);
					} catch (InvalidJunctionLocationException e) {
						throw new IllegalConnectionException("New JunctionType is invalid.", e);
					}
					try {
						junctionTypeField.set(this, newType);
						thirdConnectorField.set(this, other.ruid);
					} catch (IllegalAccessException e) {
						throw new IllegalConnectionException("Could not connect to Junction.", e);
					}
				} else if (this.fourthConnector == null && !this.thirdConnector.equals(other.ruid)) {
					Connector firstConnector, secondConnector, thirdConnector;
					try {
						firstConnector = this.firstConnector.getType() == RUID.IDType.TRACK ?
										 TrackManager.getTrack(this.firstConnector) :
										 TrackManager.getJunction(this.firstConnector);
						secondConnector = this.secondConnector.getType() == RUID.IDType.TRACK ?
										  TrackManager.getTrack(this.secondConnector) :
										  TrackManager.getJunction(this.secondConnector);
						thirdConnector = this.thirdConnector.getType() == RUID.IDType.TRACK ?
										 TrackManager.getTrack(this.thirdConnector) :
										 TrackManager.getJunction(this.thirdConnector);
						if (firstConnector == null || secondConnector == null || thirdConnector == null)
							throw new RailroadException("Connector/s that should exist, do not.");
					} catch (IncorrectRUIDTypeException e) {
						throw new IllegalConnectionException("Invalid RUID Connector in Junction " + this.ruid.toString());
					}
					JunctionType newType;
					try {
						newType = JunctionType.createJunctionType(this.location, firstConnector, secondConnector,
								thirdConnector, other);
					} catch (InvalidJunctionLocationException e) {
						throw new IllegalConnectionException("New JunctionType is invalid.", e);
					}
					try {
						junctionTypeField.set(this, newType);
						thirdConnectorField.set(this, other.ruid);
					} catch (IllegalAccessException e) {
						throw new IllegalConnectionException("Could not connect to Junction.", e);
					}
				} else if (this.fourthConnector != null && !this.thirdConnector.equals(other.ruid) &&
						   !this.fourthConnector.equals(other.ruid))
					throw new IllegalConnectionException("This Junction is full.");
			}

			if (other.thirdConnector == null) {
				Connector firstConnector, secondConnector;
				try {
					firstConnector = other.firstConnector.getType() == RUID.IDType.TRACK ?
								TrackManager.getTrack(other.firstConnector) :
							 	TrackManager.getJunction(other.firstConnector);
					secondConnector = other.secondConnector.getType() == RUID.IDType.TRACK ?
								TrackManager.getTrack(other.secondConnector) :
								TrackManager.getJunction(other.secondConnector);
					if (firstConnector == null || secondConnector == null)
						throw new RailroadException("Connector/s that should exist, do not.");
				} catch (IncorrectRUIDTypeException e) {
					throw new IllegalConnectionException("Invalid RUID Connector in Junction " + other.ruid.toString());
				}
				JunctionType newType;
				try {
					newType = JunctionType.createJunctionType(other.location, firstConnector, secondConnector, other,
							null);
				} catch (InvalidJunctionLocationException e) {
					throw new IllegalConnectionException("New JunctionType is invalid.", e);
				}

				try {
					junctionTypeField.set(other, newType);
					thirdConnectorField.set(other, this.ruid);
				} catch (IllegalAccessException e) {
					throw new IllegalConnectionException("Could not connect to Junction.", e);
				}
			} else if (other.fourthConnector == null) {
				Connector firstConnector, secondConnector, thirdConnector;
				try {
					firstConnector = other.firstConnector.getType() == RUID.IDType.TRACK ?
									 TrackManager.getTrack(other.firstConnector) :
									 TrackManager.getJunction(other.firstConnector);
					secondConnector = other.secondConnector.getType() == RUID.IDType.TRACK ?
					TrackManager.getTrack(other.secondConnector) :
									  TrackManager.getJunction(other.secondConnector);
					thirdConnector = other.thirdConnector.getType() == RUID.IDType.TRACK ?
									 TrackManager.getTrack(other.thirdConnector) :
									 TrackManager.getJunction(other.thirdConnector);
					if (firstConnector == null || secondConnector == null || thirdConnector == null)
						throw new RailroadException("Connector/s that should exist, do not.");
				} catch (IncorrectRUIDTypeException e) {
					throw new IllegalConnectionException("Invalid RUID Connector in Junction " + other.ruid.toString());
				}
				JunctionType newType;
				try {
					newType = JunctionType.createJunctionType(other.location, firstConnector, secondConnector,
							thirdConnector, other);
				} catch (InvalidJunctionLocationException e) {
					throw new IllegalConnectionException("New JunctionType is invalid.", e);
				}

				try {
					junctionTypeField.set(other, newType);
					thirdConnectorField.set(other, this.ruid);
				} catch (IllegalAccessException e) {
					throw new IllegalConnectionException("Could not connect to Junction.", e);
				}
			} else
				throw new IllegalConnectionException("Junction is full.");
		} else {
			if (this.fourthConnector != null)
				throw new IllegalConnectionException("Junction is full.");

			ITrack track = (ITrack) connector;
			Connector firstConnector, secondConnector, thirdConnector;
			try {
				firstConnector = this.firstConnector.getType() == RUID.IDType.TRACK ?
								 TrackManager.getTrack(this.firstConnector) :
								 TrackManager.getJunction(this.firstConnector);
				secondConnector = this.secondConnector.getType() == RUID.IDType.TRACK ?
								  TrackManager.getTrack(this.secondConnector) :
								  TrackManager.getJunction(this.secondConnector);
				thirdConnector = this.thirdConnector.getType() == RUID.IDType.TRACK ?
								 TrackManager.getTrack(this.thirdConnector) :
								 TrackManager.getJunction(this.thirdConnector);
				if (firstConnector == null || secondConnector == null || thirdConnector == null)
					throw new RailroadException("Connector/s that should exist, do not.");
			} catch (IncorrectRUIDTypeException e) {
				throw new IllegalConnectionException("Invalid RUID Connector in Junction " + this.ruid.toString());
			}
			JunctionType newType;
			try {
				newType = JunctionType.createJunctionType(this.location, firstConnector, secondConnector,
						thirdConnector, track);
			} catch (InvalidJunctionLocationException e) {
				throw new IllegalConnectionException("New JunctionType is invalid.", e);
			}

			try {
				junctionTypeField.set(this, newType);
				fourthConnectorField.set(this, track.getID());
			} catch (IllegalAccessException e) {
				throw new IllegalConnectionException("Could not connect to Junction.", e);
			}
		}
	}

	@Override
	public @NotNull RUID getID() {
		return this.ruid;
	}

	@Override
	public void remove() {

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		this.writeHeader(out);

		this.writeRUID(out);
		this.writeLocation(out);

		this.writeDefaultShape(out);

		this.writeFooter(out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.readHeader(in);

		this.readRUID(in);
		this.readLocation(in);

		this.readDefaultShape(in);

		this.readFooter(in);
	}

	private void writeHeader(ObjectOutput out) throws IOException {
		out.writeChar('{');
		out.writeObject("Junction");
		out.writeObject(Railroad.version);
	}

	private void writeFooter(ObjectOutput out) throws IOException {
		out.writeChar('}');
	}

	private void writeRUID(ObjectOutput out) throws IOException {
		out.writeObject("RUID");
		out.writeObject(this.ruid);
	}

	private void writeLocation(ObjectOutput out) throws IOException {
		out.writeObject("Location");
		out.writeChar('{');
		out.writeObject("World");
		org.bukkit.World world = this.location.getWorld();
		if (world == null)
			throw new RuntimeException("Failed to serialize Junction.", new InvalidJunctionLocationException(
					"The location of this Junction does not exist."));
		else
			out.writeObject(world.getName());
		out.writeObject("x:");
		out.writeInt(this.location.getBlockX());
		out.writeObject("y:");
		out.writeInt(this.location.getBlockY());
		out.writeObject("z:");
		out.writeInt(this.location.getBlockZ());
		out.writeChar('}');
	}

	private void writeDefaultShape(ObjectOutput out) throws IOException {
		out.writeObject("DefaultRailShape");
		out.writeObject(this.defaultShape.name());
	}

	private void readHeader(ObjectInput in) throws IOException, ClassNotFoundException {
		char left = in.readChar();
		if (left != '{')
			throw new ClassNotFoundException("'{' is missing from the start of Junction serialization.");

		String name = (String) in.readObject();
		if (!name.equals("Junction"))
			throw new ClassNotFoundException("Not a Junction object.");

		String ver = (String) in.readObject();
		if (!ver.equals(Railroad.version))
			throw new ClassNotFoundException("Not a valid Junction version.");
	}

	private void readFooter(ObjectInput in) throws IOException, ClassNotFoundException {
		char right = in.readChar();
		if (right != '}')
			throw new ClassNotFoundException("'}' is missing from the end of Junction serialization.");
	}

	private void readRUID(ObjectInput in) throws IOException, ClassNotFoundException {
		String ruidHeading = (String) in.readObject();
		if (!ruidHeading.equals("RUID"))
			throw new ClassNotFoundException("\"RUID\" is missing from Junction serialization.");

		RUID ruid;
		try {
			ruid = (RUID) in.readObject();
		} catch (ClassNotFoundException clsEx) {
			throw new ClassNotFoundException("Failed Junction deserialization.", clsEx);
		}

		try {
			ruidField.set(this, ruid);
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException("Failed to set 'ruid' field in Junction deserialization.", e);
		}
	}

	private void readLocation(ObjectInput in) throws IOException, ClassNotFoundException {
		String locHeading = (String) in.readObject();
		if (!locHeading.equals("Location"))
			throw new ClassNotFoundException("\"Location\" is missing from Junction serialization.");

		char locLeft = in.readChar();
		if (locLeft != '{')
			throw new ClassNotFoundException("'{' is missing from the start of \"Location\" in Junction " +
					"serialization.");

		String worldHeading = (String) in.readObject();
		if (!worldHeading.equals("World"))
			throw new ClassNotFoundException("\"Location.World\" is missing from Junction serialization");

		org.bukkit.World world = (org.bukkit.World) in.readObject();
		if (world == null)
			throw new ClassNotFoundException("\"Location.World\" is null when it should never be in Junction" +
					" serialization.");

		String xHeading = (String) in.readObject();
		if (!xHeading.equals("x:"))
			throw new ClassNotFoundException("\"Location.x\" is missing from Junction serialization.");
		int x = in.readInt();

		String yHeading = (String) in.readObject();
		if (!yHeading.equals("y:"))
			throw new ClassNotFoundException("\"Location.y\" is missing from Junction serialization.");
		int y = in.readInt();

		String zHeading = (String) in.readObject();
		if (!zHeading.equals("z:"))
			throw new ClassNotFoundException("\"Location.z\" is missing from Junction serialization.");
		int z = in.readInt();

		double size = world.getWorldBorder().getSize() / 2;
		if (Math.abs(x) > size)
			throw new ClassNotFoundException("\"Location.x\" in Junction serialization is out of bounds.");
		if (Math.abs(z) > size)
			throw new ClassNotFoundException("\"Location.z\" in Junction serialization is out of bounds.");
		//Might have to change for 1.17
		if (y < 0 || y > world.getMaxHeight())
			throw new ClassNotFoundException("\"Location.y\" in Junction serialization is out of bounds.");

		char locRight = in.readChar();
		if (locRight != '}')
			throw new ClassNotFoundException("'}' is missing from the end of \"Location\" in Junction serialization.");

		Location location = new Location(world, x, y, z);
		try {
			locField.set(this, location);
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException("Failed to set 'location' field in Junction deserialization.", e);
		}
	}

	private void readDefaultShape(ObjectInput in) throws IOException, ClassNotFoundException {
		String dsHeading = (String) in.readObject();
		if (!dsHeading.equals("DefaultRailShape"))
			throw new ClassNotFoundException("\"DefaultRailShape\" is missing in Junction serialization.");

		String railShapeName = (String) in.readObject();
		Rail.Shape railShape;
		try {
			railShape = Rail.Shape.valueOf(railShapeName);
		} catch (IllegalArgumentException e) {
			throw new ClassNotFoundException("\"DefaultRailShape.Name\" is invalid in Junction serialization.", e);
		}

		this.defaultShape = railShape;
	}
}
