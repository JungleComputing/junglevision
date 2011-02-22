package junglevision.gathering.impl;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Element;
import junglevision.gathering.Metric.MetricModifier;
import junglevision.gathering.MetricDescription.MetricOutput;
import junglevision.gathering.MetricDescription.MetricType;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import junglevision.gathering.exceptions.OutputUnavailableException;

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
	
	public Element getSource() {		
		return origin;
	}

	public Element getDestination() {
		return destination;
	}
	
	public int getNumberOfDescendants() {
		int result = 1;
		
		for (junglevision.gathering.Link child : children) {
			result += child.getNumberOfDescendants();
		}
		
		return result;
	} 
	
	public void addChild(junglevision.gathering.Link newChild) {
		if (!children.contains(newChild)) {
			children.add(newChild);
		}
	}
	
	public String debugPrint() {
		String result = "";
		
		result += "link: "+((junglevision.gathering.Location)origin).getName()+"->"+((junglevision.gathering.Location)destination).getName()+ "\n";
		
		for (junglevision.gathering.Link child : children) {
			result += "  " + child.debugPrint();
		}		
		
		return result;
	}
	
	public void update() { 
		//First update all of our children
		for (junglevision.gathering.Link child : children) {
			try {
				child.update();
			} catch (TimeoutException neverthrown) {
				logger.error("never happened.");
			}
		}
		
		for (Entry<junglevision.gathering.MetricDescription, junglevision.gathering.Metric> data : metrics.entrySet()) {
			junglevision.gathering.MetricDescription desc = data.getKey();
			
			if (desc.getType() != MetricType.LINK) {
				break;
			}
			
			junglevision.gathering.Metric metric = data.getValue();			
			ArrayList<MetricOutput> types = desc.getOutputTypes();
			
			for (MetricOutput outputtype : types) {
				try {
					if (outputtype == MetricOutput.PERCENT || outputtype == MetricOutput.R || outputtype == MetricOutput.RPOS) {
						float total = 0f, max = -10000000f, min = 10000000f;
						int childLinks = 0;
						
						//First, we gather our own metrics
						junglevision.gathering.Metric srcMetric = origin.getMetric(desc);
						junglevision.gathering.Metric dstMetric = destination.getMetric(desc);
						
						float srcValue = (Float)srcMetric.getLinkValue(MetricModifier.NORM, outputtype).get(destination);
						float dstValue = (Float)dstMetric.getLinkValue(MetricModifier.NORM, outputtype).get(origin);
						
						//TODO find a new function for this
						total += srcValue+dstValue;
						
						if (srcValue > max) max = srcValue;
						if (srcValue < min) min = srcValue;
						
						if (dstValue > max) max = dstValue;
						if (dstValue < min) min = dstValue;
												
						if (outputtype == MetricOutput.PERCENT) {
							//Gather the metrics of our children, and multiply by their weight
							for (junglevision.gathering.Link child : children) {							
								float childValue = (Float)child.getMetric(desc).getValue(MetricModifier.NORM, outputtype);							
								
								childLinks += child.getNumberOfDescendants();
								
								total += childValue * child.getNumberOfDescendants();
								
								if (childValue > max) max = childValue;								
								if (childValue < min) min = childValue;
							}
							metric.setValue(MetricModifier.NORM, outputtype, total/childLinks);
							metric.setValue(MetricModifier.MAX, outputtype, max);
							metric.setValue(MetricModifier.MIN, outputtype, min);
						} else {							
							//Then we add the metric values of our child locations					
							for (junglevision.gathering.Link child : children) {
								float childValue = (Float)child.getMetric(desc).getValue(MetricModifier.NORM, outputtype);
								
								total += childValue;
								
								if (childValue > max) max = childValue;								
								if (childValue < min) min = childValue;
							}
							metric.setValue(MetricModifier.NORM, outputtype, total);
							metric.setValue(MetricModifier.MAX, outputtype, max);
							metric.setValue(MetricModifier.MIN, outputtype, min);
						}						
					} else { //We are MetricOutput.N
						long total  = 0, max = 0, min = 1000000;
						
						//First, we gather our own metrics
						junglevision.gathering.Metric srcMetric = origin.getMetric(desc);
						junglevision.gathering.Metric dstMetric = destination.getMetric(desc);
						
						long srcValue = (Long)srcMetric.getLinkValue(MetricModifier.NORM, outputtype).get(destination);
						long dstValue = (Long)dstMetric.getLinkValue(MetricModifier.NORM, outputtype).get(origin);
						
						//TODO find a new function for this
						total += srcValue+dstValue;
						
						if (srcValue > max) max = srcValue;
						if (srcValue < min) min = srcValue;
						
						if (dstValue > max) max = dstValue;
						if (dstValue < min) min = dstValue;
						
						//Then we add the metric values of our child locations					
						for (junglevision.gathering.Link child : children) {
							int childValue = (Integer) child.getMetric(desc).getValue(MetricModifier.NORM, outputtype);
							
							total += childValue;
							
							if (childValue > max) max = childValue;								
							if (childValue < min) min = childValue;
						}
						metric.setValue(MetricModifier.NORM, outputtype, total);
						metric.setValue(MetricModifier.MAX, outputtype, max);
						metric.setValue(MetricModifier.MIN, outputtype, min);
					}				
				} catch (OutputUnavailableException impossible) {
					//Impossible since we tested if it was available first.
					logger.error("The impossible OutputUnavailableException just happened anyway.");
				} catch (BeyondAllowedRangeException e) {
					//Impossible unless one of the children has a value that is already bad
					logger.error("The impossible BeyondAllowedRangeException just happened anyway.");
				}
			}
		}
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