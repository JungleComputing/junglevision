package junglevision.test;

import ibis.ipl.IbisIdentifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeRegistryService implements ibis.ipl.server.RegistryServiceInterface {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.test.FakeRegistryService");
	
	private HashMap<String, IbisIdentifier[]> pools;
	private ArrayList<IbisIdentifier> ibises;
	
	public FakeRegistryService() {
		pools = new HashMap<String, IbisIdentifier[]>();
		ibises = new ArrayList<IbisIdentifier>();
				
		int poolnumber = 5; //1 +(int)(Math.random()*3);		
		for (int k=0; k<poolnumber; k++) {
			String poolName = "pool"+k;
			ArrayList<IbisIdentifier> poolIbises = new ArrayList<IbisIdentifier>();
			
			int sitenumber = 10; //1+(int)(Math.random()*9);			
			for (int j=0; j<sitenumber; j++) {
				String siteName = "site"+j;
				
				int ibisnumber = 10;//1+(int)(Math.random()*25);
				for (int i=0; i<ibisnumber; i++) {
					IbisIdentifier fakeibis = new FakeIbisIdentifier(i+"_"+poolName+"@"+siteName);
					poolIbises.add(fakeibis);
					ibises.add(fakeibis);
				}				
			}
			IbisIdentifier[] ibises = poolIbises.toArray(new IbisIdentifier[0]);
			pools.put(poolName, ibises);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("FakeRegistry has created: ");
			logger.debug(ibises.size()+" ibises.");
			logger.debug("divided among "+pools.size()+" pools.");
		}
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
		IbisIdentifier result[] = ibises.toArray(new IbisIdentifier[0]);
		return result;
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
