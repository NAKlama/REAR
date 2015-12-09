package de.uni.goettingen.REARController;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import de.uni.goettingen.REARController.DataStruct.AreaTreeNode;
import de.uni.goettingen.REARController.DataStruct.ClientStatus;
import de.uni.goettingen.REARController.DataStruct.FileDataObject;
import de.uni.goettingen.REARController.DataStruct.MachinesTable;
import de.uni.goettingen.REARController.GUI.DataTablePanel;
import de.uni.goettingen.REARController.GUI.TreePanel;
import de.uni.goettingen.REARController.GUI.Dialogs.ExamStartDialog;
import de.uni.goettingen.REARController.GUI.Dialogs.InfoDialog;
import de.uni.goettingen.REARController.GUI.Tools.IDfactory;
import de.uni.goettingen.REARController.GUI.Tools.RearFileFilter;
import de.uni.goettingen.REARController.GUI.Tools.UpdateMainWindow;

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
import net.miginfocom.swing.MigLayout;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JCheckBox;

public class MainWindow {
	public  static final String PROGRAM_NAME	= "REAR Controller";
	private static final int	MajorVersion 	= 0;
	private static final int	MedVersion		= 1;
	private static final int	MinorVersion	= 6;
	
	public static final String	UPLOAD_SERVER			= "134.76.187.188";
//	public static final String	UPLOAD_SERVER			= "192.168.246.132";
	public static final String	UPLOAD_SERVER_USER		= "REAR";
	
	public static final File	TEMP_DIR				= new File("C:\\tmp");


	private JFrame frmRemoteAudioRecorder;

	private DataTablePanel	table;

	//	private int 		mode; // 0 = Uninitialized; 1 = Initialized pre-Exam; 2 = Recording; 3 = Uploading & Done
	// private int 		mode; // 0 = Uninitialized; 1 = Initialized pre-Exam; 3 = Recording; 5 = Uploading & Done
	private int step;
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
	private JButton		btnInfo;	

	private JLabel		lblStoppedState;
	private JLabel		lblArrowToRec;
	private JLabel		lblRec;
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
	private static final ImageIcon	iconNext			= new ImageIcon(MainWindow.class.getResource("/icons/32/next.png"));
	private static final ImageIcon	iconRestart			= new ImageIcon(MainWindow.class.getResource("/icons/32/reload.png"));
	private static final ImageIcon	iconStop			= new ImageIcon(MainWindow.class.getResource("/icons/32/stop.png"));
	private JLabel lblArrowUnInitToStopped;
	private Component horizontalStrut_6;
	private JCheckBox chckbxAllowStopp;
	
	private String uploadServer = UPLOAD_SERVER;
	private String uploadUser	= UPLOAD_SERVER_USER;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		TEMP_DIR.mkdirs();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmRemoteAudioRecorder.setVisible(true);
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

		frmRemoteAudioRecorder = new JFrame();
		frmRemoteAudioRecorder.setTitle(PROGRAM_NAME);
		frmRemoteAudioRecorder.setBounds(100, 100, 1106, 717);
		frmRemoteAudioRecorder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRemoteAudioRecorder.getContentPane().setLayout(mainLayout);

		toolBarMain = new JToolBar();
		toolBarMain.setAlignmentX(Component.LEFT_ALIGNMENT);
		frmRemoteAudioRecorder.getContentPane().add(toolBarMain, "flowx,cell 0 0 2 1,grow");

		btnNewFile = new JButton("");
		btnNewFile.setToolTipText("New File");
		btnNewFile.setActionCommand("NewFile");
		btnNewFile.addActionListener(listener);
		btnNewFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/document-new.png")));
		toolBarMain.add(btnNewFile);

		btnOpenFile = new JButton("");
		btnOpenFile.setToolTipText("Open File...");
		btnOpenFile.setActionCommand("OpenFile");
		btnOpenFile.addActionListener(listener);
		btnOpenFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/document-open.png")));
		toolBarMain.add(btnOpenFile);

		btnSaveFile = new JButton("");
		btnSaveFile.setToolTipText("Save File");
		btnSaveFile.setActionCommand("SaveFile");
		btnSaveFile.addActionListener(listener);
		btnSaveFile.setEnabled(false);
		btnSaveFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/document-save.png")));
		toolBarMain.add(btnSaveFile);

		btnSaveAsFile = new JButton("");
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

		btnExamMode = new JButton("");
		btnExamMode.setToolTipText("Exam Mode");
		btnExamMode.setActionCommand("ExamMode");
		btnExamMode.addActionListener(listener);
		btnExamMode.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/computer.png")));
		toolBarMain.add(btnExamMode);

		btnEditMode = new JButton("");
		btnEditMode.setToolTipText("Edit Mode");
		btnEditMode.setEnabled(false);
		btnEditMode.setActionCommand("EditMode");
		btnEditMode.addActionListener(listener);
		btnEditMode.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/stock_edit.png")));
		toolBarMain.add(btnEditMode);

		Component horizontalStrut_8 = Box.createHorizontalStrut(10);
		toolBarMain.add(horizontalStrut_8);

		btnNextStep = new JButton("");
		btnNextStep.setToolTipText("Start Exam");
		btnNextStep.setEnabled(false);
		btnNextStep.setActionCommand("Next");
		btnNextStep.addActionListener(listener);
		toolBarMain.add(btnNextStep);
		btnNextStep.setIcon(iconNext);

		chckbxAllowStopp = new JCheckBox("Allow Stopp Exam");
		chckbxAllowStopp.setVisible(false);
		toolBarMain.add(chckbxAllowStopp);

		Component horizontalGlue_2 = Box.createHorizontalGlue();
		toolBarMain.add(horizontalGlue_2);

		btnInfo = new JButton((String) null);
		btnInfo.setActionCommand("Info");
		btnInfo.addActionListener(listener);
		btnInfo.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/info.png")));
		btnInfo.setToolTipText("About this Program");
		toolBarMain.add(btnInfo);
		frmRemoteAudioRecorder.getContentPane().add(table, "cell 1 1 1 2,grow");
		frmRemoteAudioRecorder.getContentPane().add(panelTree, "cell 0 1 1 2,alignx left,growy");

		panelProgress = new JPanel();
		panelProgress.setVisible(false);
		frmRemoteAudioRecorder.getContentPane().add(panelProgress, "cell 1 2,grow");
		panelProgress.setLayout(new BoxLayout(panelProgress, BoxLayout.LINE_AXIS));

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		panelProgress.add(horizontalGlue_1);

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

		UpdateMainWindow umw = new UpdateMainWindow(this);
		timer = new Timer(5000, null);
		timer.addActionListener(umw);
		timer.start();
	}

	private void toggleEdit() {
		if(editMode) {
			editMode = false;
			panelProgress.setVisible(true);
			btnEditMode.setEnabled(true);
			btnExamMode.setEnabled(false);
			btnNextStep.setEnabled(true);
			mainLayout.setComponentConstraints(table, "cell 1 1,grow");
			table.setEditMode(editMode);
		}
		else {
			editMode = true;
			panelProgress.setVisible(false);
			btnEditMode.setEnabled(false);
			btnExamMode.setEnabled(true);
			btnNextStep.setEnabled(false);
			mainLayout.setComponentConstraints(table, "cell 1 1 1 2,grow");
			table.setEditMode(editMode);
		}
		table.setEditMode(editMode);
	}

	public Boolean isEdit() {
		return editMode;
	}

	public void timerEvent() {
		if(!editMode) {
			mode = table.getStatus();
			System.out.println(mode);
			if(mode.getNone() && mode.getInit())
				lblArrowUnInitToStopped.setIcon(iconRightArrow);
			else
				lblArrowUnInitToStopped.setIcon(iconRightArrowGray);

			if(mode.getInit()) 
				lblStoppedState.setIcon(iconStopped);
			else
				lblStoppedState.setIcon(iconStoppedGray);

			if(mode.getInit() && mode.getRec())
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

			switch(step) {
			case 0:
				if(mode.isUninitialized()) {
					btnNextStep.setEnabled(true);
					btnEditMode.setEnabled(true);
				}
				else
					btnNextStep.setEnabled(false);
				break;
			case 1:
				btnEditMode.setEnabled(false);
				if(mode.isInitialized())
					btnNextStep.setEnabled(true);
				else
					btnNextStep.setEnabled(false);
				break;
			case 2:
				btnEditMode.setEnabled(false);
				if(mode.isRec())
					btnNextStep.setEnabled(true);
				else
					btnNextStep.setEnabled(false);
				break;
			case 3:
				btnEditMode.setEnabled(false);
				if(mode.isDone())
					btnNextStep.setEnabled(true);
				else
					btnNextStep.setEnabled(false);
				break;
			}
			table.timerEvent();
		}
	}

	private void next() {
		boolean nextStep = true;
		switch(step) {
		case 0:	
			int studCount = table.studentCount();
			if(studCount > 0) {
				esd = new ExamStartDialog(studCount);
				esd.addListener(listener);
				esd.setVisible(true);
			}
			break;
		case 1:
			btnNextStep.setIcon(iconStop);
			btnNextStep.setEnabled(false);
			chckbxAllowStopp.setVisible(true);
			table.rec();
			break;
		case 2:
			if(chckbxAllowStopp.isSelected()) {
				btnNextStep.setIcon(iconRestart);
				btnNextStep.setEnabled(false);
				chckbxAllowStopp.setVisible(false);
				table.stop();
			}
			else
				nextStep = false;
			break;
		case 3:
			btnNextStep.setIcon(iconNext);
			btnNextStep.setEnabled(false);
			table.reset();
			break;
		}
		if(nextStep) {
			step++;
			if(step > 3)
				step = 0;
		}
		frmRemoteAudioRecorder.repaint();
	}

	public void newFile() {
		panelTree.newEmpty();
		table.newEmpty();
		file = null;
		btnSaveFile.setEnabled(false);
	}

	public void saveFile() {
		try {
			FileOutputStream	fileOut	= new FileOutputStream(file);
			ObjectOutputStream	objOut	= new ObjectOutputStream(fileOut);
			FileDataObject o = new FileDataObject();
			o.tree	= panelTree.getRoot().getSaveObject();
			o.table	= table.getMainTable().cleanForSerialize();
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

	public void saveAsFile() {
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new RearFileFilter());
		int returnVal = fileChooser.showSaveDialog(btnSaveAsFile);

		if(returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			String s = file.getAbsolutePath();
			if(!s.endsWith(".rear") && !s.endsWith(".REAR"))
				file = new File(file.getName() + ".rear");
		}

		btnSaveFile.setEnabled(true);
		saveFile();
	}

	public void loadFile() {
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new RearFileFilter());
		int returnVal = fileChooser.showOpenDialog(btnSaveAsFile);

		if(returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();


			try {
				FileInputStream		fileIn	= new FileInputStream(file);
				ObjectInputStream	objIn	= new ObjectInputStream(fileIn);
				FileDataObject o = (FileDataObject) objIn.readObject();
				panelTree.setRootNode(AreaTreeNode.loadSaveObject(o.tree));
				table.setTable(MachinesTable.loadSaveObject(o.table));
				objIn.close();
				fileIn.close();
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

	public static String getVersion() {
		String ver;
		ver  = "v" + String.valueOf(MajorVersion);
		ver += "." + String.valueOf(MedVersion);
		ver += "." + String.valueOf(MinorVersion);
		return ver;
	}

	private class BtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if(cmd.equals("ExamMode"))
				toggleEdit();
			else if(cmd.equals("EditMode"))
				toggleEdit();
			else if(cmd.equals("Next"))
				next();
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
			else if(cmd.equals("ExamID_OK")) {
				String examID = esd.getExamID();
				esd.setVisible(false);
				if(examID != null && ! examID.equals("")) {
					table.setExamID(examID);
					table.setServer(uploadServer, uploadUser);
					btnNextStep.setIcon(iconRec);
					btnNextStep.setEnabled(false);
					table.init();
				}
			}
			else if(cmd.equals("ExamID_Cancel"))
				esd.setVisible(false);
		}
	}
}
