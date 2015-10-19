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
 * The Class HadoopStorage. It manages a storage that uses Apache Hadoop programs to handle the data.
 */
public class HadoopStorage extends GenericStorage {

	/** The name of the storage (its path). */
	private String dbname;
	
	/** The bash executer used to execute system commands (Commands used to pass files to Hadoop File System). */
	BashExecuter b;
	
	/** The R executer used to execute RHadoop commands (Actual executor of Hadoop programs). */
	private Rexecuter r;
	
	/**
	 * Instantiates a new hadoop storage.
	 *
	 * @param name the name
	 */
	public HadoopStorage(String name){
		dbname = name;
		b = new BashExecuter();
		b.exec("cd "+name);
		resetRExecuter();
	}
	
	@Override
	public void loadData(String tablename, String[] files) {
		b.exec("rm -f /tmp/hadooptemp");
		for (String s:files){
			b.exec("cat "+s+ " >> /tmp/hadooptemp");
		}
		b.exec("bin/hadoop fs -put /tmp/hadooptemp input/" +tablename);

	}

	@Override
	public String[] getTableNames() {
		String ret = b.exec("bin/hadoop fs -lsr input | awk -F/ '{ print $(NF) }'");
		if( ret.equals("") )return null;
		String lines[] = ret.split("\n");
		return lines;
	}

	@Override
	public String getStorageName() {
		return dbname + "@ Hadoop";
	}

	/**
	 * Gets the generic name.
	 *
	 * @return the generic name
	 */
	public static String getGenericName(){
		return "Hadoop";
	}

	@Override
	public String[][] getPreviewForTable(String table, int npreview) {
		String ret[][] = b.execTable("bin/hadoop fs -cat input/"+table+ "| head -n "+npreview);
		return ret;
	}

	@Override
	public void removeTable(String table) {
		b.exec("bin/hadoop fs -rm -skipTrash input/"+table);
	}

	@Override
	protected String getMappedQuery(String dest, StatementNode querytree) {
		String ret = "";
		HadoopVisitor sv = new HadoopVisitor();
		try {
			querytree.accept(sv);
			ret += sv.getGeneratedCommand();
		} catch (StandardException e) {e.printStackTrace();}
		
		ret += 
			"mapredfun <- function (input, output=NULL) {\n" +
			"   mapreduce(input=input, output=output, input.format=make.input.format(format =\"csv\",sep=\",\"), map=map, reduce=reduce) \n" +
			"} \n" +
			"hdfs.root <- 'input'\n" + 
			"hdfs.rootout <- 'output'\n" + 
			"hdfs.data <- file.path(hdfs.root, source)\n" +
			"hdfs.out <- file.path(hdfs.rootout, 't')\n" +
			"out <- mapredfun(hdfs.data, hdfs.out)\n"+
			"results <- from.dfs(out)\n"+
			"save <- file.path(hdfs.root,'"+dest+"')\n"+
			"to.dfs(results,output=save,format=make.output.format(format =\"csv\",sep=\",\"))\n";
		
		return ret;
	}

	@Override
	public void executeMappedQuery(String query) {
		r.exec(query);
		b.exec("rm -rf output/t");
	}

	@Override
	public List<Command> getCommands() {
		List<Command> commands = new LinkedList<Command>();
		String foldername = "hadoopcommands";
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
					//System.out.println("name "+name + " content "+content);
					commands.add(new Command(this,foldername2,name,params,content));
				}catch(Exception e){e.printStackTrace();}
			}
		}
		return commands;
	}

	@Override
	public boolean execCommand(Command c, AnalysisSource as) {
		b.exec("rm -rf output/t");
		System.out.println(getStorageName()+ " : New Source: "+as);
		if( c != null )System.out.println(getStorageName()+ " : Command "+c.getCommand());
		if( as != null ){
			if( r.exec("source = '"+as.getTableName()+"';") == null ){
				resetRExecuter();
				return false;
			}
		}else{
			if( r.exec("source = 't';") == null ){
				resetRExecuter();
				return false;
			}
		}
		if( c != null ){
			if( r.exec(c.getCommand()) == null){
				resetRExecuter();
				return false;
			}
			importTemp();
			b.exec("rm -rf output/t");
		}
		return true;
	}

	@Override
	public boolean exportTemp() {
		b.exec("bin/hadoop fs -get input/t /tmp/temp.csv");
		return true;
	}

	@Override
	public boolean importTemp() {
		b.exec("bin/hadoop fs -rm -skipTrash input/t");
		b.exec("bin/hadoop fs -put /tmp/temp.csv input/t");
		return true;
	}

	/* (non-Javadoc)
	 * @see analyzer.GenericStorage#previewTemp(int)
	 */
	@Override
	public String[][] previewTemp(int n) {
		String ret[][] = b.execTable("bin/hadoop fs -cat input/t | head -n "+n);
		return ret;
	}

	
	/**
	 * Reset r executer.
	 */
	private void resetRExecuter(){
		r = new Rexecuter();
		////r.exec("Sys.setenv(\"HADOOP_HOME\"=\"/home/me/Downloads/hadoop-1.2.1\");library(rmr2)");
		//HADOOP_HOME variable must be exported in some bash configuration file, with a command like
		//export HADOOP_HOME=/path/to/hadoop-1.2.1/
	    r.exec("library(rmr2)");
		r.exec("setwd(\""+dbname+"\");");
	}

	
}
