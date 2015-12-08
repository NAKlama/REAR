package de.uni.goettingen.REARController.Net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import de.uni.goettingen.REARController.MainWindow;
import de.uni.goettingen.REARController.DataStruct.ClientStatus;
import de.uni.goettingen.REARController.DataStruct.Serializable.SerMachinesTable;

public class NetConnections {
	private Vector<Long>							clientIDs;
	private ConcurrentHashMap<Long, InetAddress>	ipMap;
	private ConcurrentHashMap<Long, ClientConn> 	connMap;
	private ConcurrentHashMap<Long, String> 		recTimeMap;
	private ConcurrentHashMap<Long, ClientStatus>	statusMap;
	private ConcurrentHashMap<Long, String>			clientSSHkeys;
	private int										cores;

	public NetConnections() {
		cores			= Runtime.getRuntime().availableProcessors();
		clientIDs		= new Vector<Long>();
		connMap			= new ConcurrentHashMap<Long, ClientConn>();
		ipMap			= new ConcurrentHashMap<Long, InetAddress>();
		recTimeMap		= new ConcurrentHashMap<Long, String>();
		statusMap		= new ConcurrentHashMap<Long, ClientStatus>();
		clientSSHkeys	= new ConcurrentHashMap<Long, String>();
	}

	public void update(SerMachinesTable mList) {
		System.out.println("Update (" + mList.data.size() + ")");
		for(Vector<Object> m : mList.data) {
			long		id	= (long) m.get(7);
			IPreachable ipr = (IPreachable) m.get(2);
			if(ipr != null) {
				InetAddress	ip	= ipr.getAddress();
				if(ip != null && (!clientIDs.contains(id) || !ip.equals(ipMap.get(id)))) {
					ClientConn	c	= new ClientConn(ip);
					if(!clientIDs.contains(id)) 
						clientIDs.add(id);
					connMap.put(id, c);
					ipMap.put(id, ip);
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
		System.out.println("Clientcount: " + clientIDs.size());
		for(long id : clientIDs) {
			if(ids.containsKey(id)) {
				System.out.println(id);
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
		SCP scp = new SCP(MainWindow.UPLOAD_SERVER_USER, MainWindow.UPLOAD_SERVER);
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
		ipMap		= new ConcurrentHashMap<Long, InetAddress>();
		connMap		= new ConcurrentHashMap<Long, ClientConn>();
		recTimeMap	= new ConcurrentHashMap<Long, String>();
		statusMap	= new ConcurrentHashMap<Long, ClientStatus>();
	}

	public Boolean hasID(long id) {
		return clientIDs.contains(id);
	}

	public InetAddress getIP(long id) {
		return ipMap.get(id);
	}

	public void setIP(long id, InetAddress ip) {
		if(! clientIDs.contains(id))
			clientIDs.add(id);
		ClientConn c = new ClientConn(ip);
		connMap.put(id, c);
		ipMap.put(id, ip);
	}

	public ClientStatus getStatus(long id) {
		if(! statusMap.containsKey(id)) {
			getStatus();
			return null;
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
			getRecTime();
			return null;
		}
		return recTimeMap.get(id);
	}

	public ClientStatus getStatus() {
		ClientStatus out = new ClientStatus();
		out.setNone(false);
		int numThreads = clientIDs.size() > cores ? cores : clientIDs.size();
		if(numThreads > 0) {
			Collection<Callable<IdTouple>> tasks = new ArrayList<>();
			for(long id : clientIDs)
				tasks.add(new GetClientStatusThread(connMap.get(id), id));
			ExecutorService exec = Executors.newFixedThreadPool(numThreads);
			List<Future<IdTouple>> results;
			try {
				results = exec.invokeAll(tasks);
				for(Future<IdTouple> r : results) {
					ClientStatus s = (ClientStatus) r.get().o;
					statusMap.put(r.get().id, s);
					out.or(s);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return out;

	}

	public void getRecTime() {
		int numThreads = clientIDs.size() > cores ? cores : clientIDs.size();
		if(numThreads > 0) {
			Collection<Callable<IdTouple>> tasks = new ArrayList<>();
			for(long id : clientIDs)
				tasks.add(new GetRecTimeThread(connMap.get(id), id));
			ExecutorService exec = Executors.newFixedThreadPool(numThreads);
			List<Future<IdTouple>> results;
			try {
				results = exec.invokeAll(tasks);
				for(Future<IdTouple> r : results) {
					recTimeMap.put(r.get().id, (String) r.get().o);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class GetRecTimeThread implements Callable<IdTouple> {
		private ClientConn	conn;
		long				id;

		public GetRecTimeThread(ClientConn c, long i) {
			conn	= c;
			id		= i;
		}

		@Override
		public IdTouple call() throws Exception {
			IdTouple out = new IdTouple();
			out.id = id;
			if(connMap.containsKey(id)) {
				String time = conn.getTime();
				recTimeMap.put(id, time);
				out.o = time;
				return out;
			}
			return null;
		}
	}

	private class GetClientStatusThread implements Callable<IdTouple> {
		private ClientConn	conn;
		long				id;

		public GetClientStatusThread(ClientConn c, long i) {
			conn	= c;
			id		= i;
		}

		@Override
		public IdTouple call() throws Exception {
			IdTouple out = new IdTouple();
			out.id = id;
			if(connMap.containsKey(id)) {
				ClientStatus status = conn.status();
				statusMap.put(id, status);
				out.o = status;
				return out;
			}
			return null;
		}
	}

	private class IdTouple {
		public long id;
		public Object o;
	}

	public void setExamID(String eID) {
		System.out.println(clientIDs);
		for(long id : clientIDs) {
			connMap.get(id).setExamID(eID);
		}
		
	}
}
