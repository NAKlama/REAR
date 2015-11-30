package de.uni.goettingen.RemoteAudioRecorderController.DataStruct.Serializable;

import java.io.Serializable;
import java.util.Vector;

import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.Area;
import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.AreaTreeNode;

public class SerMachinesTable implements Serializable {
	private static final long serialVersionUID = -3241321017722896939L;
	public Vector<Vector<Object>> data;

	public SerMachinesTable() {
		data = new Vector<Vector<Object>>();
	}

	public void update(SerMachinesTable t) {
		for(Vector<Object> inLine : t.data) {
			Boolean found = false;
			for(Vector<Object> line : data) 
				if(inLine.get(7) == line.get(7) && inLine.get(1) != null) {
					found = true;
					for(int i=0; i < 7; i++)
						line.set(i, inLine.get(i));
				}
			if(!found && (inLine.get(0) != "" || inLine.get(1) != null || inLine.get(2) != null)) {
				data.addElement(inLine);
			}
		}
	}

	public SerMachinesTable filter(Vector<AreaTreeNode> v) {
		SerMachinesTable out = new SerMachinesTable();
		for(Vector<Object> line : data)
			for(AreaTreeNode node : v) {
				if(((AreaTreeNode) line.get(1)) != null)
				{
					long nodeID = ((Area) node.getUserObject()).getID();
					long lineID = ((Area) ((AreaTreeNode) line.get(1)).getUserObject()).getID();
					if(nodeID == lineID)
						out.data.addElement(line);
				}
			}
		return out;
	}
}
