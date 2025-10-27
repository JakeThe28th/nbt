package disaethia.io.nbt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NBTInt24 extends NBTTag {
	
	int payload;
	public NBTInt24(int val) 	{ set(val); this.TYPE = 32; }
	
	public void set(int val) 	{ this.payload = val; }
	public int get() 			{ return this.payload; }
	
	public int getSize() { return 3; }
	
	public void write(ByteBuffer dest) { 
		int value = this.payload;
		if (dest.order() == ByteOrder.BIG_ENDIAN) {
			value = value << 8; // Cut off the leftmost byte -- the 'big end'
		} 
		
		// If the order is little endian, we don't need to do anything, since when it's flipped,
		// the end we want to remove is left at the rightmost side.
		// we could do >> and then <<, but it would be redundant since we either overwrite it later or EOF anyways.
		
		dest.putInt(value);
		dest.position(dest.position()-1);
		}
	public static NBTInt24 read(ByteBuffer source) {
		int value = source.getInt();
		source.position(source.position()-1);
		if (source.order() == ByteOrder.BIG_ENDIAN) {
			value = value >> 8;
		} else {
			// If the value was stored in little endian, we have a four byte number stored: 123?
			// ? is because the number is 3 bytes but we read extra.
			// When flipped to big endian, that's ?321
			// So we need to cut off the leftmost part, and then shift it back because we're storing in a long.
			value <<= 8;
			value >>= 8;
		}
		return new NBTInt24(value);
	}
	
	public void print(int i) {
		print(i, payload + "\n");
	}

}
