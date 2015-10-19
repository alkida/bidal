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

import java.io.Serializable;

/**
 * The Class AnalysisSource.
 * It describes a possible source of data by the pair (table name, storage).
 */
public class AnalysisSource implements Serializable{

	/** The storage.  */
	transient GenericStorage gs;
	
	/** The table name. */
	String tablename;
	
	/** Used to recover the storage after its deserialization. */
	int storageidx;
	
	/**
	 * Instantiates a new analysis source.
	 *
	 * @param gs the storage
	 * @param tablename the table name
	 */
	public AnalysisSource(GenericStorage gs,String tablename){
		storageidx = Controller.getInstance().getStorages().indexOf(gs);
		this.gs = gs;
		this.tablename = tablename;
	}
	
	public String toString(){
		return tablename +" @ "+getStorage().getStorageName(); 
	}

	/**
	 * Gets the table name.
	 *
	 * @return the table name
	 */
	public String getTableName() {
		return tablename;
	}
	
	
	/**
	 * Gets the storage.
	 *
	 * @return the storage
	 */
	public GenericStorage getStorage(){
		if( gs == null ){
			gs=Controller.getInstance().getStorages().get(storageidx);
		}
		return gs;
	}
}
