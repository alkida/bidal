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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ScrollPaneConstants;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

// TODO: Auto-generated Javadoc
/**
 * The Class ManageTablesWindow.
 * It is the GUI for manage the tables, allowing to perform SQL statements on them.
 */
public class ManageTablesWindow extends JFrame {
	
	/**
	 * A storage is selected, a list of tables that are present in the storage is shown to the user.
	 */
	void listStoragesSelected(){
		int idx = getListStorages().getSelectedIndex();
		if(idx == -1)return;
		GenericStorage gs = Controller.getInstance().getStorages().get(idx);
		String[] names = gs.getTableNames();
		DefaultListModel dlm = (DefaultListModel)getListTables().getModel();
		dlm.clear();
		if( names == null )return;
		for (String s : names){
			dlm.addElement(s);
		}
	}
	
	
	@Override
	public void setVisible(boolean b){
		if( b ){
			DefaultListModel dlm = (DefaultListModel)getListStorages().getModel();
			dlm.clear();
			for( GenericStorage gs : Controller.getInstance().getStorages() ){
				dlm.addElement(gs.getStorageName());
			}
		}
		super.setVisible(b);
	}
	
	/**
	 * A table is selected, its columns are shown to the user.
	 */
	public void listTablesSelected(){
		int idxstorage = getListStorages().getSelectedIndex();
		int idxtables = getListTables().getSelectedIndex();
		if( idxstorage == -1 || idxtables == -1 )return;
		
		String[][] preview = Controller.getInstance().getStorages().get(idxstorage).getPreviewForTable((String)getListTables().getSelectedValue(), 10);
		DefaultTableModel dtm = (DefaultTableModel)getTablePreview().getModel();
		dtm.setColumnCount(preview[0].length);
		dtm.setRowCount(10);
		for(int i=0;i<preview.length&&i<10;i++){
			for(int j=0;j<preview[0].length;j++){
				dtm.setValueAt(preview[i][j], i,j);
			}
		}
		DefaultListModel dlm = (DefaultListModel)getListColumns().getModel();
		dlm.clear();
		for (int i=1;i<=preview[0].length;i++){
			dlm.addElement("V"+i);
		}
	}
	
	
	/**
	 * Button execute query pressed. The command associated to the SQL statement is passed to the storage, that executes it.
	 */
	public void buttonExecuteQueryPressed(){
		String q = getTextMappedQuery().getText();
		Controller.getInstance().getStorages().get(getListStorages().getSelectedIndex()).executeMappedQuery(q);
		listStoragesSelected();
	}
	
	/**
	 * A popup is shown when the user right-clicks the list of tables, asking if remove the table.
	 *
	 * @param e the event
	 */
	public void listTablesClicked(MouseEvent e){

		int r = getListTables().locationToIndex(e.getPoint());
		if (r >= 0 && r < getListTables().getModel().getSize()) {
			getListTables().setSelectionInterval(r, r);
		} else {
			getListTables().clearSelection();
		}

		if ( (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
			popup3.show(getListTables(), e.getX(), e.getY());
	}
	
	/**
	 * When a column is double-clicked it is copied in the selected columns list.
	 *
	 * @param e the event
	 */
	public void listColumnsClicked(MouseEvent e){
		String s = (String) getListColumns().getSelectedValue();
		DefaultListModel dlm = (DefaultListModel)getListSelectedColumns().getModel();
		if (e.getClickCount() == 2 && !dlm.contains(s)){
			dlm.addElement(s);
		}
		updateQueryArea();
	}
	
	/**
	 * When a column in the selected columns list is double-clicked it is copied in the group by list.
	 *
	 * @param e the event
	 */
	public void listSelectedColumnsClicked(MouseEvent e){
		int r = getListSelectedColumns().locationToIndex(e.getPoint());
		if (r >= 0 && r < getListSelectedColumns().getModel().getSize()) {
			getListSelectedColumns().setSelectionInterval(r, r);
		} else {
			getListSelectedColumns().clearSelection();
		}

		String s = (String) getListSelectedColumns().getSelectedValue();
		DefaultListModel dlm = (DefaultListModel)getListGroupbyColumns().getModel();

		if ( e.getClickCount() == 2 && !dlm.contains(s) && !s.contains("(")){
			dlm.addElement(s);
		}
		if ( (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
			popup.show(getListSelectedColumns(), e.getX(), e.getY());
		updateQueryArea();
	}
	
	/**
	 * When the user right-clicks the group by list a popup is shown, asking if remove the column from this list.
	 *
	 * @param e the e
	 */
	public void listGroupbyColumnsClicked(MouseEvent e){
		int r = getListGroupbyColumns().locationToIndex(e.getPoint());
		if (r >= 0 && r < getListGroupbyColumns().getModel().getSize()) {
			getListGroupbyColumns().setSelectionInterval(r, r);
		} else {
			getListGroupbyColumns().clearSelection();
		}
		if ( (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
			popup2.show(getListGroupbyColumns(), e.getX(), e.getY());
		updateQueryArea();
	}
	
	/**
	 * Event triggered when the user writes the name of the table to create, the query area is updated.
	 */
	public void textTableNameChanged(){
		updateQueryArea();
	}
	
	/**
	 * Event triggered when the user writes the SQL where condition, the query area is updated.
	 */
	public void textConditionChanged(){
		updateQueryArea();
	}
	
	/**
	 * Event triggered when the user hand-modifies the query area, the generated command associated to the SQL statement is updated.
	 */
	public void textQueryChanged(){
		updateMappedQueryArea();
	}

	/**
	 * Generate the SQL statement from the name and columns specified by the user.
	 *
	 * @return the SQL statement
	 */
	String generateQuery(){
		String s = (String) getListGroupbyColumns().getSelectedValue();
		DefaultTableModel dtm = (DefaultTableModel)getTablePreview().getModel();
		DefaultListModel dlm = (DefaultListModel)getListSelectedColumns().getModel();
		DefaultListModel dlm1 = (DefaultListModel)getListGroupbyColumns().getModel();
		String comando = "Select ";/*"create table ";
		comando += getTextTableName().getText() + " as select ";*/
		for (int i=0; i<dlm.getSize(); i++){
			comando += dlm.elementAt(i) + " as V" + (i + 1);
			if (i != dlm.getSize() - 1)
				comando += ",";
		}
		if( dlm.getSize() == 0 )comando += " * ";
		comando+= " from "+ getListTables().getSelectedValue()+ " ";
		if (!getTextCondition().getText().equals(""))
			comando+=" where " + getTextCondition().getText();
		if (dlm1.getSize() > 0){
			comando+= " group by ";
			for (int i=0; i<dlm1.getSize(); i++){
				comando+= dlm1.elementAt(i);
				if (i != dlm1.getSize() - 1)
					comando += ",";
			}
		}
		//comando+= ";";
		return comando;
	}

	/**
	 * When something is modified, the SQL statement must be recalculated, and its associated storage command also.
	 */
	void updateQueryArea(){
		String t = generateQuery();
		getTextQuery().setText(t);
		updateMappedQueryArea();
	}
	
	/**
	 * Recalculates the storage command associated to a SQL statement. The statement is passed to the storage, that converts it to a tree,
	 * and from the tree a specific command is generated.
	 */
	void updateMappedQueryArea(){
		String t = getTextQuery().getText();
		String t2 = Controller.getInstance().getStorages().get(getListStorages().getSelectedIndex()).getMappedQuery(getTextTableName().getText(),t);
		getTextMappedQuery().setText(t2);
	}
	
	private JPopupMenu popup;
	
	/**
	 * A command inside a popup shown in selected columns list has been selected.
	 */
	public void popupSelected( ) {
		popup = new JPopupMenu();
		ActionListener menuListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Delete")){
					int n =  getListSelectedColumns().getSelectedIndex();
					DefaultListModel dlm = (DefaultListModel)getListSelectedColumns().getModel();
					dlm.remove(n);
				}
				else{
					int n =  getListSelectedColumns().getSelectedIndex();
					DefaultListModel dlm = (DefaultListModel)getListSelectedColumns().getModel();
					String name = (String) dlm.getElementAt(n);
					if (name.contains("(")){
						int begin = name.indexOf("(");
						int end = name.indexOf(")");
						String newname = name.substring(begin+1, end);
						String rename = event.getActionCommand() + "(" + newname + ")";
						dlm.set(n, rename);
					}
					else {
						String rename = event.getActionCommand() + "(" + name + ")";
						dlm.set(n, rename);
					}
				}
				updateQueryArea();
			}
		};
		JMenuItem item;
		popup.add(item = new JMenuItem("avg"));
		item.addActionListener(menuListener);
		popup.add(item = new JMenuItem("count"));
		item.addActionListener(menuListener);
		popup.add(item = new JMenuItem("min"));
		item.addActionListener(menuListener);
		popup.add(item = new JMenuItem("max"));
		item.addActionListener(menuListener);
		popup.add(item = new JMenuItem("sum"));
		item.addActionListener(menuListener);
		popup.add(item = new JMenuItem("stdev"));
		item.addActionListener(menuListener);
		popup.add(item = new JMenuItem("var"));
		item.addActionListener(menuListener);
		popup.addSeparator();
		popup.add(item = new JMenuItem("Delete"));
		item.addActionListener(menuListener);
		popup.setBorder(new BevelBorder(BevelBorder.RAISED));
	}

	private JPopupMenu popup2;
	
	/**
	 * Delete has been pressed in the popup shown in the group by list.
	 */
	public void popupGroupby( ) {
		popup2 = new JPopupMenu();
		ActionListener menuListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Delete")){
					int n =  getListGroupbyColumns().getSelectedIndex();
					DefaultListModel dlm = (DefaultListModel)getListGroupbyColumns().getModel();
					dlm.remove(n);
				}
				updateQueryArea();
			}
		};
		JMenuItem item;
		popup2.add(item = new JMenuItem("Delete"));
		item.addActionListener(menuListener);
		popup2.setBorder(new BevelBorder(BevelBorder.RAISED));
	}

	private JPopupMenu popup3;
	
	/**
	 * Delete has been pressed inside the popup shown on the table list, a table must be deleted, and the list redrawn.
	 */
	public void popupListTables( ) {
		popup3 = new JPopupMenu();
		ActionListener menuListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Delete")){
					int n =  getListTables().getSelectedIndex();
					//if(n>5){
						Controller.getInstance().getStorages().get(getListStorages().getSelectedIndex()).removeTable((String)getListTables().getSelectedValue());
					//}
				}
				//updateQueryArea();
				listStoragesSelected();
			}
		};
		JMenuItem item;
		popup3.add(item = new JMenuItem("Delete"));
		item.addActionListener(menuListener);
		popup3.setBorder(new BevelBorder(BevelBorder.RAISED));
	}
	
	/**
	 * Inits the GUI.
	 */
	void init(){
		popupSelected();
		popupGroupby();
		popupListTables();
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
	
	/** The table. */
	private JTable table;
	
	/** The text field. */
	private JTextField textField;
	
	/** The text field_1. */
	private JTextField textField_1;
	
	/** The list_2. */
	private JList list_2;
	
	/** The list_3. */
	private JList list_3;
	
	/** The list_4. */
	private JList list_4;
	
	/** The text area. */
	private JTextArea textArea;
	
	/** The text area_1. */
	private JTextArea textArea_1;

	/**
	 * Instantiates a new manage tables window.
	 */
	public ManageTablesWindow() {
		setBounds(100, 100, 1143, 961);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		list = new JList(new DefaultListModel());
		scrollPane_1.setViewportView(list);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				listStoragesSelected();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		
		list_1 = new JList(new DefaultListModel());
		list_1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				listTablesSelected();
			}
		});
		list_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listTablesClicked(e);
			}
		});
		scrollPane.setViewportView(list_1);
		
		JLabel lblStorages = new JLabel("Storages:");
		
		JLabel lblTables = new JLabel("Tables:");
		
		JScrollPane scrollPane_6 = new JScrollPane();
		
		table = new JTable(new DefaultTableModel());
		scrollPane_6.setViewportView(table);
		
		JLabel lblPreview = new JLabel("Preview:");
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		list_2 = new JList(new DefaultListModel());
		list_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listColumnsClicked(e);
			}
		});
		scrollPane_2.setViewportView(list_2);
		
		JLabel lblColumns = new JLabel("Columns:");
		
		JScrollPane scrollPane_3 = new JScrollPane();
		
		list_3 = new JList(new DefaultListModel());
		list_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listSelectedColumnsClicked(e);
			}
		});
		scrollPane_3.setViewportView(list_3);
		
		JLabel lblColumnsSelected = new JLabel("Selected columns:");
		
		JScrollPane scrollPane_4 = new JScrollPane();
		
		list_4 = new JList(new DefaultListModel());
		list_4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listGroupbyColumnsClicked(e);
			}
		});
		scrollPane_4.setViewportView(list_4);
		
		JLabel lblGroupBy = new JLabel("Group by:");
		
		textField = new JTextField();
		textField.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				textTableNameChanged();
			}
		});
		textField.setColumns(10);
		
		JLabel lblTableName = new JLabel("Table name:");
		
		JLabel lblCondition = new JLabel("Condition:");
		
		textField_1 = new JTextField();
		textField_1.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				textConditionChanged();
			}
		});
		textField_1.setColumns(10);
		
		JLabel lblQuery = new JLabel("Query:");
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				textQueryChanged();
			}
		});
		scrollPane_5.setViewportView(textArea);
		
		JLabel lblMappedQuery = new JLabel("Mapped query:");
		
		JScrollPane scrollPane_7 = new JScrollPane();
		scrollPane_7.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		textArea_1 = new JTextArea();
		textArea_1.setLineWrap(true);
		scrollPane_7.setViewportView(textArea_1);
		
		JButton btnExecute = new JButton("Execute");
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonExecuteQueryPressed();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(1007)
					.addComponent(btnExecute, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(17)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
							.addGap(12))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblStorages)
							.addGap(122)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
							.addGap(12))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblTables, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addGap(128)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblPreview, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane_6, GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE)
							.addGap(7))))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(17)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
							.addGap(12))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblColumns, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addGap(122)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
							.addGap(12))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblColumnsSelected, GroupLayout.PREFERRED_SIZE, 164, GroupLayout.PREFERRED_SIZE)
							.addGap(34)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane_4, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
							.addGap(36))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblGroupBy, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addGap(146)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(lblTableName, GroupLayout.PREFERRED_SIZE, 163, GroupLayout.PREFERRED_SIZE)
								.addContainerGap())
							.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addComponent(textField, GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
									.addComponent(textField_1, GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
									.addComponent(lblQuery, GroupLayout.PREFERRED_SIZE, 172, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblMappedQuery, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
									.addComponent(scrollPane_5, GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(scrollPane_7, GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
										.addPreferredGap(ComponentPlacement.RELATED)))
								.addGap(7)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblCondition, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblStorages)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(1)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblTables)
								.addComponent(lblPreview))))
					.addGap(16)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
						.addComponent(scrollPane_6, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
					.addGap(26)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblColumns)
						.addComponent(lblColumnsSelected)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblGroupBy)
							.addComponent(lblTableName)))
					.addGap(10)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(2)
							.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(2)
							.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(2)
							.addComponent(scrollPane_4, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblCondition)
							.addGap(12)
							.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(12)
							.addComponent(lblQuery)
							.addGap(12)
							.addComponent(scrollPane_5, GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
							.addGap(14)
							.addComponent(lblMappedQuery)
							.addGap(12)
							.addComponent(scrollPane_7, GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)))
					.addGap(50)
					.addComponent(btnExecute)
					.addGap(2))
		);
		contentPane.setLayout(gl_contentPane);
		
		init();
	}

	/**
	 * Gets the list storages.
	 *
	 * @return the list storages
	 */
	private JList getListStorages() {
		return list;
	}
	
	/**
	 * Gets the list tables.
	 *
	 * @return the list tables
	 */
	private JList getListTables() {
		return list_1;
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
	 * Gets the list columns.
	 *
	 * @return the list columns
	 */
	private JList getListColumns() {
		return list_2;
	}
	
	/**
	 * Gets the list selected columns.
	 *
	 * @return the list selected columns
	 */
	private JList getListSelectedColumns() {
		return list_3;
	}
	
	/**
	 * Gets the list groupby columns.
	 *
	 * @return the list groupby columns
	 */
	private JList getListGroupbyColumns() {
		return list_4;
	}
	
	/**
	 * Gets the text table name.
	 *
	 * @return the text table name
	 */
	private JTextField getTextTableName() {
		return textField;
	}
	
	/**
	 * Gets the text condition.
	 *
	 * @return the text condition
	 */
	private JTextField getTextCondition() {
		return textField_1;
	}
	
	/**
	 * Gets the text query.
	 *
	 * @return the text query
	 */
	private JTextArea getTextQuery() {
		return textArea;
	}
	
	/**
	 * Gets the text mapped query.
	 *
	 * @return the text mapped query
	 */
	private JTextArea getTextMappedQuery() {
		return textArea_1;
	}
}
