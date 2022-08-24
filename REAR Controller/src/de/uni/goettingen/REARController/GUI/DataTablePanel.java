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
import de.uni.goettingen.REARController.Net.NetConnSignal;
import de.uni.goettingen.REARController.Net.NetConnections;

import javax.swing.JScrollPane;

import java.awt.Component;
import java.awt.Dimension;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("serial")
public class DataTablePanel extends JPanel implements TableModelListener {
	private static final ImageIcon stoppedIcon	= new ImageIcon(MainWindow.class.getResource("/icons/16/stopped.png"));
	private static final ImageIcon recIcon		= new ImageIcon(MainWindow.class.getResource("/icons/16/rec.png"));
	private static final ImageIcon uploadIcon	= new ImageIcon(MainWindow.class.getResource("/icons/16/upload.png"));
	private static final ImageIcon okIcon		= new ImageIcon(MainWindow.class.getResource("/icons/16/OK.png"));
	private static final ImageIcon warnIcon		= new ImageIcon(MainWindow.class.getResource("/icons/16/warning.png"));
	private static final ImageIcon connIcon		= new ImageIcon(MainWindow.class.getResource("/icons/16/connected.png"));
	private static final ImageIcon disconnIcon	= new ImageIcon(MainWindow.class.getResource("/icons/16/disconnected.png"));
	private static final ImageIcon audTestIcon	= new ImageIcon(MainWindow.class.getResource("/icons/16/audio-headset.png"));

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
		table.setDefaultRenderer(InetAddress.class, new IpRenderer());
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
		SerMachinesTable ser = tm.getSaveObject().removeEmpty();
		mainTable = ser;
		tm.removeEmptyRows();
		ConcurrentHashMap<Long, String> studIDs = new ConcurrentHashMap<Long, String>();
		for(Vector<Object> line : mainTable.data) {
			String 	studID	= (String) 	line.get(3);
			long	id		= (long)	line.get(8);
//			System.out.println(id + ": " + studID);
			if(studID != null && ! studID.trim().equals("")) {
				studIDs.put(id, studID);
			}
		}
 		connections.init(studIDs);
 		if(!editMode)
			((MachinesTable) table.getModel()).setExamMode(!editMode, true);
 		
	}
	
	public JTable getJTable() {
		return table;
	}
	
	public void setExamID(String eID)
	{
		connections.setExamID(eID);
	}
	
	public void micRetry() {
		connections.micRetry();
	}

	public void rec() {
		connections.rec();
	}
	
	public void recTest() {
		connections.recTest();
	}

	public void stop() {
		connections.stop();
	}

	public void reset() {
		connections.reset();
		if(!editMode)
			((MachinesTable) table.getModel()).setExamMode(false, false);
	}
	
	public void setPlayFile(String URL) {
		connections.setPlayFile(URL);
	}
	
	public void setPlayOnly() {
		connections.setPlayOnly();
	}

	public ClientStatus getStatus() {
		return connections.getStatus();
	}
	
	public void removeRows(int[] rows) {
		if(rows.length > 0) {
			Arrays.sort(rows);
			for(int i = 0; i < rows.length / 2; i++) {  // reverse sorted after this
				int tmp = rows[i];
				rows[i] = rows[rows.length - i - 1];
				rows[rows.length - i - 1] = tmp;
			}
			for(int r : rows) {
				Long id = machines.getID(r);
				connections.delete(id);
				machines.removeRow(r);
				mainTable.removeRow(r);
			}
			machines.fireTableRowsDeleted(rows[rows.length - 1], rows[0]);
		}
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
		} else {
			l.addElement(tree.getRoot());
			for(int i=0; i < tree.getRoot().getChildCount(); i++)
				populateNodeList(l, (AreaTreeNode) tree.getRoot().getChildAt(i));
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
		if(machines.getRowCount() == 0 && editMode)
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
				tab.setRecSize(i, connections.getFileSize(id));
			}
		}
		for(Vector<Object> line : mainTable.data) {
			long id = (long) line.get(8);
			if((String) line.get(0) != "" && line.get(1) != null && line.get(2) != null) {
				line.set(4, connections.getStatus(id));
				line.set(5, connections.getRecTime(id));
				line.set(6, connections.getFileSize(id));
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
		if(!editMode) {
			connections.update(mainTable);
			updateConn();
		}
	}

	private void updateConn() {
		for(Vector<Object> line : mainTable.data) {
			long id = (long) line.get(8);
			if(line.get(2) != null)
				if(connections.hasID(id)) {
					InetAddress ip	= (InetAddress) line.get(2);
					if(ip != null) {
						if(connections.isReachabel(id) && !ip.equals(connections.getIP(id))) {
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
	
	public Vector<NetConnSignal> getConnections(int[] lines) {
		Vector<NetConnSignal> out = new Vector<>();
		if(lines.length > 0) {
			for(int l : lines) {
				Long id = machines.getID(l);
				System.out.println("line: " + l + "  ID: " + id);
				NetConnSignal signalObj = connections.getClientConn(id);
				if(signalObj != null) 
					out.addElement(signalObj);
			}
		}
		return out;
	}

	public NetConnSignal getConnection(int line) {
		Long id = machines.getID(line);
		return connections.getClientConn(id);
	}
	
	public String getID(int line) {
		return (String) machines.getObjectAt(line, 3);
	}
	
	public void setServer(String uploadServer, String uploadUser) {
		connections.setServer(uploadServer, uploadUser);		
	}
	
	private class StatusRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			String text = "";
			if(editMode == false) {
				try {
					ClientStatus status = (ClientStatus) value;
					if(status == null || status.isDisconnected())
						this.setIcon(disconnIcon);
					else if(status.isUninitialized())
						this.setIcon(connIcon);
					else if(status.getNoMic())
						this.setIcon(warnIcon);
					else if(status.getInit())
						this.setIcon(stoppedIcon);
					else if(status.getUpload())
						this.setIcon(uploadIcon);
					else if(status.getDone())
						this.setIcon(okIcon);
					else if(status.getRec())
						this.setIcon(recIcon);
					else if(status.getRecTest() || status.getRecTestDone())
						this.setIcon(audTestIcon);
				}
				catch(ClassCastException e) {
					try {
						String status = (String) value;
						this.setText(status);
					}
					catch(ClassCastException e2) {}
				}
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
				InetAddress a = (InetAddress) value;
				String ipStr = a.toString();
				if(ipStr.substring(0, 1).equals("/"))
					this.setText(ipStr.substring(1));
				else
					this.setText(ipStr);
			} else {
				this.setText("None");
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
				this.setText(a.getName());  // + "(" + a.getID() + ")");
				this.setForeground(a.getColor());
			} else {
				this.setText("None");
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
}
