package de.uni.goettingen.RemoteAudioRecorderController.GUI;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import net.miginfocom.swing.MigLayout;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.Area;
import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.AreaTreeNode;
import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.MachinesTable;

import javax.swing.JScrollPane;

import java.awt.Component;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class DataTablePanel extends JPanel {
	private JTable 			table;
	private MachinesTable	machines;
	private TreePanel		tree;

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
		updateComboBox();

		scrollPane.setViewportView(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}
	
	private void updateComboBox() {
		TableColumn areaColumn = table.getColumnModel().getColumn(1);
		areaSelector = new JComboBox<AreaTreeNode>();
		areaSelector.setRenderer(new AreaListCellRenderer<Area>());
		for(AreaTreeNode a : tree.getAreaList())
			areaSelector.addItem(a);
		areaColumn.setCellEditor(new DefaultCellEditor(areaSelector));
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
