package junglevision.gathering;

import ibis.ipl.IbisIdentifier;

import java.util.HashMap;

import junglevision.gathering.MetricDescription.MetricOutput;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import junglevision.gathering.exceptions.OutputUnavailableException;

/**
 * The interface for metrics, used in the gathering module
 * @author Maarten van Meersbergen
 *
 */
public interface Metric {
	public enum MetricModifier { MAX, MIN, NORM };
	
	//A variable that is sometimes needed as a helper in the metric descriptions, but still metric-specific 
	public Number getHelperVariable(String name);	
	public void setHelperVariable(String name, Number value);
		
	/**
	 * Returns the value(s) of a metric
	 * @param mod
	 * 		the modifier of the requested output, MAX, MIN or NORM
	 * @param outputmethod
	 * 		the output method requested by the user, as defined in MetricDescription
	 * @return
	 * 		the current value of this metric
	 * @throws OutputUnavailableException
	 * 		if the selected outputmethod is considered nonsensical or otherwise unavailable
	 */
	public Number getValue(MetricModifier mod, MetricOutput outputmethod) throws OutputUnavailableException;
	
	/**
	 * Returns the value(s) of a metric
	 * @param mod
	 * 		the modifier of the requested output, MAX, MIN or NORM
	 * @param outputmethod
	 * 		the output method requested by the user, as defined in MetricDescription
	 * @return
	 * 		the map of IbisIdentifiers and values of this metric's links
	 * @throws OutputUnavailableException
	 * 		if the selected outputmethod is considered nonsensical or otherwise unavailable
	 */
	public HashMap<Element,Number> getLinkValue(MetricModifier mod, MetricOutput outputmethod) throws OutputUnavailableException;
			
	/**
	 * Callback function for the update method in MetricDescriptions
	 * @param mod
	 * 		the modifier of the requested output, MAX, MIN or NORM
	 * @param outputmethod
	 * 		The max output value to be updated
	 * @param value
	 * 		the max value to be set
	 * @throws BeyondAllowedRangeException 
	 * 		if the value parameter does not conform to the outputmethod parameter's bounds
	 */
	public void setValue(MetricModifier mod, MetricOutput outputmethod, Number value) throws BeyondAllowedRangeException;

	/**
	 * Callback function for the update method in MetricDescriptions
	 * @param mod
	 * 		the modifier of the requested output, MAX, MIN or NORM
	 * @param outputmethod
	 * 		The output value to be updated
	 * @param values
	 * 		the map of ibisidentifiers and value to be set
	 * @throws BeyondAllowedRangeException 
	 * 		if the value parameter does not conform to the outputmethod parameter's bounds
	 */
	public void setValue(MetricModifier mod, MetricOutput outputmethod, HashMap<IbisIdentifier, Number> values) throws BeyondAllowedRangeException;

	/**
	 * Returns the MetricDescription of this metric.
	 * @return 
	 * 		the MetricDescription of this metric
	 */
	public junglevision.gathering.MetricDescription getDescription();

	/**
	 * Updates the value(s) of this metric by asking it's host over the network
	 */
	public void update(Object[] results);
}