package junglevision.gathering.impl;

import java.util.HashMap;

import junglevision.gathering.impl.Link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General methods for any element within the data gathering universe
 * @author Maarten van Meersbergen
 */
public abstract class Element implements junglevision.gathering.Element {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Element");
	
	junglevision.gathering.MetricDescription[] descriptions;
	HashMap<String, junglevision.gathering.Metric> metrics;
	HashMap<junglevision.gathering.Element, junglevision.gathering.Link> links;
	
	public Element() {
		metrics = new HashMap<String, junglevision.gathering.Metric>();
		links	= new HashMap<junglevision.gathering.Element, junglevision.gathering.Link>();
	}
		
	//getters		
	public junglevision.gathering.Metric getMetric(junglevision.gathering.MetricDescription desc) {
		return metrics.get(desc.getName());
	}
	
	public junglevision.gathering.Metric getMetric(String metricName) {
		return metrics.get(metricName);
	}
	
	public junglevision.gathering.Link getLink(junglevision.gathering.Element destination) {
		junglevision.gathering.Link result;
		if (links.containsKey(destination)) {
			result = links.get(destination);
		} else {
			result = new Link(this, destination);
			result.setMetrics(descriptions);
			links.put(destination, result);
			destination.addLink(this, result);			
		}
		return result;
	}
	
	public junglevision.gathering.Link[] getLinks() {
		return links.values().toArray(new junglevision.gathering.Link[0]);
	}
	
	//Setters
	public void setMetrics(junglevision.gathering.MetricDescription[] descriptions) {
		this.descriptions = descriptions;
		for (junglevision.gathering.MetricDescription md : descriptions) {
			metrics.put(md.getName(), md.getMetric(this));
		}
	}
	
	public void addMetric(junglevision.gathering.MetricDescription description) {
		metrics.put(description.getName(), description.getMetric(this));
	}
	
	public void removeMetric(junglevision.gathering.MetricDescription description) {
		metrics.remove(description.getName());
	}	
	
	public void addLink(junglevision.gathering.Element destination, junglevision.gathering.Link newLink) {
		links.put(destination, newLink);		
	}
}