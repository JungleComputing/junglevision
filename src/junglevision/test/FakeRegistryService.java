package junglevision.test;

import ibis.ipl.IbisIdentifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeRegistryService implements ibis.ipl.server.RegistryServiceInterface {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.test.FakeRegistryService");

	public enum State { ALIVE, FAILING };	

	private HashMap<String, IbisIdentifier[]> pools;
	private HashMap<IbisIdentifier, State> ibises;
	private HashMap<IbisIdentifier, Integer> failingIbises;

	public FakeRegistryService() {		
		final int POOLS = 2;
		final int COUNTRIES = 2;
		final int UNIVERSITIES = 2;
		final int CLUSTERS = 2;
		final int IBISES = 10;

		pools = new HashMap<String, IbisIdentifier[]>();
		ibises = new HashMap<IbisIdentifier, State>();
		failingIbises = new HashMap<IbisIdentifier, Integer>();
		
		for (int p=0; p<POOLS; p++) {
			String poolName = "pool"+p;
			ArrayList<IbisIdentifier> poolIbises = new ArrayList<IbisIdentifier>();

			for (int c=0; c<COUNTRIES; c++) {
				String countryName = "country"+c;

				for (int u=0; u<UNIVERSITIES; u++) {
					String universityName = "university"+u;

					for (int s=0; s<CLUSTERS; s++) {
						String clusterName = "cluster"+s;

						for (int i=0; i<IBISES; i++) {
							IbisIdentifier fakeibis = new FakeIbisIdentifier(i+"_"+poolName+"@"+universityName+"@"+clusterName+"@"+countryName);
							poolIbises.add(fakeibis);
							ibises.put(fakeibis, State.ALIVE);
						}	
					}
				}
			}
			
			if (poolIbises.size()>0) {
				pools.put(poolName, poolIbises.toArray(new IbisIdentifier[0]));
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("FakeRegistry has created "+ibises.size()+" ibises.");			
			logger.debug("in "+COUNTRIES+" countries, "+UNIVERSITIES+" universities and "+CLUSTERS+" clusters" );
			logger.debug("and divided among "+pools.size()+" pools.");
		}
		
		//Start an update timer for the list mutations
		UpdateTimer timer = new UpdateTimer(this);
		new Thread(timer).start();
	}

	public IbisIdentifier[] getMembers(String poolName) throws IOException {		
		return pools.get(poolName);
	}

	public Map<String, Integer> getPoolSizes() throws IOException {
		HashMap<String, Integer> newMap = new HashMap<String, Integer>();
		for (Map.Entry<String, IbisIdentifier[]> pool : pools.entrySet()) {
			newMap.put(pool.getKey(), pool.getValue().length);
		}
		return newMap;
	}

	public IbisIdentifier[] getIbises() {
		IbisIdentifier result[] = ibises.keySet().toArray(new IbisIdentifier[0]);
		return result;
	}

	/* ------------------------------------------------------------------------------------------------ 
	 *  Functions needed by the timer 
	 * */
	public synchronized void doUpdate() {
		double CHANCE_OF_IBIS_FAILURE  = 0.001;
		double CHANCE_OF_IBIS_RECOVERY = 0.1;
		int MAX_FAILRATE = 10;
					
		//Put new ibises in failure mode.
		int toKill = (int) (Math.random()*(1/CHANCE_OF_IBIS_FAILURE));
		if (toKill < ibises.size()) {
			IbisIdentifier iArray[] = ibises.keySet().toArray(new IbisIdentifier[0]);
			IbisIdentifier failingIbis = iArray[toKill];
			ibises.put(failingIbis, State.FAILING);
			
			int failrate = 0;
			if (failingIbises.containsKey(failingIbis)) {
				failrate = failingIbises.get(failingIbis);
			}
			failingIbises.put(failingIbis, failrate);
			
			logger.debug("Put ibis "+failingIbis+" in fail mode.");
		}
		
		//Update the list of failing ibises, give them a chance to recover or make them 
		//disappear if they have failed for too long.
		HashMap<IbisIdentifier, Integer> failingIbisesSnapshot = new HashMap<IbisIdentifier, Integer>();
		failingIbisesSnapshot.putAll(failingIbises);
		for (Entry<IbisIdentifier, Integer> entry : failingIbisesSnapshot.entrySet()) {
			IbisIdentifier failingIbis = entry.getKey();
			int failrate = entry.getValue();
			logger.debug("Ibis "+failingIbis+" failrate: "+ failrate);
			if (Math.random() < CHANCE_OF_IBIS_RECOVERY) {
				failingIbises.remove(failingIbis);
				ibises.put(failingIbis, State.ALIVE);
				logger.debug("Ibis "+failingIbis+" is alive!");
			} else {
				if (failrate > MAX_FAILRATE) {
					//Remove the failing ibis from the pool first
					String poolName = failingIbis.poolName();
					IbisIdentifier[] poolIbises = pools.get(poolName);
					ArrayList<IbisIdentifier> newPoolIbises = new ArrayList<IbisIdentifier>();
					for (IbisIdentifier id : poolIbises) {
						if (!id.equals(failingIbis)) {
							newPoolIbises.add(id);
						}
					}
					pools.put(poolName, newPoolIbises.toArray(new IbisIdentifier[0]));
					
					//And then remove it from the internal lists
					ibises.remove(failingIbis);
					failingIbises.remove(failingIbis);
					
					logger.debug("Ibis "+failingIbis+" failed too long and has been removed.");
				} else {				
					failingIbises.put(failingIbis, failrate+1);
				}
			}
		}
	}

	/* ------------------------------------------------------------------------------------------------ 
	 *  The rest is unneeded by the Collector 
	 * */ 	

	public String[] getLocations(String arg0) throws IOException {
		//Not needed by the collector
		return null;
	}

	public String[] getPools() throws IOException {
		//Not needed by the collector
		return null;
	}

}
