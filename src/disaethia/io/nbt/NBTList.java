package disaethia.io.nbt;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;

import disaethia.io.nbt.utils.ActualStringBuffer;

public class NBTList extends NBTTag {

	NBTTag[] payload;
	NBTByte tag_type;
	public NBTInt length;
	
	public NBTList(byte type) 	
		{ 
		this.TYPE = 9;
		this.tag_type = new NBTByte(type); 
		this.length = new NBTInt(0); 
		this.payload = new NBTTag[0]; 
		}
	
	/**
	 * Make a list with an array. The type is deduced automatically.
	 * @param payload
	 */
	public NBTList(NBTTag[] payload) 
		{ 
		this.TYPE = 9; 
		this.payload = payload; 
		this.length = new NBTInt(payload.length); 
		this.tag_type = new NBTByte(typeOf(payload)); }
	
	/**
	 * Less if statements so probably faster constructor
	 * @param payload
	 * @param type
	 */
	public NBTList(NBTTag[] payload, NBTByte type) 
	{ 
	this.TYPE = 9; 
	this.payload = payload; 
	this.length = new NBTInt(payload.length); 
	this.tag_type = type; }

	// Same as compound tag add but with an extra check
	public NBTList add(NBTTag tag) {
		if (tag.TYPE != tag_type.get()) {
			System.out.println("Cannot add a TAG:" + tag.TYPE+" to a list of TAG:"+tag_type.get());
			throw new RuntimeException("Cannot add a TAG:" + tag.TYPE+" to a list of TAG:"+tag_type.get());
		}
		
		NBTTag[] new_arr = new NBTTag[payload.length+1];
		for (int i = 0; i < new_arr.length; i++) {
			new_arr[i] = tag;
			if (i != new_arr.length-1) new_arr[i] = payload[i];
		}
		payload = new_arr;
		
		this.length = new NBTInt(length.get()+1);
		return this;
	}
	
	// Same as compound tag remove
	public NBTList remove(int j) {
		NBTTag[] new_arr = new NBTTag[payload.length-1];
		for (int i = 0, pi = 0; i < new_arr.length; i++, pi++) {
			if (pi == j) pi++;
			new_arr[i] = payload[pi];
		}
		payload = new_arr;
		this.length = new NBTInt(length.get()-1);
		return this;
	}
	
	public NBTTag get(int i) { return payload[i]; } 
	
	public static NBTList read(ByteBuffer source) {
		NBTByte tag_type = NBTByte.read(source);
		NBTInt length = NBTInt.read(source);
		
		ArrayList<NBTTag> tags = new ArrayList<NBTTag>();
		for (int i = 0; i < length.get(); i ++) {
			tags.add(readAny(source, tag_type.get()));
		}
		
		NBTTag[] array = new NBTTag[tags.size()];
		for (int i = 0; i < tags.size(); i++) {
			array[i] = tags.get(i);
		}
		
		return new NBTList(array, tag_type);
	}
	
	public void write(ByteBuffer dest) {
		tag_type.write(dest);
		length.write(dest);
		for (int i = 0; i < payload.length; i++) {
			payload[i].write(dest);
		}
	}
	
	@Override
	public String toSNBT(boolean formatted) {
		String string = "[";
		for (int i = 0; i < payload.length; i++) {
			string += ""
				   +  payload[i].toSNBT(formatted)//.replace("\n", "\n  ")
				   + ((i != payload.length-1) ? ", " : "");
				   //+ "\n";
		}
		
		return string + "]";
	}
	
	public static NBTList fromSNBT(ActualStringBuffer snbt) throws ParseException {
		
		ArrayList<NBTTag> tags = new ArrayList<NBTTag>();
		while (snbt.currentChar() != ']') {
			snbt.readChar(); // Skip over the opening bracket, and over commas.
			tags.add(NBTTag.readAnySNBT(snbt));
		}
		
		if (tags.get(0) == null) {
			 // Was probably empty, and failed to read the ending ']' as a tag.
			tags = new ArrayList<NBTTag>();
			return new NBTList((byte) 0);
		}
		
		NBTTag[] array = new NBTTag[tags.size()];
		for (int i = 0; i < tags.size(); i++) {
			array[i] = tags.get(i);
		}
		
		return new NBTList(array, new NBTByte(array[0].TYPE));
	}
	
	public int getSize() {
		int size = 0;
		size += length.getSize();
		size += tag_type.getSize();
		for (int i = 0; i < payload.length; i++) {
			size+= payload[i].getSize();
		}
		return size;
	}
	
	public void print(int i) {
		print(i, "TAG_LIST\n");
		print(i, "TYPE: " + tag_type.get() +"\n");
		print(i, "LENGTH: " + length.get()+"\n");
		for (int j = 0; j < payload.length; j++) {
			payload[j].print(i+1);
		}
		//print(i, "END_OF_LIST\n");
	}
	
	public NBTList add(byte value) { return add(new NBTByte(value)); }
	public NBTList add(short value) { return add(new NBTShort(value)); }
	public NBTList add(int value) { return add(new NBTInt(value)); }
	public NBTList add(long value) { return add(new NBTLong(value)); }
	public NBTList add(float value) { return add(new NBTFloat(value)); }
	public NBTList add(double value) { return add(new NBTDouble(value)); }
	public NBTList add(byte[] value) { return add(new NBTByteArray(value)); }
	public NBTList add(String value) { return add(new NBTString(value)); }
	
	public NBTString getString(int i) 		{ return (NBTString) get(i); }
	public NBTList getList(int i) 			{ return (NBTList) get(i); }
	public NBTCompound getCompound(int i) 	{ return (NBTCompound) get(i); }

}
