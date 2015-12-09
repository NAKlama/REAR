package de.uni.goettingen.REARController.GUI.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;

public class MessageDialog extends JDialog {

	private static final long serialVersionUID = -3343227626932857425L;
	private final JPanel contentPanel = new JPanel();
	private final Component horizontalGlue = Box.createHorizontalGlue();

	public MessageDialog(String message) {
		setTitle("Message");
		setBounds(100, 100, 450, 145);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblMessage = new JLabel(message);
			contentPanel.add(lblMessage);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			buttonPane.add(horizontalGlue);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("Close_Message");
				okButton.addActionListener(new MsgListener());
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				Component horizontalGlue_1 = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue_1);
			}
		}
	}

	private class MsgListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if(cmd.equals("Close_Message")) {
				((MessageDialog) e.getSource()).setVisible(false);
			}
		}
		
	}
}
