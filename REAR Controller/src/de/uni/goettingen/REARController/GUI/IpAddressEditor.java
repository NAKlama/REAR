package de.uni.goettingen.REARController.GUI;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class IpAddressEditor extends DefaultCellEditor {
	public IpAddressEditor(JTextField textField) {
		super(textField);
	}

	@Override
	public Object getCellEditorValue() {
		try {
			String ip = ((JTextField)editorComponent).getText();
			if(ip.substring(0, 1).equals("/"))
				return InetAddress.getByName(ip.substring(1));
			return InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
