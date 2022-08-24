package de.uni.goettingen.REARController.GUI.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uni.goettingen.REARController.GUI.DataTablePanel;
import de.uni.goettingen.REARController.Net.NetConnSignal;
import net.miginfocom.swing.MigLayout;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JCheckBox;

public class DebugSignals extends JDialog {
	private static final long serialVersionUID = 6518835592027708815L;

	private final JPanel contentPanel = new JPanel();

	private DataTablePanel			table;
	private int[]					rows;
	
	private JRadioButton rdbtnInitialize;
	private JRadioButton rdbtnStartExam;
	private JRadioButton rdbtnStopExam;
	private JRadioButton rdbtnReset;
	
	private JButton okButton;
	private JButton cancelButton;
	private JRadioButton rdbtnMicRetry;
	private JTextField txtExamTitle;
	private JLabel lblExamTitle;
	private Component horizontalStrut;
	private JLabel lblAudioUrl;
	private Component horizontalStrut_1;
	private JTextField txtURL;
	private Component horizontalStrut_2;
	private JCheckBox chckbxRecord;
	private JCheckBox chckbxPlayAudio;
	/**
	 * Create the dialog.
	 */
	public DebugSignals(DataTablePanel t, int[] r) {
		table = t;
		rows  = r;
		setTitle("Send Signals Manually");

		setBounds(100, 100, 288, 309);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow]", "[][][][][][][][]"));
		
		rdbtnMicRetry = new JRadioButton("Mic. retry");
		contentPanel.add(rdbtnMicRetry, "cell 0 0");

		rdbtnInitialize = new JRadioButton("Initialize");
		rdbtnInitialize.setSelected(true);
		contentPanel.add(rdbtnInitialize, "cell 0 1");
		
		horizontalStrut_2 = Box.createHorizontalStrut(20);
		contentPanel.add(horizontalStrut_2, "flowx,cell 0 2");
		
		horizontalStrut = Box.createHorizontalStrut(20);
		contentPanel.add(horizontalStrut, "flowx,cell 0 3");
		
		lblExamTitle = new JLabel("Exam Title:");
		contentPanel.add(lblExamTitle, "cell 0 3");
		
		txtExamTitle = new JTextField();
		contentPanel.add(txtExamTitle, "cell 0 3,growx");
		txtExamTitle.setColumns(10);
		
		horizontalStrut_1 = Box.createHorizontalStrut(20);
		contentPanel.add(horizontalStrut_1, "flowx,cell 0 4");
		
		lblAudioUrl = new JLabel("Audio URL:");
		contentPanel.add(lblAudioUrl, "cell 0 4");

		rdbtnStartExam = new JRadioButton("Start Exam / Recording");
		contentPanel.add(rdbtnStartExam, "cell 0 5");

		rdbtnStopExam = new JRadioButton("Stop Exam / Recording");
		contentPanel.add(rdbtnStopExam, "cell 0 6");

		rdbtnReset = new JRadioButton("Reset");
		contentPanel.add(rdbtnReset, "cell 0 7");
		
		ButtonGroup bGroup = new ButtonGroup();
		bGroup.add(rdbtnMicRetry);
		bGroup.add(rdbtnInitialize);
		bGroup.add(rdbtnStartExam);
		bGroup.add(rdbtnStopExam);
		bGroup.add(rdbtnReset);
		
		txtURL = new JTextField();
		txtURL.setEditable(false);
		txtURL.setEnabled(false);
		contentPanel.add(txtURL, "cell 0 4,growx");
		txtURL.setColumns(10);
		
		chckbxRecord = new JCheckBox("Record");
		chckbxRecord.setSelected(true);
		contentPanel.add(chckbxRecord, "cell 0 2");
		
		chckbxPlayAudio = new JCheckBox("Play audio");
		contentPanel.add(chckbxPlayAudio, "cell 0 2");
		chckbxPlayAudio.addChangeListener(new PlayFileButtonListener(txtURL));
		
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		okButton.setActionCommand("Debug_OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Debug_Cancel");
		buttonPane.add(cancelButton);
	}

	public void sendSignals() {
		for(int r : rows) {
			NetConnSignal c = table.getConnection(r);
			if(rdbtnMicRetry.isSelected()) {
				c.micRetry();
			}
			if(rdbtnInitialize.isSelected()) {
				c.setID(table.getID(r));
				c.setExamID(txtExamTitle.getText());
				if(chckbxPlayAudio.isSelected()) {
					c.setPlayFile(txtURL.getText());
					if(!chckbxRecord.isSelected())
						c.setPlayOnly();
				}
				c.init();
			}
			else if(rdbtnStartExam.isSelected())
				c.rec();
			else if(rdbtnStopExam.isSelected())
				c.stop();
			else if(rdbtnReset.isSelected())
				c.reset();
		}
	}
	
	public void addListener(ActionListener l) {
		okButton.addActionListener(l);
		cancelButton.addActionListener(l);
	}
	
	private class PlayFileButtonListener  implements ChangeListener {
		private JTextField url;
		
		protected PlayFileButtonListener(JTextField textAudioURL) {
			url = textAudioURL;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			JCheckBox source = (JCheckBox) e.getSource();
			url.setEnabled(source.isSelected());
			url.setEditable(source.isSelected());
		}
	}
}
