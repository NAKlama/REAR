package de.uni.goettingen.REARController.Net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPreachable implements Serializable {

	private static final long serialVersionUID = 7951754742903794194L;
	private InetAddress		ip;
	private Boolean			reachable;
	private String			ipString;
	private NetConnSignal	connSig;
	
	public IPreachable(String ips) {
		ipString		= ips;
		reachable		= false;
		connSig			= null;
		try {
			ip 			= InetAddress.getByName(ips);
		} catch (UnknownHostException e) {
			ip			= null;
		}
	}
	
	public IPreachable(InetAddress ipArg) {
		ipString		= ipArg.getHostAddress();
		reachable		= false;
		ip				= ipArg;
		connSig			= null;
	}
	
	public IPreachable(IPreachable ipr) {
		ip			= ipr.ip;
		reachable	= ipr.reachable;
		ipString	= ipr.ipString;
		connSig		= ipr.connSig;
	}

	public InetAddress getAddress() {
		return ip;
	}
	
	public String getIPstr() {
		return ipString;
	}
	
	public boolean isReachable() {
		if(!reachable) {
			if(connSig != null && connSig.isReachable())
				reachable = true;
		}
		return reachable;
	}
	
	public void setReachable(Boolean r) {
		reachable = r;
	}
	
	public void setConnection(NetConnSignal cS) {
		connSig = cS;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		reachable = false;
		out.defaultWriteObject();
	}
}
