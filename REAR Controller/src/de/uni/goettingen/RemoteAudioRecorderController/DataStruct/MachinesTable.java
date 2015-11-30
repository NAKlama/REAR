package de.uni.goettingen.RemoteAudioRecorderController.DataStruct;

import java.net.InetAddress;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class MachinesTable extends AbstractTableModel implements TableModel {
	private final static int		COL_NUM			= 6;
	private final static String []	COLUMN_NAMES 	=
		{	"Computer ID",
			"Area",
			"IP Address",
			"Student ID",
			"Status",
			"Rec. Time"
		};
	
	private final static Class<?>[] COLUMN_CLASSES =
		{	String.class,
			AreaTreeNode.class,
			InetAddress.class,
			String.class,
			Status.class,
			String.class
		};
	
	private Vector<Vector<Object>>	table;
	private Boolean					examMode	= false;
	private Boolean					examStarted	= false;
	private Boolean[] 				editableCol = {true, true, true, false, false, false};
	
	public MachinesTable() {
		table = new Vector<Vector<Object>>();
	}
	
	public void setExamMode(Boolean exam, Boolean started) {
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
		table.addElement(new Machine().getObjVector());
	}
	
	@Override
	public int getColumnCount() {
		return 6;
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
	
	private Machine objVectorToMachine(Vector<Object> o) {
		Machine m = new Machine(
				(String) o.get(0),
				(Area) ((AreaTreeNode) o.get(1)).getUserObject(),
				(InetAddress) o.get(2),
				(String) o.get(3),
				(Status) o.get(4),
				(String) o.get(5) );
		return m;
	}
}
