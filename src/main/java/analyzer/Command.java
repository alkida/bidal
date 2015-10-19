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
import java.util.LinkedList;

/**
 * The Class Command. A command is characterized by a type, a name, a list of parameters, a datasource and a textual representation
 */
public class Command implements Serializable{
	
	/** The type. */
	String type;
	
	/** The name. */
	String name;
	
	/** The parameters. */
	LinkedList<String> parameters;
	
	/** The original text command. */
	String command;
	
	/** The storage. */
	transient GenericStorage gs;
	
	/** The storage index. */
	int storageidx;
	
	/** The data source. */
	AnalysisSource source;
	
	/** The textual representation of the command. */
	String savedCommand;
	
	/**
	 * Instantiates a new command.
	 *
	 * @param gs the storage
	 * @param type the type
	 * @param name the name
	 * @param parameters the parameters
	 * @param command the command
	 */
	public Command(GenericStorage gs, String type, String name, LinkedList<String> parameters, String command){
		storageidx = Controller.getInstance().getStorages().indexOf(gs);
		this.gs = gs;
		this.type = type;
		this.name = name;
		this.parameters = parameters;
		this.command = command;
	}
	
	/**
	 * Instantiates a new command (Copy Constructor).
	 *
	 * @param c the command to clone
	 */
	public Command(Command c){
		this.storageidx = c.storageidx;
		this.gs = c.getStorage();
		this.name = c.name;
		this.parameters = new LinkedList<String>(c.parameters);
		this.command = c.command;
		this.source = source;
	}
	
	/**
	 * Recalculate the textual representation of the command by replacing all parameters.
	 */
	public void regenerateCommand(){
		String ret = command;
		int i=1;
		for( String s : parameters ){
			ret = ret.replace("$PAR"+i+"$", s);
			i++;
		}
		savedCommand =  ret;
	}
	
	
	/**
	 * Sets the data source.
	 *
	 * @param as the data source
	 */
	public void setTable(AnalysisSource as){
		this.source = as;
	}
	
	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	public AnalysisSource getTable(){
		return source;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName(){
		return name + " @ " + getStorage().getStorageName();
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
	
	/**
	 * Sets a custom command by its textual representation.
	 *
	 * @param s the new command
	 */
	public void setCommand(String s){
		savedCommand = s;
	}
	
	/**
	 * Gets the saved command.
	 *
	 * @return the saved command
	 */
	public String getCommand(){
		return savedCommand;
	}
	
}
