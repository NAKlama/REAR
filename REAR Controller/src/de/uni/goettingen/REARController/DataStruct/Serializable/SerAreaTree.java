package de.uni.goettingen.REARController.DataStruct.Serializable;

import java.io.Serializable;
import java.util.Vector;

import de.uni.goettingen.REARController.DataStruct.Area;

public class SerAreaTree implements Serializable {
	private static final long serialVersionUID = -8289525693633415337L;
	public Area					area;
	public Vector<SerAreaTree>	children;
	
	public SerAreaTree() {
		area = null;
		children = new Vector<SerAreaTree>();
	}
}
