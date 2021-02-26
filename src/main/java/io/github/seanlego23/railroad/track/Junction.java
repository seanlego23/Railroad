package io.github.seanlego23.railroad.track;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.util.Pair;
import io.github.seanlego23.railroad.util.target.RUID;
import io.github.seanlego23.railroad.util.target.RailroadTarget;
import org.bukkit.Location;
import org.bukkit.util.Vector;
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
//Held by main rail,
public final class Junction implements RailroadTarget, ITrackStop {

	enum Path {
		NORTH(0, 0, -1),
		SOUTH(0, 0, 1),
		EAST(1, 0, 0),
		WEST(-1, 0, 0),
		NORTH_UP(0, 1, -1),
		SOUTH_UP(0, 1, 1),
		EAST_UP(1, 1, 0),
		WEST_UP(-1, 1, 0),
		NORTH_DOWN(0, -1, -1),
		SOUTH_DOWN(0, -1, 1),
		EAST_DOWN(1, -1, 0),
		WEST_DOWN(-1, -1, 0);

		private final int dx, dy, dz;

		Path(int dx, int dy, int dz) {
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
		}

		public Vector getVector() {
			return new Vector(dx, dy, dz);
		}
	}

	private final RUID ruid;
	private final Location location;
	private final ITrack mainTrack;
	private final Pair<Path, Path> mainPaths = new Pair<>();
	private final List<ITrack> otherTracks;
	private final List<Pair<Path, Path>> othersPaths = new ArrayList<>();
	private org.bukkit.block.data.Rail.Shape defaultShape;

	private static Field ruidField;
	private static Field locField;
	private static Field mainTrackField;
	private static Field mainPathPairField;
	private static Field otherTracksField;
	private static Field otherPathsPairField;

	static {
		try {
			ruidField = Junction.class.getDeclaredField("ruid");
			locField = Junction.class.getDeclaredField("location");
			mainTrackField = Junction.class.getDeclaredField("mainTrack");
			mainPathPairField = Junction.class.getDeclaredField("mainPaths");
			otherTracksField = Junction.class.getDeclaredField("otherTracks");
			otherPathsPairField = Junction.class.getDeclaredField("othersPaths");
			ruidField.setAccessible(true);
			locField.setAccessible(true);
			mainTrackField.setAccessible(true);
			mainPathPairField.setAccessible(true);
			otherTracksField.setAccessible(true);
			otherPathsPairField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public Junction() {
		this.ruid = null;
		this.location = null;
		this.mainTrack = null;
		this.otherTracks = null;
	}

	public Junction(@NotNull Location location, @NotNull ITrack mainTrack, @NotNull ITrack other)
			throws InvalidJunctionLocationException {
		this.ruid = new RUID(this);
		this.location = location;
		this.mainTrack = mainTrack;
		this.otherTracks = new ArrayList<>(Collections.singletonList(other));
		findPaths();
	}

	public Junction(@NotNull Location location, @NotNull ITrack mainTrack, @NotNull ITrack other1, @NotNull ITrack other2)
			throws InvalidJunctionLocationException {
		this.ruid = new RUID(this);
		this.location = location;
		this.mainTrack = mainTrack;
		this.otherTracks = new ArrayList<>(Arrays.asList(other1, other2));
		findPaths();
	}

	private void findPaths() throws InvalidJunctionLocationException {
		org.bukkit.World world = this.location.getWorld();
		if (world == null)
			throw new InvalidJunctionLocationException("The location given does not exist.");

		Rail junctionRail = this.mainTrack.getRail(this.location);
		if (junctionRail == null)
			throw new InvalidJunctionLocationException("The location given is not part of the main track.");


		int railsFound = 0;
		for (Path path : Path.values()) {
			if (railsFound == this.otherTracks.size() * 2 + 2)
				break;
			Location loc = this.location.clone();
			if (Rail.materialIsRail(world.getBlockAt(loc.add(path.getVector())).getType())) {
				if (this.mainTrack.contains(loc)) {

				}
			}
		}
	}

	public Location getLocation() {
		return this.location;
	}

	public ITrack getRail() {
		return this.mainTrack;
	}

	public org.bukkit.block.data.Rail.Shape getDefaultShape() {
		return this.defaultShape;
	}

	public ITrack getFirstConnectingRail() {
		return this.otherTracks.get(0);
	}

	public @Nullable ITrack getSecondConnectingRail() {
		if (this.otherTracks.size() == 1)
			return null;
		return this.otherTracks.get(1);
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

		out.writeObject("RUID");
		out.writeObject(this.ruid);

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

		out.writeObject("Main Track");
		out.writeObject(this.mainTrack.getRUID());

		out.writeObject("Main Paths");
		out.writeChar('{');
		out.writeObject(this.mainPaths.getFirst().name());
		out.writeChar(',');
		out.writeObject(this.mainPaths.getSecond().name());
		out.writeChar('}');

		out.writeObject("Other Tracks");
		out.writeInt(this.otherTracks.size());
		out.writeChar('{');
		out.writeObject(this.otherTracks.get(0).getRUID());
		if (this.otherTracks.size() == 2) {
			out.writeChar(',');
			out.writeObject(this.otherTracks.get(1).getRUID());
		}
		out.writeChar('}');

		out.writeObject("Other Paths");
		out.writeInt(this.othersPaths.size());
		out.writeChar('{');
		out.writeChar('{');
		out.writeObject(this.othersPaths.get(0).getFirst().name());
		out.writeChar(',');
		out.writeObject(this.othersPaths.get(0).getSecond().name());
		out.writeChar('}');
		if (this.othersPaths.size() == 2) {
			out.writeChar(',');
			out.writeChar('{');
			out.writeObject(this.othersPaths.get(1).getFirst().name());
			out.writeChar(',');
			out.writeObject(this.othersPaths.get(1).getSecond().name());
			out.writeChar('}');
		}
		out.writeChar('}');

		this.writeFooter(out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.readHeader(in);

		String ruidName = (String) in.readObject();
		if (!ruidName.equals("RUID"))
			throw new ClassNotFoundException("\"RUID\" is missing from Junction serialization.");
		try {
			ruidField.set(this, in.readObject());
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException("Failed to set 'ruid' field.", e);
		}

		String locName = (String) in.readObject();
		if (!locName.equals("Location"))
			throw new ClassNotFoundException("\"Location\" is missing from Junction serialization.");
		char locLeft = in.readChar();
		if (locLeft != '{')
			throw new ClassNotFoundException("'{' is missing from the start of \"Location\" for class Junction.");
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
		//
		if (y < 0 || y > world.getMaxHeight())

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

	private void readHeader(ObjectInput in) throws IOException, ClassNotFoundException {
		char left = in.readChar();
		if (left != '{')
			throw new ClassNotFoundException("'{' is missing from the start of Junction.");

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
			throw new ClassNotFoundException("'}' is missing from the end of Junction.");
	}
}
