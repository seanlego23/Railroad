package io.github.seanlego23.railroad.util.target;

import io.github.seanlego23.railroad.Railroad;
import io.github.seanlego23.railroad.destinations.IDestination;
import io.github.seanlego23.railroad.track.ITrack;
import io.github.seanlego23.railroad.stations.IStation;
import io.github.seanlego23.railroad.stations.schedule.Schedule;
import io.github.seanlego23.railroad.stations.selection.ISelection;
import io.github.seanlego23.railroad.line.ILine;
import io.github.seanlego23.railroad.track.Junction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public final class RUID implements Externalizable {

	private static final Random random = new Random(System.currentTimeMillis());
	private static final long serialVersionUID = 4464369782416988441L;

	public enum IDType {
		LINE("LI"),
		STATION("ST"),
		SELECTION("SL"),
		SCHEDULE("SC"),
		TRACK("TR"),
		JUNCTION("JN"),
		DESTINATION("DT"),
		TEMP("TP"),
		INVALID("ER");

		private final String signature;

		IDType(String signature) {
			this.signature = signature;
		}

		String getSignature() {
			return this.signature;
		}

		@Nullable static IDType getType(String signature) {
			for (IDType type : values())
				if (type.signature.equals(signature))
					return type;
			return null;
		}

		@Nullable static IDType getType(byte first, byte second) {
			String sig = String.valueOf(first) + String.valueOf(second);
			for (IDType type : values())
				if (type.signature.equals(sig))
					return type;
			return null;
		}
	}

	private final IDType type;
	private final byte[] bytes;

	private static Field typeField;
	private static Field byteField;

	static {
		try {
			typeField = RUID.class.getDeclaredField("type");
			byteField = RUID.class.getDeclaredField("bytes");
			typeField.setAccessible(true);
			byteField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public RUID() {
		this.type = IDType.INVALID;
		this.bytes = null;
	}

	public RUID(RailroadTarget target) {
		this.type = this.getType(target);
		this.bytes = new byte[16];
		random.nextBytes(bytes);
		byte[] sigBytes = this.type.getSignature().getBytes();
		bytes[0] = sigBytes[0];
		bytes[1] = sigBytes[1];
	}

	private RUID(@NotNull IDType type, byte[] bytes) {
		this.type = type;
		this.bytes = bytes;
	}

	public static @NotNull RUID getTempRUID() {
		IDType typeID = IDType.TEMP;
		byte[] bytes = new byte[16];
		random.nextBytes(bytes);
		byte[] sigBytes = typeID.getSignature().getBytes();
		bytes[0] = sigBytes[0];
		bytes[1] = sigBytes[1];
		return new RUID(typeID, bytes);
	}

	private IDType getType(RailroadTarget railroadTarget) {
		if (railroadTarget instanceof ILine)
			return IDType.LINE;
		else if (railroadTarget instanceof IStation)
			return IDType.STATION;
		else if (railroadTarget instanceof ISelection)
			return IDType.SELECTION;
		else if (railroadTarget instanceof Schedule)
			return IDType.SCHEDULE;
		else if (railroadTarget instanceof ITrack)
			return IDType.TRACK;
		else if (railroadTarget instanceof Junction)
			return IDType.JUNCTION;
		else if (railroadTarget instanceof IDestination)
			return IDType.DESTINATION;
		return IDType.INVALID;
	}

	public IDType getType() {
		return this.type;
	}

	public long getMostSignificantBits() {
		long msb = 0;
		for (int i=0; i<8; i++)
			msb = (msb << 8) | (this.bytes[i] & 0xff);
		return msb;
	}

	public long getLeastSignificantBits() {
		long lsb = 0;
		for (int i=8; i<16; i++)
			lsb = (lsb << 8) | (this.bytes[i] & 0xff);
		return lsb;
	}

	public boolean isValid() {
		return !this.type.equals(IDType.INVALID);
	}

	public boolean isTemp() {
		return this.type.equals(IDType.TEMP);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof RUID))
			return false;
		RUID other = (RUID) o;
		if (this.type.equals(IDType.INVALID) || other.type.equals(IDType.INVALID))
			return false;
		if (!other.type.equals(this.type))
			return false;
		return Arrays.equals(other.bytes, this.bytes);
	}

	@Override
	public int hashCode() {
		if (!this.isValid())
			return 0;
		long msb = this.getMostSignificantBits();
		long lsb = this.getLeastSignificantBits();
		int hash = 37 * (int)(msb >> 32);
		hash = 37 * (int)((msb << 32) >> 32) + hash;
		hash = 37 * (int)(lsb >> 32) + hash;
		hash = 37 * (int)((lsb << 32) >> 32) + hash;
		return hash;
	}

	public String toString() {
		if (!this.isValid())
			return null;
		long mostSigBits = this.getMostSignificantBits();
		long leastSigBits = this.getLeastSignificantBits();
		return (hexDigits(mostSigBits >> 48, 4) + "-" +
				hexDigits(mostSigBits >> 16, 8) + "-" +
				hexDigits(mostSigBits, 4) + "-" +
				hexDigits(leastSigBits >> 48, 4) + "-" +
				hexDigits(leastSigBits, 12));
	}

	/** Returns val represented by the specified number of hex digits. */
	private static String hexDigits(long val, int digits) {
		long hi = 1L << (digits * 4);
		return Long.toHexString(hi | (val & (hi - 1))).substring(1).toUpperCase(Locale.ROOT);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if (!this.isValid())
			throw new IOException("Invalid RUID's cannot be serialized.");
		if (this.isTemp())
			throw new IOException("Temporary RUID's cannot be serialized.");

		this.writeHeader(out);
		out.writeObject("Type");
		out.writeObject(this.type.getSignature());
		out.writeObject("ID");
		out.writeObject(this.toString());
		this.writeFooter(out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.readHeader(in);

		String typeSignifier = (String) in.readObject();
		if (!typeSignifier.equals("Type"))
			throw new ClassNotFoundException("\"Type\" is missing from RUID serialization.");

		String typeSignature = (String) in.readObject();
		IDType newType = IDType.getType(typeSignature);
		if (newType == null)
			throw new ClassNotFoundException("No IDType found.");
		if (newType.equals(IDType.INVALID))
			throw new ClassNotFoundException("Invalid IDType found.");
		if (newType.equals(IDType.TEMP))
			throw new ClassNotFoundException("Temporary IDType found.");
		try {
			typeField.set(this, newType);
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException("Failed to set 'type' field.", e);
		}

		String idSignifier = (String) in.readObject();
		if (!idSignifier.equals("ID"))
			throw new ClassNotFoundException("\"ID\" is missing from RUID serialization.");

		//noinspection SpellCheckingInspection
		String format = "AAAA-AAAAAAAA-AAAA-AAAA-AAAAAAAAAAAA";
		ClassNotFoundException clsEx = new ClassNotFoundException("Invalid RUID. Format: " + format);

		String ruidString = (String) in.readObject();
		if (ruidString.length() != 36)
			throw clsEx;
		String hexMatch = "[A-Fa-f0-9]";
		StringBuilder matcher = new StringBuilder();
		matcher.append(hexMatch).append("{4}-").append(hexMatch).append("{8}-").append(hexMatch)
				.append("{4}-").append(hexMatch).append("{4}-").append(hexMatch).append("{12}");
		if (!ruidString.matches(matcher.toString()))
			throw clsEx;

		String ruid = String.join("", ruidString.split("-"));
		byte first = Byte.parseByte(ruid.substring(0,2), 16);
		byte second = Byte.parseByte(ruid.substring(2,4), 16);
		IDType type = IDType.getType(first, second);
		if (type != this.type)
			throw new ClassNotFoundException("Invalid RUID. Type in RUID doesn't match.");
		byte[] bytes = new byte[16];
		for (int i = 0; i < 32; i += 2)
			bytes[i/2] = Byte.parseByte(ruid.substring(i, i + 2), 16);
		try {
			byteField.set(this, bytes);
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException("Failed to set 'bytes' field.", e);
		}

		this.readFooter(in);
	}

	private void writeHeader(ObjectOutput out) throws IOException {
		out.writeChar('{');
		out.writeObject("RUID");
		out.writeObject(Railroad.version);
	}

	private void writeFooter(ObjectOutput out) throws IOException {
		out.writeChar('}');
	}

	private void readHeader(ObjectInput in) throws IOException, ClassNotFoundException {
		char left = in.readChar();
		if (left != '{')
			throw new ClassNotFoundException("'{' is missing from the start of RUID.");

		String name = (String) in.readObject();
		if (!name.equals("RUID"))
			throw new ClassNotFoundException("Not a RUID object.");

		String ver = (String) in.readObject();
		if (!ver.equals(Railroad.version))
			throw new ClassNotFoundException("Not a valid RUID version.");
	}

	private void readFooter(ObjectInput in) throws IOException, ClassNotFoundException {
		char right = in.readChar();
		if (right != '}')
			throw new ClassNotFoundException("'}' is missing  the end of RUID.");
	}
}
