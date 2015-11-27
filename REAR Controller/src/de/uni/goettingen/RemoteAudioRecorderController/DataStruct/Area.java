package de.uni.goettingen.RemoteAudioRecorderController.DataStruct;

import java.awt.Color;

public class Area {
	private String			name;
	private Color			color;
//	private String			description;
	
	public Area(String n, Color c) {
		init(n, c);
	}
	
	public Area(Color c) {
		init("New Area", c);
	}
	
	public Area(String n) {
		init(n, Color.BLACK);
	}
	
	public Area() {
		init("New Area", Color.BLACK);
	}
	
	public void init(String n, Color c) {
		name		= n;
		color		= c;
//		description = "";
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
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
