package junglevision.gathering.impl;

import java.util.HashMap;

/**
 * General methods for any element within the data gathering universe
 * @author Maarten van Meersbergen
 */
public abstract class Element implements junglevision.gathering.Element {
	HashMap<String, junglevision.gathering.Metric> metrics;
	
	public Element() {
		metrics = new HashMap<String, junglevision.gathering.Metric>();
	}
		
	//getters
	public junglevision.gathering.Metric[] getMetrics() {
		Metric[] type = new Metric[metrics.size()];
		return metrics.values().toArray(type);
	}
	
	public junglevision.gathering.Metric getMetric(junglevision.gathering.MetricDescription desc) {
		return metrics.get(desc.getName());
	}
	
	public junglevision.gathering.Metric getMetric(String metricName) {
		return metrics.get(metricName);
	}

	//Setters
	public void setMetrics(junglevision.gathering.MetricDescription[] descriptions) {
		for (junglevision.gathering.MetricDescription md : descriptions) {
			metrics.put(md.getName(), md.getMetric());
		}
	}
	
	public void addMetric(junglevision.gathering.MetricDescription description) {
		metrics.put(description.getName(), description.getMetric());
	}
	
	public void removeMetric(junglevision.gathering.MetricDescription description) {
		metrics.remove(description.getName());
	}	
}