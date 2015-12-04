package de.uni.goettingen.REARController.GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.uni.goettingen.REARController.MainWindow;
import de.uni.goettingen.REARController.DataStruct.Area;
import de.uni.goettingen.REARController.DataStruct.AreaTreeNode;
import de.uni.goettingen.REARController.GUI.Dialogs.NewAreaDialog;
import de.uni.goettingen.REARController.GUI.Tools.TreeChangeListener;

import java.awt.Component;

@SuppressWarnings("serial")
public class TreePanel extends JPanel implements TreeSelectionListener {
	private JTree		treeDisp;
	private JToolBar	toolBar;
	private JButton		btnAdd;
	private JButton		btnRemove;
	private JButton		btnEdit;
	
	private DataTablePanel table;

	private AreaTreeNode	selectedArea		= null;
	private AreaTreeNode	selectedAreaParent	= null;
	private AreaTreeNode	rootNode;
	private Component horizontalGlue;
	private Component horizontalGlue_1;
	
	private Vector<TreeChangeListener> treeChangeListeners;

	public TreePanel() {
		table = null;
		treeChangeListeners = new Vector<TreeChangeListener>();
		setMinimumSize(new Dimension(100, 200));
		rootNode	= new AreaTreeNode();
		treeDisp 	= new JTree(rootNode);
		treeDisp.setPreferredSize(new Dimension(200, 200));
		treeDisp.setMaximumSize(new Dimension(500, 5000));
		treeDisp.setMinimumSize(new Dimension(200, 0));
		treeDisp.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeDisp.addTreeSelectionListener(this);
		treeDisp.setCellRenderer(new TreeRenderer());
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(treeDisp);

		toolBar = new JToolBar();
		toolBar.setMaximumSize(new Dimension(200, 20));
		toolBar.setFloatable(false);
		add(toolBar);

		BtnListener listener = new BtnListener();

		btnAdd = new JButton((String) null);
		btnAdd.setActionCommand("Add");
		btnAdd.addActionListener(listener);

		horizontalGlue_1 = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue_1);
		btnAdd.setToolTipText("Add Area");
		toolBar.add(btnAdd);
		btnAdd.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/16/add.png")));

		btnRemove = new JButton("");
		btnRemove.setActionCommand("Remove");
		btnRemove.addActionListener(listener);
		btnRemove.setToolTipText("Remove Area");
		toolBar.add(btnRemove);
		btnRemove.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/16/remove.png")));

		btnEdit = new JButton("");
		btnEdit.setActionCommand("Edit");
		btnEdit.addActionListener(listener);
		btnEdit.setToolTipText("Edit Area");
		toolBar.add(btnEdit);
		btnEdit.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/16/stock_edit.png")));

		horizontalGlue = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue);
	}
	
	public void setTable(DataTablePanel t) {
		table = t;
	}
	
	public void newEmpty() {
		rootNode	= new AreaTreeNode();
		treeDisp.setModel(new DefaultTreeModel(rootNode));
		((DefaultTreeModel) treeDisp.getModel()).reload();
	}
	
	public void setRootNode(AreaTreeNode n) {
		rootNode = n;
		treeDisp.setModel(new DefaultTreeModel(n));
		((DefaultTreeModel) treeDisp.getModel()).reload();
	}
	
	public JTree getTree() {
		return treeDisp;
	}

	public void addTreeChangeListener(TreeChangeListener l) {
		treeChangeListeners.addElement(l);
	}

	public void removeTreeChangeListener(TreeChangeListener l) {
		treeChangeListeners.removeElement(l);
	}
	
	public AreaTreeNode getRoot() {
		return rootNode;
	}

	public void addArea(Area newArea) {
		AreaTreeNode parent = selectedArea;
		if(parent == null)
			parent = rootNode;
		AreaTreeNode newNode = new AreaTreeNode(newArea);
		parent.add(newNode);
		if(treeChangeListeners.size() > 0)
			for(TreeChangeListener l : treeChangeListeners)
				l.changedTreeStructure();
		DefaultTreeModel model = (DefaultTreeModel) treeDisp.getModel();
		model.reload(parent);		
	}

	public void removeArea() {
		if(selectedAreaParent != null) {
			selectedAreaParent.remove(selectedArea);
			if(treeChangeListeners.size() > 0)
				for(TreeChangeListener l : treeChangeListeners)
					l.changedTreeStructure();
			DefaultTreeModel model = (DefaultTreeModel) treeDisp.getModel();
			model.reload(selectedAreaParent);	
		}
		
	}

	public Vector<AreaTreeNode> getAreaList() {
		Vector<AreaTreeNode> out = new Vector<AreaTreeNode>();
		getTreeText(out, treeDisp.getModel(), treeDisp.getModel().getRoot(), "");
		return out;
	}
	
	private static void getTreeText(Vector<AreaTreeNode> out, TreeModel model, Object object, String indent) {
	    out.addElement((AreaTreeNode) object);
	    for (int i = 0; i < model.getChildCount(object); i++) {
	        getTreeText(out, model, model.getChild(object, i), indent + "  ");
	    }
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		AreaTreeNode	parent;
		AreaTreeNode	node 	= (AreaTreeNode) treeDisp.getLastSelectedPathComponent();
		if(node == null)
			parent = null;
		else
			parent	= (AreaTreeNode) node.getParent();

		selectedArea		= node;
		selectedAreaParent	= parent;
		
		if(table != null)
			table.setFilter(selectedArea);
	}

	private class TreeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
			Area node = (Area) ((AreaTreeNode) value).getUserObject();

			setForeground(node.getColor());
			return this;
		}
	}

	private class BtnListener implements ActionListener {
		private Boolean			add;
		private NewAreaDialog	dialog;
		private AreaTreeNode	parentArea;

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			parentArea = selectedArea;
			if(parentArea == null)
				parentArea = rootNode;
			if(cmd.equals("Add")) {
				dialog = new NewAreaDialog((Area) parentArea.getUserObject(), ((Area) parentArea.getUserObject()).getColor());
				add = true;
				dialog.addListener(this);
				dialog.setVisible(true);
			}
			else if(cmd.equals("Remove")) {
				if(selectedAreaParent != null && selectedArea != null)
					removeArea();
			}
			else if(cmd.equals("Edit")) {
				if(selectedArea != null) {
					add = false;
					dialog = new NewAreaDialog(
							(Area) selectedAreaParent.getUserObject(), 
							((Area) selectedArea.getUserObject()).getName(), 
							((Area) selectedArea.getUserObject()).getColor() );
					dialog.addListener(this);
					dialog.setVisible(true);
				}
			}
			else if(cmd.equals("OK")) {
				if(add) 
					addArea(new Area(dialog.getName(), dialog.getColor()));
				else {
					selectedArea.setName(dialog.getName());
					selectedArea.setColor(dialog.getColor());
					if(treeChangeListeners.size() > 0)
						for(TreeChangeListener l : treeChangeListeners)
							l.changedTreeStructure();
					DefaultTreeModel model = (DefaultTreeModel) treeDisp.getModel();
					model.reload();	
				}
				dialog.setVisible(false);
				treeDisp.repaint();
			}
			else if(cmd.equals("Cancel"))
				dialog.setVisible(false);
		}
	}
}
