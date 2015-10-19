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

// TODO: Auto-generated Javadoc
/**
 * The Class Rexecuter.
 */
public class Rexecuter {

	/** The output of R commands. */
	InputStream is;
	
	/** The input given to R. */
	PrintStream ps;
	
	/** The error output of R commands. */
	InputStream es;

	
	/**
	 * Instantiates a new R executer.
	 */
	public Rexecuter(){
		ProcessBuilder pb = new ProcessBuilder("R","--no-save","--silent");
		Process p;
		try {
			p = pb.start();
			ps = new PrintStream(p.getOutputStream());
			is = p.getInputStream();
			es = p.getErrorStream();
		} catch (IOException e) {e.printStackTrace();}
		exec("options(echo=F);");
	}
	
	/**
	 * It executes a R command.
	 *
	 * @param s the command to execute
	 * @return the output of the executed command
	 */
	public String exec(String s){
		ps.print(s+"\nprint('TAG')\n");
		ps.flush();
		String ret = "";
		int c;
		try {
			do{
				c=is.read();
				ret += (char)c;
				if( c == -1 ){
					boolean b = false;
					while( es.available() > 0){
						c=es.read();
						System.out.print((char)c);
						b = true;
					}
					if( b ){
						return null;
					}
					try{ Thread.sleep(100); }catch(Exception e){}
				}else
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
		String s2 = "t="+s+";t2 = length(t); cat(c(t2,\"\\n\")); apply(t,1,function(x){cat(x,sep=\"\\n\")})";
		String r = exec(s2);
		String v[] = r.split("\n");
		int n = Integer.parseInt(v[0].replaceAll("\\W", "").trim());
		int rows = (v.length-1)/n;
		String ret[][] = new String[rows][n];
		for(int i=0;i<rows;i++){
			for(int j=0;j<n;j++){
				ret[i][j] = v[i*n+j+1];
			}
		}
		return ret;
	}

}
