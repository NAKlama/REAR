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
//	private ConcurrentHashMap<Long, IPreachable>	ipMap;
	private ConcurrentHashMap<Long, NetConnSignal>	connMap;
	private ConcurrentHashMap<Long, String> 		recTimeMap;
	private ConcurrentHashMap<Long, ClientStatus>	statusMap;
	private ConcurrentHashMap<Long, Boolean>		activeMap;
	
	private PushSSHkeys sshPush;

	public NetConnections() {
		clientIDs		= new Vector<Long>();
		connMap			= new ConcurrentHashMap<Long, NetConnSignal>();
//		ipMap			= new ConcurrentHashMap<Long, IPreachable>();
		recTimeMap		= new ConcurrentHashMap<Long, String>();
		statusMap		= new ConcurrentHashMap<Long, ClientStatus>();
		activeMap		= new ConcurrentHashMap<Long, Boolean>();
		sshPush 		= null;
	}

	public void update(SerMachinesTable mList) {
//		System.out.println("Update (" + mList.data.size() + ")");
		for(Vector<Object> m : mList.data) {
			long		id	= (long) m.get(7);
			String		studID = (String) m.get(3);
			IPreachable ipr = new IPreachable((IPreachable) m.get(2));
			Boolean		active = !studID.isEmpty();
			if(ipr != null) {
				InetAddress	ip	= ipr.getAddress();
				if(ip != null && !clientIDs.contains(id)) {
					System.out.println(">>>> " + id + " >>>>   " + ip);
//					if(ipMap.containsKey(id))
//						System.out.println("    => " + ip.equals(ipMap.get(id).getAddress()));
					if(!clientIDs.contains(id)) 
						clientIDs.add(id);

					NetConnSignal c = connMap.get(id);
					if(c == null || !c.isReachable()) {
						System.out.print("   Connecting...");
						c = new NetConnSignal(ipr);
						connMap.put(id, c);
						System.out.println("connected");
					}
					System.out.print("   Checking reachability...");
					if(c != null && c.isReachable()) {
						ipr.setReachable(true);
					}
					System.out.println("done");
				}
			}
			if(clientIDs.contains(id))
				activeMap.put(id, active);
		}
	}
	
	private Vector<String> getPubKeys() {
		Vector<String> out = new Vector<String>();
		for(long id : clientIDs) {
			out.add(connMap.get(id).getPubKey());
		}
		return out;
	}

	public void init(ConcurrentHashMap<Long, String> ids) {
		System.out.println("Clientcount: " + clientIDs.size());
		for(long id : clientIDs) {
			if(ids.containsKey(id)) {
//				System.out.println(id);
				NetConnSignal c = connMap.get(id);
				c.setID(ids.get(id));
				c.init();
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
	
	public void setPlayFile(String URL) {
		for(long id : clientIDs) {
			connMap.get(id).setPlayFile(URL);
		}
	}
	
	public void setPlayOnly() {
		for(long id: clientIDs) {
			connMap.get(id).setPlayOnly();
		}
	}
	
	public void clearData() {
		clientIDs	= new Vector<Long>();
//		ipMap		= new ConcurrentHashMap<Long, IPreachable>();
		connMap		= new ConcurrentHashMap<Long, NetConnSignal>();
		recTimeMap	= new ConcurrentHashMap<Long, String>();
		statusMap	= new ConcurrentHashMap<Long, ClientStatus>();
	}

	public Boolean hasID(long id) {
		return clientIDs.contains(id);
	}

	public InetAddress getIP(long id) {
		if(connMap.get(id).isReachable())
			return connMap.get(id).getIP();
		return null;
	}
	
	public IPreachable getIPR(long id) {
		return connMap.get(id).getIPR();
	}

	public void setIP(long id, InetAddress ip) {
		if(! clientIDs.contains(id))
			clientIDs.add(id);
		IPreachable ipr = new IPreachable(ip);
		NetConnSignal c = new NetConnSignal(ipr);
		connMap.put(id, c);
	}

	public ClientStatus getStatus(long id) {
		if(! statusMap.containsKey(id)) {	
			return null;
		}
		if(connMap.get(id).isReachable()) {
			IPreachable ipr = connMap.get(id).getIPR();
			if(ipr != null)
				ipr.setReachable(true);
		}
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
			if(connMap.containsKey(id) && activeMap.containsKey(id) && activeMap.get(id)) {
				NetConnSignal	conn	= connMap.get(id);
				ClientStatus 	status	= new ClientStatus(conn.getStatus());
				if(status != null) {
					statusMap.put(id, status);
					out.or(status);
				}
				String			recTime = conn.getTime();
				if(recTime != null)
					recTimeMap.put(id, new String(recTime));
				
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
	
	public NetConnSignal getClientConn(long id) {
		if(connMap.containsKey(id))
			return connMap.get(id);
		return null;
	}
}
