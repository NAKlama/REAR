package de.uni.goettingen.REARController.Net;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
	
	private String getPW(String question) {
		JPanel pwFrame = new JPanel();
		JLabel pwLabel = new JLabel();
		pwFrame.setLayout(new BoxLayout(pwFrame, BoxLayout.PAGE_AXIS));
		pwLabel.setText(question);
		JPasswordField pwField = new JPasswordField();
		pwFrame.add(pwLabel);
		pwFrame.add(pwField);
		int ret = JOptionPane.showConfirmDialog(null, pwFrame, "SSH Question", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(ret == JOptionPane.OK_OPTION) {
			passphrase = new String(pwField.getPassword());
			return passphrase;
		}
		return null;
	}

	@Override
	public boolean promptPassphrase(String question) {
		String pw = getPW(question);
		if(pw != null) {
			passphrase = pw;
			return true;
		}
		return false;
	}

	@Override
	public boolean promptPassword(String question) {
		String pw = getPW(question);
		if(pw != null) {
			passwd = pw;
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
