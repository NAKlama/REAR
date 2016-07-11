package de.uni.goettingen.REARController;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import de.uni.goettingen.REARController.DataStruct.AreaTreeNode;
import de.uni.goettingen.REARController.DataStruct.ClientStatus;
import de.uni.goettingen.REARController.DataStruct.FileDataObject;
import de.uni.goettingen.REARController.DataStruct.MachinesTable;
import de.uni.goettingen.REARController.DataStruct.PropertiesStore;
import de.uni.goettingen.REARController.GUI.DataTablePanel;
import de.uni.goettingen.REARController.GUI.TreePanel;
import de.uni.goettingen.REARController.GUI.Dialogs.DebugSignals;
import de.uni.goettingen.REARController.GUI.Dialogs.ExamStartDialog;
import de.uni.goettingen.REARController.GUI.Dialogs.InfoDialog;
import de.uni.goettingen.REARController.GUI.Dialogs.SettingsDialog;
import de.uni.goettingen.REARController.GUI.Tools.IDfactory;
import de.uni.goettingen.REARController.GUI.Tools.RearFileFilter;
import de.uni.goettingen.REARController.GUI.Tools.Step;
import de.uni.goettingen.REARController.Net.NetConnSignal;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;

public class MainWindow implements ActionListener {
	public  static final String PROGRAM_NAME	= "REAR Controller";
	private static final int	MajorVersion 	= 0;
	private static final int	MedVersion		= 4;
	private static final int	MinorVersion	= 1;
	
	private JFrame frmREAR;

	private DataTablePanel	table;

	private Step 			step = new Step(-1, 5, 0);
	private ClientStatus	mode;

	private Boolean		editMode = true;

	private JToolBar	toolBarMain;

	private MigLayout	mainLayout;

	private TreePanel	panelTree;
	private JPanel		panelProgress;

	private JButton		btnNewFile;
	private JButton		btnOpenFile;
	private JButton		btnSaveFile;
	private JButton		btnSaveAsFile;
	private JButton		btnExamMode;
	private JButton		btnEditMode;
	private JButton		btnNextStep;
	private JButton		btnReset;
	private JButton		btnInfo;	

	private JLabel		lblStoppedState;
	private JLabel		lblArrowToRec;
	private JLabel		lblArrowToATest;
	private JLabel		lblRec;
	private JLabel		lblATest;
	private JLabel		lblArrowToUpload;
	private JLabel		lblUpload;
	private JLabel		lblArrowToDone;
	private JLabel		lblDone;

	private JFileChooser	fileChooser;

	private File		file;

	private Timer		timer;
	
	private BtnListener listener;
	
	private ExamStartDialog esd;

	@SuppressWarnings("unused")
	private IDfactory 	idFactory;

	private static final ImageIcon	iconStoppedGray		= new ImageIcon(MainWindow.class.getResource("/icons/32/gray/stopped.png"));
	private static final ImageIcon	iconStopped			= new ImageIcon(MainWindow.class.getResource("/icons/32/stopped.png"));
	private static final ImageIcon	iconRightArrowGray	= new ImageIcon(MainWindow.class.getResource("/icons/32/gray/Arrow_facing_right_-_Green.png"));
	private static final ImageIcon	iconRightArrow		= new ImageIcon(MainWindow.class.getResource("/icons/32/Arrow_facing_right_-_Green.png"));
	private static final ImageIcon	iconRecGray			= new ImageIcon(MainWindow.class.getResource("/icons/32/gray/rec.png"));
	private static final ImageIcon	iconRec				= new ImageIcon(MainWindow.class.getResource("/icons/32/rec.png"));
	private static final ImageIcon	iconUploadGray		= new ImageIcon(MainWindow.class.getResource("/icons/32/gray/upload.png"));
	private static final ImageIcon	iconUpload			= new ImageIcon(MainWindow.class.getResource("/icons/32/upload.png"));
	private static final ImageIcon	iconOkGray			= new ImageIcon(MainWindow.class.getResource("/icons/32/gray/OK.png"));
	private static final ImageIcon	iconOk				= new ImageIcon(MainWindow.class.getResource("/icons/32/OK.png"));
	private static final ImageIcon	iconATestGray		= new ImageIcon(MainWindow.class.getResource("/icons/32/gray/audio-headset.png"));
	private static final ImageIcon	iconATest			= new ImageIcon(MainWindow.class.getResource("/icons/32/audio-headset.png"));
	private static final ImageIcon	iconNext			= new ImageIcon(MainWindow.class.getResource("/icons/32/next.png"));
	private static final ImageIcon	iconRestart			= new ImageIcon(MainWindow.class.getResource("/icons/32/reload.png"));
	private static final ImageIcon	iconStop			= new ImageIcon(MainWindow.class.getResource("/icons/32/stop.png"));
	private JLabel lblArrowUnInitToStopped;
	private Component horizontalStrut_6;
	private JCheckBox chckbxAllowStopp;

	private static PropertiesStore prop;
	private JButton btnSettings;
	private Component horizontalStrut_9;
	private Component horizontalStrut_10;
	private JButton btnDeleteRow;
	private Component horizontalGlue_3;
	private JButton btnSendManualSignals;
	private Component horizontalStrut_11;
	private JLabel lblWarning;
	private Component horizontalStrut_12;
	private Component horizontalGlue_2;
	private JSpinner stepSpinner;
	private JLabel lblStep;
	private JButton btnManualAudioTest;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		prop = new PropertiesStore();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmREAR.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		file = null;
		idFactory = new IDfactory();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainLayout = new MigLayout("", "[200px][grow]", "[39px][grow][1px]");
		listener = new BtnListener();

		frmREAR = new JFrame();
		frmREAR.setTitle(PROGRAM_NAME);
		frmREAR.setBounds(100, 100, 1106, 717);
		frmREAR.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmREAR.getContentPane().setLayout(mainLayout);

		toolBarMain = new JToolBar();
		toolBarMain.setAlignmentX(Component.LEFT_ALIGNMENT);
		frmREAR.getContentPane().add(toolBarMain, "flowx,cell 0 0 2 1,grow");

		btnNewFile = new JButton("New");
		btnNewFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnNewFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnNewFile.setToolTipText("New File");
		btnNewFile.setActionCommand("NewFile");
		btnNewFile.addActionListener(listener);
		btnNewFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/document-new.png")));
		toolBarMain.add(btnNewFile);

		btnOpenFile = new JButton("Open");
		btnOpenFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnOpenFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnOpenFile.setToolTipText("Open File...");
		btnOpenFile.setActionCommand("OpenFile");
		btnOpenFile.addActionListener(listener);
		btnOpenFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/document-open.png")));
		toolBarMain.add(btnOpenFile);

		btnSaveFile = new JButton("Save");
		btnSaveFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveFile.setToolTipText("Save File");
		btnSaveFile.setActionCommand("SaveFile");
		btnSaveFile.addActionListener(listener);
		btnSaveFile.setEnabled(false);
		btnSaveFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/document-save.png")));
		toolBarMain.add(btnSaveFile);

		btnSaveAsFile = new JButton("Save As...");
		btnSaveAsFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveAsFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveAsFile.setToolTipText("SaveAs File...");
		btnSaveAsFile.setActionCommand("SaveAsFile");
		btnSaveAsFile.addActionListener(listener);
		btnSaveAsFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/document-save-as.png")));
		toolBarMain.add(btnSaveAsFile);

		Component horizontalStrut_7 = Box.createHorizontalStrut(10);
		toolBarMain.add(horizontalStrut_7);

		panelTree = new TreePanel();
		panelTree.setMinimumSize(new Dimension(200, 200));
		table = new DataTablePanel(panelTree);
		panelTree.setTable(table);

		btnExamMode = new JButton("Exam Mode");
		btnExamMode.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnExamMode.setHorizontalTextPosition(SwingConstants.CENTER);
		btnExamMode.setToolTipText("Exam Mode");
		btnExamMode.setActionCommand("ExamMode");
		btnExamMode.addActionListener(listener);
		btnExamMode.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/computer.png")));
		toolBarMain.add(btnExamMode);

		btnEditMode = new JButton("Edit Mode");
		btnEditMode.setHorizontalTextPosition(SwingConstants.CENTER);
		btnEditMode.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnEditMode.setToolTipText("Edit Mode");
		btnEditMode.setEnabled(false);
		btnEditMode.setActionCommand("EditMode");
		btnEditMode.addActionListener(listener);
		btnEditMode.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/stock_edit.png")));
		toolBarMain.add(btnEditMode);
		
		horizontalStrut_10 = Box.createHorizontalStrut(10);
		toolBarMain.add(horizontalStrut_10);
		
		btnDeleteRow = new JButton("Delete Row");
		btnDeleteRow.setActionCommand("DeleteRow");
		btnDeleteRow.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/stop.png")));
		btnDeleteRow.setHorizontalTextPosition(SwingConstants.CENTER);
		btnDeleteRow.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnDeleteRow.addActionListener(listener);
		btnDeleteRow.setVisible(true);
		toolBarMain.add(btnDeleteRow);

		Component horizontalStrut_8 = Box.createHorizontalStrut(10);
		toolBarMain.add(horizontalStrut_8);

		btnNextStep = new JButton("Start Exam");
		btnNextStep.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnNextStep.setHorizontalTextPosition(SwingConstants.CENTER);
		btnNextStep.setToolTipText("Start Exam");
		btnNextStep.setEnabled(true);
		btnNextStep.setActionCommand("Next");
		btnNextStep.addActionListener(listener);
		toolBarMain.add(btnNextStep);
		btnNextStep.setIcon(iconNext);

		chckbxAllowStopp = new JCheckBox("Allow Stopp Exam");
		chckbxAllowStopp.setVerticalTextPosition(SwingConstants.BOTTOM);
		chckbxAllowStopp.setHorizontalTextPosition(SwingConstants.CENTER);
		chckbxAllowStopp.setVisible(false);
		
		btnReset = new JButton("Reset");
		btnReset.setActionCommand("Reset");
		btnReset.setVisible(false);
		
		btnManualAudioTest = new JButton("Manual Audio Test");
		btnManualAudioTest.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/audio-headset.png")));
		btnManualAudioTest.addActionListener(listener);
		btnManualAudioTest.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnManualAudioTest.setToolTipText("Do an audio test with the selected client");
		btnManualAudioTest.setHorizontalTextPosition(SwingConstants.CENTER);
		btnManualAudioTest.setEnabled(true);
		btnManualAudioTest.setActionCommand("ManAudioTest");
		btnManualAudioTest.setVisible(false);
		
		toolBarMain.add(btnManualAudioTest);
		btnReset.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/reload.png")));
		btnReset.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnReset.setToolTipText("Reset");
		btnReset.setHorizontalTextPosition(SwingConstants.CENTER);
		toolBarMain.add(btnReset);
		toolBarMain.add(chckbxAllowStopp);
		
		horizontalGlue_3 = Box.createHorizontalGlue();
		toolBarMain.add(horizontalGlue_3);
		
		btnSendManualSignals = new JButton("Send Manual Signals");
		btnSendManualSignals.setActionCommand("DebugSignals");
		btnSendManualSignals.setToolTipText("Send Manual Signals");
		btnSendManualSignals.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSendManualSignals.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/gear.png")));
		btnSendManualSignals.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSendManualSignals.addActionListener(listener);
		btnSendManualSignals.setVisible(prop.getDebugMode());
		
		stepSpinner = new JSpinner();
		stepSpinner.setSize(new Dimension(40, 20));
		stepSpinner.setVisible(prop.getDebugMode());
		stepSpinner.setEnabled(!editMode);
		stepSpinner.addChangeListener(new changeListener());
		step.linkSpinner(stepSpinner);
		
		lblStep = new JLabel("Step: ");
		lblStep.setVisible(prop.getDebugMode());
		
		toolBarMain.add(lblStep);
		toolBarMain.add(stepSpinner);
		
		horizontalGlue_2 = Box.createHorizontalGlue();
		toolBarMain.add(horizontalGlue_2);
		toolBarMain.add(btnSendManualSignals);

		btnInfo = new JButton("About");
		btnInfo.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		btnInfo.setActionCommand("Info");
		btnInfo.addActionListener(listener);
		
		btnSettings = new JButton("Properties");
		btnSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSettings.setActionCommand("Settings");
		btnSettings.setToolTipText("Properties");
		btnSettings.addActionListener(listener);
		
		horizontalStrut_12 = Box.createHorizontalStrut(20);
		toolBarMain.add(horizontalStrut_12);
		btnSettings.setActionCommand("Settings");
		btnSettings.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/settings.png")));
		toolBarMain.add(btnSettings);
		
		horizontalStrut_9 = Box.createHorizontalStrut(20);
		toolBarMain.add(horizontalStrut_9);
		btnInfo.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/info.png")));
		btnInfo.setToolTipText("About this Program");
		toolBarMain.add(btnInfo);
		frmREAR.getContentPane().add(table, "cell 1 1 1 2,grow");
		frmREAR.getContentPane().add(panelTree, "cell 0 1 1 2,alignx left,growy");

		panelProgress = new JPanel();
		panelProgress.setVisible(false);
		frmREAR.getContentPane().add(panelProgress, "cell 1 2,grow");
		panelProgress.setLayout(new BoxLayout(panelProgress, BoxLayout.LINE_AXIS));

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		panelProgress.add(horizontalGlue_1);
		
		lblWarning = new JLabel("");
		lblWarning.setVisible(false);
		lblWarning.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/warning.png")));
		panelProgress.add(lblWarning);
		
		horizontalStrut_11 = Box.createHorizontalStrut(20);
		panelProgress.add(horizontalStrut_11);

		lblArrowUnInitToStopped = new JLabel("");
		lblArrowUnInitToStopped.setIcon(iconRightArrowGray);
		panelProgress.add(lblArrowUnInitToStopped);

		horizontalStrut_6 = Box.createHorizontalStrut(20);
		panelProgress.add(horizontalStrut_6);

		lblStoppedState = new JLabel("");
		lblStoppedState.setIcon(iconStoppedGray);
		panelProgress.add(lblStoppedState);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		panelProgress.add(horizontalStrut);

		lblArrowToATest = new JLabel("");
		lblArrowToATest.setIcon(iconRightArrowGray);
		panelProgress.add(lblArrowToATest);
		
		Component horizontalStrut_6 = Box.createHorizontalStrut(20);
		panelProgress.add(horizontalStrut_6);
		
		lblATest = new JLabel("");
		lblATest.setIcon(iconATestGray);
		panelProgress.add(lblATest);
		
		Component horizontalStrut_9 = Box.createHorizontalStrut(20);
		panelProgress.add(horizontalStrut_9);
		
		lblArrowToRec = new JLabel("");
		lblArrowToRec.setIcon(iconRightArrowGray);
		panelProgress.add(lblArrowToRec);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panelProgress.add(horizontalStrut_1);

		lblRec = new JLabel("");
		lblRec.setIcon(iconRecGray);
		panelProgress.add(lblRec);

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		panelProgress.add(horizontalStrut_2);

		lblArrowToUpload = new JLabel("");
		lblArrowToUpload.setIcon(iconRightArrowGray);
		panelProgress.add(lblArrowToUpload);

		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		panelProgress.add(horizontalStrut_3);

		lblUpload = new JLabel("");
		lblUpload.setIcon(iconUploadGray);
		panelProgress.add(lblUpload);

		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		panelProgress.add(horizontalStrut_4);

		lblArrowToDone = new JLabel("");
		lblArrowToDone.setIcon(iconRightArrowGray);
		panelProgress.add(lblArrowToDone);

		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		panelProgress.add(horizontalStrut_5);

		lblDone = new JLabel("");
		lblDone.setIcon(iconOkGray);
		panelProgress.add(lblDone);

		Component horizontalGlue = Box.createHorizontalGlue();
		panelProgress.add(horizontalGlue);
		
		timer = new Timer(5000, null);
		timer.addActionListener(this);
		timer.start();
	}

	private void toggleEdit() {
		if(editMode) {
			editMode = false;
			panelProgress.setVisible(true);
			btnEditMode.setEnabled(true);
			btnExamMode.setEnabled(false);
			btnDeleteRow.setEnabled(false);
			btnNextStep.setEnabled(true);
			mainLayout.setComponentConstraints(table, "cell 1 1,grow");
			panelTree.setExamMode(true);
			table.setFilter(null);
			stepSpinner.setEnabled(true);
		}
		else {
			editMode = true;
			panelProgress.setVisible(false);
			btnEditMode.setEnabled(false);
			btnExamMode.setEnabled(true);
			btnDeleteRow.setEnabled(true);
			btnNextStep.setEnabled(false);
			mainLayout.setComponentConstraints(table, "cell 1 1 1 2,grow");
			panelTree.setExamMode(false);
			stepSpinner.setEnabled(false);
		}
		table.setEditMode(editMode);
	}

	public Boolean isEdit() {
		return editMode;
	}

	private Boolean changedMode() {
		boolean nextStep = true;
		System.out.println("changedMode(): " + step.get());
		switch(step.get()) {
		case -1:
			table.micRetry();
			break;
		case 0:	
			int studCount = table.studentCount();
			if(studCount > 0) {
				esd = new ExamStartDialog(studCount);
				esd.addListener(listener);
				esd.setVisible(true);
				btnReset.setVisible(true);
				chckbxAllowStopp.setVisible(true);
			}
			else
				nextStep = false;
			break;
		case 1:
			table.recTest();
			btnManualAudioTest.setVisible(true);
			break;
		case 2:
			break;
		case 3:
			btnManualAudioTest.setVisible(false);
			btnNextStep.setIcon(iconStop);
//			btnNextStep.setEnabled(false);
			table.rec();
			break;
		case 4:
			if(chckbxAllowStopp.isSelected()) {
				btnNextStep.setIcon(iconRestart);
//				btnNextStep.setEnabled(false);
				chckbxAllowStopp.setSelected(false);
				chckbxAllowStopp.setVisible(false);
				table.stop();
			}
			else
				nextStep = false;
			break;
		case 5:
			btnNextStep.setIcon(iconNext);
//			btnNextStep.setEnabled(false);
			btnReset.setVisible(false);
			table.reset();
			break;
		}
		return nextStep;
	}
	
	private void next() {
		System.out.println("next()");
		if(this.changedMode()) 
			step.inc();
		frmREAR.repaint();
	}
	
	private void reset() {
		System.out.println("Reset Button pressed.");
		if(chckbxAllowStopp.isSelected()) {
			chckbxAllowStopp.setSelected(false);
			chckbxAllowStopp.setVisible(false);
			btnReset.setVisible(false);
			table.reset();
			step.set(0);;
		}
	}

	private void newFile() {
		panelTree.newEmpty();
		table.newEmpty();
		file = null;
		btnSaveFile.setEnabled(false);
	}

	private void saveFile() {
		try {
			FileOutputStream	fileOut	= new FileOutputStream(file);
			ObjectOutputStream	objOut	= new ObjectOutputStream(fileOut);
			FileDataObject o = new FileDataObject();
			o.tree	= panelTree.getRoot().getSaveObject();
			o.table	= table.getMainTable().cleanForSerialize();
			System.out.println(o.table.print());
			System.out.println(o.table.size());
			objOut.writeObject(o);
			objOut.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveAsFile() {
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new RearFileFilter());
		int returnVal = fileChooser.showSaveDialog(btnSaveAsFile);

		if(returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			String s = file.getAbsolutePath();
			if(!s.endsWith(".rear") && !s.endsWith(".REAR"))
				file = new File(file.getAbsolutePath() + ".rear");
		}

		btnSaveFile.setEnabled(true);
		saveFile();
	}

	private void loadFile() {
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new RearFileFilter());
		int returnVal = fileChooser.showOpenDialog(btnSaveAsFile);

		if(returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();


			try {
				FileInputStream		fileIn	= new FileInputStream(file);
				ObjectInputStream	objIn	= new ObjectInputStream(fileIn);
				FileDataObject o = (FileDataObject) objIn.readObject();
				objIn.close();
				fileIn.close();
				panelTree.setRootNode(AreaTreeNode.loadSaveObject(o.tree));
				table.setTable(MachinesTable.loadSaveObject(o.table));
				System.out.println(o.table.size());
				btnSaveFile.setEnabled(true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void manAudioTest(int[] rows) {
		for(int r : rows) {
			NetConnSignal c = table.getConnection(r);
			c.recTest();
		}
	}

	public static String getVersion() {
		String ver;
		ver  = "v" + String.valueOf(MajorVersion);
		ver += "." + String.valueOf(MedVersion);
		ver += "." + String.valueOf(MinorVersion);
		return ver;
	}

	public static PropertiesStore getProp() {
		return prop;
	}
	
	private class BtnListener implements ActionListener {
		private SettingsDialog	sd;
		private DebugSignals	dSigs;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			System.out.println("Action command = " + cmd);
			if(cmd.equals("ExamMode"))
				toggleEdit();
			else if(cmd.equals("EditMode"))
				toggleEdit();
			else if(cmd.equals("Next"))
			{
				System.out.println("Next Button pressed");
				next();
			}
			else if(cmd.equals("Reset"))
				reset();
			else if(cmd.equals("Info")) {
				InfoDialog infoDialog = new InfoDialog();
				infoDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			}
			else if(cmd.equals("NewFile")) 
				newFile();
			else if(cmd.equals("OpenFile"))
				loadFile();
			else if(cmd.equals("SaveFile"))
				saveFile();
			else if(cmd.equals("SaveAsFile"))
				saveAsFile();
			else if(cmd.equals("ManAudioTest")) {
				int[] rows = table.getJTable().getSelectedRows();
				manAudioTest(rows);
			}
			else if(cmd.equals("ExamID_OK")) {
				Boolean	play, record;
				String	examID, playFileURL;
				play	= esd.getPlay();
				record	= esd.getRecord();
				examID	= esd.getExamID();
				if(play)
					playFileURL = esd.getPlayURL();
				else
					playFileURL = null;
				esd.setVisible(false);
				if(examID != null && ! examID.equals("")) {
					table.setExamID(examID);
					if(play) {
						table.setPlayFile(playFileURL);
						if(!record) 
							table.setPlayOnly();
					}
					table.setServer(prop.getUploadServer(), prop.getUploadUser());
					table.init();
					btnNextStep.setIcon(iconRec);
//					btnNextStep.setEnabled(false);
				}
			}
			else if(cmd.equals("ExamID_Cancel")) {
				esd.setVisible(false);
				btnNextStep.setEnabled(true);
			}
			else if(cmd.equals("Settings")) {
				sd = new SettingsDialog(prop);
				sd.addListener(this);
				sd.setVisible(true);
			}
			else if(cmd.equals("Settings_OK")) {
				prop.setUploadServer(sd.getServer());
				prop.setUploadUser(sd.getUser());
				prop.setDebugMode(sd.getDebug());
				btnSendManualSignals.setVisible(sd.getDebug());
				lblStep.setVisible(sd.getDebug());
				stepSpinner.setVisible(sd.getDebug());
				
				table.setServer(sd.getServer(), sd.getUser());
				
				sd.setVisible(false);
			}
			else if(cmd.equals("Settings_Cancel")) {
				sd.setVisible(false);
			}
			else if(cmd.equals("DeleteRow")) {
				int[] rows = table.getJTable().getSelectedRows();
				if(rows.length > 0) {
					table.removeRows(rows);
				}
			}
			else if(cmd.equals("DebugSignals")) {
				int[] rows = table.getJTable().getSelectedRows();
				if(rows.length > 0) {
					dSigs = new DebugSignals(table, rows);
					dSigs.addListener(this);
					dSigs.setVisible(true);
				}
			}
			else if(cmd.equals("Debug_OK")) {
				dSigs.sendSignals();
				dSigs.setVisible(false);
			}
			else if(cmd.equals("Debug_Cancel")) {
				dSigs.setVisible(false);
			}
 		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(!editMode) {
			mode = table.getStatus();
			
			if(mode.getNoMic()) {
				lblWarning.setVisible(true);
				step.set(-1);;
			} else
				lblWarning.setVisible(false);
//			System.out.println(mode);
			if(mode.getNone() && mode.getInit())
				lblArrowUnInitToStopped.setIcon(iconRightArrow);
			else
				lblArrowUnInitToStopped.setIcon(iconRightArrowGray);

			if(mode.getInit()) 
				lblStoppedState.setIcon(iconStopped);
			else
				lblStoppedState.setIcon(iconStoppedGray);
			
			if(mode.getInit() && mode.getRecTest())
				lblArrowToATest.setIcon(iconRightArrow);
			else
				lblArrowToATest.setIcon(iconRightArrowGray);
			
			if(mode.getRecTest())
				lblATest.setIcon(iconATest);
			else
				lblATest.setIcon(iconATestGray);

			if(mode.getRecTestDone() && ! mode.getRecTest())
				lblArrowToRec.setIcon(iconRightArrow);
			else
				lblArrowToRec.setIcon(iconRightArrowGray);

			if(mode.getRec())
				lblRec.setIcon(iconRec);
			else
				lblRec.setIcon(iconRecGray);

			if(mode.getRec() && (mode.getUpload() || mode.getDone()))
				lblArrowToUpload.setIcon(iconRightArrow);
			else
				lblArrowToUpload.setIcon(iconRightArrowGray);

			if(mode.getUpload())
				lblUpload.setIcon(iconUpload);
			else
				lblUpload.setIcon(iconUploadGray);

			if((mode.getRec() || mode.getUpload()) && mode.getDone())
				lblArrowToDone.setIcon(iconRightArrow);
			else
				lblArrowToDone.setIcon(iconRightArrowGray);

			if(mode.getDone())
				lblDone.setIcon(iconOk);
			else
				lblDone.setIcon(iconOkGray);
			
			if(mode.getDone() && ! mode.getRec() && ! mode.getUpload())
				step.set(3);

			switch(step.get()) {
			case -1:
				btnNextStep.setText("Retry Mic Connection");
				btnNextStep.setToolTipText("Retry connecting clients to microphone");
				btnNextStep.setEnabled(true);
				break;
			case 0:
				btnNextStep.setText("Prepare Exam");
				btnNextStep.setToolTipText("Prepare Exam");
				break;
			case 1:
				btnNextStep.setText("Start Audio Test");
				btnNextStep.setToolTipText("Start Audio Test");
				break;
			case 2:
				btnNextStep.setText("Audio Test Complete");
				btnNextStep.setToolTipText("Audio Test Complete");
				break;
			case 3:
				btnNextStep.setText("Start Exam");
				btnNextStep.setToolTipText("Start Exam");
//				btnEditMode.setEnabled(false);
				break;
			case 4:
				btnNextStep.setText("Stop Exam");
				btnNextStep.setToolTipText("Stop Exam");
//				btnEditMode.setEnabled(false);
				break;
			case 5:
				btnNextStep.setText("Reset Exam");
				btnNextStep.setToolTipText("Reset Exam");
//				btnEditMode.setEnabled(false);
				break;
			}
			table.timerEvent();
		}
	}
	
	private class changeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSpinner src = (JSpinner) e.getSource();
			Integer  val = (Integer) src.getValue();
			step.set(val);
//			changedMode();
		}
		
	}
}
