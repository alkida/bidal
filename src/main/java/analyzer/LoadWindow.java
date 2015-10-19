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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

// TODO: Auto-generated Javadoc
/**
 * The Class LoadWindow.
 * It is the GUI used to create a new table from a list of files
 */
public class LoadWindow extends JFrame {

	/**
	 * It opens a window that allows the user to choose a list of files.
	 */
	void buttonChooseFilesPressed(){
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(getFrame());
		if( returnVal == 0 ){
			File selected[] = fc.getSelectedFiles();
			DefaultListModel dlm = (DefaultListModel)getListChoosenFiles().getModel();
			for(int i=0;i<selected.length;i++){
				dlm.addElement(selected[i].getPath());
			}
		}
		
	}
	
	/**
	 * It removes all the entries that are present in the list of selected files.
	 */
	void buttonClearPressed(){
		DefaultListModel dlm = (DefaultListModel)getListChoosenFiles().getModel();
		dlm.clear();
	}
	
	/**
	 * It tells to the storage to load the chosen files and to create the table with the specified name.
	 */
	void buttonLoadPressed(){
		String tableName = getTextTableName().getText();
		int storageidx = getListDestination().getSelectedIndex();
		if( storageidx == -1 || tableName.length() == 0 )return;
		
		DefaultListModel dlm = (DefaultListModel)getListChoosenFiles().getModel();
		String filenames[] = new String[dlm.getSize()];
		for(int i=0;i<dlm.getSize();i++){
			filenames[i] = (String)dlm.elementAt(i);
		}
		Controller.getInstance().getStorages().get(storageidx).loadData(tableName, filenames);
	}
	
	
	@Override
	public void setVisible(boolean b){
		if( b ){
			DefaultListModel dlm = (DefaultListModel)getListDestination().getModel();
			dlm.clear();
			for( GenericStorage gs : Controller.getInstance().getStorages() ){
				dlm.addElement(gs.getStorageName());
			}
		}
		super.setVisible(b);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////
	// BEGIN GENERATED CODE                                                           //
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/** The content pane. */
	private JPanel contentPane;
	
	/** The list. */
	private JList list;
	
	/** The list_1. */
	private JList list_1;
	
	/** The text field. */
	private JTextField textField;


	/**
	 * Instantiates a new load window.
	 */
	public LoadWindow() {
		setBounds(100, 100, 853, 442);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JButton btnChooseFiles = new JButton("Choose Files");
		btnChooseFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonChooseFilesPressed();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		
		list = new JList(new DefaultListModel());
		scrollPane.setViewportView(list);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonClearPressed();
			}
		});
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		list_1 = new JList(new DefaultListModel());
		scrollPane_1.setViewportView(list_1);
		
		JLabel lblDestination = new JLabel("Destination:");
		
		textField = new JTextField();
		textField.setColumns(10);
		
		JLabel lblTableName = new JLabel("Table Name:");
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonLoadPressed();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnChooseFiles, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
							.addComponent(btnClear, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE))
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
					.addGap(32)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane_1)
							.addGap(26))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblDestination, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
							.addGap(79)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTableName, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
							.addComponent(btnLoad, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnClear)
							.addComponent(lblDestination)
							.addComponent(lblTableName))
						.addComponent(btnChooseFiles))
					.addGap(12)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnLoad, Alignment.TRAILING))
					.addGap(7))
		);
		contentPane.setLayout(gl_contentPane);
		
	}
	
	/**
	 * Gets the list choosen files.
	 *
	 * @return the list choosen files
	 */
	private JList getListChoosenFiles() {
		return list;
	}
	
	/**
	 * Gets the list destination.
	 *
	 * @return the list destination
	 */
	private JList getListDestination() {
		return list_1;
	}
	
	/**
	 * Gets the frame.
	 *
	 * @return the frame
	 */
	private JPanel getFrame() {
		return contentPane;
	}
	
	/**
	 * Gets the text table name.
	 *
	 * @return the text table name
	 */
	private JTextField getTextTableName() {
		return textField;
	}
}
