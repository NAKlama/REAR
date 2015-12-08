package de.uni.goettingen.REARController.GUI.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;

public class YesNoDialog extends JDialog {

	private static final long serialVersionUID = 5911468253428195595L;

	private final JPanel contentPanel = new JPanel();
	
	private JButton yesButton;
	private JButton noButton;

	public YesNoDialog(String label) {
		setTitle("Question");
		setBounds(100, 100, 450, 175);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblNewLabel = new JLabel(label);
			contentPanel.add(lblNewLabel);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			{
				yesButton = new JButton("Yes");
				yesButton.setActionCommand("Yes");
				buttonPane.add(yesButton);
				getRootPane().setDefaultButton(yesButton);
			}
			{
				Component horizontalGlue = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue);
			}
			{
				noButton = new JButton("No");
				noButton.setActionCommand("No");
				buttonPane.add(noButton);
			}
		}
	}

	public void addListener(ActionListener al) {
		yesButton.addActionListener(al);
		noButton.addActionListener(al);
	}
}
