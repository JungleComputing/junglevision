package junglevision.gathering.impl;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Metric.MetricModifier;
import junglevision.gathering.MetricDescription.MetricOutput;
import junglevision.gathering.MetricDescription.MetricType;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import junglevision.gathering.exceptions.MetricNotAvailableException;
import junglevision.gathering.exceptions.OutputUnavailableException;
import junglevision.gathering.exceptions.SelfLinkeageException;

/**
 * A representation of a location (Node, Site) in the data gathering universe
 * @author Maarten van Meersbergen
 */
public class Location extends Element implements junglevision.gathering.Location {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Location");
	
	private String name;
	private Float[] color;
	
	private ArrayList<junglevision.gathering.Ibis> ibises;
	private ArrayList<junglevision.gathering.Location> children;
	
	public Location(String name, Float[] color) {
		super();
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
	
	public junglevision.gathering.Metric[] getMetrics() {
		ArrayList<junglevision.gathering.Metric> result = new ArrayList<junglevision.gathering.Metric>();
		for (junglevision.gathering.Metric metric : metrics.values()) {
			if (metric.getDescription().getType() == MetricType.NODE) {
				result.add(metric);
			}
		}		
		return result.toArray(new junglevision.gathering.Metric[0]);
	}
	
	public Float[] getColor() {
		return color;
	}
	
	public ArrayList<junglevision.gathering.Ibis> getIbises() {
		return ibises;
	}
	
	public ArrayList<junglevision.gathering.Ibis> getAllIbises() {
		ArrayList<junglevision.gathering.Ibis> result = new ArrayList<junglevision.gathering.Ibis>();
		result.addAll(ibises);
		
		for (junglevision.gathering.Location child : children) {
			result.addAll(child.getAllIbises());
		}
		
		return result;
	}
	
	public ArrayList<junglevision.gathering.Location> getChildren() {
		return children;
	}
	
	/*
	public Number getReducedValue(Reducefunction function, junglevision.gathering.MetricDescription metric, MetricOutput outputmethod) {
		if (outputmethod == MetricOutput.N) {
			int result = 0;
			
			if (function == Reducefunction.LOCATIONSPECIFIC_MINIMUM) {
				result = 10000000;
				for (junglevision.gathering.Ibis ibis : ibises) {
					int metricvalue;
					try {
						metricvalue = (Integer) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						if (metricvalue < result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
					}					
				}
			} else if (function == Reducefunction.LOCATIONSPECIFIC_MAXIMUM) {
				result = 0;
				for (junglevision.gathering.Ibis ibis : ibises) {
					int metricvalue;
					try {
						metricvalue = (Integer) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						if (metricvalue > result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
					}					
				}
			} else if (function == Reducefunction.LOCATIONSPECIFIC_AVERAGE) {
				result = 0;
				for (junglevision.gathering.Ibis ibis : ibises) {
					int metricvalue;
					try {
						metricvalue = (Integer) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						result += metricvalue;	
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
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
						metricvalue = (Integer) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						if (metricvalue < result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
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
						metricvalue = (Integer) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						if (metricvalue > result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
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
						metricvalue = (Integer) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						result += metricvalue;	
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
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
						metricvalue = (Float) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						if (metricvalue < result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
					}					
				}
			} else if (function == Reducefunction.LOCATIONSPECIFIC_MAXIMUM) {
				result = 0f;
				for (junglevision.gathering.Ibis ibis : ibises) {
					float metricvalue;
					try {
						metricvalue = (Float) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						if (metricvalue > result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
					}					
				}
			} else if (function == Reducefunction.LOCATIONSPECIFIC_AVERAGE) {
				result = 0f;
				for (junglevision.gathering.Ibis ibis : ibises) {
					float metricvalue;
					try {
						metricvalue = (Float) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						result += metricvalue;	
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
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
						metricvalue = (Float) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						if (metricvalue < result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
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
						metricvalue = (Float) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						if (metricvalue > result) {
							result = metricvalue;
						}
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
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
						metricvalue = (Float) ibis.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
						result += metricvalue;	
					} catch (OutputUnavailableException e) {
						logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
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
	*/
	public ArrayList<junglevision.gathering.Link> getLinks(junglevision.gathering.MetricDescription metric, MetricOutput outputmethod, float minimumValue, float maximumValue) {
		ArrayList<junglevision.gathering.Link> result = new ArrayList<junglevision.gathering.Link>();
		
		if (outputmethod == MetricOutput.N) {
			for (Entry<Element, junglevision.gathering.Link> entry : links.entrySet()) {
				Link link = ((Link)entry.getValue());
				int linkvalue;
				try {
					linkvalue = (Integer) link.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
					if (linkvalue >= minimumValue && linkvalue <= maximumValue ) {
						result.add(link);
					}
				} catch (OutputUnavailableException e) {
					logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
				} catch (MetricNotAvailableException e) {					
					logger.error("The impossible MetricNotAvailableException just happened anyway.");
				}			
			}			
		} else {
			for (Entry<Element, junglevision.gathering.Link> entry : links.entrySet()) {
				Link link = ((Link)entry.getValue());
				float linkvalue;
				try {
					linkvalue = (Float) link.getMetric(metric).getValue(MetricModifier.NORM, outputmethod);
					if (linkvalue >= minimumValue && linkvalue <= maximumValue ) {
						result.add(link);
					}
				} catch (OutputUnavailableException e) {
					logger.debug("OutputUnavailableException caught. Metric is probably undefined.");
				} catch (MetricNotAvailableException e) {					
					logger.error("The impossible MetricNotAvailableException just happened anyway.");
				}				
			}	
		}	
		return result;
	}
	
	public int getNumberOfDescendants() {
		int result = ibises.size();
		
		for (junglevision.gathering.Location child : children) {
			result += ((Location)child).getNumberOfDescendants();
		}
		
		return result;
	} 
	
	public String debugPrint() {
		String result = "";
		result += name + " has "+children.size()+" children. \n" ;
		result += name + " has "+links.size()+" links. \n" ;
		result += name + " has "+ibises.size()+" ibises. \n" ;
		
		for (junglevision.gathering.Link link : links.values()) {
			result += name + " "+((Link)link).debugPrint();
		}
		
		result += "\n";
		
		for (junglevision.gathering.Ibis ibis : ibises) {
			result += name + " "+((Ibis)ibis).debugPrint();
		}
		
		result += "\n";
		
		for (junglevision.gathering.Location child : children) {
			result += ((Location)child).debugPrint();
		}
		return result;
	}
	
	//Setters
	public void addIbis(junglevision.gathering.Ibis ibis) {
		ibises.add(ibis);
	}
	
	public void removeIbis(junglevision.gathering.Ibis ibis) {
		ibises.remove(ibis);
	}
	
	public void addChild(junglevision.gathering.Location location) {
		if (!children.contains(location)) {
			children.add(location);
		}
	}
	
	public void removeChild(junglevision.gathering.Location location) {
		children.remove(location);
	}
		
	public void setMetrics(Set<junglevision.gathering.MetricDescription> newMetrics) {
		for (junglevision.gathering.Ibis ibis : ibises) {
			ibis.setMetrics(newMetrics);
		}
		for (junglevision.gathering.Location child : children) {
			child.setMetrics(newMetrics);
		}
		for (junglevision.gathering.Link link : links.values()) {
			link.setMetrics(newMetrics);
		}
		for (junglevision.gathering.MetricDescription md : newMetrics) {
			metrics.put(md, md.getMetric(this));
		}
	}
	
	public void makeLinkHierarchy() {
		for (junglevision.gathering.Link link : links.values()) {
			junglevision.gathering.Location source = (junglevision.gathering.Location) link.getSource();
			junglevision.gathering.Location destination = (junglevision.gathering.Location) link.getDestination();
			
			for (junglevision.gathering.Location sourceChild : source.getChildren()) {
				for (junglevision.gathering.Location destinationChild : destination.getChildren()) {
					junglevision.gathering.Link childLink;					
					try {
						childLink = sourceChild.getLink(destinationChild);
						((Link)link).addChild(childLink);
					} catch (SelfLinkeageException ignored) {
						//ignored, because we do not want this link
					}				
				}
			}
		}
		for (junglevision.gathering.Location child : children) {
			((Location)child).makeLinkHierarchy();
		}			
	}
	
	public void update() {
		logger.debug("updating "+name+" children: "+children.size()+" ibises: "+ ibises.size());
		//make sure the children are updated first
		for (junglevision.gathering.Location child : children) {			
			((Location)child).update();
		}
		
		for (Entry<junglevision.gathering.MetricDescription, junglevision.gathering.Metric> data : metrics.entrySet()) {
			junglevision.gathering.MetricDescription desc = data.getKey();
			Metric metric = (Metric)data.getValue();
		
			try {				
				ArrayList<MetricOutput> types = desc.getOutputTypes();
				
				for (MetricOutput outputtype : types) {
					if (outputtype == MetricOutput.PERCENT || outputtype == MetricOutput.R || outputtype == MetricOutput.RPOS) {
						float total = 0f, max = -10000000f, min = 10000000f;
						
						//First, we gather our own metrics
						for (junglevision.gathering.Ibis ibis : ibises) {
							Metric ibisMetric = (Metric)ibis.getMetric(desc);
							if (ibisMetric == null) {
								logger.debug("Null at "+name+" metric: "+desc.getName());
							} else {
								logger.debug("OK at "+name+" metric: "+desc.getName());
							}
							float ibisValue = (Float) ibisMetric.getValue(MetricModifier.NORM, outputtype);
							
							total += ibisValue ;
							
							if (ibisValue > max) max = ibisValue;
							if (ibisValue < min) min = ibisValue;
						}						
						
						if (outputtype == MetricOutput.PERCENT) {
							//Then we add the metric values of our child locations, 
							//multiplied by their weight.
							int childIbises = 0;
							for (junglevision.gathering.Location child : children) {
								float childValue = (Float)child.getMetric(desc).getValue(MetricModifier.NORM, outputtype);
								
								childIbises += ((Location)child).getNumberOfDescendants();
								
								total += childValue * ((Location)child).getNumberOfDescendants();
								
								if (childValue > max) max = childValue;								
								if (childValue < min) min = childValue;
							}
							metric.setValue(MetricModifier.NORM, outputtype, total/(ibises.size()+childIbises));
							metric.setValue(MetricModifier.MAX, outputtype, max);
							metric.setValue(MetricModifier.MIN, outputtype, min);
						} else {							
							//Then we add the metric values of our child locations					
							for (junglevision.gathering.Location child : children) {
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
						for (junglevision.gathering.Ibis ibis : ibises) {							
							long ibisValue = (Long) ibis.getMetric(desc).getValue(MetricModifier.NORM, outputtype);
							
							total += ibisValue ;
							
							if (ibisValue > max) max = ibisValue;
							if (ibisValue < min) min = ibisValue;
						}
						
						//Then we add the metric values of our child locations					
						for (junglevision.gathering.Location child : children) {
							long childValue = (Long) child.getMetric(desc).getValue(MetricModifier.NORM, outputtype);
							
							total += childValue;
							
							if (childValue > max) max = childValue;								
							if (childValue < min) min = childValue;
						}
						metric.setValue(MetricModifier.NORM, outputtype, total);
						metric.setValue(MetricModifier.MAX, outputtype, max);
						metric.setValue(MetricModifier.MIN, outputtype, min);
					}
				}				
			} catch (OutputUnavailableException impossible) {
				//Impossible since we tested if it was available first.
				logger.error("The impossible OutputUnavailableException just happened anyway.");
			} catch (BeyondAllowedRangeException e) {
				//Impossible unless one of the children has a value that is already bad
				logger.error("The impossible BeyondAllowedRangeException just happened anyway.");
			} catch (MetricNotAvailableException e) {					
				logger.error("The impossible MetricNotAvailableException just happened anyway.");
			}
		}
	}
		
}