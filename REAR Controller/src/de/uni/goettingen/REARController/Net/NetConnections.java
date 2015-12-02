package de.uni.goettingen.REARController.Net;

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

import de.uni.goettingen.REARController.DataStruct.Machine;

public class NetConnections {
	private Vector<ClientConn>						clients;
	private ConcurrentHashMap<Long, ClientConn> 	map;
	private int										cores;
	
	public NetConnections(Vector<Machine> mList) {
		cores	= Runtime.getRuntime().availableProcessors();
		clients = new Vector<ClientConn>();
		map = new ConcurrentHashMap<Long, ClientConn>();
		for(Machine m : mList) {
			ClientConn c = new ClientConn(m);
			clients.addElement(c);
			map.put(m.getID(), c);
		}
	}

	public ClientStatus getStatus() {
		ClientStatus out = new ClientStatus();
		int numThreads = clients.size() > cores ? cores : clients.size();
		Collection<Callable<ClientStatus>> tasks = new ArrayList<>();
		for(ClientConn c : clients)
			tasks.add(new GetClientStatusThread(c));
		ExecutorService exec = Executors.newFixedThreadPool(numThreads);
		List<Future<ClientStatus>> results;
		try {
			results = exec.invokeAll(tasks);
			for(Future<ClientStatus> r : results) {
				ClientStatus s = r.get();
				out.or(s);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
	
	private class GetClientStatusThread implements Callable<ClientStatus> {
		private ClientConn conn;
		
		public GetClientStatusThread(ClientConn c) {
			conn = c;
		}

		@Override
		public ClientStatus call() throws Exception {
			ClientStatus status = conn.status();
			conn.getMachine().setStatus(status);
			return status;
		}
	}
}
