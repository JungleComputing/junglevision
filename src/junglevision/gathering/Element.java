package junglevision.gathering;

/**
 * General interface for any element within the data gathering universe
 */
public interface Element {		
	/**
	 * Returns all of the Metrics gathered by this element. 
	 * @return
	 * 		Metrics that can be queried for their values.
	 */
	public Metric[] getMetrics();
	public Metric getMetric(MetricDescription desc);
	public Metric getMetric(String metricName);
	public Link getLink(Element destination);
	public Link[] getLinks();

	//Setters
	/**
	 * Sets the group of Metrics that is to be gathered by this element 
	 * from this moment onwards.
	 * @param metrics
	 * 		The metrics that need to be gathered from now on.
	 */
	public void setMetrics(MetricDescription[] metrics);
	public void addMetric(MetricDescription metric);
	public void removeMetric(MetricDescription metric);
	public void addLink(Element element, Link newLink);
	
}