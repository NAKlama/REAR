package de.uni.goettingen.REARController.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.uni.goettingen.REARController.MainWindow;

public class UpdateMainWindow implements ActionListener {
	private MainWindow win;
	
	public UpdateMainWindow(MainWindow w) {
		win = w;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		win.timerEvent();
	}

}
