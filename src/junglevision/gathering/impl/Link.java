package junglevision.gathering.impl;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Element;
import junglevision.gathering.MetricDescription.MetricType;

/**
 * An interface for a link between two elements that exist within the managed universe.
 * @author Maarten van Meersbergen 
 */
public class Link extends junglevision.gathering.impl.Element implements junglevision.gathering.Link {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Link");
	
	private ArrayList<junglevision.gathering.Link> children;
	private Element origin;
	private Element destination;
	
	public Link(Element origin, Element destination) {
		this.origin = origin;
		this.destination = destination;
		
		children = new ArrayList<junglevision.gathering.Link>();
	}
	
	public junglevision.gathering.Metric[] getMetrics() {
		ArrayList<junglevision.gathering.Metric> result = new ArrayList<junglevision.gathering.Metric>();
		for (junglevision.gathering.Metric metric : metrics.values()) {
			if (metric.getDescription().getType() == MetricType.LINK) {
				result.add(metric);
			}
		}		
		return result.toArray(new junglevision.gathering.Metric[0]);
	}
		
	public ArrayList<junglevision.gathering.Link> getChildren() {
		return children;
	}
	
	public void addChild(junglevision.gathering.Link link) {
		children.add(link);
	}
	
	public void removeChild(junglevision.gathering.Link link) {
		children.remove(link);
	}
	
	@Override public boolean equals(Object thatObject) {
	    if ( this == thatObject ) return true;
	    if ( !(thatObject instanceof Link) ) return false;

	    //cast to native object is now safe
	    Link that = (Link)thatObject;

	    //now a proper field-by-field evaluation can be made
	    return 	(this.origin.equals(that.origin) &&
	    		 this.destination.equals(that.destination)) ||
	    		(this.origin.equals(that.destination) &&
	    		 this.destination.equals(that.origin));	    		
	  }
	
	@Override public int hashCode() {
		int hashCode = origin.hashCode()+destination.hashCode();
		return hashCode;
    }
}