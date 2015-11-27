package de.uni.goettingen.RemoteAudioRecorderController.DataStruct;

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
	
	private Vector<Machine> table;
	private Boolean			examMode	= false;
	private Boolean			examStarted	= false;
	private Boolean[] 		editableCol = {true, true, true, false, false, false};
	
	public MachinesTable() {
		table = new Vector<Machine>();
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
	
	public Machine getLine(int y) {
		return table.get(y);
	}
	
	public void addBlankLine() {
		table.addElement(new Machine());
	}
	
	@Override
	public int getColumnCount() {
		return 6;
	}

	@Override
	public String getColumnName(int i) {
		if(i < COL_NUM)
			return COLUMN_NAMES[i]; 
		return null;
	}

	@Override
	public int getRowCount() {
		return table.size();
	}

	@Override
	public Object getValueAt(int x, int y) {
		if(x < COL_NUM && y < table.size())
			return table.get(y).getValue(x);
		return null;
	}

	@Override
	public boolean isCellEditable(int x, int y) {
		if(x < COL_NUM)
			return editableCol[x];
		return false;
	}

	@Override
	public void setValueAt(Object o, int x, int y) {
		System.out.println(String.valueOf(x) + ", " + String.valueOf(y));
		if(x < COL_NUM && y < table.size()) {
			Machine m = table.get(y);
			m.setValue(x, o);
			table.set(y, m);
			fireTableCellUpdated(x, y);
		}
	}

}
