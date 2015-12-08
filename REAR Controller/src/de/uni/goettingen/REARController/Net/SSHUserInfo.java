package de.uni.goettingen.REARController.Net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jcraft.jsch.UserInfo;

import de.uni.goettingen.REARController.GUI.Dialogs.MessageDialog;
import de.uni.goettingen.REARController.GUI.Dialogs.PasswordDialog;
import de.uni.goettingen.REARController.GUI.Dialogs.YesNoDialog;

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
		PasswordDialog pwd = new PasswordDialog(question, true);
		pwd.addListener(new BtnListener());
		pwd.setVisible(true);
		return ret;
	}

	@Override
	public boolean promptPassword(String question) {
		PasswordDialog pwd = new PasswordDialog(question, false);
		pwd.addListener(new BtnListener());
		pwd.setVisible(true);
		return ret;
	}

	@Override
	public boolean promptYesNo(String question) {
		YesNoDialog ynd = new YesNoDialog(question);
		ynd.addListener(new BtnListener());
		ynd.setVisible(true);
		return ret;
	}

	@Override
	public void showMessage(String message) {
		MessageDialog md = new MessageDialog(message);
		md.setVisible(true);
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
			if(cmd.equals("PPH_OK"))
			{
				ret = true;
				passphrase = ((PasswordDialog) e.getSource()).getPassword();
				((PasswordDialog) e.getSource()).setVisible(false);
			}
			else if(cmd.equals("PPH_Cancel"))
			{
				passphrase = null;
				ret = false;
				((PasswordDialog) e.getSource()).setVisible(false);
			}
			else if(cmd.equals("Yes"))
			{
				ret = true;
				((YesNoDialog) e.getSource()).setVisible(false);
			}
			else if(cmd.equals("No"))
			{
				ret = false;
				((YesNoDialog) e.getSource()).setVisible(false);
			}
		}
	}
}
