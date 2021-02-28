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
	private final RUID mainTrack;
	private final Pair<Path, Path> mainPaths = new Pair<>();
	private final List<RUID> otherTracks;
	private final List<Path> othersPaths = new ArrayList<>();
	private Set<RUID> mainTrackStopsAfter;
	private Set<RUID> mainTrackStopsBefore;
	private List<Set<RUID>> otherTrackStops;
	private Rail.RailShape defaultShape;

	private static Field ruidField;
	private static Field locField;
	private static Field mainTrackField;
	private static Field mainPathPairField;
	private static Field otherTracksField;
	private static Field otherPathsField;

	static {
		try {
			ruidField = Junction.class.getDeclaredField("ruid");
			locField = Junction.class.getDeclaredField("location");
			mainTrackField = Junction.class.getDeclaredField("mainTrack");
			mainPathPairField = Junction.class.getDeclaredField("mainPaths");
			otherTracksField = Junction.class.getDeclaredField("otherTracks");
			otherPathsField = Junction.class.getDeclaredField("othersPaths");
			ruidField.setAccessible(true);
			locField.setAccessible(true);
			mainTrackField.setAccessible(true);
			mainPathPairField.setAccessible(true);
			otherTracksField.setAccessible(true);
			otherPathsField.setAccessible(true);
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
		this.mainTrack = mainTrack.getRUID();
		this.otherTracks = new ArrayList<>(Collections.singletonList(other.getRUID()));
		findPaths(mainTrack, other);
	}

	public Junction(@NotNull Location location, @NotNull ITrack mainTrack, @NotNull ITrack other1, @NotNull ITrack other2)
			throws InvalidJunctionLocationException {
		this.ruid = new RUID(this);
		this.location = location;
		this.mainTrack = mainTrack.getRUID();
		this.otherTracks = new ArrayList<>(Arrays.asList(other1.getRUID(), other2.getRUID()));
		findPaths(mainTrack, other1, other2);
	}

	private void findPaths(ITrack mainTrack, ITrack... others) throws InvalidJunctionLocationException {
		org.bukkit.World world = this.location.getWorld();
		if (world == null)
			throw new InvalidJunctionLocationException("The location given does not exist.");

		Rail junctionRail = mainTrack.getRail(this.location);
		if (junctionRail == null)
			throw new InvalidJunctionLocationException("The location given is not part of the main track.");


	}

	public Location getLocation() {
		return this.location;
	}

	public RUID getMainTrackID() {
		return this.mainTrack;
	}

	public Rail.RailShape getDefaultShape() {
		return this.defaultShape;
	}

	public RUID getFirstConnectingTrackID() {
		return this.otherTracks.get(0);
	}

	public @Nullable RUID getSecondConnectingTrackID() {
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

		this.writeRUID(out);
		this.writeLocation(out);
		this.writeMainTrack(out);
		this.writeMainPaths(out);
		this.writeOtherTracks(out);
		this.writeOtherPaths(out);
		this.writeDefaultShape(out);

		this.writeFooter(out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.readHeader(in);

		this.readRUID(in);
		this.readLocation(in);
		this.readMainTrack(in);
		this.readMainPaths(in);
		this.readOtherTracks(in);
		this.readOtherPaths(in);
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

	private void writeMainTrack(ObjectOutput out) throws IOException {
		out.writeObject("MainTrack");
		out.writeChar('{');
		out.writeObject(this.mainTrack);
		this.writeMainTrackStops(out);
		out.writeChar('}');
	}

	private void writeMainTrackStops(ObjectOutput out) throws IOException {
		out.writeObject("TrackStops");
		out.writeChar('{');
		out.writeObject("After");
		out.writeChar('{');
		out.writeInt(this.mainTrackStopsAfter.size());
		out.writeChar(':');
		Iterator<RUID> itAfter = this.mainTrackStopsAfter.iterator();
		if (itAfter.hasNext()) {
			out.writeObject(itAfter.next());
			while (itAfter.hasNext()) {
				out.writeChar(',');
				out.writeObject(itAfter.next());
			}
		}
		out.writeChar('}');
		out.writeChar(',');
		out.writeObject("Before");
		out.writeChar('{');
		out.writeInt(this.mainTrackStopsBefore.size());
		out.writeChar(':');
		Iterator<RUID> itBefore = this.mainTrackStopsBefore.iterator();
		if (itBefore.hasNext()) {
			out.writeObject(itBefore.next());
			while (itBefore.hasNext()) {
				out.writeChar(',');
				out.writeObject(itBefore.next());
			}
		}
		out.writeChar('}');
		out.writeChar('}');
	}

	private void writeMainPaths(ObjectOutput out) throws IOException {
		out.writeObject("MainPaths");
		out.writeChar('{');
		out.writeObject(this.mainPaths.getFirst().name());
		out.writeChar(',');
		out.writeObject(this.mainPaths.getSecond().name());
		out.writeChar('}');
	}

	private void writeOtherTracks(ObjectOutput out) throws IOException {
		out.writeObject("OtherTracks");
		out.writeChar('{');
		out.writeInt(this.otherTracks.size());
		out.writeChar(':');
		out.writeObject(this.otherTracks.get(0));
		this.writeOtherTrackStops(out, this.otherTrackStops.get(0));
		if (this.otherTracks.size() == 2) {
			out.writeChar(',');
			out.writeObject(this.otherTracks.get(1));
			this.writeOtherTrackStops(out, this.otherTrackStops.get(1));
		}
		out.writeChar('}');
	}

	private void writeOtherTrackStops(ObjectOutput out, Set<RUID> stops) throws IOException {
		out.writeObject("TrackStops");
		out.writeChar('{');
		out.writeInt(stops.size());
		out.writeChar(':');
		Iterator<RUID> itBefore = stops.iterator();
		if (itBefore.hasNext()) {
			out.writeObject(itBefore.next());
			while (itBefore.hasNext()) {
				out.writeChar(',');
				out.writeObject(itBefore.next());
			}
		}
		out.writeChar('}');
	}

	private void writeOtherPaths(ObjectOutput out) throws IOException {
		out.writeObject("OtherPaths");
		out.writeChar('{');
		out.writeInt(this.othersPaths.size());
		out.writeChar(':');
		out.writeObject(this.othersPaths.get(0).name());
		if (this.othersPaths.size() == 2) {
			out.writeChar(',');
			out.writeObject(this.othersPaths.get(1).name());
		}
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

	private void readMainTrack(ObjectInput in) throws IOException, ClassNotFoundException {
		String mainTrackHeading = (String) in.readObject();
		if (!mainTrackHeading.equals("MainTrack"))
			throw new ClassNotFoundException("\"MainTrack\" is missing from Junction serialization.");

		char mtLeft = in.readChar();
		if (mtLeft != '{')
			throw new ClassNotFoundException("'{' is missing from the start of \"MainTrack\" in Junction " +
					"serialization.");

		RUID mainTrackRUID;
		try {
			mainTrackRUID = (RUID) in.readObject();
		} catch (ClassNotFoundException clsEx) {
			throw new ClassNotFoundException("Failed Junction deserialization.", clsEx);
		}

		try {
			mainTrackField.set(this, mainTrackRUID);
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException("Failed to set 'mainTrack' field in Junction deserialization.", e);
		}

		this.readMainTrackStops(in);

		char mtRight = in.readChar();
		if (mtRight != '}')
			throw new ClassNotFoundException("'}' is missing from the end of \"MainTrack\" in Junction " +
					"serialization.");
	}

	private void readMainTrackStops(ObjectInput in) throws IOException, ClassNotFoundException {
		String tsHeading = (String) in.readObject();
		if (!tsHeading.equals("TrackStops"))
			throw new ClassNotFoundException("\"MainTrack.TrackStops\" is missing from Junction serialization.");

		char tsLeft = in.readChar();
		if (tsLeft != '{')
			throw new ClassNotFoundException("'{' is missing from the start of \"MainTrack.TrackStops\" in Junction " +
					"serialization.");

		String afterHeading = (String) in.readObject();
		if (!afterHeading.equals("After"))
			throw new ClassNotFoundException("\"MainTrack.TrackStops.After\" is missing from Junction serialization.");

		char tsaLeft = in.readChar();
		if (tsaLeft != '{')
			throw new ClassNotFoundException("'{' is missing from the start of \"MainTrack.TrackStops.After\" in " +
					"Junction serialization.");

		int aSize = in.readInt();
		if (aSize < 0)
			throw new ClassNotFoundException("\"MainTrack.TrackStops.After.Size\" is negative in Junction " +
					"serialization.");
		char aColon = in.readChar();
		if (aColon != ':')
			throw new ClassNotFoundException("':' is missing from \"MainTrack.TrackStops.After\" in Junction " +
					"serialization.");

		Set<RUID> aRuidSet = new LinkedHashSet<>();
		if (aSize > 0) {
			RUID first;
			try {
				first = (RUID) in.readObject();
			} catch (ClassNotFoundException clsEx) {
				throw new ClassNotFoundException("Failed Junction deserialization.", clsEx);
			}

			aRuidSet.add(first);
			for (int i = 1; i < aSize; i++) {
				char aComma = in.readChar();
				if (aComma != ',')
					throw new ClassNotFoundException("',' is missing after TrackStop #" + i + " in \"MainTrack." +
							"TrackStops.After\" in Junction serialization.");

				RUID next;
				try {
					next = (RUID) in.readObject();
				} catch (ClassNotFoundException clsEx) {
					throw new ClassNotFoundException("Failed Junction deserialization.", clsEx);
				}
				//TODO: Task-Debug
				aRuidSet.add(next);
			}
		}
		this.mainTrackStopsAfter = aRuidSet;

		char tsaRight = in.readChar();
		if (tsaRight != '}')
			throw new ClassNotFoundException("'}' is missing from the end of \"MainTrack.TrackStops.After\" in " +
					"Junction serialization.");

		char tsComma = in.readChar();
		if (tsComma != ',')
			throw new ClassNotFoundException("',' is missing from the middle of \"MainTrack.TrackStops\" in " +
					"Junction serialization.");

		String beforeHeading = (String) in.readObject();
		if (!beforeHeading.equals("Before"))
			throw new ClassNotFoundException("\"MainTrack.TrackStops.Before\" is missing from Junction serialization.");

		char tsbLeft = in.readChar();
		if (tsbLeft != '{')
			throw new ClassNotFoundException("'{' is missing from the start of \"MainTrack.TrackStops.Before\" in " +
					"Junction serialization.");

		int bSize = in.readInt();
		if (bSize < 0)
			throw new ClassNotFoundException("\"MainTrack.TrackStops.Before.Size\" is negative in Junction " +
					"serialization.");

		char bColon = in.readChar();
		if (bColon != ':')
			throw new ClassNotFoundException("':' is missing from \"MainTrack.TrackStops.Before\" in Junction " +
					"serialization.");

		Set<RUID> bRuidSet = new LinkedHashSet<>();
		if (bSize > 0) {
			RUID first;
			try {
				first = (RUID) in.readObject();
			} catch (ClassNotFoundException clsEx) {
				throw new ClassNotFoundException("Failed Junction deserialization.", clsEx);
			}

			bRuidSet.add(first);
			for (int i = 1; i < aSize; i++) {
				char bComma = in.readChar();
				if (bComma != ',')
					throw new ClassNotFoundException("',' is missing after TrackStop #" + i + " in \"MainTrack." +
							"TrackStops.After\" in Junction serialization.");

				RUID next;
				try {
					next = (RUID) in.readObject();
				} catch (ClassNotFoundException clsEx) {
					throw new ClassNotFoundException("Failed Junction deserialization.", clsEx);
				}
				//TODO: Task-Debug
				bRuidSet.add(next);
			}
		}
		this.mainTrackStopsBefore = bRuidSet;

		char tsbRight = in.readChar();
		if (tsbRight != '}')
			throw new ClassNotFoundException("'}' is missing from the end of \"MainTrack.TrackStops.Before\" in " +
					"Junction serialization.");

		char tsRight = in.readChar();
		if (tsRight != '}')
			throw new ClassNotFoundException("'}' is missing from the end of \"MainTrack.TrackStops\" in " +
					"Junction serialization.");
	}

	private void readMainPaths(ObjectInput in) throws IOException, ClassNotFoundException {
		String mainPathHeader = (String) in.readObject();
		if (!mainPathHeader.equals("MainPaths"))
			throw new ClassNotFoundException("\"MainPaths\" is missing from Junction serialization.");

		char mpLeft = in.readChar();
		if (mpLeft != '{')
			throw new ClassNotFoundException("'{' is missing from the start of \"MainPaths\" in Junction " +
					"serialization.");

		String firstPathName = (String) in.readObject();
		Path firstPath;
		try {
			firstPath = Path.valueOf(firstPathName);
		} catch (IllegalArgumentException e) {
			throw new ClassNotFoundException("\"MainPaths.First\" is invalid in Junction serialization.", e);
		}

		char mpComma = in.readChar();
		if (mpComma != ',')
			throw new ClassNotFoundException("',' is missing from the middle of \"MainPaths\" in Junction " +
					"serialization.");

		String secondPathName = (String) in.readObject();
		Path secondPath;
		try {
			secondPath = Path.valueOf(secondPathName);
		} catch (IllegalArgumentException e) {
			throw new ClassNotFoundException("\"MainPaths.Second\" is invalid in Junction serialization.", e);
		}

		char mpRight = in.readChar();
		if (mpRight != '}')
			throw new ClassNotFoundException("'}' is missing from the end of \"MainPaths\" in Junction " +
					"serialization.");

		Pair<Path, Path> paths = new Pair<>(firstPath, secondPath);
		try {
			mainPathPairField.set(this, paths);
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException("Failed to set 'mainPaths' field in Junction deserialization.", e);
		}
	}

	private void readOtherTracks(ObjectInput in) throws IOException, ClassNotFoundException {
		String otherTracksHeader = (String) in.readObject();
		if (!otherTracksHeader.equals("OtherTracks"))
			throw new ClassNotFoundException("\"OtherTracks\" is missing from Junction serialization.");

		char otLeft = in.readChar();
		if (otLeft != '{')
			throw new ClassNotFoundException("'{' is missing from the start of \"OtherTracks\" in Junction " +
					"serialization.");

		int oSize = in.readInt();
		if (oSize < 0)
			throw new ClassNotFoundException("\"OtherTracks.Size\" is negative in Junction serialization.");
		if (oSize != 1 && oSize != 2)
			throw new ClassNotFoundException("\"OtherTracks.Size\" is invalid in Junction serialization.");

		char oColon = in.readChar();
		if (oColon != ':')
			throw new ClassNotFoundException("':' is missing from \"OtherTracks\" in Junction serialization.");

		List<RUID> otherTracks = new ArrayList<>();
		RUID other1;
		try {
			other1 = (RUID) in.readObject();
		} catch (ClassNotFoundException clsEx) {
			throw new ClassNotFoundException("Failed Junction deserialization.", clsEx);
		}
		otherTracks.add(other1);

		this.readOtherTrackStops(in, 0);

		if (oSize == 2) {
			char oComma = in.readChar();
			if (oComma != ',')
				throw new ClassNotFoundException("',' is missing from the middle of \"OtherTracks\" in Junction " +
						"serialization.");

			RUID other2;
			try {
				other2 = (RUID) in.readObject();
			} catch (ClassNotFoundException clsEx) {
				throw new ClassNotFoundException("Failed Junction deserialization.", clsEx);
			}
			otherTracks.add(other2);

			this.readOtherTrackStops(in, 1);
		}

		char otRight = in.readChar();
		if (otRight != '}')
			throw new ClassNotFoundException("'}' is missing from the end of \"OtherTracks\" in Junction " +
					"serialization.");

		try {
			otherTracksField.set(this, otherTracks);
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException("Failed to set 'otherTracks' field in Junction deserialization.", e);
		}
	}

	private void readOtherTrackStops(ObjectInput in, int index) throws IOException, ClassNotFoundException {
		String place = ((index == 1) ? "First" : "Second");

		String otsHeader = (String) in.readObject();
		if (!otsHeader.equals("TrackStops"))
			throw new ClassNotFoundException("\"OtherTracks." + place + ".TrackStops\" " +
					"is missing from Junction serialization.");

		char otsLeft = in.readChar();
		if (otsLeft != '{')
			throw new ClassNotFoundException("'{' is missing from the start of \"OtherTracks." + place +
					".TrackStops\" in Junction serialization.");

		int otsSize = in.readInt();
		if (otsSize < 0)
			throw new ClassNotFoundException("\"OtherTracks." + place + ".TrackStops.Size\" is negative in Junction " +
					"serialization.");

		char otsColon = in.readChar();
		if (otsColon != ':')
			throw new ClassNotFoundException("':' is missing from \"OtherTracks." + place + ".TrackStops\" " +
					"in Junction serialization.");

		Set<RUID> stops = new LinkedHashSet<>();
		if (otsSize > 0) {
			RUID first;
			try {
				first = (RUID) in.readObject();
			} catch (ClassNotFoundException clsEx) {
				throw new ClassNotFoundException("Failed Junction deserialization.", clsEx);
			}
			stops.add(first);

			for (int i = 1; i < otsSize; i++) {
				char otsComma = in.readChar();
				if (otsComma != ',')
					throw new ClassNotFoundException("',' is missing after TrackStop #" + i + " in \"OtherTracks." +
							place + "TrackStops\" in Junction serialization.");

				RUID next;
				try {
					next = (RUID) in.readObject();
				} catch (ClassNotFoundException clsEx) {
					throw new ClassNotFoundException("Failed Junction deserialization.", clsEx);
				}

				//TODO: Task-Debug
				stops.add(next);
			}
		}

		char otsRight = in.readChar();
		if (otsRight != '}')
			throw new ClassNotFoundException("'}' is missing from the end of \"OtherTracks." + place + ".TrackStops" +
					"\" in Junction serialization.");

		if (this.otherTrackStops == null)
			this.otherTrackStops = new ArrayList<>();
		this.otherTrackStops.add(index, stops);
	}

	private void readOtherPaths(ObjectInput in) throws IOException, ClassNotFoundException {
		String opHeading = (String) in.readObject();
		if (!opHeading.equals("OtherPaths"))
			throw new ClassNotFoundException("\"OtherPaths\" is missing from Junction serialization.");

		char opLeft = in.readChar();
		if (opLeft != '{')
			throw new ClassNotFoundException("'{' is missing from the start of \"OtherPaths\" in Junction " +
					"serialization.");

		int opSize = in.readInt();
		if (opSize < 0)
			throw new ClassNotFoundException("\"OtherPaths.Size\" is negative in Junction serialization.");
		if (opSize != 1 && opSize != 2)
			throw new ClassNotFoundException("\"OtherPaths.Size\" is invalid in Junction serialization.");

		char opColon = in.readChar();
		if (opColon != ':')
			throw new ClassNotFoundException("':' is missing from \"OtherPaths\" in Junction serialization.");

		List<Path> paths = new ArrayList<>();
		String firstPathName = (String) in.readObject();
		Path firstPath;
		try {
			firstPath = Path.valueOf(firstPathName);
		} catch (IllegalArgumentException e) {
			throw new ClassNotFoundException("\"OtherPaths.First\" is invalid in Junction serialization.", e);
		}
		paths.add(firstPath);

		if (opSize == 2) {
			char opComma = in.readChar();
			if (opComma != ',')
				throw new ClassNotFoundException("',' is missing from the middle of \"OtherPaths\" in Junction " +
						"serialization.");

			String secondPathName = (String) in.readObject();
			Path secondPath;
			try {
				secondPath = Path.valueOf(secondPathName);
			} catch (IllegalArgumentException e) {
				throw new ClassNotFoundException("\"OtherPaths.Second\" is invalid in Junction serialization.", e);
			}
			paths.add(secondPath);
		}

		char opRight = in.readChar();
		if (opRight != '}')
			throw new ClassNotFoundException("'}' is missing from the end of \"OtherPaths\" in Junction " +
					"serialization.");

		try {
			otherPathsField.set(this, paths);
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException("Failed to set 'otherPaths' field in Junction deserialization.", e);
		}
	}

	private void readDefaultShape(ObjectInput in) throws IOException, ClassNotFoundException {
		String dsHeading = (String) in.readObject();
		if (!dsHeading.equals("DefaultRailShape"))
			throw new ClassNotFoundException("\"DefaultRailShape\" is missing in Junction serialization.");

		String railShapeName = (String) in.readObject();
		Rail.RailShape railShape;
		try {
			railShape = Rail.RailShape.valueOf(railShapeName);
		} catch (IllegalArgumentException e) {
			throw new ClassNotFoundException("\"DefaultRailShape.Name\" is invalid in Junction serialization.", e);
		}

		this.defaultShape = railShape;
	}
}
