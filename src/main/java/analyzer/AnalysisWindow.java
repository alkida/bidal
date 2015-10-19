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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSeparator;
import java.awt.Dimension;

/**
 * The Class AnalysisWindow.
 * It is the GUI for the analysis.
 */
public class AnalysisWindow extends JFrame {

	/** All the possible sources of data. */
	LinkedList<AnalysisSource> sourcelist = new LinkedList<AnalysisSource>();

	/** All commands grouped by type. */
	Map<String,LinkedList<Command>> commandlist = new TreeMap<String,LinkedList<Command>>();
	
	/** All commands choosen by the user. */
	LinkedList<Command> selectedcommandlist = new LinkedList<Command>();
	
	/** All the saved commands. */
	Map<String,LinkedList<Command>> scripts;
	
	
	/** Popup used to delete a command from the selected command list. */
	private JPopupMenu popup4;
	
	/**
	 * Creates the popup used to delete a command.
	 */
	public void popupListCommands() {
		popup4 = new JPopupMenu();
		ActionListener menuListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Delete")){
					int n =  getListSelectedCommands().getSelectedIndex();
					DefaultListModel dlm1 = (DefaultListModel)getListSelectedCommands().getModel();
					dlm1.remove(n);
					selectedcommandlist.remove(n);
				}
				updateCommandArea();
			}
		};
		JMenuItem item;
		popup4.add(item = new JMenuItem("Delete"));
		item.addActionListener(menuListener);
		popup4.setBorder(new BevelBorder(BevelBorder.RAISED));
	}

	
	
	/**
	 * Redraws the selected command list (called after changes).
	 */
	void redrawSelectedCommands(){
		DefaultListModel dlm = (DefaultListModel)getListSelectedCommands().getModel();
		dlm.clear();
		for( Command c : selectedcommandlist ){
			dlm.addElement(c.getName());
		}
	}
	
	/**
	 * Rewrites the area that shows the current command (called after changes).
	 */
	void updateCommandArea(){
		String s = "";
		int n = getListSelectedCommands().getSelectedIndex();
		if( n != -1  ){
			s = selectedcommandlist.get(n).getCommand();
			AnalysisSource as = selectedcommandlist.get(n).getTable();
			if( as != null)
				getTextCurrentTable().setText(as.toString());
			else
				getTextCurrentTable().setText("Temp result");
		}
		getTextCurrentCommand().setText(s);
	}
	
	/**
	 * Event called when a user selects a command type.
	 */
	void listCommandTypesSelected(){
		int idx = getListCommandTypes().getSelectedIndex();
		DefaultListModel dlm = (DefaultListModel)getListCommands().getModel();
		dlm.clear();
		if( idx == -1 )return;
		String k = (String)getListCommandTypes().getSelectedValue();
		for( Command c : commandlist.get(k) ){
			dlm.addElement(c.getName());
		}
	}
	
	/**
	 * Event called when a user selects a command.
	 *
	 * @param e the event
	 */
	void listCommandsClicked(MouseEvent e){
		if (e.getClickCount() == 2 ){
			//int idx1 = getListCommandTypes().getSelectedIndex();
			int idx = getListCommands().getSelectedIndex();
			Command choosen = commandlist.get(getListCommandTypes().getSelectedValue()).get(idx);
			Command t = new Command(choosen);
			t.regenerateCommand();
			selectedcommandlist.add(t);
			redrawSelectedCommands();
		}
	}
	
	/**
	 * Shows the popup when the user rightclicks a command in the selected command list.
	 *
	 * @param e the event
	 */
	void listSelectedCommandsClicked(MouseEvent e){
		int r = getListSelectedCommands().locationToIndex(e.getPoint());
		if (r >= 0 && r < getListSelectedCommands().getModel().getSize()) {
			getListSelectedCommands().setSelectionInterval(r, r);
		} else {
			getListSelectedCommands().clearSelection();
		}

		if ( (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
			popup4.show(getListSelectedCommands(), e.getX(), e.getY());
	}
	
	/**
	 * Called when the user selects a commad from the selected command list. It loads the command parameters in another list.
	 */
	void listSelectedCommandsSelected(){
		DefaultListModel dlm = (DefaultListModel)getListParameters().getModel();
		dlm.clear();
		int n = getListSelectedCommands().getSelectedIndex();
		if( n == -1 )return;
		for( String s : selectedcommandlist.get(n).parameters ){
			dlm.addElement(s);
		}		
		updateCommandArea();
	}
	
	/**
	 * It shows the current value of the current selected parameter.
	 */
	void listParametersSelected(){
		int n = getListParameters().getSelectedIndex();
		if( n==-1){
			getTextCurrentParameter().setText("");
		}else{
			String s = (String)getListParameters().getSelectedValue();
			getTextCurrentParameter().setText(s);
		}
	}
	
	/**
	 * Called when the user changes the value of a parameter. The new value is saved.
	 */
	void textCurrentParameterChanged(){
		String s = getTextCurrentParameter().getText();
		if( s.equals("") )s=" ";
		int n1 = getListSelectedCommands().getSelectedIndex();
		int n2 = getListParameters().getSelectedIndex();
		if( n1 == -1 || n2 == -1 )return;
		selectedcommandlist.get(n1).parameters.set(n2, s);
		selectedcommandlist.get(n1).regenerateCommand();
		DefaultListModel dlm = (DefaultListModel)getListParameters().getModel();
		dlm.set(n2, s);
		updateCommandArea();
	}
	
	/**
	 * Moves a command up.
	 */
	void buttonUpPressed(){
		int n = getListSelectedCommands().getSelectedIndex();
		if(n==-1 || n==0)return;
		Command command = selectedcommandlist.remove(n);
		n--;
		selectedcommandlist.add(n, command);
		redrawSelectedCommands();
		getListSelectedCommands().setSelectedIndex(n);
	}
	
	/**
	 * Moves a command down.
	 */
	void buttonDownPressed(){
		int n = getListSelectedCommands().getSelectedIndex();
		if(n==-1 || n==selectedcommandlist.size()-1)return;
		Command command = selectedcommandlist.remove(n);
		n++;
		selectedcommandlist.add(n, command);
		redrawSelectedCommands();
		getListSelectedCommands().setSelectedIndex(n);
	}
	
	
	/**
	 * Called when a table is doubleclicked. In this case, the table is set as the data source of a command.
	 *
	 * @param e the event
	 */
	void listTablesClicked(MouseEvent e){
		if (e.getClickCount() == 2 ){
			int idx = getListTables().getSelectedIndex();
			AnalysisSource as = sourcelist.get(idx);
			int n = getListSelectedCommands().getSelectedIndex();
			if( n==-1 )return;
			selectedcommandlist.get(n).setTable(as);
			updateCommandArea();
		}
	}
	
	/**
	 * Called when a table is selected. It is shown a preview.
	 */
	void listTablesSelected(){
		int idx = getListTables().getSelectedIndex();
		if( idx != -1 ){
			GenericStorage gs = sourcelist.get(idx).getStorage();
			String tname = sourcelist.get(idx).getTableName();
			
			String[][] preview = gs.getPreviewForTable(tname, 10);
			DefaultTableModel dtm = (DefaultTableModel)getTablePreview().getModel();
			dtm.setColumnCount(preview[0].length);
			dtm.setRowCount(10);
			for(int i=0;i<preview.length&&i<10;i++){
				for(int j=0;j<preview[0].length;j++){
					dtm.setValueAt(preview[i][j], i,j);
				}
			}
		}
		
	}
	
	/**
	 * The result of the previous command is set as the data source of the current command.
	 */
	void buttonTempTablePressed(){
		int n = getListSelectedCommands().getSelectedIndex();
		if( n==-1 )return;
		selectedcommandlist.get(n).setTable(null);
		updateCommandArea();
	}
	
	
	/**
	 * Called when a command is manually modified.
	 */
	void textCurrentCommandChanged(){
		int n = getListSelectedCommands().getSelectedIndex();
		if( n != -1  ){
			selectedcommandlist.get(n).setCommand( getTextCurrentCommand().getText() );
		}
	}
	
	/**
	 * Executes a list of commands (that can be associated to different storages). The commands are executed sequentially;
	 * if the storage of the current command is different from the one of the previous, the partial result of the previous command
	 * is exported from the old storage and imported in the new one.
	 */
	void buttonExecPressed(){
		GenericStorage oldstorage = null;
		boolean ok = false;
		for( Command c : selectedcommandlist ){
			if( c.getStorage() != oldstorage && oldstorage != null && c.getTable() == null ){
				ok = oldstorage.exportTemp();
				if( !ok )return;
				ok = c.getStorage().importTemp();
				if( !ok )return;
			}
			if( c.getTable() != null && c.getTable().getStorage() != c.getStorage() ){
				c.getTable().getStorage().execCommand(null, c.getTable());
				c.getTable().getStorage().exportTemp();
				c.getStorage().importTemp();
				ok = c.getStorage().execCommand(c, null);
				if( !ok )return;
			}else
			ok = c.getStorage().execCommand(c, c.getTable());
			if( !ok )return;
			oldstorage = c.getStorage();
		}
		oldstorage.exportTemp();
		String preview[][] = oldstorage.previewTemp(10);
		DefaultTableModel dtm = (DefaultTableModel)getTablePreviewResult().getModel();
		dtm.setRowCount(0);
		dtm.setColumnCount(preview[0].length);
		dtm.setRowCount(10);
		for(int i=0;i<preview.length&&i<10;i++){
			for(int j=0;j<preview[0].length;j++){
				dtm.setValueAt(preview[i][j], i,j);
			}
		}
	}
	
	
	/**
	 * Called when a script (list of commands saved previously) is selected.
	 */
	void listScriptsSelected(){
		int idx = getListScripts().getSelectedIndex();
		if( idx == -1 ){
			getTextScriptName().setText("");
			return;
		}
		getTextScriptName().setText((String)getListScripts().getSelectedValue());
	}
	
	/**
	 * It loads the selected script.
	 */
	void buttonLoadPressed(){
		int idx = getListScripts().getSelectedIndex();
		if(idx==-1)return;
		LinkedList<Command> toload = scripts.get(getListScripts().getSelectedValue());
		selectedcommandlist = new LinkedList<Command>(toload);
		redrawSelectedCommands();
	}
	

	/**
	 * It saves the current script.
	 */
	void buttonSavePressed(){
		String name = getTextScriptName().getText();
		if( name.equals("") )return;
		String foldername = "savedcommands";
		try {
			FileOutputStream fo = new FileOutputStream(foldername+"/"+name);
			ObjectOutputStream oo = new ObjectOutputStream(fo);
			oo.writeObject(selectedcommandlist);
			oo.close();
			fo.close();
		} catch (Exception e) {e.printStackTrace();}
		reloadSavedScripts();
	}
	

	/**
	 * It deletes the current selected script.
	 */
	void buttonDeletePressed(){
		int idx = getListScripts().getSelectedIndex();
		if(idx==-1)return;
		String foldername = "savedcommands";
		File del = new File(foldername+"/"+getListScripts().getSelectedValue());
		del.delete();
		reloadSavedScripts();
	}
	
	/**
	 * It reloads the scripts saved on disk.
	 */
	void reloadSavedScripts(){
		if( scripts == null )scripts = new TreeMap<String,LinkedList<Command>>();
		scripts.clear();
		DefaultListModel dlm = (DefaultListModel)getListScripts().getModel();
		dlm.clear();
		String foldername = "savedcommands";
		File folder = new File(foldername);
		for (final File fileEntry : folder.listFiles()) {
			String s = fileEntry.getName();
			try{
				FileInputStream fi = new FileInputStream(foldername+"/"+s);
				ObjectInputStream oi = new ObjectInputStream(fi);
				LinkedList<Command> l = (LinkedList<Command>)oi.readObject();
				scripts.put(s, l);
				dlm.addElement(s);
				fi.close();
				oi.close();
			}catch(Exception e){e.printStackTrace();}
		}
	}
	

	@Override
	public void setVisible(boolean b){
		sourcelist.clear();
		commandlist.clear();
		selectedcommandlist.clear();
		for( GenericStorage gs : Controller.getInstance().getStorages() ){
			if( gs.getTableNames() != null ){
				for( String t : gs.getTableNames() ){
					sourcelist.add(new AnalysisSource(gs,t));
				}
			}
		}
		DefaultListModel dlm = (DefaultListModel)getListTables().getModel();
		dlm.clear();
		for(  AnalysisSource as : sourcelist ){
			dlm.addElement(as.toString());
		}
		
		commandlist.clear();
		for( GenericStorage gs : Controller.getInstance().getStorages() ){
			List<Command> commands = gs.getCommands();
			for(Command c : commands){
				LinkedList<Command> lc = commandlist.get(c.getType());
				if( lc == null ){
					lc = new LinkedList<Command>();
					commandlist.put(c.getType(), lc);
				}
				lc.push(c);
			}
		}
		DefaultListModel dlm3 = (DefaultListModel)getListCommandTypes().getModel();
		dlm3.clear();
		for( String s : commandlist.keySet() ){
			dlm3.addElement(s);
		}
		reloadSavedScripts();
		
		super.setVisible(b);
		if( isFirstInit ){
			init();
			isFirstInit=false;
		}
	}
	
	/**
	 * Resize.
	 *
	 * @param image the image
	 * @param width the width
	 * @param height the height
	 * @return the buffered image
	 */
	private static BufferedImage resize(BufferedImage image, int width, int height) {
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	    g2d.drawImage(image, 0, 0, width, height, null);
	    g2d.dispose();
	    return bi;
	}
	
	
	private boolean isFirstInit=true;
	
	
	private void init(){
		(new Thread(){
			public void run(){
				while(true){
					try{ Thread.sleep(1000); }catch(Exception e){};
					BufferedImage image;
					try {
						image = ImageIO.read(new File("temp.png"));
					} catch (IOException e) {
						continue;
					}
					Image dimg = resize(image,getLabelGraph().getWidth(),getLabelGraph().getHeight());
					ImageIcon img = new ImageIcon(dimg);
					img.getImage().flush();
					getLabelGraph().setIcon(img);
					getLabelGraph().revalidate();
					//System.out.println("img "+image+  " "+dimg+ " "+img);
				}
			}
		}).start();
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// BEGIN GENERATED CODE                                                           //
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/** The content pane. */
	private JPanel contentPane;
	
	/** The text field. */
	private JTextField textField;
	
	/** The list. */
	private JList list;
	
	/** The list_1. */
	private JList list_1;
	
	/** The list_2. */
	private JList list_2;
	
	/** The list_3. */
	private JList list_3;
	
	/** The text area. */
	private JTextArea textArea;
	
	/** The text field_1. */
	private JTextField textField_1;
	
	/** The table. */
	private JTable table;
	
	/** The list_4. */
	private JList list_4;
	
	/** The table_1. */
	private JTable table_1;
	
	/** The label. */
	private JLabel label;
	
	/** The text field_2. */
	private JTextField textField_2;
	
	/** The list_5. */
	private JList list_5;

	/**
	 * Instantiates a new analysis window.
	 */
	public AnalysisWindow() {
		setBounds(100, 100, 1355, 984);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 36, 500, 186);
		
		list = new JList(new DefaultListModel());
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				listTablesSelected();
			}
		});
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listTablesClicked(e);
			}
		});
		scrollPane.setViewportView(list);
		
		JLabel lblTables = new JLabel("Tables:");
		lblTables.setBounds(17, 9, 167, 15);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 418, 167, 214);
		
		list_1 = new JList(new DefaultListModel());
		list_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listCommandsClicked(e);
			}
		});
		scrollPane_1.setViewportView(list_1);
		
		JLabel lblCommands = new JLabel("Commands:");
		lblCommands.setBounds(12, 238, 167, 15);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(191, 265, 169, 330);
		
		list_2 = new JList(new DefaultListModel());
		list_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listSelectedCommandsClicked(e);
			}
		});
		list_2.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				listSelectedCommandsSelected();
			}
		});
		scrollPane_2.setViewportView(list_2);
		
		JLabel lblSelectedCommands = new JLabel("Selected commands:");
		lblSelectedCommands.setBounds(191, 238, 169, 15);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(17, 783, 495, 123);
		scrollPane_3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		textArea = new JTextArea();
		textArea.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				textCurrentCommandChanged();
			}
		});
		textArea.setLineWrap(true);
		scrollPane_3.setViewportView(textArea);
		
		JLabel lblCommand = new JLabel("Current command:");
		lblCommand.setBounds(17, 760, 508, 21);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(378, 265, 134, 367);
		
		list_3 = new JList(new DefaultListModel());
		list_3.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listParametersSelected();
			}
		});
		scrollPane_4.setViewportView(list_3);
		
		textField = new JTextField();
		textField.setBounds(17, 729, 495, 19);
		textField.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				textCurrentParameterChanged();
			}
		});
		textField.setColumns(10);
		
		JLabel lblCurrentParameter = new JLabel("Current parameter:");
		lblCurrentParameter.setBounds(17, 702, 412, 15);
		
		JButton btnV = new JButton("v");
		btnV.setBounds(191, 607, 70, 25);
		btnV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonDownPressed();
			}
		});
		
		JButton button = new JButton("^");
		button.setBounds(288, 607, 70, 25);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonUpPressed();
			}
		});
		
		JLabel lblCurrentTable = new JLabel("Current Table:");
		lblCurrentTable.setBounds(17, 644, 446, 15);
		
		textField_1 = new JTextField();
		textField_1.setBounds(17, 671, 397, 19);
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("Cancel");
		btnNewButton.setBounds(428, 665, 84, 25);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonTempTablePressed();
			}
		});
		
		JButton btnExec = new JButton("Exec");
		btnExec.setBounds(395, 918, 117, 25);
		btnExec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonExecPressed();
			}
		});
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(524, 36, 513, 186);
		
		table = new JTable(new DefaultTableModel());
		scrollPane_5.setViewportView(table);
		
		JLabel lblParameters = new JLabel("Parameters:");
		lblParameters.setBounds(378, 238, 88, 15);
		
		JLabel lblTablePreview = new JLabel("Table preview:");
		lblTablePreview.setBounds(524, 9, 120, 15);
		
		JScrollPane scrollPane_6 = new JScrollPane();
		scrollPane_6.setBounds(12, 265, 167, 145);
		
		list_4 = new JList(new DefaultListModel());
		list_4.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				listCommandTypesSelected();
			}
		});
		scrollPane_6.setViewportView(list_4);
		
		label = new JLabel("");
		label.setLocation(530, 545);
		label.setPreferredSize(new Dimension(300, 300));
		label.setSize(new Dimension(446, 398));
		
		JLabel lblTempResult = new JLabel("Temp result:");
		lblTempResult.setBounds(524, 238, 215, 15);
		
		JScrollPane scrollPane_7 = new JScrollPane();
		scrollPane_7.setBounds(524, 266, 508, 267);
		
		table_1 = new JTable();
		scrollPane_7.setViewportView(table_1);
		contentPane.setLayout(null);
		contentPane.add(lblTables);
		contentPane.add(lblTablePreview);
		contentPane.add(scrollPane);
		contentPane.add(scrollPane_5);
		contentPane.add(lblCommands);
		contentPane.add(lblSelectedCommands);
		contentPane.add(lblParameters);
		contentPane.add(lblCurrentTable);
		contentPane.add(scrollPane_6);
		contentPane.add(scrollPane_1);
		contentPane.add(scrollPane_2);
		contentPane.add(btnV);
		contentPane.add(button);
		contentPane.add(scrollPane_4);
		contentPane.add(textField_1);
		contentPane.add(btnNewButton);
		contentPane.add(lblCurrentParameter);
		contentPane.add(textField);
		contentPane.add(lblCommand);
		contentPane.add(scrollPane_3);
		contentPane.add(label);
		contentPane.add(lblTempResult);
		contentPane.add(btnExec);
		contentPane.add(scrollPane_7);
		
		JScrollPane scrollPane_8 = new JScrollPane();
		scrollPane_8.setBounds(1044, 266, 297, 267);
		contentPane.add(scrollPane_8);
		
		list_5 = new JList(new DefaultListModel());
		list_5.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				listScriptsSelected();
			}
		});
		scrollPane_8.setViewportView(list_5);
		
		JLabel lblNewLabel = new JLabel("Scripts:");
		lblNewLabel.setBounds(1050, 238, 215, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblName = new JLabel("Script name:");
		lblName.setBounds(1050, 545, 291, 15);
		contentPane.add(lblName);
		
		textField_2 = new JTextField();
		textField_2.setBounds(1050, 572, 291, 19);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonLoadPressed();
			}
		});
		btnLoad.setBounds(1050, 603, 69, 25);
		contentPane.add(btnLoad);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonSavePressed();
			}
		});
		btnSave.setBounds(1150, 603, 68, 25);
		contentPane.add(btnSave);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonDeletePressed();
			}
		});
		btnDelete.setBounds(1260, 603, 81, 25);
		contentPane.add(btnDelete);
		popupListCommands();
	}
	
	/**
	 * Gets the list tables.
	 *
	 * @return the list tables
	 */
	private JList getListTables() {
		return list;
	}
	
	/**
	 * Gets the list commands.
	 *
	 * @return the list commands
	 */
	private JList getListCommands() {
		return list_1;
	}
	
	/**
	 * Gets the list selected commands.
	 *
	 * @return the list selected commands
	 */
	private JList getListSelectedCommands() {
		return list_2;
	}
	
	/**
	 * Gets the list parameters.
	 *
	 * @return the list parameters
	 */
	private JList getListParameters() {
		return list_3;
	}
	
	/**
	 * Gets the text current command.
	 *
	 * @return the text current command
	 */
	private JTextArea getTextCurrentCommand() {
		return textArea;
	}
	
	/**
	 * Gets the text current parameter.
	 *
	 * @return the text current parameter
	 */
	private JTextField getTextCurrentParameter() {
		return textField;
	}
	
	/**
	 * Gets the text current table.
	 *
	 * @return the text current table
	 */
	private JTextField getTextCurrentTable() {
		return textField_1;
	}
	
	/**
	 * Gets the table preview.
	 *
	 * @return the table preview
	 */
	private JTable getTablePreview() {
		return table;
	}
	
	/**
	 * Gets the list command types.
	 *
	 * @return the list command types
	 */
	private JList getListCommandTypes() {
		return list_4;
	}
	
	/**
	 * Gets the table preview result.
	 *
	 * @return the table preview result
	 */
	private JTable getTablePreviewResult() {
		return table_1;
	}
	
	/**
	 * Gets the label graph.
	 *
	 * @return the label graph
	 */
	private JLabel getLabelGraph() {
		return label;
	}
	
	/**
	 * Gets the text script name.
	 *
	 * @return the text script name
	 */
	private JTextField getTextScriptName() {
		return textField_2;
	}
	
	/**
	 * Gets the list scripts.
	 *
	 * @return the list scripts
	 */
	private JList getListScripts() {
		return list_5;
	}
}
