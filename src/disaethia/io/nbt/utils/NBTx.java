package disaethia.io.nbt.utils;

import disaethia.io.nbt.NBTCompound;

/*
 * NBTx class to convert NBTTags to compressed versions and back. Used for registries.
	- Key names are stored and referenced with an index
		- And stored using huffman coding if that's smaller
	- Run length encoding on byte arrays
		- Or more specific encodings on speciifc file types
 */
public class NBTx {
	
	public static NBTx fromCompound(NBTCompound compound) {
		return null;
		
	}

}
