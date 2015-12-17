package de.uni.goettingen.REARController.GUI;

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
		return new IPreachable(((JTextField)editorComponent).getText());
	}
}
