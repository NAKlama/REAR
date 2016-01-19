package de.uni.goettingen.REARController.GUI.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.uni.goettingen.REARController.Net.ClientConn;
import net.miginfocom.swing.MigLayout;
import javax.swing.JRadioButton;

public class DebugSignals extends JDialog {
	private static final long serialVersionUID = 6518835592027708815L;

	private final JPanel contentPanel = new JPanel();

	private Vector<ClientConn> connList;
	
	private JRadioButton rdbtnInitialize;
	private JRadioButton rdbtnStartExam;
	private JRadioButton rdbtnStopExam;
	private JRadioButton rdbtnReset;
	
	private JButton okButton;
	private JButton cancelButton;
	/**
	 * Create the dialog.
	 */
	public DebugSignals(Vector<ClientConn> cList) {
		setTitle("Send Signals Manually");
		connList = cList;
		setBounds(100, 100, 189, 209);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[]", "[][][][]"));

		rdbtnInitialize = new JRadioButton("Initialize");
		rdbtnInitialize.setSelected(true);
		contentPanel.add(rdbtnInitialize, "cell 0 0");

		rdbtnStartExam = new JRadioButton("Start Exam / Recording");
		contentPanel.add(rdbtnStartExam, "cell 0 1");

		rdbtnStopExam = new JRadioButton("Stop Exam / Recording");
		contentPanel.add(rdbtnStopExam, "cell 0 2");

		rdbtnReset = new JRadioButton("Reset");
		contentPanel.add(rdbtnReset, "cell 0 3");
		
		ButtonGroup bGroup = new ButtonGroup();
		bGroup.add(rdbtnInitialize);
		bGroup.add(rdbtnStartExam);
		bGroup.add(rdbtnStopExam);
		bGroup.add(rdbtnReset);

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
		for(ClientConn c : connList) {
			if(rdbtnInitialize.isSelected())
				c.init();
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
}
