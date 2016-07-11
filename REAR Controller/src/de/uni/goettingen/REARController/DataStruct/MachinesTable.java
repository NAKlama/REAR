package de.uni.goettingen.REARController.DataStruct;

import java.net.InetAddress;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import de.uni.goettingen.REARController.DataStruct.Serializable.SerMachinesTable;
import de.uni.goettingen.REARController.DummyClasses.DelButton;
import de.uni.goettingen.REARController.GUI.Tools.IDfactory;

public class MachinesTable extends AbstractTableModel implements TableModel {
	private static final long serialVersionUID = 3569965886668818735L;
	private final static int		COL_NUM			= 7;
	private final static int		COL_NUM_REAL	= 9;
	private final static String []	COLUMN_NAMES 	=
		{	"Computer ID",		// 0
			"Area",				// 1
			"IP Address",		// 2
			"Student ID",		// 3
			"Status",			// 4
			"Rec. Time",		// 5
			"Rec. File Size",	// 6
			"Del",				// 7
			"ID"				// 8
		};
	
	private final static Class<?>[] COLUMN_CLASSES =
		{	String.class,
			AreaTreeNode.class,
			InetAddress.class,
			String.class,
			ClientStatus.class,
			String.class,
			DelButton.class,
			Long.class
		};
	
	private Vector<Vector<Object>>	table;
	private Boolean					examMode	= false;
	private Boolean					examStarted	= false;
	private Boolean[] 				editableCol = {true, true, true, false, false, false, false, false, false};
	
	public MachinesTable() {
		table = new Vector<Vector<Object>>();
	}
	
	public void setExamMode(Boolean exam, Boolean started) {
		this.removeEmptyRows();
		examMode	= exam;
		examStarted	= started;
		if(examMode) {
			editableCol[0] = false;
			editableCol[1] = false;
			editableCol[2] = false;
			if(examStarted)
				editableCol[3] = false;
			else
				editableCol[3] = true;
		}
		else {
			editableCol[0] = true;
			editableCol[1] = true;
			editableCol[2] = true;
			editableCol[3] = false;
		}
	}
	
	public Machine getLine(int r) {
		return objVectorToMachine(table.get(r));
	}
	
	public void addBlankLine() {
		if(!examMode)
			table.addElement(new Machine().getObjVector());
	}
	
	public SerMachinesTable getSaveObject() {
		SerMachinesTable o = new SerMachinesTable();
		for(Vector<Object> line : table) {
			Vector<Object> l = new Vector<Object>();
			for(int i = 0; i < COL_NUM_REAL; i++) { // + 1
				l.addElement(line.get(i));
			}
			o.data.addElement(l);
		}
		return o;
	}

	public static MachinesTable loadSaveObject(SerMachinesTable t) {
		MachinesTable o = new MachinesTable();
		if(t != null)
			for(Vector<Object> line : t.data) {
				Vector<Object> newLine = new Vector<Object>();
				new IDfactory().setUsedID((long) line.get(8));
				for(Object obj : line)
					newLine.addElement(obj);
				o.table.addElement(newLine);
			}
		return o;
	}
	
	public void setStatus(int r, ClientStatus cs) {
		if(cs != null)
			setObjectAt(cs, r, 4);
	}
	
	public void setRecTime(int r, String t) {
		if(t != null)
			setObjectAt(t, r, 5);
	}
	
	public void setRecSize(int r, String t) {
		if(t != null)
			setObjectAt(t, r, 6);
	}
	
	public void removeEmptyRows() {
		Vector<Vector<Object>> newTable = new Vector<Vector<Object>>();
		for(int i=0; i < table.size(); i++) {
			Vector<Object> l = table.get(i);
			boolean testComputerID	= l.get(0) != null && ! ((String)l.get(0)).equals("");
			boolean testArea		= l.get(1) != null;
			boolean testIP			= l.get(2) != null && ! ((InetAddress)l.get(2)).toString().equals("");
			if(testComputerID && testArea && testIP) 
				newTable.add(l);
		}
		table = newTable;
	}
	
	public InetAddress getIP(int row) {
		return (InetAddress) getObjectAt(row, 2);
	}
	
	public String getRecTime(int row) {
		return (String) getObjectAt(row, 5);
	}
	
	public String getRecSize(int row) {
		return (String) getObjectAt(row, 6);
	}
	
	public Long getID(int row) {
		return (Long) getObjectAt(row, 8);
	}
	
	public void removeRow(int row) {
		if(row < table.size())
			table.remove(row);
	}
	
	@Override
	public int getColumnCount() {
		return COL_NUM;
	}
	
	@Override
	public Class<?> getColumnClass(int c) {
		if(c < COL_NUM)
			return COLUMN_CLASSES[c];
		return null;
	}

	@Override
	public String getColumnName(int c) {
		if(c < COL_NUM)
			return COLUMN_NAMES[c]; 
		return null;
	}

	@Override
	public int getRowCount() {
		return table.size();
	}

	@Override
	public Object getValueAt(int r, int c) {
		if(c < COL_NUM && r < table.size())
			return table.get(r).get(c);
		return null;
	}
	
	public Object getObjectAt(int r, int c) {
		if(c < COL_NUM_REAL && r < table.size())
			return table.get(r).get(c);
		return null;
	}

	@Override
	public boolean isCellEditable(int r, int c) {
		if(c < COL_NUM)
			return editableCol[c];
		return false;
	}

	@Override
	public void setValueAt(Object o, int r, int c) {
		if(c < COL_NUM && r < table.size()) {
			table.get(r).set(c, o);
			fireTableCellUpdated(r, c);
		}
	}
	
	public void setObjectAt(Object o, int r, int c) {
		if(c < COL_NUM_REAL && r < table.size()) {
			table.get(r).set(c, o);
			fireTableCellUpdated(r, c);
		}
	}
	
	private Machine objVectorToMachine(Vector<Object> o) {
		AreaTreeNode 	atn;
		Area			area;
		InetAddress		ip;
		String			compID;
		String			studID;
		ClientStatus	status;
		String			time;
		Long			id;
		String			fileSize;
		try {
			compID = (String) o.get(0);
		} catch(ClassCastException e) {
			compID = "None";
		}
		try {
			atn		= (AreaTreeNode) o.get(1);
			area	= (Area) atn.getUserObject();
		} catch (ClassCastException | NullPointerException e) {
			atn		= null;
			area	= null;
		}
		try {
			ip = (InetAddress) o.get(2);
		} catch(ClassCastException e) {
			ip = null;
		}
		try {
			studID = (String) o.get(3);
		} catch(ClassCastException e) {
			studID = "";
		}
		try {
			status = (ClientStatus) o.get(4);
		} catch(ClassCastException e) {
			status = null;
		}
		try {
			time = (String) o.get(5);
		} catch(ClassCastException e) {
			time = "";
		}
		try {
			id = (Long) o.get(8);
		} catch(ClassCastException e) {
			id = null;
		}
		try {
			fileSize = (String) o.get(6);
		} catch(ClassCastException e) {
			fileSize = "";
		}
		Machine m = new Machine(
				compID,
				area,
				ip,
				studID,
				status,
				time,
				id,
				fileSize);
		return m;
	}
}
