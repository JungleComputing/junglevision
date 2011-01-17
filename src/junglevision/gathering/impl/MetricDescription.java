package junglevision.gathering.impl;

import java.util.ArrayList;

import ibis.ipl.support.management.AttributeDescription;

/**
 * The implementation for a description of a metric, in which several properties of this metric are defined.
 * @author Maarten van Meersbergen
 */

public abstract class MetricDescription implements junglevision.gathering.MetricDescription {
	protected String name;
	protected Float[] color;
	protected ArrayList<MetricOutput> outputTypes; 
	protected ArrayList<AttributeDescription> necessaryAttributes;
		
	protected MetricDescription() {
		color = new Float[3];
		outputTypes = new ArrayList<MetricOutput>();
		necessaryAttributes = new ArrayList<AttributeDescription>();			
	}
	
	//Getters
	public String getName() {
		return name;
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
	
	public junglevision.gathering.Metric getMetric() {
		return new Metric(this);
	}
}
