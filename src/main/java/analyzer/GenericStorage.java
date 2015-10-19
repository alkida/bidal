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

import java.util.List;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.SQLParser;
import com.akiban.sql.parser.StatementNode;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericStorage. It represents a storage and it can be extended to provide a possible source of data.
 */
public abstract class GenericStorage {
	
	/**
	 * It loads data from a list of files, creating a new table.
	 *
	 * @param tablename the table name
	 * @param files the files to load inside the table
	 */
	public abstract void loadData(String tablename, String files[]);
	
	/**
	 * Gets the tables available in this storage
	 *
	 * @return the table names
	 */
	public abstract String[] getTableNames();
	
	/**
	 * Gets the storage name.
	 *
	 * @return the storage name
	 */
	public abstract String getStorageName();
	
	/**
	 * Gets the preview for a table.
	 *
	 * @param table the table name
	 * @param npreview number of rows of the preview
	 * @return the preview of the table
	 */
	public abstract String[][] getPreviewForTable(String table, int npreview);
	
	/**
	 * Deletes a table
	 *
	 * @param table the name of the table to remove.
	 */
	public abstract void removeTable(String table);
	
	/**
	 * Gets the command that have to be executed on the storage, in order to satisfy the SQL statement passed (as a tree).
	 *
	 * @param dest the name of the thable that will be created
	 * @param querytree the SQL query tree
	 * @return the mapped query
	 */
	protected abstract String getMappedQuery(String dest, StatementNode querytree);
	
	/**
	 * Execute the command that represents the mapped query.
	 *
	 * @param query the query
	 */
	public abstract void executeMappedQuery(String query);
	
	/**
	 * Gets the list of possible commands that can be executed on this storage.
	 *
	 * @return the commands
	 */
	public abstract List<Command> getCommands();
	
	/**
	 * It executes a command on the specified data source.
	 *
	 * @param c the command
	 * @param as the data source
	 * @return true, if successful
	 */
	public abstract boolean execCommand(Command c, AnalysisSource as);
	
	/**
	 * Export the result of the last executed command on a temporary file (used to pass information between storages).
	 *
	 * @return true, if successful
	 */
	public abstract boolean exportTemp();
	
	/**
	 * Import the temporary file created by a different data source (used to pass information between storages).
	 *
	 * @return true, if successful
	 */
	public abstract boolean importTemp();
	
	/**
	 * Give a preview of the result of the last executed command.
	 *
	 * @param n the number of lines to return
	 * @return the table
	 */
	public abstract String[][] previewTemp(int n);
	
	
	/**
	 * Gets the generic name of the storage.
	 *
	 * @return the generic name
	 */
	public static String getGenericName(){
		return null;
	}

	/**
	 * Gets the mapped query, converting the SQL statement in a tree, and converting this tree in its associated command.
	 *
	 * @param dest the table to create
	 * @param query the SQL statement
	 * @return the mapped query (a storage-specific command)
	 */
	public String getMappedQuery(String dest, String query){
		System.out.println(""+dest + " "+query);
		SQLParser parser = new SQLParser();
		StatementNode stmt = null;
		try {
			stmt = parser.parseStatement(query);
		} catch (StandardException e) {e.printStackTrace(); return "";}
		String ret = getMappedQuery(dest,stmt);
		return ret;
	}
	
	
}
