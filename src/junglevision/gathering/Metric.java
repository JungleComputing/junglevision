package junglevision.gathering;

import junglevision.gathering.MetricDescription.MetricOutput;
import junglevision.gathering.exceptions.OutputUnavailableException;

/**
 * The interface for metrics, used in the gathering module
 * @author Maarten van Meersbergen
 *
 */
public interface Metric {
		
	/**
	 * Returns the value(s) of a metric
	 * @param outputmethod
	 * 		the output method requested by the user, as defined in MetricDescription
	 * @return
	 * 		the current value of this metric
	 * @throws OutputUnavailableException
	 * 		if the selected outputmethod is considered nonsensical or otherwise unavailable
	 */
	public Number getCurrentValue(MetricOutput outputmethod) throws OutputUnavailableException;
	
		
	/**
	 * Returns the maximum value of the metric, if a non-percentage based output is selected, 
	 * this will return the maximum value, which the current percentage output is based on. 
	 * @param outputmethod
	 * 		the output method requested by the user, as defined in MetricDescription
	 * @return
	 * 		the maximum value of this metric as seen so far
	 * @throws OutputUnavailableException
	 */
	public Number getMaximumValue(MetricOutput outputmethod) throws OutputUnavailableException;
	
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
	
	/**
	 * Callback function for the update method in MetricDescriptions
	 * @param outputmethod
	 * 		The output value to be updated
	 * @param value
	 * 		the value to be set
	 */
	public void setValue(MetricOutput outputmethod, Number value);
	
	/**
	 * Callback function for the update method in MetricDescriptions
	 * @param outputmethod
	 * 		The max output value to be updated
	 * @param value
	 * 		the max value to be set
	 */
	public void setMaxValue(MetricOutput outputmethod, Number value);
}