package de.uni.goettingen.REARController.Net;

import java.net.InetAddress;
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

import de.uni.goettingen.REARController.DataStruct.ClientStatus;
import de.uni.goettingen.REARController.DataStruct.Serializable.SerMachinesTable;

public class NetConnections {
	private Vector<Long>							clientIDs;
	private ConcurrentHashMap<Long, InetAddress>	ipMap;
	private ConcurrentHashMap<Long, ClientConn> 	connMap;
	private ConcurrentHashMap<Long, String> 		recTimeMap;
	private ConcurrentHashMap<Long, ClientStatus>	statusMap;
	private int										cores;

	public NetConnections() {
		cores		= Runtime.getRuntime().availableProcessors();
		clientIDs	= new Vector<Long>();
		connMap		= new ConcurrentHashMap<Long, ClientConn>();
		ipMap		= new ConcurrentHashMap<Long, InetAddress>();
		recTimeMap	= new ConcurrentHashMap<Long, String>();
		statusMap	= new ConcurrentHashMap<Long, ClientStatus>();
	}

	public void update(SerMachinesTable mList) {
		for(Vector<Object> m : mList.data) {
			long		id	= (long) m.get(7);
			InetAddress	ip	= (InetAddress) m.get(2);
			if(ip != null && (!clientIDs.contains(id) || !ip.equals(ipMap.get(id)))) {
				ClientConn	c	= new ClientConn(ip);
				if(!clientIDs.contains(id)) 
					clientIDs.add(id);
				connMap.put(id, c);
				ipMap.put(id, ip);				
			}
		}
	}

	public Boolean hasID(long id) {
		return clientIDs.contains(id);
	}

	public InetAddress getIP(long id) {
		return ipMap.get(id);
	}

	public void setIP(long id, InetAddress ip) {
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
	
	public String getRecTime(long id) {
		if(! recTimeMap.containsKey(id)) {
			getRecTime();
			return null;
		}
		return recTimeMap.get(id);
	}

	public ClientStatus getStatus() {
		ClientStatus out = new ClientStatus();
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
}
