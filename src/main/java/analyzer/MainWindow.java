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

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class MainWindow.
 */
public class MainWindow {
	

	/**
	 * It opens the load window.
	 */
	void buttonLoadPressed(){
		Controller.getInstance().getLoadWindow().setVisible(true);
	}
	
	/**
	 * It opens the manage sources window.
	 */
	void buttonManageSourcesPressed(){
		Controller.getInstance().getManageSourcesWindow().setVisible(true);
	}
	
	/**
	 * It opens the manage tables window.
	 */
	void buttonManageTablesPressed(){
		Controller.getInstance().getManageTablesWindow().setVisible(true);
	}
	
	/**
	 * It opens the analysis window.
	 */
	void buttonAnalysisPressed(){
		Controller.getInstance().getAnalysisWindow().setVisible(true);
	}
	
	
	/**
	 * Sets the window visible
	 *
	 * @param visible true, if the window must be set to visible
	 */
	public void setVisible(boolean visible){
		frame.setVisible(true);
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// BEGIN GENERATED CODE                                                           //
	////////////////////////////////////////////////////////////////////////////////////
	
	
	/** The frame. */
	private JFrame frame;


	/**
	 * Instantiates a new main window.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 713, 526);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Load Files");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonLoadPressed();
			}
		});
		btnNewButton.setBounds(12, 49, 152, 25);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnManageSources = new JButton("Manage Sources");
		btnManageSources.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonManageSourcesPressed();
			}
		});
		btnManageSources.setBounds(12, 12, 152, 25);
		frame.getContentPane().add(btnManageSources);
		
		JButton btnNewButton_1 = new JButton("Manage Tables");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonManageTablesPressed();
			}
		});
		btnNewButton_1.setBounds(12, 86, 152, 25);
		frame.getContentPane().add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Analysis");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonAnalysisPressed();
			}
		});
		btnNewButton_2.setBounds(12, 123, 152, 25);
		frame.getContentPane().add(btnNewButton_2);
	}
}
