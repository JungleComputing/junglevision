package junglevision.gathering.impl;

import java.util.ArrayList;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.ipl.support.management.AttributeDescription;

/**
 * The implementation for a description of a metric, in which several properties of this metric are defined.
 * @author Maarten van Meersbergen
 */

public abstract class MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.MetricDescription");
	
	protected String name;
	protected MetricType type;
	protected Float[] color;
	protected ArrayList<MetricOutput> outputTypes; 
	protected ArrayList<AttributeDescription> necessaryAttributes;
	protected ArrayList<junglevision.gathering.MetricDescription> necessaryMetrics;
		
	protected MetricDescription() {
		color = new Float[3];
		outputTypes = new ArrayList<MetricOutput>();
		necessaryAttributes = new ArrayList<AttributeDescription>();	
		necessaryMetrics = new ArrayList<junglevision.gathering.MetricDescription>();
	}
	
	//Getters
	public String getName() {
		return name;
	}
	
	public MetricType getType() {
		return type;
	}
		
	public Float[] getColor() {
		return color;
	}
	
	public ArrayList<MetricOutput> getOutputTypes() {
		return outputTypes;
	}
	
	public ArrayList<AttributeDescription> getNecessaryAttributes() {
		return necessaryAttributes;
	}
	
	public junglevision.gathering.Metric getMetric(junglevision.gathering.Element element) {
		return new Metric(element, this);
	}
}
