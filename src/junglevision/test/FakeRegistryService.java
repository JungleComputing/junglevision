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
		final int POOLS = 1;
		final int COUNTRIES = 10;
		final int UNIVERSITIES = 10;
		final int IBISES = 10;
		
		pools = new HashMap<String, IbisIdentifier[]>();
		ibises = new ArrayList<IbisIdentifier>();
				
		int poolnumber = POOLS; //1 +(int)(Math.random()*3);		
		for (int p=0; p<poolnumber; p++) {
			String poolName = "pool"+p;
			ArrayList<IbisIdentifier> poolIbises = new ArrayList<IbisIdentifier>();
			
			int countries = COUNTRIES;			
			for (int c=0; c<countries; c++) {
				String countryName = "country"+c;
				int universities = UNIVERSITIES; //1+(int)(Math.random()*9);			
				for (int u=0; u<universities; u++) {
					String siteName = countryName+"site"+u;
					
					int ibisnumber = IBISES;//1+(int)(Math.random()*25);
					for (int i=0; i<ibisnumber; i++) {
						IbisIdentifier fakeibis = new FakeIbisIdentifier(i+"_"+poolName+"@"+siteName+"@"+countryName);
						poolIbises.add(fakeibis);
						ibises.add(fakeibis);
					}				
				}
			}
			
			pools.put(poolName, poolIbises.toArray(new IbisIdentifier[0]));
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("FakeRegistry has created "+ibises.size()+" ibises.");			
			logger.debug("in "+COUNTRIES+" countries and "+UNIVERSITIES+" universities");
			logger.debug("and divided among "+pools.size()+" pools.");
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
