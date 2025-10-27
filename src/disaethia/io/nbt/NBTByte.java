package disaethia.io.nbt;

import java.nio.ByteBuffer;

public class NBTByte extends NBTTag {
	
	byte payload;
	public NBTByte(byte val) 	{ set(val); this.TYPE = 1; }
	
	public void set(byte val) 	{ this.payload = val; }
	public byte get() 			{ return this.payload; }
	
	public int getSize() { return 1; }
	
	public void write(ByteBuffer dest) { dest.put(this.payload); }
	public static NBTByte read(ByteBuffer source) {
		return new NBTByte(source.get());
	}
	
	@Override
	public String toSNBT(boolean formatted) {
		return payload + "b"; }
	
	public void print(int i) {
		print(i, payload + "\n");
	}

}
