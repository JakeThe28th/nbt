package disaethia.io.nbt;

import java.nio.ByteBuffer;

public class NBTEnd extends NBTTag {
	
	public static final byte TAG_TYPE = 0;

	public NBTEnd() 	{ this.TYPE = TAG_TYPE; }
	
	public int getSize() { return 1; }
	
	public void write(ByteBuffer dest) { dest.put((byte) 0); }
	public static NBTEnd read(ByteBuffer source) {
		source.get(); return new NBTEnd();
	}
	
	public void print(int i) {
		print(i, "TAG_END" + "\n");
	}

}