package junglevision.gathering;

import java.util.ArrayList;

import junglevision.gathering.MetricDescription.MetricOutput;

/**
 * The interface for a representation of a location (Node, Site) in the data gathering universe
 * @author Maarten van Meersbergen
 */
public interface Location extends Element {
	public static enum Reducefunction {
		LOCATIONSPECIFIC_MINIMUM,
		LOCATIONSPECIFIC_AVERAGE,
		LOCATIONSPECIFIC_MAXIMUM,
		ALLDESCENDANTS_MINIMUM, 
		ALLDESCENDANTS_AVERAGE, 
		ALLDESCENDANTS_MAXIMUM
	}
		
	//Getters
	public String getName();
	
	public Float[] getColor();
	
	public ArrayList<junglevision.gathering.Ibis> getIbises();	
	
	public ArrayList<junglevision.gathering.Location> getChildren();
	
	public String debugPrint();
	
	/**
	 * This function returns a value derived from the values of either all of the Ibises located at its descendants,
	 * or derived from only its own Ibises 
	 * @param whichValue
	 * 		One of the VALUE_ constants defined in this interface.
	 * @param metric
	 * 		The description of the metric you are interested in.
	 * @param outputmethod
	 * 		The output method of the metric with which we compare
	 * @return
	 * 		A Number containing the value specific to the metric.
	 */
	public Number getReducedValue(Reducefunction function, MetricDescription metric, MetricOutput outputmethod);
	
	/**
	 * Returns a number of links that correspond to the criteria.
	 * @param metric
	 * 		The metric which is used for the evaluations.
	 * @param outputmethod
	 * 		The output method of the metric with which we compare
	 * @param minimumValue
	 * 		The minimum value for the return value of the metric.
	 * @param maximumValue
	 * 		The maximum value for the return value of the metric.
	 * @return
	 * 		The links that correspond to the minimum and maximum values given.
	 */
	public ArrayList<Link> getLinks(MetricDescription metric, MetricOutput outputmethod, float minimumValue, float maximumValue);
	
	/**
	 * Returns the link corresponding to the destination, or null if no link exists
	 * @param destination
	 * 		The destination element.
	 * @return
	 * 		The link to the destination, or null if none existed.
	 */
	public Link getLink(Element destination); 
		
	/**
	 * Returns the amount of ibises that are descendants of this location
	 * @return
	 * 		the total ibis descendants
	 */
	public int getNumberOfDescendants();
	
	
	public void addIbis(junglevision.gathering.Ibis ibis);	
	public void removeIbis(junglevision.gathering.Ibis ibis);
	
	public void addChild(junglevision.gathering.Location location);	
	public void removeChild(junglevision.gathering.Location location);
	
	public void addLink(junglevision.gathering.Element destination, junglevision.gathering.Link link);	
	public void removeLink(junglevision.gathering.Element destination);
}