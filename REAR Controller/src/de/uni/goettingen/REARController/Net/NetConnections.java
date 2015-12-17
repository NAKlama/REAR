package de.uni.goettingen.REARController.Net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import de.uni.goettingen.REARController.MainWindow;
import de.uni.goettingen.REARController.DataStruct.ClientStatus;
import de.uni.goettingen.REARController.DataStruct.Serializable.SerMachinesTable;

public class NetConnections {
	private Vector<Long>							clientIDs;
	private ConcurrentHashMap<Long, IPreachable>	ipMap;
	private ConcurrentHashMap<Long, ClientConn> 	connMap;
	private ConcurrentHashMap<Long, String> 		recTimeMap;
	private ConcurrentHashMap<Long, ClientStatus>	statusMap;
	private ConcurrentHashMap<Long, String>			clientSSHkeys;
	
	private PushSSHkeys sshPush;

	public NetConnections() {
		clientIDs		= new Vector<Long>();
		connMap			= new ConcurrentHashMap<Long, ClientConn>();
		ipMap			= new ConcurrentHashMap<Long, IPreachable>();
		recTimeMap		= new ConcurrentHashMap<Long, String>();
		statusMap		= new ConcurrentHashMap<Long, ClientStatus>();
		clientSSHkeys	= new ConcurrentHashMap<Long, String>();
		sshPush 		= null;
	}

	public void update(SerMachinesTable mList) {
//		System.out.println("Update (" + mList.data.size() + ")");
		for(Vector<Object> m : mList.data) {
			long		id	= (long) m.get(7);
			IPreachable ipr = (IPreachable) m.get(2);
			if(ipr != null) {
				InetAddress	ip	= ipr.getAddress();
				if(ip != null && (!clientIDs.contains(id) || !ip.equals(ipMap.get(id).getAddress()))) {
					if(!clientIDs.contains(id)) 
						clientIDs.add(id);
					ClientConn	c = connMap.get(id);
					if(c == null || !connMap.get(id).isReachable()) {
						c = new ClientConn(ip);
						connMap.put(id, c);
					}
					if(c.isReachable())
						ipr.setReachable(true);
					ipMap.put(id, ipr);
					clientSSHkeys.put(id, c.getSSHkey());
				}
			}
		}
	}
	
	private Vector<String> getPubKeys() {
		Vector<String> out = new Vector<String>();
		for(long id : clientIDs) {
			out.add(clientSSHkeys.get(id));
		}
		return out;
	}

	public void init(ConcurrentHashMap<Long, String> ids) {
//		System.out.println("Clientcount: " + clientIDs.size());
		for(long id : clientIDs) {
			if(ids.containsKey(id)) {
//				System.out.println(id);
				ClientConn c = connMap.get(id);
				boolean idSet = false;
				for(int i = 0; i < 5 && !idSet; i++) {
					idSet = c.setID(ids.get(id));
				}
				if(idSet) {
					c.init();
				}
			}
		}
		String SSHKeys = "";
		for(String key : getPubKeys()) {
			SSHKeys += key + "\n";
		}
		File f;
		try {
			f = File.createTempFile("REAR_", "");
			OutputStreamWriter osw;
			osw = new OutputStreamWriter(new FileOutputStream(f));
			osw.write(SSHKeys, 0, SSHKeys.length());
			osw.close();
			if(sshPush == null)
				sshPush = new PushSSHkeys();
			sshPush.push(
					MainWindow.getProp().getUploadUser(), 
					MainWindow.getProp().getUploadServer(), 
					f, 
					".ssh/authorized_keys");
			f.deleteOnExit();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void rec() {
		for(long id : clientIDs) {
			connMap.get(id).rec();
		}
	}


	public void stop() {
		for(long id : clientIDs) {
			connMap.get(id).stop();
		}
	}


	public void reset() {
		for(long id : clientIDs) {
			connMap.get(id).reset();
		}
	}
	
	public void clearData() {
		clientIDs	= new Vector<Long>();
		ipMap		= new ConcurrentHashMap<Long, IPreachable>();
		connMap		= new ConcurrentHashMap<Long, ClientConn>();
		recTimeMap	= new ConcurrentHashMap<Long, String>();
		statusMap	= new ConcurrentHashMap<Long, ClientStatus>();
	}

	public Boolean hasID(long id) {
		return clientIDs.contains(id);
	}

	public InetAddress getIP(long id) {
		return ipMap.get(id).getAddress();
	}
	
	public IPreachable getIPR(long id) {
		return ipMap.get(id);
	}

	public void setIP(long id, InetAddress ip) {
		if(! clientIDs.contains(id))
			clientIDs.add(id);
		ClientConn c = new ClientConn(ip);
		connMap.put(id, c);
		IPreachable ipr = new IPreachable(ip);
		if(c.isReachable())
			ipr.setReachable(true);
		ipMap.put(id, ipr);
	}

	public ClientStatus getStatus(long id) {
		if(! statusMap.containsKey(id)) {	
			return null;
		}
		if(connMap.get(id).isReachable())
			ipMap.get(id).setReachable(true);
		return statusMap.get(id);
	}
	
	public Boolean isReachabel(long id) {
		if( connMap.containsKey(id) )
			return connMap.get(id).isReachable();
		return false;
	}

	public String getRecTime(long id) {
		if(! recTimeMap.containsKey(id)) {
			return "waiting";
		}
		return recTimeMap.get(id);
	}
	
	public ClientStatus getStatus() {
		ClientStatus out = new ClientStatus();
		for(long id : clientIDs) {
			if(connMap.containsKey(id)) {
				ClientConn		conn	= connMap.get(id);
				ClientStatus 	status	= conn.status();
				statusMap.put(id, status);
				out.or(status);
				String			recTime = conn.getTime();
				recTimeMap.put(id, recTime);
				
			}
		}
		return out;
	}

	public void setExamID(String eID) {
//		System.out.println(clientIDs);
		String str = eID.replaceAll("\\s", "_");
		for(long id : clientIDs) {
			connMap.get(id).setExamID(str);
		}
		
	}

	public void setServer(String uploadServer, String uploadUser) {
		for(long id : clientIDs) {
			connMap.get(id).setServer(uploadServer, uploadUser);
		}
	}
}
