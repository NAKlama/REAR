package de.uni.goettingen.REARClient.GUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.uni.goettingen.REARClient.REARclient;
import de.uni.goettingen.REARClient.SignalObject;

@SuppressWarnings("serial")
public class StatusWindow extends JFrame {
	private Container win;

	private JPanel iconPanel;
	private JPanel statusPanel;
	private JPanel idPanel;

	private JLabel timerLabel;
	private JLabel statusLabel;
	private JLabel idLabel;
	private JLabel id;
	private JLabel icon;

	private ImageIcon	stoppedIcon;
	private ImageIcon	recIcon;
	private ImageIcon	uploadIcon;
	private ImageIcon	okIcon;
	private ImageIcon	playIcon;
	private Timer		timer;
	private String		studentID;
	private String		examID;
	private Date		recStarted;
//	private AudioLevel	micStreamLevel;

	private int h, m, s;
	private int mode; 
	/* Modes:
	 * 0 = invisible; 
	 * 1 = stopped; 
	 * 2 = audio test running;
	 * 3 = audio test done; 
	 * 4 = recording; 
	 * 5 = transferring; 
	 * 6 = done; 
	 * 7 = playing
	 */

	private SignalObject signal = null;

	public StatusWindow()
	{
		studentID			= "";
		examID				= "";
//		micStreamLevel		= new AudioLevel(ml.getLine());
		h = m = s 			= 0;
		mode 				= 0;
		stoppedIcon 		= new ImageIcon(StatusWindow.class.getResource("/icons/50/stopped.png"));
		recIcon 			= new ImageIcon(StatusWindow.class.getResource("/icons/32/rec.png"));
		playIcon			= new ImageIcon(StatusWindow.class.getResource("/icons/32/start.png"));
		uploadIcon 			= new ImageIcon(StatusWindow.class.getResource("/icons/32/upload.png"));
		okIcon 				= new ImageIcon(StatusWindow.class.getResource("/icons/32/OK.png"));

		win 				= this.getContentPane();
		iconPanel			= new JPanel();
		statusPanel			= new JPanel();
		idPanel				= new JPanel();
		icon  				= new JLabel(stoppedIcon);
		timerLabel			= new JLabel("  0:00:00");
		idLabel				= new JLabel("Student ID:");
		id 					= new JLabel("NO ID SET");
		statusLabel			= new JLabel("Waiting for start of exam signal.");

		win.setLayout(new BoxLayout(win, BoxLayout.PAGE_AXIS));

		iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.LINE_AXIS));
		if(REARclient.DEBUG_GUI)
			iconPanel.setBackground(Color.ORANGE);

		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.LINE_AXIS));
		if(REARclient.DEBUG_GUI)
			statusPanel.setBackground(Color.CYAN);

		idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.LINE_AXIS));
		if(REARclient.DEBUG_GUI)
			idPanel.setBackground(Color.GREEN);

		timerLabel.setFont(timerLabel.getFont().deriveFont(30.0f));
		idLabel.setFont(idLabel.getFont().deriveFont(Font.PLAIN));
		id.setFont(id.getFont().deriveFont(18.0f));

		idPanel.add(Box.createRigidArea(new Dimension(5,0)));
		idPanel.add(idLabel);
		idPanel.add(Box.createRigidArea(new Dimension(10,0)));
		idPanel.add(id);
		idPanel.add(Box.createHorizontalGlue());

		iconPanel.add(Box.createRigidArea(new Dimension(5,0)));
		iconPanel.add(icon);
		iconPanel.add(timerLabel);
		iconPanel.add(Box.createHorizontalGlue());
		statusPanel.add(statusLabel);
		statusPanel.add(Box.createHorizontalGlue());

		win.add(Box.createRigidArea(new Dimension(0, 5)));
		win.add(idPanel);
		win.add(iconPanel);
		win.add(Box.createRigidArea(new Dimension(0, 2)));
		win.add(statusPanel);
		win.add(Box.createVerticalGlue());

//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


		setTitle("REAR");
		setSize(280, 135);
		setResizable(false);

		UpdateStatusWindow usw = new UpdateStatusWindow(this);
		timer = new Timer(100, null);
		timer.addActionListener(usw);
		timer.start();
	}
	
	public void setSignalObject(SignalObject s) {
		signal = s;
	}

	public void init() {
		mode = 1;
		if(!examID.equals(""))
			setTitle("REAR - " + examID);
		else
			setTitle("REAR");
		setVisible(true);
	}
	
	public void startAudioTest() {
		mode = 2;
	}
	
	public void finishedAudioTest() {
		mode = 3;
	}

	public void setRecording(Boolean doRecord, Boolean doPlay) {
		String label = new String("Recording");
		synchronized(signal) {
			if(doPlay && ! doRecord)
				mode = 7;
			else
				mode = 4;
		}
		recStarted = new Date();
		if(doPlay && doRecord)
			label = "Recording / Playing";
		else if(doPlay && !doRecord)
			label = "Playing";
		if(doRecord)
			icon.setIcon(recIcon);
		else
			icon.setIcon(playIcon);
		statusLabel.setText(label);
		win.repaint();
	}

	public void setUpload() {
		synchronized(signal) {
			mode = 5;
		}
		icon.setIcon(uploadIcon);
		statusLabel.setText("Uploading recorded file to server.");
		win.repaint();
	}

	public void setOK() {
		synchronized(signal) {
			mode = 6;
		}
		icon.setIcon(okIcon);
		statusLabel.setText("Upload finished.");
		win.repaint();
	}

	public void reset() {
		synchronized(signal) {
			mode = 0;
		}
		recStarted = null;
		icon.setIcon(stoppedIcon);
		statusLabel.setText("Waiting for start of exam signal.");
		timerLabel.setText("  0:00:00");
		h = m = s = 0;
		setVisible(false);
	}

	public void setID(String idStr) {
		studentID = idStr;
		id.setText(studentID);
	}

	public String getID() {
		return studentID;
	}

	public int getMode() {
		return mode;
	}
	
	public String getTime() {
		return String.format("%d:%02d:%02d", h, m, s);
	}

	public boolean timerEvent() {
//		levelMic.setLevel(micStreamLevel.getLevel());

		if(mode == 4) {
			Date now = new Date();
			double seconds = (now.getTime() - recStarted.getTime()) / 1000;
			h = (int) Math.floor(seconds / 3600);
			seconds %= 3600;
			m = (int) Math.floor(seconds / 60);
			seconds %= 60;
			s = (int) Math.floor(seconds);
			timerLabel.setText(String.format("  %d:%02d:%02d", h, m, s));
			return true;
		}
		return true;
	}

//	private BufferedImage createImageFromSVG(InputStream svgStream, int w, int h) {
//		Reader reader = new BufferedReader(new InputStreamReader(svgStream));
//		TranscoderInput svgImage = new TranscoderInput(reader);
//
//		BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
//		transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) w);
//		transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) h);
//		transcoder.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, UIManager.getColor("Panel.background"));
//		try {
//			transcoder.transcode(svgImage, null);
//		} catch (TranscoderException e) {
//			e.printStackTrace();
//		}
//
//		return transcoder.getBufferedImage();
//	}

	public void setExamID(String id) {
		setTitle("REAR - " + id);
		examID = id;
		
	}

	public String getExamID() {
		return examID;
	}
}
