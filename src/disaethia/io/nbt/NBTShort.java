package disaethia.io.nbt;

import java.nio.ByteBuffer;

public class NBTShort extends NBTTag {
	
	short payload;
	public NBTShort(short val) 	{ set(val); this.TYPE = 2; }
	
	public void set(short val) 	{ this.payload = val; }
	public short get() 			{ return this.payload; }
	
	public int getSize() { return 2; }
	
	public void write(ByteBuffer dest) { dest.putShort(this.payload); }
	public static NBTShort read(ByteBuffer source) {
		return new NBTShort(source.getShort());
	}
	
	@Override
	public String toSNBT(boolean formatted) {
		return payload + "s";
	}
	
	public void print(int i) {
		print(i, payload + "\n");
	}
}
