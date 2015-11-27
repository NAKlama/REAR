package de.uni.goettingen.AudioRecorderClient.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateStatusWindow implements ActionListener {
	private StatusWindow win;
	
	public UpdateStatusWindow(StatusWindow w) {
		win = w;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		win.timerEvent();
	}

}
