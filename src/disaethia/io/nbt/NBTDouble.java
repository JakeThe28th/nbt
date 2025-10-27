package disaethia.io.nbt;

import java.nio.ByteBuffer;

public class NBTDouble extends NBTTag {
	
	double payload;
	public NBTDouble(double val) 	{ set(val); this.TYPE = 6; }
	
	public void set(double val) 	{ this.payload = val; }
	public double get() 			{ return this.payload; }
	
	public int getSize() { return 8; }
	
	public void write(ByteBuffer dest) { dest.putDouble(this.payload); }
	public static NBTDouble read(ByteBuffer source) {
		return new NBTDouble(source.getDouble());
	}
	
	@Override
	public String toSNBT(boolean formatted) {
		return payload + ""; }
	
	public void print(int i) {
		print(i, payload + "\n");
	}

}
