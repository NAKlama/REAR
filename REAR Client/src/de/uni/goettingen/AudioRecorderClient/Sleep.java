package de.uni.goettingen.AudioRecorderClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.uni.goettingen.AudioRecorderClient.GUI.StatusWindow;

public class Sleep implements ActionListener {
	private int mode;
	private StatusWindow win;
	
	public Sleep(int i, StatusWindow w) {
		mode = i;
		win  = w;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		switch(mode) {
		case 0:
			win.setRecording();
			break;
		case 1:
			win.setUpload();
			break;
		case 2:
			win.setOK();
			break;
		}
	}

}
