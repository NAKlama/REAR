package de.uni.goettingen.REARController.DataStruct;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import de.uni.goettingen.REARController.DataStruct.Serializable.SerMachinesTable;
import de.uni.goettingen.REARController.DummyClasses.DelButton;
import de.uni.goettingen.REARController.GUI.Tools.IDfactory;
import de.uni.goettingen.REARController.Net.IPreachable;

@SuppressWarnings("serial")
public class MachinesTable extends AbstractTableModel implements TableModel {
	private final static int		COL_NUM			= 7;
	private final static String []	COLUMN_NAMES 	=
		{	"Computer ID",	// 0
			"Area",			// 1
			"IP Address",	// 2
			"Student ID",	// 3
			"Status",		// 4
			"Rec. Time",	// 5
			"Del",			// 6
			"ID"			// 7
		};
	
	private final static Class<?>[] COLUMN_CLASSES =
		{	String.class,
			AreaTreeNode.class,
			IPreachable.class,
			String.class,
			ClientStatus.class,
			String.class,
			DelButton.class,
			Long.class
		};
	
	private Vector<Vector<Object>>	table;
	private Boolean					examMode	= false;
	private Boolean					examStarted	= false;
	private Boolean[] 				editableCol = {true, true, true, false, false, false, false, false};
	
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
	
	public SerMachinesTable getSaveObject() {
		SerMachinesTable o = new SerMachinesTable();
		for(Vector<Object> line : table) {
			Vector<Object> l = new Vector<Object>();
			for(int i = 0; i < COL_NUM + 1; i++) { // + 1
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
				new IDfactory().setUsedID((long) line.get(7));
				for(Object obj : line)
					newLine.addElement(obj);
				o.table.addElement(newLine);
			}
		return o;
	}
	
	public void setStatus(int r, ClientStatus cs) {
		if(cs != null)
			setValueAt(cs, r, 4);
	}
	
	public void setRecTime(int r, String t) {
		if(t != null)
			setValueAt(t, r, 5);
	}
	
	public void setReachable(int r, Boolean b) {
		IPreachable ipr = (IPreachable) getValueAt(r, 2);
		ipr.setReachable(b);
		setValueAt(ipr, r, 2);
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
		AreaTreeNode 	atn	= (AreaTreeNode) o.get(1);
		Area			a	= null;
		IPreachable		ipr = (IPreachable) o.get(2);
		if(atn != null)
			a = (Area) atn.getUserObject();
		Machine m = new Machine(
				(String) 		o.get(0),
				(Area) 			a,
				ipr,
				(String) 		o.get(3),
				(ClientStatus)	o.get(4),
				(String) 		o.get(5),
				(Long)			o.get(7));
		return m;
	}
}
