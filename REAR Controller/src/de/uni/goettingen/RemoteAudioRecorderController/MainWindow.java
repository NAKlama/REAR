package de.uni.goettingen.RemoteAudioRecorderController;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.Component;

import de.uni.goettingen.RemoteAudioRecorderController.GUI.DataTablePanel;
import de.uni.goettingen.RemoteAudioRecorderController.GUI.InfoDialog;
import de.uni.goettingen.RemoteAudioRecorderController.GUI.TreePanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

public class MainWindow {
	public  static final String PROGRAM_NAME	= "REAR Controller";
	private static final int	MajorVersion 	= 0;
	private static final int	MedVersion		= 1;
	private static final int	MinorVersion	= 1;
	
	private JFrame frmRemoteAudioRecorder;
	
	private DataTablePanel	table;
	
//	private int 		mode; // 0 = Uninitialized; 1 = Initialized pre-Exam; 2 = Recording; 3 = Uploading & Done
	private int 		mode; // 0 = Uninitialized; 1 = Initialized pre-Exam; 3 = Recording; 5 = Uploading & Done
	private Boolean		editMode = true;		
	
	private JToolBar	toolBarMain;

	private MigLayout	mainLayout;
	
	private TreePanel	panelTree;
	private JPanel		panelProgress;
	
	private JButton		btnNewFile;
	private JButton		btnOpenFile;
	private JButton		btnSaveFile;
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

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainLayout = new MigLayout("", "[200px][grow]", "[39px][grow][1px]");
		
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
		btnNewFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		btnNewFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/document-new.png")));
		toolBarMain.add(btnNewFile);
		
		btnOpenFile = new JButton("");
		btnOpenFile.setToolTipText("Open File");
		btnOpenFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		btnOpenFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/document-open.png")));
		toolBarMain.add(btnOpenFile);
		
		btnSaveFile = new JButton("");
		btnSaveFile.setToolTipText("Save File");
		btnSaveFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		btnSaveFile.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/document-save.png")));
		toolBarMain.add(btnSaveFile);
		
		Component horizontalStrut_7 = Box.createHorizontalStrut(10);
		toolBarMain.add(horizontalStrut_7);
		
		panelTree = new TreePanel();
		panelTree.setMinimumSize(new Dimension(200, 200));
		table = new DataTablePanel(panelTree);
		
		btnExamMode = new JButton("");
		btnExamMode.setToolTipText("Exam Mode");
		btnExamMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				toggleEdit();
			}
		});
		
		btnEditMode = new JButton("");
		btnEditMode.setToolTipText("Edit Mode");
		btnEditMode.setEnabled(false);
		btnEditMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				toggleEdit();
			}
		});
		btnEditMode.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/stock_edit.png")));
		toolBarMain.add(btnEditMode);
		btnExamMode.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/32/computer.png")));
		toolBarMain.add(btnExamMode);
		
		Component horizontalStrut_8 = Box.createHorizontalStrut(10);
		toolBarMain.add(horizontalStrut_8);
		
		btnNextStep = new JButton("");
		btnNextStep.setToolTipText("Start Exam");
		btnNextStep.setEnabled(false);
		btnNextStep.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				next();
			}
		});
		toolBarMain.add(btnNextStep);
		btnNextStep.setIcon(iconNext);
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		toolBarMain.add(horizontalGlue_2);
		
		btnInfo = new JButton((String) null);
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InfoDialog infoDialog = new InfoDialog();
				infoDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			}
		});
		btnInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
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
	}
	
	private void toggleEdit() {
		if(editMode) {
			editMode = false;
			panelProgress.setVisible(true);
			btnEditMode.setEnabled(true);
			btnExamMode.setEnabled(false);
			btnNextStep.setEnabled(true);
			mainLayout.setComponentConstraints(table, "cell 1 1,grow");
		}
		else {
			editMode = true;
			panelProgress.setVisible(false);
			btnEditMode.setEnabled(false);
			btnExamMode.setEnabled(true);
			btnNextStep.setEnabled(false);
			mainLayout.setComponentConstraints(table, "cell 1 1 1 2,grow");
		}
		table.setEditMode(editMode);
	}
	
	public Boolean isEdit() {
		return editMode;
	}
	
	private void next() {
		switch(mode) {
		case 0:
			btnEditMode.setEnabled(false);
			lblStoppedState.setIcon(iconStopped);
			btnNextStep.setIcon(iconRec);
			// Send Signal to hosts
			break;
		case 1:
			btnNextStep.setIcon(iconNext);
			lblArrowToRec.setIcon(iconRightArrow);
			lblRec.setIcon(iconRec);
			break;
		case 2:
			btnNextStep.setIcon(iconStop);
			lblStoppedState.setIcon(iconStoppedGray);
			lblArrowToRec.setIcon(iconRightArrowGray);
			break;
		case 3:
			btnNextStep.setIcon(iconNext);
			lblArrowToUpload.setIcon(iconRightArrow);
			lblUpload.setIcon(iconUpload);
			lblArrowToDone.setIcon(iconRightArrow);
			break;
		case 4:
			lblRec.setIcon(iconRecGray);
			lblArrowToUpload.setIcon(iconRightArrowGray);
			lblDone.setIcon(iconOk);
			break;
		case 5:
			lblUpload.setIcon(iconUploadGray);
			lblArrowToDone.setIcon(iconRightArrowGray);
			btnNextStep.setIcon(iconRestart);
			break;
		case 6:
			lblDone.setIcon(iconOkGray);
			btnEditMode.setEnabled(true);
			btnNextStep.setIcon(iconNext);
			break;
		}
		mode++;
		if(mode > 6)
			mode = 0;
		frmRemoteAudioRecorder.repaint();
	}
	
	public static String getVersion() {
		String ver;
		ver  = "v" + String.valueOf(MajorVersion);
		ver += "." + String.valueOf(MedVersion);
		ver += "." + String.valueOf(MinorVersion);
		return ver;
	}
}
