package de.uni.goettingen.RemoteAudioRecorderController.DataStruct;

import java.awt.Color;
import java.io.Serializable;

import de.uni.goettingen.RemoteAudioRecorderController.GUI.IDfactory;

@SuppressWarnings("serial")
public class Area implements Serializable {
	private String			name;
	private Color			color;
	private long			id;
//	private String			description;
	
	public Area(String n, Color c) {
		init(n, c, true);
	}
	
	public Area(String n, Color c, long i) {
		init(n, c, false);
		id = i;
	}
	
	public Area(Color c) {
		init("New Area", c, true);
	}
	
	public Area(String n) {
		init(n, Color.BLACK, true);
	}
	
	public Area() {
		init("New Area", Color.BLACK, true);
	}
	
	public void init(String n, Color c, Boolean genId) {
		name		= n;
		color		= c;
		if(genId)
			id = new IDfactory().getID();
//		description = "";
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public long	getID() {
		return id;
	}
	
//	public String getDescription() {
//		return description;
//	}

	public void setName(String n) {
		name = n;
	}
	
	public void setColor(Color c) {
		color = c;
	}
	
//	public void setDescription(String d) {
//		description = d;
//	}
	
	@Override
	public String toString() {
		return name;
	}
}
