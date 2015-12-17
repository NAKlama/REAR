package de.uni.goettingen.REARController.GUI;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import de.uni.goettingen.REARController.MainWindow;
import de.uni.goettingen.REARController.DataStruct.Area;
import de.uni.goettingen.REARController.DataStruct.AreaTreeNode;
import de.uni.goettingen.REARController.DataStruct.ClientStatus;
import de.uni.goettingen.REARController.DataStruct.Machine;
import de.uni.goettingen.REARController.DataStruct.MachinesTable;
import de.uni.goettingen.REARController.DataStruct.Serializable.SerMachinesTable;
import de.uni.goettingen.REARController.GUI.Tools.TreeChangeListener;
import de.uni.goettingen.REARController.Net.IPreachable;
import de.uni.goettingen.REARController.Net.NetConnections;

import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.net.InetAddress;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("serial")
public class DataTablePanel extends JPanel implements TableModelListener {
	private static final ImageIcon stoppedIcon	= new ImageIcon(MainWindow.class.getResource("/icons/16/stopped.png"));
	private static final ImageIcon recIcon		= new ImageIcon(MainWindow.class.getResource("/icons/16/rec.png"));
	private static final ImageIcon uploadIcon	= new ImageIcon(MainWindow.class.getResource("/icons/16/upload.png"));
	private static final ImageIcon okIcon		= new ImageIcon(MainWindow.class.getResource("/icons/16/OK.png"));

	private JTable 			table;
	private MachinesTable	machines;
	private TreePanel		tree;

	private Boolean			editMode = true;

	private JComboBox<AreaTreeNode>		areaSelector;

	private SerMachinesTable			mainTable;
	private NetConnections				connections;

	public DataTablePanel(TreePanel t) {
		tree		= t;
		mainTable	= new SerMachinesTable();
		connections	= new NetConnections();

		t.addTreeChangeListener(new DataTableTreeChangeListener());
		setLayout(new MigLayout("", "[fill]", "[fill]"));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(100000, 100000));
		scrollPane.setBorder(null);
		add(scrollPane, "cell 0 0,grow");

		machines = new MachinesTable();
		machines.addBlankLine();
		addListener();

		table = new JTable();
		initTable(machines);

		scrollPane.setViewportView(table);
	}

	public void initTable(TableModel m) {
		table.setModel(m);
		table.setDefaultRenderer(AreaTreeNode.class, new AreaTreeNodeRenderer());
		table.setDefaultRenderer(IPreachable.class, new IpRenderer());
		table.setDefaultRenderer(ClientStatus.class, new StatusRenderer());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableColumn statusCol;
		statusCol = table.getColumnModel().getColumn(4);
		statusCol.setPreferredWidth(40);
		statusCol.setMaxWidth(40);

		updateComboBox();

		TableColumn ipColumn = table.getColumnModel().getColumn(2);
		ipColumn.setCellEditor(new IpAddressEditor(new JTextField()));
	}

	public int studentCount() {
		MachinesTable tm = this.getTableModel();
		SerMachinesTable ser = tm.getSaveObject();
		mainTable.update(ser);
		int c = 0;
		for(Vector<Object> line : mainTable.data) {
			String 	studID	= (String) 	line.get(3);
			if(studID != null && ! studID.trim().equals(""))
				c++;
		}
		return c;
	}
	
	public void init() {
		MachinesTable tm = this.getTableModel();
		SerMachinesTable ser = tm.getSaveObject();
		mainTable.update(ser);
		ConcurrentHashMap<Long, String> studIDs = new ConcurrentHashMap<Long, String>();
		for(Vector<Object> line : mainTable.data) {
			String 	studID	= (String) 	line.get(3);
			long	id		= (long)	line.get(7);
//			System.out.println(id + ": " + studID);
			if(studID != null && ! studID.trim().equals("")) {
				studIDs.put(id, studID);

			}
		}
 		connections.init(studIDs);
 		if(!editMode)
			((MachinesTable) table.getModel()).setExamMode(!editMode, true);
	}
	
	public void setExamID(String eID)
	{
		connections.setExamID(eID);
	}

	public void rec() {
		connections.rec();
	}

	public void stop() {
		connections.stop();
	}

	public void reset() {
		connections.reset();
		if(!editMode)
			((MachinesTable) table.getModel()).setExamMode(false, false);
	}

	public ClientStatus getStatus() {
		return connections.getStatus();
	}

	public void setFilter(AreaTreeNode n) {
		Vector<AreaTreeNode> nodeList = new Vector<AreaTreeNode>();
		populateNodeList(nodeList, n);
		machines = MachinesTable.loadSaveObject(mainTable.filter(nodeList));
		addListener();
		machines.addBlankLine();
		initTable(machines);

	}

	private void populateNodeList(Vector<AreaTreeNode> l, AreaTreeNode n) {
		if(n != null) {
			l.addElement((AreaTreeNode) n);
			for(int i=0; i < n.getChildCount(); i++)
				populateNodeList(l, (AreaTreeNode) n.getChildAt(i));
		}
	}

	private void addListener() {
		if(machines.getListeners(TableModelListener.class).length == 0)
			machines.addTableModelListener(this);
	}

	public void newEmpty() {
		machines = new MachinesTable();
		addListener();
		connections.update(mainTable);
		machines.addBlankLine();
		initTable(machines);
	}

	public void setTable(MachinesTable t) {
		machines = t;
		addListener();
		if(machines.getRowCount() == 0)
			machines.addBlankLine();
		initTable(machines);
		mainTable = machines.getSaveObject();
		connections.update(mainTable);
		updateConn();
	}
	
	public void timerEvent() {
		MachinesTable tab = (MachinesTable) table.getModel();
		for(int i = 0; i < tab.getRowCount(); i++) {
			Machine	m	= tab.getLine(i);
			long	id	= m.getID();
			if(m.getComputerID() != "" && m.getArea() != null && m.getIP() != null) {
				tab.setStatus(i, connections.getStatus(id));
				tab.setRecTime(i, connections.getRecTime(id));
			}
		}
		for(Vector<Object> line : mainTable.data) {
			long id = (long) line.get(7);
			if((String) line.get(0) != "" && line.get(1) != null && line.get(2) != null) {
				line.set(4, connections.getStatus(id));
				line.set(4, connections.getRecTime(id));
			}
		}
	}

	public MachinesTable getTableModel() {
		return machines;
	}

	public SerMachinesTable getMainTable() {
		return mainTable;
	}

	public void tableChanged(TableModelEvent e) {
		int row		= e.getFirstRow();
		//		int column	= e.getColumn();
		TableModel model = (TableModel) e.getSource();
		if(row == model.getRowCount()-1) {
			machines.addBlankLine();
		}

		MachinesTable tm = this.getTableModel();
		SerMachinesTable ser = tm.getSaveObject();
		mainTable.update(ser);
		connections.update(mainTable);
		updateConn();
	}

	private void updateConn() {
		for(Vector<Object> line : mainTable.data) {
			long id = (long) line.get(7);
			if(line.get(2) != null)
				if(connections.hasID(id)) {
					IPreachable ipr	= (IPreachable) line.get(2);
					if(ipr != null) {
						InetAddress ip	= ipr.getAddress();
						if(!ip.equals(connections.getIP(id))) {
							connections.setIP(id, ip);
						}
					}
				}
		}
	}


	public void setEditMode(Boolean e) {
		editMode = e;
		MachinesTable mt = (MachinesTable) table.getModel();
		mt.setExamMode(!editMode, false);
	}
	
	private void updateComboBox() {
		TableColumn areaColumn = table.getColumnModel().getColumn(1);
		areaSelector = new JComboBox<AreaTreeNode>();
		areaSelector.setRenderer(new AreaListCellRenderer<Area>());
		for(AreaTreeNode a : tree.getAreaList())
			areaSelector.addItem(a);
		areaColumn.setCellEditor(new DefaultCellEditor(areaSelector));
	}

	private class StatusRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			String text = "";
			if(editMode == false) {
				ClientStatus status = (ClientStatus) value;
				if(status.isUninitialized())
				{}
				else if(status.getInit())
					this.setIcon(stoppedIcon);
				else if(status.getRec())
					this.setIcon(recIcon);
				else if(status.getUpload())
					this.setIcon(uploadIcon);
				else if(status.getDone())
					this.setIcon(okIcon);
			}
			this.setText(text);
			this.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			return this;
		}
	}

	private class IpRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(value != null) {
				IPreachable a = (IPreachable) value;
				if(a.isReachable())
					this.setForeground(Color.decode("#00AA00"));
				else
					this.setForeground(Color.RED);
				this.setText(a.getIPstr());
			}
			return this;
		}
	}

	private class AreaTreeNodeRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if(value != null) { // Area
				Area a = (Area) ((AreaTreeNode) value).getUserObject();
				this.setText(a.getName() + "(" + a.getID() + ")");
				this.setForeground(a.getColor());
			}
			return this;
		}
	}

	private class DataTableTreeChangeListener implements TreeChangeListener {
		@Override
		public void changedTreeStructure() {
			updateComboBox();
		}
	}

	private class AreaListCellRenderer<E> extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if(value != null) {
				AreaTreeNode 	node 	= (AreaTreeNode) value;
				Area 			a		= (Area) node.getUserObject();
				int				depth	= node.getPath().length;
				String			padding = "";
				for(int i=0; i < depth; i++)
					padding += "  ";
				this.setForeground(a.getColor());
				this.setText(padding + a.getName());
			}
			return this;
		}
	}

	public void setServer(String uploadServer, String uploadUser) {
		connections.setServer(uploadServer, uploadUser);		
	}
}
