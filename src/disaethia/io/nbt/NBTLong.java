package disaethia.io.nbt;

import java.nio.ByteBuffer;

public class NBTLong extends NBTTag {
	
	public static final byte TAG_TYPE = 4;
	
	long payload;
	public NBTLong(long val) 	{ set(val); this.TYPE = TAG_TYPE; }
	
	public void set(long val) 	{ this.payload = val; }
	public long get() 			{ return this.payload; }
	
	public int getSize() { return 8; }
	
	public void write(ByteBuffer dest) { dest.putLong(this.payload); }
	public static NBTLong read(ByteBuffer source) {
		return new NBTLong(source.getLong());
	}
	
	@Override
	public String toSNBT(boolean formatted) {
		return payload + "l"; }
	
	public void print(int i) {
		print(i, payload + "\n");
	}

}
