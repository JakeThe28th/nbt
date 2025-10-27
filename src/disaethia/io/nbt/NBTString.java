package disaethia.io.nbt;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import disaethia.io.nbt.utils.ActualStringBuffer;

public class NBTString extends NBTTag {
	
	String payload;
	public NBTString(String val) 	{ set(val); this.TYPE = 8;}
	
	public void set(String val) 	{ this.payload = val; }
	public String get() 			{ return this.payload; }
	
	public int getSize() {
		int size = 0;
		size += new NBTShort((short) payload.length()).getSize();
		size += payload.getBytes(StandardCharsets.UTF_8).length;
		return size; }
	
	public void write(ByteBuffer dest) { 
		new NBTShort((short) payload.getBytes(StandardCharsets.UTF_8).length).write(dest);
		dest.put(payload.getBytes(StandardCharsets.UTF_8));
		}

	public static NBTString read(ByteBuffer source) {
		short length = NBTShort.read(source).get();
		byte[] string_bytes = new byte[length];
		source.get(string_bytes);
		// Remember StandardCharsets.UTF_8, otherwise you get weird results. Now I'm really alone
		return new NBTString(new String(string_bytes, StandardCharsets.UTF_8));
	}
	
	@Override
	public String toSNBT(boolean formatted) { return "\"" + payload + "\""; }
	
	public static NBTString fromSNBT(ActualStringBuffer snbt) throws ParseException {
		snbt.skipWhitespace();
		
		boolean quoted = false; // Is this text surrounded by double/single quotes, or not?
		char first_char = snbt.currentChar();
		if (snbt.currentChar() == '\"' || snbt.currentChar() == '\'') { quoted = true; snbt.readChar(); }
		
		// Read until '"', ':', or ',' is reached (strings may be unquoted)
		String string = null;
		if (quoted) {
			if (first_char == '\"') string = snbt.readUntil(new char[] {'\"'}, new char[] {});
			if (first_char == '\'') string = snbt.readUntil(new char[] {'\''}, new char[] {}); }
			else string = snbt.readUntil(new char[] {',',':'}, new char[] {});
		
		if (quoted) snbt.readChar(); // skip ending quote
		return new NBTString(string);
	}
	
	public void print(int i) {
		print(i, payload + "\n");
	}

}
