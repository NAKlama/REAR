package de.uni.goettingen.REARController.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.uni.goettingen.REARController.DataStruct.Area;

import java.awt.image.BufferedImage;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class NewAreaDialog extends JDialog {
	private static final int	COLOR_BUTTON_SIZE = 16;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField textName;
	
	private JButton okButton;
	private JButton cancelButton;
	private JButton btnColor;
	
	private String	name;
	private Color	color;
	private Area	parent;
	
	private BtnListener listener;
	
	private Graphics g;

	public NewAreaDialog() {
		init(new Area("None"), "New Area", Color.BLACK);
	}
	
	public NewAreaDialog(Area p) {
		init(p, "New Area", Color.BLACK);
	}
	
	public NewAreaDialog(Area p, String n) {
		init(p, n, Color.BLACK);
	}
	
	public NewAreaDialog(Area p, Color c) {
		init(p, "New Area", c);
	}
	
	public NewAreaDialog(Area p, String n, Color c) {
		init(p, n, c);
	}
	
	public void init(Area parentArg, String nameArg, Color colorArg) {
		name		= nameArg;
		color		= colorArg;
		parent  	= parentArg;
		listener	= new BtnListener();
		setTitle("New Area");
		setType(Type.UTILITY);
		setBounds(100, 100, 300, 160);
		setPreferredSize(new Dimension(300, 160));
		setMinimumSize(new Dimension(300, 160));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);			
		contentPanel.setLayout(new MigLayout("", "[][][grow]", "[][][][grow][]"));
		{
			JLabel lblParent = new JLabel("Parent:");
			contentPanel.add(lblParent, "cell 0 0");
		}
		{
			Component horizontalStrut = Box.createHorizontalStrut(5);
			contentPanel.add(horizontalStrut, "cell 1 0");
		}
		{
			String name = "ROOT NODE";
			if(parent != null)
				name = parent.getName();
			JLabel lblParentName = new JLabel(name);
			contentPanel.add(lblParentName, "cell 2 0");
		}
		{
			Component verticalGlue = Box.createVerticalGlue();
			contentPanel.add(verticalGlue, "cell 0 3");
		}
		{
			JLabel lblColor = new JLabel("Color:");
			contentPanel.add(lblColor, "cell 0 2");
		}
		{
			JLabel lblName = new JLabel("Name:");
			contentPanel.add(lblName, "cell 0 1");
		}
		{
			textName = new JTextField();
			textName.setText(name);
			contentPanel.add(textName, "cell 2 1,grow");
			textName.setColumns(10);
		}
		{
			BufferedImage colorField = new BufferedImage(COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE,BufferedImage.TYPE_INT_RGB);
			g = colorField.getGraphics();
			g.setColor(color);
			g.fillRect(0, 0, COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE);
			btnColor = new JButton(new ImageIcon(colorField));
			btnColor.setActionCommand("SetColor");
			btnColor.addActionListener(listener);
			contentPanel.add(btnColor, "cell 2 2");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public void addListener(ActionListener l) {
		okButton.addActionListener(l);
		cancelButton.addActionListener(l);
	}
	
	public String getName() {
		return textName.getText();
	}
	
	public Color getColor() {
		return color;
	}
	
	private class BtnListener implements ActionListener {
		private ColorChooser dialog;
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if(cmd.equals("SetColor")) {
				dialog = new ColorChooser(color);
				dialog.addListener(this);
				dialog.setVisible(true);
			}
			else if(cmd.equals("OK"))
			{
				color = dialog.getColor();
				g.setColor(color);
				g.fillRect(0, 0, COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE);
				dialog.setVisible(false);
			}
			else if(cmd.equals("Cancel"))
				dialog.setVisible(false);
		}
	}
}
