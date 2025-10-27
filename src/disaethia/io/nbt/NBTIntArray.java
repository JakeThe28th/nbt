package disaethia.io.nbt;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Arrays;

import disaethia.io.nbt.utils.ActualStringBuffer;

public class NBTIntArray extends NBTTag {
	
	int[] payload;
	public NBTIntArray(int[] val) { set(val); this.TYPE = 11; }
	
	public void set(int[] val) 	{ this.payload = val; }
	public int[] get() 			{ return this.payload; }
	
	public int getSize() { 
		int size = 0;
		size += new NBTInt(payload.length).getSize();
		
		// Probably slow, but won't break if some idiot decides to replace NBTInt with VarInts or something
		for (int i = 0; i < payload.length; i++) {
			size += new NBTInt(payload[i]).getSize();
		}
		return size; }
	
	public void write(ByteBuffer dest) { 
		new NBTInt(payload.length).write(dest);
		for (int i = 0; i < payload.length; i++) {
			new  NBTInt(payload[i]).write(dest);
			}
		}
	
	public static NBTIntArray read(ByteBuffer source) {
		NBTInt length = NBTInt.read(source);
		int[] ints = new int[length.get()];
		for (int i = 0; i < length.get(); i++) {
			ints[i] = NBTInt.read(source).get();
			}
		return new NBTIntArray(ints);
	}
	
	public void print(int i) {
		print(i, Arrays.toString(payload) + "\n");
	}
	

	@Override
	public String toSNBT(boolean formatted) {
		String string = "[I;";
		
		for (int i = 0; i < payload.length; i++) {
			string += payload[i]
				   + ((i != payload.length-1) ? "," : "");
		}
		
		return string + "]";
	}
	
	public static NBTIntArray fromSNBT(ActualStringBuffer snbt) throws ParseException {
		snbt.readUntil(new char[] { ';' });
		String data = snbt.readUntil(new char[] { ']' });
		
		String[] array = data.split(",");
		int[] intarray = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			intarray[i] = Integer.parseInt(array[i].substring(0, array[i].length()-1));
		}
		return new NBTIntArray(intarray);
	}

}