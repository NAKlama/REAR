package de.uni.goettingen.REARClient.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import de.uni.goettingen.REARClient.REARclient;

@SuppressWarnings("serial")
public class LevelMeter extends JPanel {
	private float 	level 		= 0.0f;

	public LevelMeter() {
		setPreferredSize(new Dimension(60, 20));
		setMaximumSize(new Dimension(60, 20));
		if(REARclient.DEBUG)
			setBackground(Color.BLUE);
	}

	public void setLevel(float l) {
		level = l;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int l = Math.round(level  * 60.0f); 
		if(l > 0) {
			if(level > 0.9f)
				g.setColor(Color.RED);
			else
				g.setColor(Color.GREEN);

			g.fillRect(0, 0, l, 20);
		}
		else
		{
			g.setColor(getBackground());
			g.fillRect(0, 0, 60, 20);
		}
	}
}
