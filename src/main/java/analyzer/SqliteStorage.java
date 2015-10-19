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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.StatementNode;

// TODO: Auto-generated Javadoc
/**
 * The Class SqliteStorage. It manages a storage that uses R and SQLite to handle the data.
 */
public class SqliteStorage extends GenericStorage {

	/** The name of the storage (its path). */
	private String dbname;
	
	/** The R executer used to execute R commands. */
	private Rexecuter r;
	
	
	/**
	 * Instantiates a new sqlite storage.
	 *
	 * @param name the name
	 */
	public SqliteStorage(String name){
		dbname = name;
		resetRExecuter();
	}
	
	@Override
	public void loadData(String tablename, String[] files) {
		for(String s : files){
			r.exec("dbWriteTable(con, name=\""+tablename+"\", value=\""+s+"\", row.names=FALSE, header=FALSE, sep=\",\", append=T)");
		}
	}

	@Override
	public String[] getTableNames() {
		String ret = r.exec("dbGetQuery(con,\"SELECT name FROM sqlite_master WHERE type='table'\");");
		String lines[] = ret.split("\n");
		String[] names = new String[lines.length-1];
		for(int i=1;i<lines.length;i++){
			String t[] = lines[i].split("\\s+");
			names[i-1] = t[1];
		}
		return names;
	}

	@Override
	public String getStorageName() {
		return dbname + " @ SQLite";
	}
	
	/**
	 * Gets the generic name.
	 *
	 * @return the generic name
	 */
	public static String getGenericName(){
		return "SQLite";
	}

	@Override
	public String[][] getPreviewForTable(String table, int npreview) {
		String ret[][] = r.execTable("dbGetQuery(con, \"select * from " +table+ " limit "+npreview+"\")");
		return ret;
	}

	@Override
	public void removeTable(String table) {
		r.exec("dbSendQuery(con, \"drop table "+ table + " \")");
	}

	@Override
	protected String getMappedQuery(String dest, StatementNode querytree) {

		String ret = "dbGetQuery(con,\"CREATE TABLE "+dest+" AS ";
		SqliteVisitor sv = new SqliteVisitor();
		try {
			querytree.accept(sv);
			ret += sv.getGeneratedCommand();
		} catch (StandardException e) {e.printStackTrace();}
		ret +=  " ;\");";
		
		
		return ret;
	}

	@Override
	public void executeMappedQuery(String query) {
		r.exec(query);	
		resetRExecuter();
	}

	@Override
	public List<Command> getCommands() {
		List<Command> commands = new LinkedList<Command>();
		String foldername = "rcommands";
		File folder = new File(foldername);
		int j=0;
		for (final File fileEntry : folder.listFiles()) {
			String foldername2 = fileEntry.getName();
			File folder2 = new File(foldername+"/"+foldername2);
			for (final File fileEntry2 : folder2.listFiles()){
				String s = fileEntry2.getName();
				//System.out.println(">>>"+s);
				try{
					BufferedReader br = new BufferedReader(new FileReader(foldername+"/"+foldername2+"/"+s));
					String line = null;
					String name = null;
					int nparams = 0;
					LinkedList<String> params = new LinkedList<String>();
					String content = "";
					int n =0;
					while ((line = br.readLine()) != null) {
						if(n==0)name=line;
						else if(n==1)nparams=Integer.parseInt(line.trim());
						else if(n<nparams+2 )params.add(line);
						else content += line+"\n";
						n++;
					}
					commands.add(new Command(this,foldername2,name,params,content));
				}catch(Exception e){e.printStackTrace();}
			}
		}
		return commands;
	}

	@Override
	public boolean execCommand(Command c, AnalysisSource as) {
		System.out.println(getStorageName()+ " : New Source: "+as);
		if( c != null )System.out.println(getStorageName()+ " : Command "+c.getCommand());
		if( as != null ){
			if( r.exec("t=dbGetQuery(con, \"select * from "+as.getTableName()+"\")") == null ){
				resetRExecuter();
				return false;
			}
		}
		if( c != null ){
			if( r.exec(c.getCommand()) == null){
				resetRExecuter();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean exportTemp() {
		System.out.println(getStorageName()+ " : export");
		if( r.exec("write.table(t,\"/tmp/temp.csv\",sep=',', col.names=FALSE,row.names=F)") == null){
			resetRExecuter();
			return false;
		}
		return true;
	}

	@Override
	public boolean importTemp() {
		System.out.println(getStorageName()+ " : import");
		if( r.exec("t=read.csv(\"/tmp/temp.csv\",header=F,strip.white=TRUE)") == null){
			resetRExecuter();
			return false;
		}
		return true;
	}

	/**
	 * Reset R executer, called in case of errors.
	 */
	private void resetRExecuter(){
		r = new Rexecuter();
		r.exec("library(DBI); con<-dbConnect(RSQLite::SQLite(),\""+ dbname +"\");");
	}

	@Override
	public String[][] previewTemp(int n) {
		return r.execTable("head(t,10)");
	}

}
