package junglevision.gathering.impl;

import ibis.ipl.IbisIdentifier;
import ibis.ipl.server.ManagementServiceInterface;
import ibis.ipl.server.RegistryServiceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serves as the main class for the data collecting module.
 */
public class Collector implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Collector");
	private static final ibis.ipl.Location universe = new ibis.ipl.impl.Location(new String[0]);
	private static final int workercount = 8;
	
	//Interfaces to the IPL
	private ManagementServiceInterface manInterface;
	private RegistryServiceInterface regInterface;
	
	//Map to hold the status quo
	private Map<String, Integer> poolSizes;
	
	//Refreshrate for the status updates
	int refreshrate;

	private HashMap<String, junglevision.gathering.Location> locations;
	private HashMap<String, junglevision.gathering.Pool> pools;
	private ArrayList<junglevision.gathering.MetricDescription> descriptions;
	private ArrayList<junglevision.gathering.Ibis> ibises;
	
	private junglevision.gathering.Location root;
	private Queue<junglevision.gathering.Ibis> jobQueue;
	
	public Collector(ManagementServiceInterface manInterface, RegistryServiceInterface regInterface) {
		this.manInterface = manInterface;
		this.regInterface = regInterface;
		
		poolSizes = new HashMap<String, Integer>();
		
		refreshrate = 500;
		
		locations = new HashMap<String, junglevision.gathering.Location>();
		pools = new HashMap<String, junglevision.gathering.Pool>();
		descriptions = new ArrayList<junglevision.gathering.MetricDescription>();
		ibises = new ArrayList<junglevision.gathering.Ibis>();
		
		Float[] color = {0f,0f,0f};
		root = new Location("root", color);
		
		jobQueue = new LinkedList<junglevision.gathering.Ibis>();
		
		for (int i=0; i<workercount; i++) {
			Thread worker = new Worker(this);
			worker.start();			
		}
		
		init();
	}
	
	public void init() {
		jobQueue.clear();
		
		initPools();
		initLocations();		
		
		jobQueue.addAll(ibises);
	}
	
	private void initPools() {
		Map<String, Integer> newSizes = new HashMap<String, Integer>();
		
		try {
			newSizes = regInterface.getPoolSizes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//clear the pools list, if warranted
		for (Map.Entry<String, Integer> entry : newSizes.entrySet()) {
			String poolName = entry.getKey();
	        int newSize = entry.getValue();

	        if (!poolSizes.containsKey(poolName) || newSize != poolSizes.get(poolName)) {
	         	pools.clear();
	        }
		}
		
		//reinitialize the pools list
		for (Map.Entry<String, Integer> entry : newSizes.entrySet()) {
			String poolName = entry.getKey();
	        int newSize = entry.getValue();

	        if (!poolSizes.containsKey(poolName) || newSize != poolSizes.get(poolName)) {
	        	if (newSize > 0) {
		          	pools.put(poolName, new Pool(poolName));
		        }
	        }
		}
		
		poolSizes = newSizes;
	}	
	
	private void initLocations() {
		//For all pools
		for (Entry<String, junglevision.gathering.Pool> entry : pools.entrySet()) {
			String poolName = entry.getKey();
			
			//Get the members of this pool
			IbisIdentifier[] poolIbises;
			try {
				poolIbises = regInterface.getMembers(poolName);
				
				//for all ibises
				for (IbisIdentifier ibisid : poolIbises) {
					ibis.ipl.Location currentIPLLocation = ibisid.location();
					junglevision.gathering.Location currentJVLocation;
					
					while ( !currentIPLLocation.equals(universe) ) {
						String currentName = currentIPLLocation.toString();
						if (locations.containsKey(currentName)) {
							currentJVLocation = locations.get(currentName);
						} else {
							Float[] color = {0f,0f,0f};
							currentJVLocation = new Location(currentName, color);
							locations.put(currentName, currentJVLocation);
						}
					}					
					
					//Get the lowest location
					String lowestlevel = ibisid.location().getLevel(0);
					
					junglevision.gathering.Location current;
					if (locations.containsKey(lowestlevel)) {
						current = locations.get(lowestlevel);
					} else {
						Float[] color = {0f,0f,0f};
						current = new Location(lowestlevel, color);
						locations.put(lowestlevel, current);
					}
					
					//And add the ibis to that location
					Ibis ibis = new Ibis(manInterface, ibisid, entry.getValue(), current);
					current.addIbis(ibis);
					ibises.add(ibis);
					
					//for all location levels, get parent
					ibis.ipl.Location parentIPLLocation = ibisid.location().getParent();										
					while (parentIPLLocation != universe) {
						String parentName = ibisid.location().getLevel(0);
						junglevision.gathering.Location parent;
					
						//Make a new location if we have not encountered the parent 
						if (locations.containsKey(parentName)) {
							parent = locations.get(parentName);
						} else {
							Float[] color = {0f,0f,0f};
							parent = new Location(parentName, color);
							locations.put(parentName, parent);
						}
						
						//And add the current location as a child of the parent
						parent.addChild(current);
						current = parent;
						
						parentIPLLocation = ibisid.location().getParent();
					}
					
					//Finally, add the top-level location to the root location
					root.addChild(current);
				}
			} catch (IOException e1) {			
				logger.debug("Could not get Ibises from pool: " + poolName);
			}
		}
	}
		
	//Getters	
	public junglevision.gathering.Location getRoot() {
		return root;
	}
	
	public ArrayList<junglevision.gathering.Pool> getPools() {
		ArrayList<junglevision.gathering.Pool> result = new ArrayList<junglevision.gathering.Pool>();
		for (junglevision.gathering.Pool pool : pools.values()) {
			result.add(pool);
		}
		return result;
	}
	
	public ArrayList<junglevision.gathering.MetricDescription> getAvailableMetrics() {
		return descriptions;
	}
	
	public junglevision.gathering.Ibis getWork() {
		junglevision.gathering.Ibis result = null;
		synchronized(jobQueue) {
			while (jobQueue.isEmpty()) {
				try {
					jobQueue.wait();
				} catch (InterruptedException e) {					
					logger.debug("Interrupted Queue.");
				}
			}
			result = jobQueue.poll();
		}
		return result;
	}
	
	//Tryout for interface updates.
	public void setRefreshrate(int newInterval) {
		refreshrate = newInterval;
	}
	
	public void run() {
		while (true) {
			synchronized(jobQueue) {
				if (jobQueue.isEmpty()) {
					jobQueue.addAll(ibises);
					jobQueue.notify();
				} else {
					//TODO implement
					logger.debug("one hangs");
				}
			}
			try {
				Thread.sleep(refreshrate);
			} catch (InterruptedException e) {
				logger.debug("Interrupted");
				break;
			}
		}
	}
}