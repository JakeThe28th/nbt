package disaethia.io.nbt;

public class QNBTR {
	
	
	// DEBUG. Print a tree of the whole file.
	public void tree() {
		
	}
	
	//public void getString()
	
	// Return an instance of this class w/ data
	public static QNBTR readFromFile(String filename) {
		return null;
	}
	
	/**
	 * Get the NBT Tag at this path as an NBTTag object.<br>
	 * Features: Caches the indexes of every tag as it searches.<br>
	 * 		 <li><idt>Named tags are cached as <...>.NAME<br>
	 *       <li>Unnamed tags in lists are cached as <...>.INDEX<br>
	 *       <li>Example: level.data.list.0.banna<br>
	 *       
	 * First searches the cache for the given path, if not found or only partial then start there and search file<br>
	 * <br>
	 * Once obtained, the NBTTag object is completely decoupled from this tag, and can be used independently.
	 * <br>
	 * @param path
	 * @return
	 */
	public NBTTag get(String path) {
		return null;
	}
	

}
