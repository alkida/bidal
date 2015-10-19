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

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

// TODO: Auto-generated Javadoc
/**
 * The Class ManageSourcesWindow.
 * It is the GUI for manage the data sources.
 */
public class ManageSourcesWindow extends JFrame {

	/**
	 * Button open pressed. It opens a window that allows the user to choose a file or a directory that specifies the storage.
	 */
	void buttonOpenPressed(){
		int storageidx = getListStorageTypes().getSelectedIndex();
		if( storageidx == -1 )return;
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = fc.showOpenDialog(this);
		if( returnVal == 0 ){
			String selected = fc.getSelectedFile().getPath();
			DefaultListModel dlm = (DefaultListModel)getListStorages().getModel();
			GenericStorage gs = StorageFactory.newStorage(storageidx+1, selected);
			Controller.getInstance().getStorages().add(gs);
			dlm.addElement(gs.getStorageName());
		}
	}
	
	/**
	 * Button new pressed. It opens a window that allows the user to choose a file or a directory that specifies the new storage.
	 */
	void buttonNewPressed(){
		int storageidx = getListStorageTypes().getSelectedIndex();
		if( storageidx == -1 )return;
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = fc.showOpenDialog(this);
		if( returnVal == 0 ){
			
			String selected = fc.getSelectedFile().getPath();
			File f = new File(selected);
			System.out.println(selected);
			//f.mkdirs(); 
			try {
				f.createNewFile();
			} catch (IOException e) {

			}
			DefaultListModel dlm = (DefaultListModel)getListStorages().getModel();
			GenericStorage gs = StorageFactory.newStorage(storageidx+1, selected);
			Controller.getInstance().getStorages().add(gs);
			dlm.addElement(gs.getStorageName());
		}
	}
	
	
	@Override
	public void setVisible(boolean b){
		if(firstTime){
			init();
			firstTime = false;
		}
		if( b ){
			DefaultListModel dlm = (DefaultListModel)getListStorageTypes().getModel();
			dlm.clear();
			for(int i=0;i<StorageFactory.getNumberStorages();i++){
				String stname = StorageFactory.getNameOfType(i+1);
				dlm.addElement(stname);
			}
			DefaultListModel dlm2 = (DefaultListModel)getListStorages().getModel();
			dlm2.clear();
			for(GenericStorage gs : Controller.getInstance().getStorages()){
				String stname = gs.getStorageName();
				dlm2.addElement(stname);
			}
		}
		super.setVisible(b);
	}
	
	/**
	 * Inits the variables.
	 */
	void init(){
		//GenericStorage gs = StorageFactory.newStorage(1, "/home/me/tests/test");
		//Controller.getInstance().getStorages().add(gs);
		/*GenericStorage gs3 = StorageFactory.newStorage(3, "/home/me/testBash/");
		Controller.getInstance().getStorages().add(gs3);
		GenericStorage gs2 = StorageFactory.newStorage(1, "/home/me/testR/test");
		Controller.getInstance().getStorages().add(gs2);
		GenericStorage gs1 = StorageFactory.newStorage(2, "/home/me/Downloads/hadoop-1.2.1");
		Controller.getInstance().getStorages().add(gs1);*/
	}
		
	
	private boolean firstTime = true;
	////////////////////////////////////////////////////////////////////////////////////
	// BEGIN GENERATED CODE                                                           //
	////////////////////////////////////////////////////////////////////////////////////
	
	
	/** The content pane. */
	private JPanel contentPane;
	
	/** The list_1. */
	private JList list_1;
	
	/** The list. */
	private JList list;
	
	/** The scroll pane. */
	private JScrollPane scrollPane;
	
	/** The scroll pane_1. */
	private JScrollPane scrollPane_1;


	
	/**
	 * Instantiates a new manage sources window.
	 */
	public ManageSourcesWindow() {
		setBounds(100, 100, 590, 396);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		scrollPane_1 = new JScrollPane();
		
		list = new JList(new DefaultListModel());
		scrollPane_1.setViewportView(list);
		
		JButton btnNewButton = new JButton("Open");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonOpenPressed();
			}
		});
		
		scrollPane = new JScrollPane();
		
		list_1 = new JList(new DefaultListModel());
		scrollPane.setViewportView(list_1);
		
		JButton btnNew = new JButton("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonNewPressed();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(17)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
					.addGap(12)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnNew, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE))
					.addGap(12)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
					.addGap(7))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(7)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
					.addGap(7))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(7)
					.addComponent(btnNew)
					.addGap(12)
					.addComponent(btnNewButton))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(3)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
					.addGap(7))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	/**
	 * Gets the list storage types.
	 *
	 * @return the list storage types
	 */
	private JList getListStorageTypes() {
		return list_1;
	}
	
	/**
	 * Gets the list storages.
	 *
	 * @return the list storages
	 */
	private JList getListStorages() {
		return list;
	}
}
