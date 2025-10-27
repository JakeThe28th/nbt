package disaethia.io.nbt;

import java.nio.ByteBuffer;

public class NBTInt extends NBTTag {
	
	int payload;
	public NBTInt(int val) 	{ set(val); this.TYPE = 3; }
	
	public void set(int val) 	{ this.payload = val; }
	public int get() 			{ return this.payload; }
	
	public int getSize() { return 4; }
	
	public void write(ByteBuffer dest) { dest.putInt(this.payload); }
	public static NBTInt read(ByteBuffer source) {
		return new NBTInt(source.getInt());
	}
	
	@Override
	public String toSNBT(boolean formatted) {
		return payload + ""; }
	
	public void print(int i) {
		print(i, payload + "\n");
	}

}
