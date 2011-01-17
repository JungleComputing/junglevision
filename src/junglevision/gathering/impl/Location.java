package junglevision.gathering.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import junglevision.gathering.Link;
import junglevision.gathering.MetricDescription.MetricOutput;
import junglevision.gathering.exceptions.OutputUnavailableException;

/**
 * A representation of a location (Node, Site) in the data gathering universe
 * @author Maarten van Meersbergen
 */
public class Location extends Element implements junglevision.gathering.Location {
	private String name;
	private Float[] color;
	
	private ArrayList<junglevision.gathering.Ibis> ibises;
	private ArrayList<junglevision.gathering.Location> children;
	private HashMap<junglevision.gathering.Element, junglevision.gathering.Link> links;
	
	public Location(String name, Float[] color) {
		this.name = name;
		this.color = new Float[3];
		this.color[0] = color[0];
		this.color[1] = color[1];
		this.color[2] = color[2];
		
		ibises = new ArrayList<junglevision.gathering.Ibis>();
		children = new ArrayList<junglevision.gathering.Location>();
	}
	
	//Getters
	public String getName() {
		return name;
	}
	
	public Float[] getColor() {
		return color;
	}
	
	public ArrayList<junglevision.gathering.Ibis> getIbises() {
		return ibises;
	}	
	
	public ArrayList<junglevision.gathering.Location> getChildren() {
		return children;
	}
	
	public Number getReducedValue(Reducefunction function, junglevision.gathering.MetricDescription metric, MetricOutput outputmethod) {
		if (outputmethod == MetricOutput.N) {
			int result = 0;
			
			if (function == Reducefunction.LOCATIONSPECIFIC_MINIMUM) {
				result = 10000000;
				for (junglevision.gathering.Ibis ibis : ibises) {
					int metricvalue;
					try {
						metricvalue = (Integer) ibis.getMetric(metric).getCurrentValue(outputmethod);
						if (metricvalue < result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}					
				}
			} else if (function == Reducefunction.LOCATIONSPECIFIC_MAXIMUM) {
				result = 0;
				for (junglevision.gathering.Ibis ibis : ibises) {
					int metricvalue;
					try {
						metricvalue = (Integer) ibis.getMetric(metric).getCurrentValue(outputmethod);
						if (metricvalue > result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}					
				}
			} else if (function == Reducefunction.LOCATIONSPECIFIC_AVERAGE) {
				result = 0;
				for (junglevision.gathering.Ibis ibis : ibises) {
					int metricvalue;
					try {
						metricvalue = (Integer) ibis.getMetric(metric).getCurrentValue(outputmethod);
						result += metricvalue;	
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}				
				}
				if (ibises.size() > 0) {
					result = result / ibises.size();
				}
			} else if (function == Reducefunction.ALLDESCENDANTS_MINIMUM) {
				result = 10000000;
				for (junglevision.gathering.Ibis ibis : ibises) {
					int metricvalue;
					try {
						metricvalue = (Integer) ibis.getMetric(metric).getCurrentValue(outputmethod);
						if (metricvalue < result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}					
				}
				
				for (junglevision.gathering.Location child : children) {
					int childvalue = (Integer) child.getReducedValue(function, metric, outputmethod);
					if (childvalue < result) {
						result = childvalue;
					}
				}
			} else if (function == Reducefunction.ALLDESCENDANTS_MAXIMUM) {
				result = 0;
				for (junglevision.gathering.Ibis ibis : ibises) {
					int metricvalue;
					try {
						metricvalue = (Integer) ibis.getMetric(metric).getCurrentValue(outputmethod);
						if (metricvalue > result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}					
				}
				
				for (junglevision.gathering.Location child : children) {
					int childvalue = (Integer) child.getReducedValue(function, metric, outputmethod);
					if (childvalue > result) {
						result = childvalue;
					}
				}
			} else if (function == Reducefunction.ALLDESCENDANTS_AVERAGE) {
				result = 0;
				int numberOfIbises = ibises.size();
				
				for (junglevision.gathering.Ibis ibis : ibises) {
					int metricvalue;
					try {
						metricvalue = (Integer) ibis.getMetric(metric).getCurrentValue(outputmethod);
						result += metricvalue;	
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}				
				}
								
				for (junglevision.gathering.Location child : children) {
					result += (Integer) child.getReducedValue(function, metric, outputmethod) * child.getNumberOfDescendants();
					numberOfIbises += child.getNumberOfDescendants();
				}
				
				if (ibises.size() > 0) {
					result = result / numberOfIbises;
				}
			}
			return result;
		} else { //Any other metric is defined as a float
			float result = 0f;
			
			if (function == Reducefunction.LOCATIONSPECIFIC_MINIMUM) {
				result = 10000000f;
				for (junglevision.gathering.Ibis ibis : ibises) {
					float metricvalue;
					try {
						metricvalue = (Float) ibis.getMetric(metric).getCurrentValue(outputmethod);
						if (metricvalue < result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}					
				}
			} else if (function == Reducefunction.LOCATIONSPECIFIC_MAXIMUM) {
				result = 0f;
				for (junglevision.gathering.Ibis ibis : ibises) {
					float metricvalue;
					try {
						metricvalue = (Float) ibis.getMetric(metric).getCurrentValue(outputmethod);
						if (metricvalue > result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}					
				}
			} else if (function == Reducefunction.LOCATIONSPECIFIC_AVERAGE) {
				result = 0f;
				for (junglevision.gathering.Ibis ibis : ibises) {
					float metricvalue;
					try {
						metricvalue = (Float) ibis.getMetric(metric).getCurrentValue(outputmethod);
						result += metricvalue;	
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}				
				}
				if (ibises.size() > 0) {
					result = result / ibises.size();
				}
			} else if (function == Reducefunction.ALLDESCENDANTS_MINIMUM) {
				result = 10000000f;
				for (junglevision.gathering.Ibis ibis : ibises) {
					float metricvalue;
					try {
						metricvalue = (Float) ibis.getMetric(metric).getCurrentValue(outputmethod);
						if (metricvalue < result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}					
				}
				
				for (junglevision.gathering.Location child : children) {
					float childvalue = (Float) child.getReducedValue(function, metric, outputmethod);
					if (childvalue < result) {
						result = childvalue;
					}
				}
			} else if (function == Reducefunction.ALLDESCENDANTS_MAXIMUM) {
				result = 0f;
				for (junglevision.gathering.Ibis ibis : ibises) {
					float metricvalue;
					try {
						metricvalue = (Float) ibis.getMetric(metric).getCurrentValue(outputmethod);
						if (metricvalue > result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}					
				}
				
				for (junglevision.gathering.Location child : children) {
					float childvalue = (Float) child.getReducedValue(function, metric, outputmethod);
					if (childvalue > result) {
						result = childvalue;
					}
				}
			} else if (function == Reducefunction.ALLDESCENDANTS_AVERAGE) {
				result = 0f;
				int numberOfIbises = ibises.size();
				
				for (junglevision.gathering.Ibis ibis : ibises) {
					float metricvalue;
					try {
						metricvalue = (Float) ibis.getMetric(metric).getCurrentValue(outputmethod);
						result += metricvalue;	
					} catch (OutputUnavailableException e) {
						//This shouldn't happen if the metric is well defined
						e.printStackTrace();
					}				
				}
								
				for (junglevision.gathering.Location child : children) {
					result += (Float) child.getReducedValue(function, metric, outputmethod) * child.getNumberOfDescendants();
					numberOfIbises += child.getNumberOfDescendants();
				}
				
				if (ibises.size() > 0) {
					result = result / numberOfIbises;
				}
			}
			return result;
		}
	}
	
	public ArrayList<Link> getLinks(junglevision.gathering.MetricDescription metric, MetricOutput outputmethod, float minimumValue, float maximumValue) {
		ArrayList<Link> result = new ArrayList<Link>();
		
		if (outputmethod == MetricOutput.N) {
			for (Entry<junglevision.gathering.Element, Link> entry : links.entrySet()) {
				int linkvalue;
				try {
					linkvalue = (Integer) entry.getValue().getMetric(metric).getCurrentValue(outputmethod);
					if (linkvalue >= minimumValue && linkvalue <= maximumValue ) {
						result.add(entry.getValue());
					}
				} catch (OutputUnavailableException e) {
					//This shouldn't happen if the metric is well defined
					e.printStackTrace();
				}				
			}			
		} else {
			for (Entry<junglevision.gathering.Element, Link> entry : links.entrySet()) {
				float linkvalue;
				try {
					linkvalue = (Float) entry.getValue().getMetric(metric).getCurrentValue(outputmethod);
					if (linkvalue >= minimumValue && linkvalue <= maximumValue ) {
						result.add(entry.getValue());
					}
				} catch (OutputUnavailableException e) {
					//This shouldn't happen if the metric is well defined
					e.printStackTrace();
				}				
			}	
		}	
		return result;
	}
	
	public Link getLink(junglevision.gathering.Element destination) {		
		return links.get(destination);
	}
	
	public int getNumberOfDescendants() {
		int result = ibises.size();
		
		for (junglevision.gathering.Location child : children) {
			result += child.getNumberOfDescendants();
		}
		
		return result;
	} 

	public void addIbis(junglevision.gathering.Ibis ibis) {
		ibises.add(ibis);
	}
	
	public void removeIbis(junglevision.gathering.Ibis ibis) {
		ibises.remove(ibis);
	}
	
	public void addChild(junglevision.gathering.Location location) {
		children.add(location);
	}
	
	public void removeChild(junglevision.gathering.Location location) {
		children.remove(location);
	}
	
	public void addLink(junglevision.gathering.Element destination, junglevision.gathering.Link link) {
		links.put(destination, link);
	}
	
	public void removeLink(junglevision.gathering.Element destination) {
		links.remove(destination);
	}
}