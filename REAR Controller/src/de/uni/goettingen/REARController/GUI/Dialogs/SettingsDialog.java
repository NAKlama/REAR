package de.uni.goettingen.REARController.GUI.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.uni.goettingen.REARController.DataStruct.PropertiesStore;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SettingsDialog extends JDialog {
	private static final long serialVersionUID = 8383669857793567575L;

	private final JPanel contentPanel = new JPanel();
	
	private JButton okButton;
	private JButton cancelButton;
	private JLabel lblServerAddress;
	private JTextField txtServerAddress;
	private JLabel lblServerUser;
	private JTextField txtServerUser;
	
	public SettingsDialog(PropertiesStore prop) {
		setTitle("REAR Controller - Settings");
		setBounds(100, 100, 450, 147);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][]"));
		{
			lblServerAddress = new JLabel("Server Address:");
			contentPanel.add(lblServerAddress, "cell 0 0,alignx trailing");
		}
		{
			txtServerAddress = new JTextField();
			txtServerAddress.setText(prop.getUploadServer());
			contentPanel.add(txtServerAddress, "cell 1 0,growx");
			txtServerAddress.setColumns(10);
		}
		{
			lblServerUser = new JLabel("Server User:");
			contentPanel.add(lblServerUser, "cell 0 1,alignx trailing");
		}
		{
			txtServerUser = new JTextField();
			txtServerUser.setText(prop.getUploadUser());
			contentPanel.add(txtServerUser, "cell 1 1,growx");
			txtServerUser.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("Settings_OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Settings_Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public String getServer() {
		return txtServerAddress.getText();
	}
	
	public String getUser() {
		return txtServerUser.getText();
	}

	public void addListener(ActionListener l) {
		okButton.addActionListener(l);
		cancelButton.addActionListener(l);
	}
}
