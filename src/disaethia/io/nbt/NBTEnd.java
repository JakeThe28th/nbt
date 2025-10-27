package disaethia.io.nbt;

import java.nio.ByteBuffer;

public class NBTEnd extends NBTTag {
	
	public NBTEnd() 	{ this.TYPE = 0; }
	
	public int getSize() { return 1; }
	
	public void write(ByteBuffer dest) { dest.put((byte) 0); }
	public static NBTEnd read(ByteBuffer source) {
		source.get(); return new NBTEnd();
	}
	
	public void print(int i) {
		print(i, "TAG_END" + "\n");
	}

}