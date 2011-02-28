package junglevision.gathering.impl;

import java.util.HashMap;
import java.util.Set;

import junglevision.gathering.exceptions.MetricNotAvailableException;
import junglevision.gathering.exceptions.SelfLinkeageException;
import junglevision.gathering.impl.Link;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General methods for any element within the data gathering universe
 * @author Maarten van Meersbergen
 */
public abstract class Element implements junglevision.gathering.Element {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Element");
	
	HashMap<junglevision.gathering.MetricDescription, junglevision.gathering.Metric> metrics;
	HashMap<Element, junglevision.gathering.Link> links;
	
	public Element() {
		metrics = new HashMap<junglevision.gathering.MetricDescription, junglevision.gathering.Metric>();
		links	= new HashMap<Element, junglevision.gathering.Link>();
	}
		
	//getters		
	public junglevision.gathering.Metric getMetric(junglevision.gathering.MetricDescription desc) throws MetricNotAvailableException {
		if (metrics.containsKey(desc)) {
			return metrics.get(desc.getName());
		} else {
			throw new MetricNotAvailableException();
		}
	}
	
	public junglevision.gathering.Link getLink(junglevision.gathering.Element destination) throws SelfLinkeageException {
		junglevision.gathering.Link result;
		if (destination == this) {
			throw new SelfLinkeageException();
		} else if (links.containsKey(destination)) {
			result = links.get(destination);
		} else {
			result = new Link(this, destination);
			result.setMetrics(metrics.keySet());
			links.put(((Element)destination), result);
			((Element)destination).addLink(this, result);			
		}
		return result;
	}
	
	public void addLink(junglevision.gathering.Element destination, junglevision.gathering.Link link) {
		links.put(((Element)destination), link);
	}
	
	public void removeLink(junglevision.gathering.Element destination) {
		links.remove(destination);
	}
	
	public junglevision.gathering.Link[] getLinks() {
		return links.values().toArray(new junglevision.gathering.Link[0]);
	}
	
	//Setters
	public void setMetrics(Set<junglevision.gathering.MetricDescription> descriptions) {
		for (junglevision.gathering.MetricDescription md : descriptions) {
			metrics.put(md, md.getMetric(this));
		}
	}
	
	public void addMetric(junglevision.gathering.MetricDescription description) {
		metrics.put(description, description.getMetric(this));
	}
	
	public void removeMetric(junglevision.gathering.MetricDescription description) {
		metrics.remove(description);
	}
		
	
}