package de.uni.goettingen.REARController.DataStruct;

import java.io.Serializable;

import de.uni.goettingen.REARController.DataStruct.Serializable.SerAreaTree;
import de.uni.goettingen.REARController.DataStruct.Serializable.SerMachinesTable;

@SuppressWarnings("serial")
public class FileDataObject implements Serializable {
	public SerAreaTree		tree;
	public SerMachinesTable	table;
	
	public FileDataObject() {
		tree = null;
		table = null;
	}
}
