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
import javax.swing.JRadioButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;

public class SettingsDialog extends JDialog {
	private static final long serialVersionUID = 8383669857793567575L;

	private final JPanel contentPanel = new JPanel();
	
	private JButton okButton;
	private JButton cancelButton;
	private JLabel lblServerAddress;
	private JTextField txtServerAddress;
	private JLabel lblServerUser;
	private JTextField txtServerUser;
	private JLabel lblDebugModeOn;
	private JRadioButton debugYes;
	private JRadioButton debugNo;
	private JPanel debugChoicePanel;
	
	public SettingsDialog(PropertiesStore prop) {
		setTitle("REAR Controller - Settings");
		setBounds(100, 100, 450, 199);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
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
			lblDebugModeOn = new JLabel("Debug Mode:");
			contentPanel.add(lblDebugModeOn, "cell 0 2,alignx trailing");
		}
		{
			debugChoicePanel = new JPanel();
			contentPanel.add(debugChoicePanel, "cell 1 2");
			debugChoicePanel.setLayout(new BoxLayout(debugChoicePanel, BoxLayout.PAGE_AXIS));
			
			debugYes = new JRadioButton("Yes");
			debugNo = new JRadioButton("No");
			debugYes.setSelected(prop.getDebugMode());
			debugNo.setSelected(!prop.getDebugMode());
			debugChoicePanel.add(debugYes);
			debugChoicePanel.add(debugNo);

			ButtonGroup debugMode = new ButtonGroup();
			debugMode.add(debugYes);
			debugMode.add(debugNo);
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
	
	public Boolean getDebug() {
		return debugYes.isSelected();
	}

	public void addListener(ActionListener l) {
		okButton.addActionListener(l);
		cancelButton.addActionListener(l);
	}
}
