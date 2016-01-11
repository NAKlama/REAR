package de.uni.goettingen.REARController.DataStruct.Serializable;

import java.io.Serializable;
import java.util.Vector;

import de.uni.goettingen.REARController.DataStruct.Area;
import de.uni.goettingen.REARController.DataStruct.AreaTreeNode;
import de.uni.goettingen.REARController.DataStruct.ClientStatus;
import de.uni.goettingen.REARController.Net.IPreachable;

public class SerMachinesTable implements Serializable {
	private static final long serialVersionUID = -3241321017722896939L;
	public Vector<Vector<Object>> data;

	public SerMachinesTable() {
		data = new Vector<Vector<Object>>();
	}
	
	public int size() {
		return data.size();
	}
	
	public SerMachinesTable removeEmpty() {
		SerMachinesTable out = new SerMachinesTable();
		for(Vector<Object> line : data) {
			boolean testCompID = line.get(0) != null && ! ((String) line.get(0)).equals("");
			boolean testArea   = line.get(1) != null;
			boolean testIP     = line.get(2) != null && ! ((IPreachable) line.get(2)).getIPstr().equals("");
			if(testCompID && testArea && testIP) {
				Vector<Object> oLine = new Vector<Object>();
				for(int i=0; i < line.size(); i++) {
					oLine.add(line.get(i));
				}
				out.data.add(oLine);
			}
		}
		return out;
	}
	
	public SerMachinesTable cleanForSerialize() {
		SerMachinesTable out = new SerMachinesTable();
		for(Vector<Object> line : data) {
			Vector<Object> oLine = new Vector<Object>();
			for(int i=0; i < line.size(); i++) {
				if(i==3) 
					oLine.add("");
				else if(i==4)
					oLine.add(new ClientStatus());
				else if(i==5)
					oLine.add("0:00:00");
				else if(i==6)
					oLine.add(null);
				else
					oLine.add(line.get(i));
				
			}
			out.data.add(oLine);
		}
		return out;
	}

	public void update(SerMachinesTable t) {
		for(Vector<Object> inLine : t.data) {
			Boolean found = false;
			for(Vector<Object> line : data) 
				if(inLine.get(7) == line.get(7)) {
					found = true;
					for(int i=0; i < 7; i++) {
						line.set(i, inLine.get(i));
//						System.out.println("Setting col " + i + " to " + inLine.get(i));
					}
				}
			if(!found && (inLine.get(0) != "" || inLine.get(1) != null || inLine.get(2) != null)) {
				data.addElement(inLine);
			}
		}
		System.out.println(this.print()); 
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
	
	public String print() {
		String out = new String();
		for(Vector<Object> line : data) {
			out += (String) line.get(0);										// Computer ID
			if(line.get(1) != null)
				out += ", " + ((Area) ((AreaTreeNode) line.get(1)).getUserObject()).getName();		// Area
			else
				out += ", null";
			if(line.get(2) != null)
				out += ", " + ((IPreachable) line.get(2)).getIPstr();				// IP Address
			else
				out += ", null";
			out += ", " + (String) line.get(3);									// Student ID
			if(line.get(4) != null)
				out += ", " + ((ClientStatus) line.get(4)).getStatus();				// Status
			else
				out += ", null";
			out += ", " + (String) line.get(5);
			out += ", " + String.valueOf((Long) line.get(7));
			out += "\n";
		}
		return out;
	}
}
