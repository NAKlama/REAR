package de.uni.goettingen.RemoteAudioRecorderController.GUI;

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

import de.uni.goettingen.RemoteAudioRecorderController.MainWindow;
import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.Area;
import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.AreaTreeNode;
import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.MachinesTable;
import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.Status;

import javax.swing.JScrollPane;

import java.awt.Component;
import java.awt.Dimension;
import java.net.InetAddress;

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

	public DataTablePanel(TreePanel t) {
		tree = t;
		t.addTreeChangeListener(new DataTableTreeChangeListener());
		setLayout(new MigLayout("", "[fill]", "[fill]"));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(100000, 100000));
		scrollPane.setBorder(null);
		add(scrollPane, "cell 0 0,grow");

		machines = new MachinesTable();
		machines.addBlankLine();
		

		table = new JTable(machines);
		table.setDefaultRenderer(AreaTreeNode.class, new AreaTreeNodeRenderer());
		table.setDefaultRenderer(InetAddress.class, new IpRenderer());
		table.setDefaultRenderer(Status.class, new StatusRenderer());
		table.getModel().addTableModelListener(this);
		
		TableColumn statusCol;
		statusCol = table.getColumnModel().getColumn(4);
		statusCol.setPreferredWidth(40);
		statusCol.setMaxWidth(40);
		
		updateComboBox();		

		TableColumn ipColumn = table.getColumnModel().getColumn(2);
		ipColumn.setCellEditor(new IpAddressEditor(new JTextField()));

		scrollPane.setViewportView(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void tableChanged(TableModelEvent e) {
		int row		= e.getFirstRow();
		//		int column	= e.getColumn();
		TableModel model = (TableModel) e.getSource();
		if(row == model.getRowCount()-1) {
			machines.addBlankLine();
		}
	}

	public void setEditMode(Boolean e) {
		editMode = e;
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
			System.out.println("FOO");
			if(editMode == false) {
				Status status = (Status) value;
				if(status.isUninitialized())
					text = "Waiting for initialization";
				else if(status.isStopped())
					this.setIcon(stoppedIcon);
				else if(status.isRecording())
					this.setIcon(recIcon);
				else if(status.isUploading())
					this.setIcon(uploadIcon);
				else if(status.isDone())
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
				InetAddress a = (InetAddress) value;
				this.setText(a.getHostAddress());
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
				this.setText(a.getName());
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
