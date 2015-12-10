package de.uni.goettingen.REARController.GUI.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class PasswordDialog extends JDialog {
	private static final long serialVersionUID = -8859697181882364644L;

	private final JPanel contentPanel = new JPanel();

	private JButton okButton;
	private JButton cancelButton;
	private JLabel lblPleaseEnterPassword;
	
	private boolean passphrase;
	private JPasswordField passwordField;
	
	public PasswordDialog(String label, Boolean passphr) {
		passphrase = passphr;
		setTitle(label);
		setBounds(100, 100, 384, 127);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		{
			lblPleaseEnterPassword = new JLabel(label);
			contentPanel.add(lblPleaseEnterPassword);
		}
		{
			passwordField = new JPasswordField();
			contentPanel.add(passwordField);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				if(passphrase)
					okButton.setActionCommand("PPH_OK");
				else
					okButton.setActionCommand("PW_OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				if(passphrase)
					cancelButton.setActionCommand("PPH_Cancel");
				else
					cancelButton.setActionCommand("PW_Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public String getPassword() {
		return new String(passwordField.getPassword());
	}
	
	public void addListener(ActionListener l) {
		okButton.addActionListener(l);
		cancelButton.addActionListener(l);
	}
}
