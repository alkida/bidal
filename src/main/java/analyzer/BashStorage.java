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
import java.util.LinkedList;
import java.util.List;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.StatementNode;

/**
 * The Class BashStorage. It manages a storage that uses bash commands to handle the data.
 */
public class BashStorage extends GenericStorage{

	/** The name of the storage (its path). */
	String name;
	
	/** The bash executer used to execute bash commands. */
	BashExecuter b;
	
	/**
	 * Instantiates a new bash storage.
	 *
	 * @param name the name
	 */
	public BashStorage(String name){
		this.name = name;
		b = new BashExecuter();
		b.exec("cd "+name);
	}
	
	@Override
	public void loadData(String tablename, String[] files) {
		String command = "cat ";
		for( String s : files )command += " "+s+" ";
		command += " > "+tablename+".csv";
		b.exec(command);
	}

	@Override
	public String[] getTableNames() {
		String ret = b.exec("ls *.csv");
		if( ret.equals("") )return null;
		String lines[] = ret.split("\n");
		return lines;
	}

	@Override
	public String getStorageName() {
		return name + " @ Bash";
	}
	
	/**
	 * Gets the generic name.
	 *
	 * @return the generic name
	 */
	public static String getGenericName(){
		return "Bash";
	}

	@Override
	public String[][] getPreviewForTable(String table, int npreview) {
		String ret[][] = b.execTable("cat "+table+ "| head -n "+npreview);
		return ret;
	}

	@Override
	public void removeTable(String table) {
		b.exec("rm "+table);
		
	}

	@Override
	protected String getMappedQuery(String dest, StatementNode querytree) {
		
		BashVisitor sv = new BashVisitor();
		String ret = "";
		try {
			querytree.accept(sv);
			ret += sv.getGeneratedCommand();
		} catch (StandardException e) {e.printStackTrace();}
		ret +=  " > "+dest;
		
		
		return ret;
	}

	@Override
	public void executeMappedQuery(String query) {
		b.exec(query);	
		
	}

	@Override
	public List<Command> getCommands() {
		List<Command> commands = new LinkedList<Command>();
		String foldername = "bashcommands";
		File folder = new File(foldername);
		int j=0;
		for (final File fileEntry : folder.listFiles()) {
			String foldername2 = fileEntry.getName();
			File folder2 = new File(foldername+"/"+foldername2);
			for (final File fileEntry2 : folder2.listFiles()){
				String s = fileEntry2.getName();
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
		if( as != null ){
			b.exec("cp "+as.getTableName()+" t.csv");
		}
		if( c != null ){
			b.exec( c.getCommand() );
		}
		return true;
	}

	@Override
	public boolean exportTemp() {
		b.exec("cp t.csv /tmp/temp.csv");
		return true;
	}

	@Override
	public boolean importTemp() {
		b.exec("cp /tmp/temp.csv t.csv");
		return true;
	}

	@Override
	public String[][] previewTemp(int n) {
		String ret[][] = b.execTable("cat t.csv | head -n "+n);
		return ret;
	}

}
