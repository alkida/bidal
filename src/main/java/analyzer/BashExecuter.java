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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * The Class BashExecuter. It executes bash commands allowing to get the output.
 */
public class BashExecuter {
	
	/** The output of bash commands. */
	InputStream is;
	
	/** The input given to bash. */
	PrintStream ps;
	
	/** The error output of bash commands. */
	InputStream es;

	
	/**
	 * Instantiates a new bash executer.
	 */
	public BashExecuter(){
		ProcessBuilder pb = new ProcessBuilder("bash");
		Process p;
		try {
			p = pb.start();
			ps = new PrintStream(p.getOutputStream());
			is = p.getInputStream();
			es = p.getErrorStream();
		} catch (IOException e) {e.printStackTrace();}
		
	}
	
	/**
	 * It executes a bash command.
	 *
	 * @param s the command to execute
	 * @return the output of the executed command
	 */
	public String exec(String s){
		ps.print(s+"\necho TAG\n");
		ps.flush();
		String ret = "";
		int c;
		try {
			while( es.available() > 0){
				c=es.read();
				System.out.print((char)c);
			}
			do{
				c=is.read();
				ret += (char)c;
				System.out.print((char)c);
			}while( ret.indexOf("TAG") == -1 );
		} catch (IOException e) {e.printStackTrace();}
		String lines[] = ret.split("\n");
		String ret2 = "";
		for(int i=1;i<lines.length-1;i++){
			ret2 += lines[i]+"\n";
		}
		return ret2;
	}
	
	/**
	 * It executes a command that gives a table in output.
	 *
	 * @param s the command to be executed
	 * @return the table
	 */
	String[][] execTable(String s){
		String r = exec(s);
		String v[] = r.split("\n");
		int rows = v.length;
		if( rows == 0 )return null;
		int n = Integer.parseInt(exec("echo \""+v[0]+"\" | tr ',' '\\n' | wc -l").replace("\n","").trim());
		System.out.printf("SIZE: %d %d\n",rows,n);
		String ret[][] = new String[rows][n];
		for(int i=0;i<rows;i++){
			String t[] = v[i].split(",");
			System.out.println(""+t.length);
			for(int j=0;j<n;j++){
				ret[i][j] = t[j];
			}
		}
		return ret;
	}
}
