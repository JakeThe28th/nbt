package disaethia.io.nbt;

import java.nio.ByteBuffer;

public class NBTFloat extends NBTTag {
	
	float payload;
	public NBTFloat(float val) 	{ set(val); this.TYPE = 5; }
	
	public void set(float val) 	{ this.payload = val; }
	public float get() 			{ return this.payload; }
	
	public int getSize() { return 4; }
	
	public void write(ByteBuffer dest) { dest.putFloat(this.payload); }
	public static NBTFloat read(ByteBuffer source) {
		return new NBTFloat(source.getFloat());
	}
	
	@Override
	public String toSNBT(boolean formatted) {
		return payload + "f"; }
	
	public void print(int i) {
		print(i, payload + "\n");
	}

}
