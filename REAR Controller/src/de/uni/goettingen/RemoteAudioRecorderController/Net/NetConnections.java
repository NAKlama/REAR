package de.uni.goettingen.RemoteAudioRecorderController.Net;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import de.uni.goettingen.RemoteAudioRecorderController.DataStruct.Machine;

public class NetConnections {
	private Vector<ClientConn>						clients;
	private ConcurrentHashMap<Long, ClientConn> 	map;
	
	private NetConnections(Vector<Machine> mList) {
		clients = new Vector<ClientConn>();
		map = new ConcurrentHashMap<Long, ClientConn>();
		for(Machine m : mList) {
			ClientConn c = new ClientConn(m);
			clients.addElement(c);
			map.put(m.getID(), c);
		}
	}
	
	
	
}
