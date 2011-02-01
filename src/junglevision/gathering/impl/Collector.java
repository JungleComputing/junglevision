package junglevision.gathering.impl;

import ibis.ipl.IbisIdentifier;
import ibis.ipl.server.ManagementServiceInterface;
import ibis.ipl.server.RegistryServiceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import junglevision.gathering.Element;
import junglevision.gathering.exceptions.SelfLinkeageException;
import junglevision.gathering.exceptions.SingletonObjectNotInstantiatedException;
import junglevision.gathering.metrics.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serves as the main class for the data collecting module.
 */
public class Collector implements junglevision.gathering.Collector, Runnable {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Collector");
	private static final ibis.ipl.Location universe = new ibis.ipl.impl.Location(new String[0]);
	private static final int workercount = 8;
	
	private static Collector ref = null;
	
	//Interfaces to the IPL
	private ManagementServiceInterface manInterface;
	private RegistryServiceInterface regInterface;
	
	//Map to hold the status quo
	private Map<String, Integer> poolSizes;
	
	//Refreshrate for the status updates
	int refreshrate;

	private HashMap<String, junglevision.gathering.Location> locations;
	private HashMap<String, junglevision.gathering.Pool> pools;
	private HashSet<junglevision.gathering.MetricDescription> descriptions;
	private HashMap<IbisIdentifier, junglevision.gathering.Ibis> ibises;
	
	private junglevision.gathering.Location root;
	private LinkedList<Element> jobQueue;
	
	private Collector(ManagementServiceInterface manInterface, RegistryServiceInterface regInterface) {
		this.manInterface = manInterface;
		this.regInterface = regInterface;
		
		//Initialize all of the lists and hashmaps needed
		poolSizes = new HashMap<String, Integer>();		
		locations = new HashMap<String, junglevision.gathering.Location>();
		pools = new HashMap<String, junglevision.gathering.Pool>();
		descriptions = new HashSet<junglevision.gathering.MetricDescription>();
		ibises = new HashMap<IbisIdentifier, junglevision.gathering.Ibis>();
		jobQueue = new LinkedList<junglevision.gathering.Element>();
		
		//Create a universe (location root)
		Float[] color = {0f,0f,0f};
		root = new Location("root", color);
		
		//Set the default refreshrate
		refreshrate = 500;
		
		//Set the default metrics
		descriptions.add(new CPUUsage());
		descriptions.add(new HeapMemory());
		descriptions.add(new NonHeapMemory());
		//descriptions.add(new ThreadsMetric());
		descriptions.add(new BytesReceivedPerSecond());
		
	}		
		
	private void initWorkers() {			
		//Create and start worker threads for the metric updates
		for (int i=0; i<workercount; i++) {
			Thread worker = new Worker();
			worker.start();			
		}
	}
		
	public static Collector getCollector(ManagementServiceInterface manInterface, RegistryServiceInterface regInterface) {
		if (ref == null) {
			ref = new Collector(manInterface, regInterface);
			ref.initWorkers();
			ref.initUniverse();
		}
		return ref;		
	}
	
	public static Collector getCollector() throws SingletonObjectNotInstantiatedException {
		if (ref != null) {
			return ref;
		} else {
			throw new SingletonObjectNotInstantiatedException();
		}
	}
	
	public void initUniverse() {		
		//Clear the jobqueue
		jobQueue.clear();
		
		initPools();
		initLocations();
		initLinks();
		initMetrics();		
		
		if (logger.isDebugEnabled()) {
			//logger.debug(root.debugPrint());
		}
	}
	
	private void initPools() {
		Map<String, Integer> newSizes = new HashMap<String, Integer>();
		
		try {
			newSizes = regInterface.getPoolSizes();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Could not get pool sizes from registry.");
			}
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
					//Get the lowest location, skip the lowest (ibis) location
					ibis.ipl.Location ibisLocation = ibisid.location().getParent();
					String ibisName = ibisLocation.getLevel(0);
										
					junglevision.gathering.Location current;
					if (locations.containsKey(ibisName)) {
						current = locations.get(ibisName);
					} else {
						Float[] color = {0f,0f,0f};
						current = new Location(ibisName, color);
						locations.put(ibisName, current);
					}
					
					//And add the ibis to that location
					Ibis ibis = new Ibis(manInterface, ibisid, entry.getValue(), current);
					current.addIbis(ibis);
					ibises.put(ibisid, ibis);					
										
					//for all location levels, get parent
					ibis.ipl.Location parentIPLLocation = ibisLocation.getParent();						
					while (!parentIPLLocation.equals(universe)) {
						String name = parentIPLLocation.getLevel(0);
						
						//Make a new location if we have not encountered the parent 
						junglevision.gathering.Location parent;
						if (locations.containsKey(name)) {
							parent = locations.get(name);
						} else {
							Float[] color = {0f,0f,0f};
							parent = new Location(name, color);
							locations.put(name, parent);
						}
						
						//And add the current location as a child of the parent
						parent.addChild(current);
						
						current = parent;
						
						parentIPLLocation = parentIPLLocation.getParent();
					}
					
					//Finally, add the top-level location to the root location, 
					//it will only add if it is not already there					
					root.addChild(current);
				}
			} catch (IOException e1) {	
				if (logger.isErrorEnabled()) {
					logger.error("Could not get Ibises from pool: " + poolName);
				}
			}
		}
	}
	
	private void initMetrics() {
		root.setMetrics(descriptions);		
	}
	
	private void initLinks() {
		//pre-make only the location-location links
		for (junglevision.gathering.Location source : locations.values()) {
			for (junglevision.gathering.Location destination : locations.values()) {
				try {
					source.getLink(destination);
				} catch (SelfLinkeageException ignored) {
					//ignored, because we do not want this link
				}
			}
		}
		
		root.makeLinkHierarchy();
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
	
	public HashSet<junglevision.gathering.MetricDescription> getAvailableMetrics() {
		return descriptions;
	}
	
	public junglevision.gathering.Element getWork() {
		junglevision.gathering.Element result = null;
		synchronized(jobQueue) {
			while (jobQueue.isEmpty()) {
				try {
					jobQueue.wait();
				} catch (InterruptedException e) {	
					if (logger.isErrorEnabled()) {
						logger.error("Interrupted Queue.");
					}
				}
			}
			result = jobQueue.removeFirst();
		}
		return result;
	}
	
	public junglevision.gathering.Ibis getIbis(IbisIdentifier ibisid) {
		return ibises.get(ibisid);
	}
	
	//Tryout for interface updates.
	public void setRefreshrate(int newInterval) {
		refreshrate = newInterval;
	}
	
	public void run() {
		while (true) {
			synchronized(jobQueue) {
				if (jobQueue.isEmpty()) {
					jobQueue.addAll(ibises.values());
					
					//TODO wait for all the ibises to be complete first
					jobQueue.add(root);
					jobQueue.notify();
				} else {
					//TODO implement
					if (logger.isDebugEnabled()) {
						logger.debug("Queue not empty when refreshed.");
						logger.debug("Ibises left in queue: "+jobQueue.size());
						
					}
				}
			}
			try {
				Thread.sleep(refreshrate);
			} catch (InterruptedException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Interrupted, this should be ignored.");
				}
				break;
			}
		}
	}
}