package de.uni.goettingen.REARController.Net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jcraft.jsch.UserInfo;

import de.uni.goettingen.REARController.GUI.Dialogs.PasswordDialog;

public class SSHUserInfo implements UserInfo {
	private String	passwd;
	private String	passphrase;
	private Boolean	ret;
	
	public SSHUserInfo() {
		passwd = null;
		passphrase = null;
		ret = false;
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
		PasswordDialog pwd = new PasswordDialog(question, false);
		pwd.addListener(new BtnListener());
		pwd.setVisible(true);
		return ret;
	}

	@Override
	public boolean promptPassword(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean promptYesNo(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showMessage(String arg0) {
		// TODO Auto-generated method stub

	}

	public class BtnListener  implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if(cmd.equals("PW_OK"))
			{
				ret = true;
				passwd = ((PasswordDialog) e.getSource()).getPassword();
				((PasswordDialog) e.getSource()).setVisible(false);
			}
			else if(cmd.equals("PW_Cancel"))
			{
				passwd = null;
				ret = false;
				((PasswordDialog) e.getSource()).setVisible(false);
			}
		}
	}
}
