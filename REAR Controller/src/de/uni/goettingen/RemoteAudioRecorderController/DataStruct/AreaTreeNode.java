package de.uni.goettingen.RemoteAudioRecorderController.DataStruct;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

@SuppressWarnings("serial")
public class AreaTreeNode extends DefaultMutableTreeNode implements Cloneable, MutableTreeNode, Serializable, TreeNode {
	public AreaTreeNode() {
		super(new Area("All Computers"));
	}
	
	public AreaTreeNode(Area a) {
		super(a);
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
}
