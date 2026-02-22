package disaethia.io.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

import disaethia.io.nbt.utils.ActualStringBuffer;

public class NBTNamedTag extends NBTTag {
	
	public NBTByte type;
	public NBTString name;
	public NBTTag payload;
	
	public NBTNamedTag(NBTByte type, NBTString name, NBTTag payload) 	{ set(type, name, payload); }
	public NBTNamedTag(NBTString name, NBTTag payload) 	{ set(new NBTByte(payload.TYPE), name, payload); }

	// Quick create methods
	public NBTNamedTag(String name, NBTTag payload) { this(new NBTString(name), payload); }
	
	public NBTNamedTag(String name, byte value) { this(new NBTString(name), new NBTByte(value)); }
	public NBTNamedTag(String name, short value) { this(new NBTString(name), new NBTShort(value)); }
	public NBTNamedTag(String name, int value) { this(new NBTString(name), new NBTInt(value)); }
	public NBTNamedTag(String name, long value) { this(new NBTString(name), new NBTLong(value)); }
	public NBTNamedTag(String name, float value) { this(new NBTString(name), new NBTFloat(value)); }
	public NBTNamedTag(String name, double value) { this(new NBTString(name), new NBTDouble(value)); }
	public NBTNamedTag(String name, byte[] value) { this(new NBTString(name), new NBTByteArray(value)); }

	public NBTNamedTag(String name, String value) { this(new NBTString(name), new NBTString(value)); }
	
	public void set(NBTByte type, NBTString name, NBTTag payload) 		{ 
		this.type = type; 
		this.name = name; 
		this.payload = payload; }
	
	// public NBTTag get() 			{ return this.payload; }
	
	public int getSize() {
		int size = 0;
		size += type.getSize();
		size += name.getSize();
		size += payload.getSize();
		return size; }
	
	public void write(ByteBuffer dest) { 
		type.write(dest);
		name.write(dest);
		payload.write(dest);
		}
	
	public static NBTNamedTag read(String filename) throws IOException {
		return read(ByteBuffer.wrap(Files.readAllBytes(Paths.get(filename))));
	}
	
	// Skip the first n bytes of the file before reading
	public static NBTNamedTag readSkip(String filename, ByteOrder order, int n) throws IOException {
		byte[] rawbytes = Files.readAllBytes(Paths.get(filename));
		ByteBuffer bb = ByteBuffer.wrap(new byte[rawbytes.length-n]).order(order);
		bb.put(rawbytes, n, rawbytes.length-n);
		bb.position(0);
		return read(bb);
	}
	
	public static NBTNamedTag read(String filename, ByteOrder order) throws IOException {
		return read(ByteBuffer.wrap(Files.readAllBytes(Paths.get(filename))).order(order));
	}
	
	public static NBTNamedTag read(ByteBuffer source) {
		if (source.remaining() == 0) {
			System.out.println("EOF reached, aborting by returning TAG_END");
			return new NBTNamedTag(new NBTByte((byte)0), new NBTString(""), new NBTEnd());
		}
		
		// 1F 8B 08 are Gzip's magic numbers.
		// The first byte will always be 1F, or 31, and (Minecraft) NBT doesn't tag ids over 12,
		// so this is a sufficient check.
		int p = (source.position());
		if (source.get() == 31) {
			try { 
				source = ByteBuffer.wrap(decompress(source.array())); 
			} catch (IOException e) { e.printStackTrace(); System.out.println("Gzip decompression failed."); }
			
		} else source.position(p);
		
		NBTByte type = NBTByte.read(source);
	
		// TAG_END is *always* unnamed.
		if (type.get() == 0) { return new NBTNamedTag(type, new NBTString(""), new NBTEnd()); }
		
		NBTString name = NBTString.read(source);
		NBTTag payload = readAny(source, type.get()); // Moved readany to NBTTag for easy reuse.
		
		return new NBTNamedTag(type, name, payload);
	}
	
	public void print(int i) {
		print(i, "TYPE: ");
		type.print(i);
		print(i, "NAME: ");
		name.print(i);
		print(i, "PAYLOAD: ");
		payload.print(i);
	}
	
	public byte getType() {
		return type.get();
	}
	public String getName() {
		return name.get();
	}
	
	public NBTByte getByte() 			{ return (NBTByte) payload; }
	public NBTShort getShort() 			{ return (NBTShort) payload; }
	public NBTInt getInt() 				{ return (NBTInt) payload; }
	public NBTLong getLong() 			{ return (NBTLong) payload; }
	public NBTFloat getFloat() 			{ return (NBTFloat) payload; }
	public NBTDouble getDouble() 		{ return (NBTDouble) payload; }
	public NBTString getString() 		{ return (NBTString) payload; }
	public NBTByteArray getByteArray() 	{ return (NBTByteArray) payload; }
	public NBTList getList() 			{ return (NBTList) payload; }
	public NBTCompound getCompound() 	{ return (NBTCompound) payload; }
	public NBTIntArray getIntArray() 	{ return (NBTIntArray) payload; }

	@Override
	public String toSNBT(boolean formatted) {
		String string = "";
			string += name.toSNBT(formatted) + ": ";
			string += payload.toSNBT(formatted) + "";
		return string;
		}
	
	public static NBTNamedTag fromSNBT(ActualStringBuffer snbt) throws ParseException {
		// Read the 'name' of the tag. EX: "name": "value"
		NBTString name = (NBTString) NBTString.fromSNBT(snbt);
		if (snbt.currentChar() == ':') snbt.readChar();
		else throw new ParseException("SNBT: Expected \":\" after reading key in key-value pair.\n" + snbt.surrounding(), snbt.index());
		NBTTag payload = NBTTag.readAnySNBT(snbt);
		
		return new NBTNamedTag(name, payload);
	}
	
}

