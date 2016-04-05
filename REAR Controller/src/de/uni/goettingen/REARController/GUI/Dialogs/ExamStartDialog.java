package de.uni.goettingen.REARController.GUI.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Dimension;
import javax.swing.JCheckBox;

public class ExamStartDialog extends JDialog {
	private static final long serialVersionUID = 803446210867843547L;
	private final JPanel contentPanel = new JPanel();
	
	private JTextField textExamID;
	private JTextField textAudioURL;
	
	private JButton okButton;
	private JButton cancelButton;

	private JCheckBox playFileCheckBox;
	private JCheckBox recordCheckBox;
	/**
	 * Create the dialog.
	 */
	public ExamStartDialog(int sCount) {
		setTitle("Initialize Exam");
		setBounds(100, 100, 555, 215);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));
		{
			JLabel lblRecordSpeech = new JLabel("Record speech?");
			contentPanel.add(lblRecordSpeech, "cell 0 0,alignx trailing");
		}
		{
			recordCheckBox = new JCheckBox("");
			recordCheckBox.setSelected(true);
			contentPanel.add(recordCheckBox, "cell 1 0");
		}
		{
			JLabel lblPlayAudioFile = new JLabel("Play audio file?");
			contentPanel.add(lblPlayAudioFile, "cell 0 1,alignx trailing");
		}
		{
			JLabel lblAudioFile = new JLabel("Audio URL:");
			contentPanel.add(lblAudioFile, "cell 0 2,alignx trailing");
		}
		{
			textAudioURL = new JTextField();
			textAudioURL.setEnabled(false);
			textAudioURL.setEditable(false);
			textAudioURL.setText("http://ilias-intern.wiso.uni-goettingen.de/pi.mp3");
			contentPanel.add(textAudioURL, "cell 1 2,growx");
			textAudioURL.setColumns(10);
		}
		{
			playFileCheckBox = new JCheckBox("");
			contentPanel.add(playFileCheckBox, "cell 1 1");
			playFileCheckBox.addChangeListener(new PlayFileButtonListener(textAudioURL));
		}
		{
			JLabel lblExamIdentifier = new JLabel("Exam Identifier:");
			contentPanel.add(lblExamIdentifier, "cell 0 3,alignx trailing");
		}
		{
			textExamID = new JTextField();
			textExamID.setPreferredSize(new Dimension(500, 20));
			contentPanel.add(textExamID, "cell 1 3,growx");
			textExamID.setColumns(10);
		}
		{
			JLabel lblNumberOfStudents = new JLabel("Number of Students:");
			contentPanel.add(lblNumberOfStudents, "cell 0 4");
		}
		{
			JLabel label = new JLabel(String.valueOf(sCount));
			contentPanel.add(label, "cell 1 4");
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
	
	public Boolean getRecord() {
		return recordCheckBox.isSelected();
	}
	
	public Boolean getPlay() {
		return playFileCheckBox.isSelected();
	}
	
	public String getPlayURL() {
		return textAudioURL.getText();
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
