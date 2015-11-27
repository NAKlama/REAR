package de.uni.goettingen.RemoteAudioRecorderController.GUI;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.uni.goettingen.RemoteAudioRecorderController.MainWindow;

import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class InfoDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	
	private static final String GNU_STATEMENT 	= "<html>"
												+ "<p>This program is free software: you can redistribute it and/or modify<br>"
												+ "it under the terms of the GNU General Public License as published by<br>"
												+ "the Free Software Foundation, either version 3 of the License, or<br>"
												+ "(at your option) any later version.<br>&nbsp;</p>"
												+ "<p>This program is distributed in the hope that it will be useful,<br>"
												+ "but WITHOUT ANY WARRANTY; without even the implied warranty of<br>"
												+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br>"
												+ "GNU General Public License for more details.<br>&nbsp;</p>"
												+ "<p>You should have received a copy of the GNU General Public License<br>"
												+ "along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.</p>"
												+ "</html>"; 

	
	public InfoDialog() {
		setTitle(MainWindow.PROGRAM_NAME+" "+ MainWindow.getVersion());
		setType(Type.UTILITY);
		setMinimumSize(new Dimension(450, 420));
		setBounds(100, 100, 450, 356);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JLabel lblSoftwareTitle = new JLabel(MainWindow.PROGRAM_NAME+" " + MainWindow.getVersion());
			lblSoftwareTitle.setFont(new Font("Tahoma", Font.PLAIN, 20));
			lblSoftwareTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblSoftwareTitle);
		}
		{
			Component verticalStrut = Box.createVerticalStrut(5);
			contentPanel.add(verticalStrut);
		}
		{
			JLabel lblCopyright = new JLabel("Copyright \u00A9 2015 Frederik Klama");
			lblCopyright.setFont(new Font("Tahoma", Font.PLAIN, 12));
			lblCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblCopyright);
		}
		{
			Component verticalGlue = Box.createVerticalGlue();
			contentPanel.add(verticalGlue);
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(null);
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			flowLayout.setHgap(0);
			flowLayout.setVgap(0);
			contentPanel.add(panel);
			{
				JLabel lblGPL = new JLabel(GNU_STATEMENT);
				panel.add(lblGPL);
				lblGPL.setHorizontalAlignment(SwingConstants.LEFT);
				lblGPL.setAlignmentX(Component.CENTER_ALIGNMENT);
			}
		}
		{
			Component verticalGlue = Box.createVerticalGlue();
			contentPanel.add(verticalGlue);
		}
		{
			JLabel lblAttrib1 = new JLabel("Using meliae Gnome Icon Set produced by Sora");
			lblAttrib1.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblAttrib1);
		}
		{
			JLabel lblAttrib2 = new JLabel("Checkered flag by Ren\u00E9 Andritsch from the Noun Project");
			lblAttrib2.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblAttrib2);
		}
		{
			JLabel lblAttrib3 = new JLabel("Arrow facing right - Green from Wikimedia Commons by Fouwen");
			lblAttrib3.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblAttrib3);
		}
		{
			JLabel lblAttrib4 = new JLabel("Symbol OK from Wikimedia Commons by Steschke");
			lblAttrib4.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblAttrib4);
		}
		{
			JLabel lblAttrib5 = new JLabel("Folder Upload and Microphone Recording Icon from Pixabay by ClkerFreeVectorImages");
			lblAttrib5.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblAttrib5);
		}
		{
			Component verticalGlue = Box.createVerticalGlue();
			contentPanel.add(verticalGlue);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane);
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
			{
				Component horizontalGlue = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue);
			}
			{
				JButton btnLicense = new JButton("License");
				btnLicense.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						LicenseDialog license = new LicenseDialog();
						license.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						license.setVisible(true);
					}
				});
				buttonPane.add(btnLicense);
			}
			{
				Component horizontalGlue = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue);
			}
			{
				JButton okButton = new JButton("Close");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						setVisible(false);
					}
				});
				okButton.setActionCommand("Close");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				Component horizontalGlue = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue);
			}
		}
		{
			Component verticalStrut = Box.createVerticalStrut(2);
			getContentPane().add(verticalStrut);
		}
		setVisible(true);
	}
}
