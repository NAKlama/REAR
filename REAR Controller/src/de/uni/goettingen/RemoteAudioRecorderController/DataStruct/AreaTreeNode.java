package de.uni.goettingen.RemoteAudioRecorderController.DataStruct;

import java.awt.Color;
import java.io.Serializable;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.Serializable.SerAreaTree;
import de.uni.goettingen.RemoteAudioRecorderController.GUI.IDfactory;

@SuppressWarnings("serial")
public class AreaTreeNode extends DefaultMutableTreeNode implements Cloneable, MutableTreeNode, Serializable, TreeNode, Comparator<AreaTreeNode> {
	public AreaTreeNode() {
		super(new Area("All Computers"));
	}
	
	public AreaTreeNode(Area a) {
		super(a);
	}
	
	public SerAreaTree getSaveObject() {
		return getSaveObject(this);
	}
	
	private SerAreaTree getSaveObject(AreaTreeNode n) {
		SerAreaTree root = new SerAreaTree();
		root.area		= (Area) n.getUserObject();
		if(n.getChildCount() > 0)
				for(Object child : n.children)
					root.children.addElement(getSaveObject((AreaTreeNode) child));
		return root;
	}
	
	public long getID() {
		return ((Area) this.getUserObject()).getID();
	}
	
	public static AreaTreeNode loadSaveObject(SerAreaTree t) {
		AreaTreeNode root = new AreaTreeNode(t.area);
		new IDfactory().setUsedID(root.getID());
		addChildren(root, t);
		return root;
	}
	
	private static void addChildren(AreaTreeNode n, SerAreaTree t) {
		for(SerAreaTree c : t.children) {
			AreaTreeNode newNode = new AreaTreeNode(c.area);
			new IDfactory().setUsedID(newNode.getID());
			addChildren(newNode, c);
			n.add(newNode);
		}
	}
	
	public String getName() {
		if(userObject != null)
			return ((Area) userObject).getName();
		return "";
	}
	
	public Color getColor() {
		if(userObject != null)
			return ((Area) userObject).getColor();
		return Color.BLACK;
	}
	
	public void setName(String n) {
		if(userObject != null)
			((Area) userObject).setName(n);
	}
	
	public void setColor(Color c) {
		if(userObject != null)
			((Area) userObject).setColor(c);
	}
	
	@Override
	public String toString() {
		if(userObject != null)
			return ((Area) userObject).getName();
		return "";
	}

	@Override
	public boolean equals(Object o) {
		if(o != null && o.toString() == this.toString())
			return true;
		return false;
	}
	
	@Override
	public int compare(AreaTreeNode a, AreaTreeNode b) {
		if(a.equals(b))
			return 0;
		return -1;
	}
}
