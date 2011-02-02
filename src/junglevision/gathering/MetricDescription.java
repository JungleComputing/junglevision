package junglevision.gathering;

import java.util.ArrayList;

import junglevision.gathering.exceptions.IncorrectParametersException;
import junglevision.gathering.exceptions.NotALinkMetricException;
import junglevision.gathering.exceptions.SourceNotProvidedException;

import ibis.ipl.support.management.AttributeDescription;

/**
 * An interface for a description of a metric, in which several properties of this metric are defined.
 * @author Maarten van Meersbergen
 * 
 * This Interface defines several public constants used as return values
 * 		PERCENT 	if the value is represented as a percentage value, this means the returned float 
 * 					is always between 0.0f and 1.0f
 * 		RPOS		if the value is represented as a positive real value from 0.0f to the max float 
 * 					value
 * 		R			if the value returned can be any value represented by a float from minimum float 
 * 					to maximum float
 * 		N			if the value is a discrete positive value that can be directly casted to an int
 */

public interface MetricDescription {
	public static enum MetricType {
		NODE, LINK, DERIVED_NODE, DERIVED_LINK
	}
	public static enum MetricOutput {
		PERCENT, RPOS, R, N
	}
	
	//Getters
	public String getName();
	
	public MetricType getType();
	
	public Float[] getColor();
		
	/**
	 * Returns an array of available output types for this metric.
	 * @return
	 * 		any of the MetricOutput enum constants defined in this interface
	 */		
	public ArrayList<MetricOutput> getOutputTypes();
		
	/**
	 * Returns the attributes necessary for the updating of this metric
	 * @return
	 * 		an array of AttributeDescriptions needed for this metric
	 */
	public ArrayList<AttributeDescription> getNecessaryAttributes();	
	
	/**
	 * Function that specifies what to do with the resulting values from the attribute update
	 * @param results
	 * 		the results array returned by the update cycle and passed on by the Metric class 		
	 * @param metric
	 * 		the metric that asks for the update to be done (this metric will be updated by callback)	
	 * @throws IncorrectParametersException 
	 * 		if the given Object[] does not contain the right type(s)
	 */
	public void update(Object[] results, Metric metric) throws IncorrectParametersException;	
	
	/**
	 * Returns a new metric based on this description
	 * @param element
	 * 		the element for which the metric should count.
	 * @return
	 * 		a new instance of the Metric class
	 */
	public Metric getMetric(Element element);
}
