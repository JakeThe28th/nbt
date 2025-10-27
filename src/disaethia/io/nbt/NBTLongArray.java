package disaethia.io.nbt;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Arrays;

import disaethia.io.nbt.utils.ActualStringBuffer;

public class NBTLongArray extends NBTTag {
	
	long[] payload;
	public NBTLongArray(long[] val) { set(val); this.TYPE = 12; }
	
	public void set(long[] val) 	{ this.payload = val; }
	public long[] get() 			{ return this.payload; }
	
	public int getSize() { 
		int size = 0;
		size += new NBTInt(payload.length).getSize();
		
		// Probably slow, but won't break if some idiot decides to replace NBTInt with VarInts or something
		for (int i = 0; i < payload.length; i++) {
			size += new NBTLong(payload[i]).getSize();
		}
		return size; }
	
	public void write(ByteBuffer dest) { 
		new NBTInt(payload.length).write(dest);
		for (int i = 0; i < payload.length; i++) {
			new  NBTLong(payload[i]).write(dest);
			}
		}
	
	public static NBTLongArray read(ByteBuffer source) {
		NBTInt length = NBTInt.read(source);
		long[] longs = new long[length.get()];
		for (int i = 0; i < length.get(); i++) {
			longs[i] = NBTLong.read(source).get();
			}
		return new NBTLongArray(longs);
	}
	
	public void print(int i) {
		print(i, Arrays.toString(payload) + "\n");
	}
	
	@Override
	public String toSNBT(boolean formatted) {
		String string = "[L;";
		
		for (int i = 0; i < payload.length; i++) {
			string += payload[i] + "l"
				   + ((i != payload.length-1) ? "," : "");
		}
		
		return string + "]";
	}
	
	public static NBTLongArray fromSNBT(ActualStringBuffer snbt) throws ParseException {
		snbt.readUntil(new char[] { ';' });
		String data = snbt.readUntil(new char[] { ']' });
		
		String[] array = data.split(",");
		long[] longarray = new long[array.length];
		for (int i = 0; i < array.length; i++) {
			longarray[i] = Long.parseLong(array[i].substring(0, array[i].length()-1));
		}
		return new NBTLongArray(longarray);
		}

}
