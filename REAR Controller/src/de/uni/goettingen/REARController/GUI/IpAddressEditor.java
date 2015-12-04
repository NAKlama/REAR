package de.uni.goettingen.REARController.GUI;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

import de.uni.goettingen.REARController.Net.IPreachable;

@SuppressWarnings("serial")
public class IpAddressEditor extends DefaultCellEditor {
	public IpAddressEditor(JTextField textField) {
		super(textField);
	}

	@Override
	public Object getCellEditorValue() {
		InetAddress addr;
		try {
			addr = InetAddress.getByName(((JTextField)editorComponent).getText());
		} catch (UnknownHostException e) {
			addr = null;
		}
		return new IPreachable(addr);
	}
}
