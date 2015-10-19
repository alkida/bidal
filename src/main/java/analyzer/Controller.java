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

import java.util.LinkedList;

/**
 * The Class Controller. It is a singleton that connects all classes
 */
public class Controller {
	
	/** The main window. */
	private MainWindow mainwindow;
	
	/** The load window. */
	private LoadWindow loadwindow;
	
	/** The manage sources window. */
	private ManageSourcesWindow mswindow;
	
	/** The manage tables window. */
	private ManageTablesWindow mtwindow;
	
	/** The analysis window. */
	private AnalysisWindow analysiswindow;
	
	/** The opened storages. */
	private LinkedList<GenericStorage> storages;
	
	/**
	 * Instantiates a new controller.
	 */
	private Controller(){
		storages = new LinkedList<GenericStorage>();
	}
	
	/**
	 * Gets the storage list.
	 *
	 * @return the storage list
	 */
	public LinkedList<GenericStorage> getStorages(){
		return storages;
	}
	
	/**
	 * Gets the load window.
	 *
	 * @return the load window
	 */
	public LoadWindow getLoadWindow(){
		return loadwindow;
	}
	
	/**
	 * Gets the manage sources window.
	 *
	 * @return the manage sources window
	 */
	public ManageSourcesWindow getManageSourcesWindow(){
		return mswindow;
	}
	
	/**
	 * Gets the manage tables window.
	 *
	 * @return the manage tables window
	 */
	public ManageTablesWindow getManageTablesWindow(){
		return mtwindow;
	}
	
	/**
	 * Gets the analysis window.
	 *
	 * @return the analysis window
	 */
	public AnalysisWindow getAnalysisWindow(){
		return analysiswindow;
	}
	
	
	/**
	 * initialize all variables and starts the GUI.
	 */
	public void start(){
		mainwindow = new MainWindow();
		loadwindow = new LoadWindow();
		mswindow = new ManageSourcesWindow();
		mtwindow = new ManageTablesWindow();
		analysiswindow = new AnalysisWindow();
		mainwindow.setVisible(true);
	}
	
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]){
		Controller.getInstance().start();
	}
	
	/**
	 * The Class ControllerContainer.
	 */
	private static class ControllerContainer{
		
		/** The Constant INSTANCE. */
		private final static Controller INSTANCE = new Controller();
	}
	
	/**
	 * Gets the single instance of Controller.
	 *
	 * @return single instance of Controller
	 */
	public static Controller getInstance(){
		return ControllerContainer.INSTANCE;
	}
}
