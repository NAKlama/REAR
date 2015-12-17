package de.uni.goettingen.REARController.Net;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.jcraft.jsch.UserInfo;

public class SSHUserInfo implements UserInfo {
	private String	passwd;
	private String	passphrase;

	public SSHUserInfo() {
		passwd = null;
		passphrase = null;
	}

	@Override
	public String getPassphrase() {
		return passphrase;
	}

	@Override
	public String getPassword() {
		return passwd;
	}

	@Override
	public boolean promptPassphrase(String question) {
		JPasswordField pf = new JPasswordField();
		int ret = JOptionPane.showConfirmDialog(null, pf, question, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(ret == JOptionPane.OK_OPTION) {
			passphrase = new String(pf.getPassword());
			return true;
		}
		return false;
	}

	@Override
	public boolean promptPassword(String question) {
		JPasswordField pf = new JPasswordField();
		int ret = JOptionPane.showConfirmDialog(null, pf, question, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(ret == JOptionPane.OK_OPTION) {
			passwd = new String(pf.getPassword());
			return true;
		}
		return false;
	}

	@Override
	public boolean promptYesNo(String question) {
		int n = JOptionPane.showInternalConfirmDialog(null, question, "SSH Question", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		if(n == JOptionPane.YES_OPTION)
			return true;
		return false;
	}

	@Override
	public void showMessage(String message) {
		JOptionPane.showInternalMessageDialog(null, message, "SSH Message", JOptionPane.INFORMATION_MESSAGE);
	}
}
