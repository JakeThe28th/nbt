package disaethia.io.nbt;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Arrays;

import disaethia.io.nbt.utils.ActualStringBuffer;

public class NBTByteArray extends NBTTag {
	
	byte[] payload;
	public NBTByteArray(byte[] val) { set(val); this.TYPE = 7; }
	
	public void set(byte[] val) 	{ this.payload = val; }
	public byte[] get() 			{ return this.payload; }
	
	public int getSize() { return payload.length+new NBTInt(payload.length).getSize(); }
	
	public void write(ByteBuffer dest) { 
		new NBTInt(payload.length).write(dest);
		dest.put(this.payload); // Will break if byte nbt type changes drastically fyi
		}
	
	public static NBTByteArray read(ByteBuffer source) {
		NBTInt length = NBTInt.read(source);
		byte[] bytes = new byte[length.get()];
		source.get(bytes);
		return new NBTByteArray(bytes);
	}
	
	public void print(int i) {
		print(i, Arrays.toString(payload) + "\n");
	}
	
	public boolean equals(NBTByteArray compare) {		
		if (payload.length != compare.payload.length) return false;	
		for (int i = 0; i < payload.length; i++) {	if (payload[i] != compare.payload[i]) return false; }
		return true;
	}
	
	public boolean equals(byte[] compare) {		
		if (payload.length != compare.length) return false;	
		for (int i = 0; i < payload.length; i++) {	if (payload[i] != compare[i]) return false; }
		return true;
	}

	@Override
	public String toSNBT(boolean formatted) {
		String string = "[B;";
		
		for (int i = 0; i < payload.length; i++) {
			string += payload[i] + "b"
				   + ((i != payload.length-1) ? "," : "");
		}
		
		return string + "]";
	}
	
	public static NBTByteArray fromSNBT(ActualStringBuffer snbt) throws ParseException {
		snbt.readUntil(new char[] { ';' });
		snbt.readChar();
		String data = snbt.readUntil(new char[] { ']' });
		
		String[] array = data.split(",");
		byte[] bytearray = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			bytearray[i] = Byte.parseByte(array[i].substring(0, array[i].length()-1));
		}
		return new NBTByteArray(bytearray);
	}

}