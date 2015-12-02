package de.uni.goettingen.REARController.Net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;

public class IPreachable implements Serializable {

	private static final long serialVersionUID = 510733392283538305L;
	private InetAddress ip;
	private Boolean		reachable;

	
	public IPreachable(InetAddress i) {
		reachable		= false;
		ip = i;
	}
	
	public InetAddress getAddress() {
		return ip;
	}
	
	public boolean isReachable() {
		return reachable;
	}
	
	public void setReachable(Boolean r) {
		reachable = r;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		reachable = false;
		out.defaultWriteObject();
	}
}
