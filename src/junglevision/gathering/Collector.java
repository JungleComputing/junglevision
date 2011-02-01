package junglevision.gathering;

import ibis.ipl.IbisIdentifier;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Serves as the main interface for the data collecting module.
 */
public interface Collector extends Runnable {
	//Getters	
	/**
	 * Returns teh root of the top-level locations in the data gathering universe.
	 * @return
	 * 		The locations root.
	 */
	public Location getRoot();
	
	/**
	 * Returns the Ibis pools present in the data gathering universe.
	 * @return
	 * 		The pools.
	 */
	public ArrayList<junglevision.gathering.Pool> getPools();
		
	/**
	 * Returns the Metrics that have been defined and are ready to use.
	 * @return
	 * 		The Metrics that could be gathered.
	 */
	public HashSet<junglevision.gathering.MetricDescription> getAvailableMetrics();
	
	/**
	 * Resets the data gathering module.
	 */
	public void initUniverse();
	
	
	/**
	 * Internal method for getting collector ibises by id. 
	 * @param ibisid
	 * 		The IbisIdentifier of the wanted ibis.
	 * @return
	 * 		The collector ibis.
	 */
	public junglevision.gathering.Ibis getIbis(IbisIdentifier ibisid);
	
	//Tryout for interface updates.
	/**
	 * Sets the minimum refreshrate for the updating of metrics. 
	 * @param newInterval
	 */
	public void setRefreshrate(int newInterval);
}