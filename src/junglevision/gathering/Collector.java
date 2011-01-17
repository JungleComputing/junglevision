package junglevision.gathering;

import java.util.ArrayList;

import junglevision.gathering.impl.Location;

/**
 * Serves as the main interface for the data collecting module.
 */
public interface Collector {
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
	public ArrayList<junglevision.gathering.MetricDescription> getAvailableMetrics();
	
	//Tryout for interface updates.
	/**
	 * Sets the minimum refreshrate for the updating of metrics. 
	 * @param newInterval
	 */
	public void setRefreshrate(int newInterval);
}