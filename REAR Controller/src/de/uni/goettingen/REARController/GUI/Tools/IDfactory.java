package de.uni.goettingen.REARController.GUI.Tools;

public class IDfactory {
	static Factory factory = null;
	
	public IDfactory() {
		if(factory == null)
			factory = new Factory();
	}
	
	public long getID() {
		synchronized(factory) {
			return factory.getID();	
		}
	}
	
	public void setUsedID(long i) {
		synchronized(factory) {
			factory.setUsedID(i);
		}
	}
	
	private static class Factory {
		static long id;
		
		public Factory() {
			id = 0L;
		}
		
		public long getID() {
			synchronized(this) {
				long i = id;
				id++;
				return i;
			}
		}
		
		public void setUsedID(long i) {
			synchronized(this) {
				if(i >= id)
					id = i+1;
			}
		}
	}
}
