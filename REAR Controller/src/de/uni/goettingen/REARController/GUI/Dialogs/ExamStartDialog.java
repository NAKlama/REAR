package de.uni.goettingen.REARController.GUI.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Dimension;

public class ExamStartDialog extends JDialog {
	private static final long serialVersionUID = 803446210867843547L;
	private final JPanel contentPanel = new JPanel();
	
	private JTextField textExamID;
	
	private JButton okButton;
	private JButton cancelButton;
	/**
	 * Create the dialog.
	 */
	public ExamStartDialog(int sCount) {
		setBounds(100, 100, 337, 141);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow]", "[][]"));
		{
			JLabel lblExamIdentifier = new JLabel("Exam Identifier:");
			contentPanel.add(lblExamIdentifier, "cell 0 0,alignx trailing");
		}
		{
			textExamID = new JTextField();
			textExamID.setPreferredSize(new Dimension(500, 20));
			contentPanel.add(textExamID, "cell 1 0,growx");
			textExamID.setColumns(10);
		}
		{
			JLabel lblNumberOfStudents = new JLabel("Number of Students:");
			contentPanel.add(lblNumberOfStudents, "cell 0 1");
		}
		{
			JLabel label = new JLabel(String.valueOf(sCount));
			contentPanel.add(label, "cell 1 1");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("ExamID_OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("ExamID_Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public String getExamID() {
		return textExamID.getText();
	}
	
	public void addListener(ActionListener l) {
		okButton.addActionListener(l);
		cancelButton.addActionListener(l);
	}
}
