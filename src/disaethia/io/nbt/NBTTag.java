package disaethia.io.nbt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import disaethia.io.nbt.utils.ActualStringBuffer;

public class NBTTag {
	
	public byte TYPE = -1;
	
	public static NBTTag readAny(ByteBuffer source, byte type) {
		NBTTag payload = null;
		switch (type) {
		case 1: payload = NBTByte.read(source); break;
		case 2: payload = NBTShort.read(source); break;
		case 3: payload = NBTInt.read(source); break;
		case 4: payload = NBTLong.read(source); break;
		case 5: payload = NBTFloat.read(source); break;
		case 6: payload = NBTDouble.read(source); break;
		case 7: payload = NBTByteArray.read(source); break;
		case 8: payload = NBTString.read(source); break;
		case 9: payload = NBTList.read(source); break;
		case 10: payload = NBTCompound.read(source); break;
		case 11: payload = NBTIntArray.read(source); break;
		case 12: payload = NBTLongArray.read(source); break;
		
		// My tag IDs
		case 32: payload = NBTInt24.read(source); break;

		default: System.out.println("Unknown TAG " + type);
		}
		return payload;
	}
	
	public byte typeOf(NBTTag[] payload) {
		byte type = -1;
		
		if (payload instanceof NBTEnd[]) type = 0;
		if (payload instanceof NBTByte[]) type = 1;
		if (payload instanceof NBTShort[]) type = 2;
		if (payload instanceof NBTInt[]) type = 3;
		if (payload instanceof NBTLong[]) type = 4;
		if (payload instanceof NBTFloat[]) type = 5;
		if (payload instanceof NBTDouble[]) type = 6;
		if (payload instanceof NBTByteArray[]) type = 7;
		if (payload instanceof NBTString[]) type = 8;
		if (payload instanceof NBTList[]) type = 9;
		if (payload instanceof NBTCompound[]) type = 10;
		if (payload instanceof NBTIntArray[]) type = 11;
		if (payload instanceof NBTLongArray[]) type = 12;
		
		// My tag IDs
		if (payload instanceof NBTInt24[]) type = 32;

		
		if (type == -1) System.out.println("Unknown TAG Array, cannot deduce type. " + payload.toString());
		return type;
	}
	
	public void write(ByteBuffer dest) {
		System.out.println("override@write");
	}
	
	public int getSize() {
		System.out.println("override@getSize");
		return -1;
	}
	
	public void save(String filename) throws IOException {
		Path path = Paths.get(filename);
		ByteBuffer buf = ByteBuffer.wrap(new byte[getSize()]);
		write(buf);
		Files.write(path, buf.array());
	}
	
	public void save(String filename, ByteOrder order) throws IOException {
		Path path = Paths.get(filename);
		ByteBuffer buf = ByteBuffer.wrap(new byte[getSize()]);
		buf.order(order);
		write(buf);
		Files.write(path, buf.array());
	}



	public void print(int i) {
		System.out.println("override@print");
	}
	
	public void print(int prefix, String print) {
		String pre = "";
		for (int i = 0; i < prefix; i++) {
			pre += " ";
		}
		System.out.print(pre + print);
	}
	
	/*
	 * Copied code from old, cleanup later TODO
	 */
	public static byte[] decompress(byte[] b) throws IOException {
			  System.out.println("Reading gzipped file");
			  
			  java.io.ByteArrayInputStream bytein = new java.io.ByteArrayInputStream(b);
			  java.util.zip.GZIPInputStream gzin = new java.util.zip.GZIPInputStream(bytein);
			  java.io.ByteArrayOutputStream byteout = new java.io.ByteArrayOutputStream();
			  
			  int res = 0;
			  byte buf[] = new byte[512 * 4096];
			  while (res >= 0) {
			      res = gzin.read(buf, 0, buf.length);
			      if (res > 0) {
			          byteout.write(buf, 0, res);
			      }
			  }
			  return byteout.toByteArray();
	}
	
	public String toSNBT(boolean formatted) {
		System.out.println("override@writeAsSNBT");
		return null;
	}
	
	// TODO: Make it so non-named tags can be read from SNBT.
	public static NBTNamedTag readSNBTFromFile(String filename) throws IOException, ParseException {
		return NBTNamedTag.fromSNBT(new ActualStringBuffer(Files.readString(Paths.get(filename), StandardCharsets.UTF_8)));
	}
	
	public static NBTNamedTag readSNBT(String contents) throws IOException, ParseException {
		return NBTNamedTag.fromSNBT(new ActualStringBuffer(contents));
	}

	public static NBTTag readUnnamedSNBTFromFile(String filename) throws IOException, ParseException {
		return readAnySNBT(new ActualStringBuffer(Files.readString(Paths.get(filename), StandardCharsets.UTF_8)));
	}
	
	public void saveAsSNBT(String filename) throws IOException {
		saveAsSNBT(filename, true);
	}
	
	public void saveAsSNBT(String filename, boolean formatted) throws IOException {
		Path path = Paths.get(filename);
		String string = "";
		string += toSNBT(formatted);
		Files.write(path, string.getBytes(StandardCharsets.UTF_8));
	}

	public static NBTTag readAnySNBT(String snbt) throws ParseException {
		return readAnySNBT(new ActualStringBuffer(snbt));
	}

	public static NBTTag readAnySNBT(ActualStringBuffer snbt) throws ParseException {
		// Read the 'value' of the tag, depending on it's type.
		char c = snbt.currentChar();
				
		NBTTag payload = null;
		

		/* TAG_BYTE, TAG_SHORT, TAG_INT, TAG_LONG, TAG_FLOAT, TAG_DOUBLE 
		 * 
		 * TAG_DOUBLE is checked second to last as it doesn't have any indicator other
		 * than the presence of a decimal point and lack of the 'f' indicator.
		 * 
		 * TAG_INT is checked last as it doesn't have any indicator other than the lack of an indicator.
		 * */
		if (Character.isDigit(c) || c == '-') 	{
			
			// We need to know the length of this value.
			// We're either in a compound, list, or the tag will end at EOF.
			String data = snbt.readUntil(new char[] {']',',','}'}, new char[] {}).trim();
			int li = data.length()-1;
			String parse = data.substring(0, li);
			if (Character.isDigit(data.charAt(li))) parse = data; // Don't cut of Ints and Doubles
			
			// Now that we've isolated the value, 
			// we can do checks based on the last character or if it contains a '.'
			if (data.charAt(li) == 'b' || data.charAt(li) == 'B') payload = new NBTByte(Byte.parseByte(parse));
			else if (data.charAt(li) == 's' || data.charAt(li) == 'S') payload = new NBTShort(Short.parseShort(parse));
			else if (data.charAt(li) == 'l' || data.charAt(li) == 'L') payload = new NBTLong(Long.parseLong(parse));
			else if (data.charAt(li) == 'f' || data.charAt(li) == 'F') payload = new NBTFloat(Float.parseFloat(parse));
			else if (data.contains(".")) payload = new NBTDouble(Double.parseDouble(parse));
			else payload = new NBTInt(Integer.parseInt(parse));
		}
		
		/* Lists and Arrays */
		if (c == '[') 	{ 
			// I know DRY exists but it's just once okkk... l>- -<l
			// We need to know the length of this value.
			// Lists end at ]. All potential string tags should be escaped.
			String data = snbt.readUntil(new char[] {']'}, new char[] {});
			data += snbt.readChar();	// Read the ending ]
			int li = data.length()-1;
			String parse = data.substring(0, li);
			
			/* TAG_BYTE_ARRAY */
			if (data.charAt(1) == 'b' || data.charAt(1) == 'B' ) payload = NBTByteArray.fromSNBT(new ActualStringBuffer(data)); 
			
			/* TAG_INT_ARRAY */
			else if (data.charAt(1) == 'i' || data.charAt(1) == 'I' ) payload = NBTIntArray.fromSNBT(new ActualStringBuffer(data)); 
			
			/* TAG_LONG_ARRAY */
			else if (data.charAt(1) == 'l' || data.charAt(1) == 'L' ) payload = NBTLongArray.fromSNBT(new ActualStringBuffer(data)); 
			
			/* TAG_LIST */
			else payload = NBTList.fromSNBT(new ActualStringBuffer(data)); 
		
		}
		
		/* TAG_COMPOUND */
		if (c == '{') 				{ payload = NBTCompound.fromSNBT(snbt); }
		
		/* TAG_STRING.
		 * Strings can use double-quotes, single-quotes, 
		 * TODO: or even be unquoted, so any misc data we'll treat as a string.*/
		if (c == '\"' || c == '\'') { payload = NBTString.fromSNBT(snbt);  }
		//if (payload == null) 		{ payload = NBTString.fromSNBT(snbt);  }

		
		return payload;
	}

}