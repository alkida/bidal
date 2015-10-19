//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see http://www.gnu.org/licenses/.
// 

package analyzer;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Storage objects.
 */
public class StorageFactory {
	
	/** The Constants that identifies storage types. */
	public static final int SQLITE = 1, HADOOP = 2, BASH=3;
	
	/**
	 * Create a new storage.
	 *
	 * @param storagetype the storage type
	 * @param name the name
	 * @return the storage of the specified type
	 */
	public static GenericStorage newStorage(int storagetype, String name){
		if( storagetype == SQLITE )return new SqliteStorage(name);
		else if( storagetype == HADOOP )return new HadoopStorage(name);
		else if( storagetype == BASH )return new BashStorage(name);
		return null;
	}
	
	/**
	 * Gets the generic name of a storage type.
	 *
	 * @param storagetype the id of a storage
	 * @return the name of the storage type
	 */
	public static String getNameOfType(int storagetype){
		if( storagetype == SQLITE )return SqliteStorage.getGenericName();
		else if( storagetype == HADOOP )return HadoopStorage.getGenericName();
		else if( storagetype == BASH )return BashStorage.getGenericName();
		return null;
	}
	
	/**
	 * Gets the number of available storage types.
	 *
	 * @return the number of storage types
	 */
	public static int getNumberStorages(){
		return 3;
	}
	
}

