package disaethia.io.nbt;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import disaethia.io.nbt.utils.ActualStringBuffer;

public class NBTCompound extends NBTTag implements Iterable<NBTNamedTag> {
	
	NBTNamedTag[] payload;
	
	public NBTCompound() 	{ this.TYPE = 10; this.payload = new NBTNamedTag[0]; }
	public NBTCompound(NBTNamedTag[] payload) 	{ this.TYPE = 10; this.payload = payload; }

	/**
	 * Adds a tag, and doesn't check for a duplicate with the same key.
	 * @param tag
	 */
	private void add(NBTNamedTag tag) {
		NBTNamedTag[] new_arr = new NBTNamedTag[payload.length+1];
		for (int i = 0; i < new_arr.length; i++) {
			new_arr[i] = tag;
			if (i != new_arr.length-1) new_arr[i] = payload[i];
		}
		payload = new_arr;
	}
	
	public void put(NBTNamedTag tag) {
		int ind = getFirstIndexOf(tag.name);
		if (ind < 0) add(tag);			// If the tag doesn't exist
		else payload[ind] = tag; 		// If the tag already exists
	}
	
	public int getFirstIndexOf(NBTString name) { 
		for (int i = 0; i < payload.length; i++) {
			if (payload[i].getName().equals(name.get())) return i;
		}
		return -1;
	}
	
	public void remove(int j) {
		NBTNamedTag[] new_arr = new NBTNamedTag[payload.length-1];
		for (int i = 0; i < new_arr.length; i++) {
			if (i == j) i++;
			new_arr[i] = payload[i];
		}
		payload = new_arr;
	}
	
	public NBTNamedTag get(int i) { return payload[i]; } 
	public NBTNamedTag get(String name) { 
		int idx = getFirstIndexOf(new NBTString(name)); 
		if (idx < 0) return null; 
		return get(idx); } 
	

	private boolean exists(String name) {
		int ind = getFirstIndexOf(new NBTString(name));
		return (ind >= 0);
	}
	
	public int getSize() { 
		int size = 0;
		
		for (int i = 0; i < payload.length; i++) {
			size += payload[i].getSize();
		}
		
		size += new NBTEnd().getSize(); // TAG_END
		return size; }
	
	public void write(ByteBuffer dest) {
		for (int i = 0; i < payload.length; i++) {
			payload[i].write(dest);
		}
		new NBTEnd().write(dest); // TAG_END
	}
	
	@Override
	public String toSNBT(boolean formatted) {
		String string = "{\n";
		for (int i = 0; i < payload.length; i++) {
			string += "  "
				   +  payload[i].toSNBT(formatted).replace("\n", formatted ? "\n  " : "\n")
				   + ((i != payload.length-1) ? "," : "")
				   + "\n";
		}
		string += "}";
		return string;
	}
	
	public static NBTCompound fromSNBT(ActualStringBuffer snbt) throws ParseException {
		snbt.readChar(); // skip over opening {
		
		ArrayList<NBTNamedTag> tags = new ArrayList<NBTNamedTag>();
		while (snbt.currentChar() != '}') {
			tags.add(NBTNamedTag.fromSNBT(snbt));
			if (snbt.currentChar() != ',') break;
			snbt.readChar();
		}
		snbt.readChar(); // skip over '}'
		NBTNamedTag[] array = new NBTNamedTag[tags.size()];
		for (int i = 0; i < tags.size(); i++) {
			array[i] = tags.get(i);
		}
		
		return new NBTCompound(array);	}
	
	public static NBTCompound read(ByteBuffer source) {
		byte current_tag_type = -1;
		
		ArrayList<NBTNamedTag> tags = new ArrayList<NBTNamedTag>();
		while (current_tag_type != 0) {
			NBTNamedTag current_tag = NBTNamedTag.read(source);
			current_tag_type = current_tag.getType();
			if (current_tag_type != 0) tags.add(current_tag);
		}
		
		NBTNamedTag[] array = new NBTNamedTag[tags.size()];
		for (int i = 0; i < tags.size(); i++) {
			array[i] = tags.get(i);
		}
		
		return new NBTCompound(array);
	}
	
	public void print(int i) {
		print(i, "COMPOUND\n");
		
		for (int j = 0; j < payload.length; j++) {
			payload[j].print(i+1);
			print(i+1, "...\n");
		}
		
		print(i+1, "TAG_END\n");
	}
	
	public void put(String name, NBTTag value) { put (new NBTNamedTag(name, value)); }

	public void put(String name, byte value) { put (new NBTNamedTag(name, value)); }
	public void put(String name, short value) { put (new NBTNamedTag(name, value)); }
	public void put(String name, int value) { put (new NBTNamedTag(name, value)); }
	public void put(String name, long value) { put (new NBTNamedTag(name, value)); }
	public void put(String name, float value) { put (new NBTNamedTag(name, value)); }
	public void put(String name, double value) { put (new NBTNamedTag(name, value)); }
	public void put(String name, byte[] value) { put (new NBTNamedTag(name, value)); }
	public void put(String name, String value) { put (new NBTNamedTag(name, value)); }
	
	public void remove(String string) { remove(new NBTString(string)); }
	public void remove(NBTString string) { remove(getFirstIndexOf(string)); }
	
	
	
	
	public void findAndSet(String[] path, NBTTag tag) {
		// On filename part, so set the value.
		if (path.length == 1) {
			put(path[0], tag);
			return;
		} else { // Still searching for the correct compound
			if (!exists(path[0])) put(path[0], new NBTCompound());
			
			// The next compound on the path
			NBTCompound next = get(path[0]).getCompound();
			
			// Remove first element of path
			String[] newpath = new String[path.length-1];
			for (int i = 0; i < newpath.length; i++) {
				newpath[i] = path[i+1];
			}
			
			// Recursive!!!!
			next.findAndSet(newpath, tag);
		}
	}
	public NBTNamedTag findAndGet(String[] path) {
		if (path.length == 0) return new NBTNamedTag("", this); // i'm surprised i never ran into this edge case until now
		
		// On filename part, so get the value.
		if (path.length == 1) {
			if (!exists(path[0])) return null;
			return get(path[0]);
		} else { // Still searching for the correct compound
			if (!exists(path[0])) return null;
			
			// The next compound on the path
			NBTCompound next = get(path[0]).getCompound();
			
			// Remove first element of path
			String[] newpath = new String[path.length-1];
			for (int i = 0; i < newpath.length; i++) {
				newpath[i] = path[i+1];
			}
			
			// Recursive!!!!
			return next.findAndGet(newpath);
		}
	}
	
	public int length() { return payload.length; }
	
	
	
	@Override public Iterator<NBTNamedTag> iterator() { return new NBTCompoundIterator(this); }
	
	public static class NBTCompoundIterator implements Iterator<NBTNamedTag> {
		NBTCompound compound;
		int index = 0;
		public NBTCompoundIterator(NBTCompound c) { compound = c; }
		@Override public boolean hasNext() { return index < compound.length(); }
		@Override public NBTNamedTag next() { index++; return compound.get(index-1); } 
	}
	
}
