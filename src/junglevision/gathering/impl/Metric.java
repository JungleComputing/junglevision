package junglevision.gathering.impl;

import java.util.HashMap;

import junglevision.gathering.MetricDescription.MetricOutput;
import junglevision.gathering.exceptions.OutputUnavailableException;

/**
 * The abstract implementation of the interface for metrics, used in the gathering module
 * @author Maarten van Meersbergen
 *
 */
public class Metric implements junglevision.gathering.Metric {	
	protected junglevision.gathering.MetricDescription myDescription;
	protected HashMap<MetricOutput, Number> values, maxValues;
	
	public Metric(junglevision.gathering.MetricDescription desc) {
		this.myDescription = desc;
		
		values = new HashMap<MetricOutput, Number>();
		for (MetricOutput current : desc.getOutputTypes()) {
			if (current == MetricOutput.PERCENT) {
				values.put(current, 0.0f);
			} else if (current == MetricOutput.RPOS) {
				values.put(current, 0.0f);
			} else if (current == MetricOutput.R) {
				values.put(current, 0.0f);
			} else if (current == MetricOutput.N) {
				values.put(current, 0);
			} 
		}
		
		maxValues = new HashMap<MetricOutput, Number>();
		for (MetricOutput current : desc.getOutputTypes()) {
			if (current == MetricOutput.RPOS) {
				maxValues.put(current, 0.0f);
			} else if (current == MetricOutput.R) {
				maxValues.put(current, 0.0f);
			} else if (current == MetricOutput.N) {
				maxValues.put(current, 0);
			} 
		}
	}
		
	public Number getCurrentValue(MetricOutput outputmethod) throws OutputUnavailableException {
		if (values.containsKey(outputmethod)) {
			return values.get(outputmethod);
		} else {
			throw new OutputUnavailableException();
		}
	}
	
	public Number getMaximumValue(MetricOutput outputmethod) throws OutputUnavailableException {
		if (values.containsKey(outputmethod) && outputmethod != MetricOutput.PERCENT) {
			return maxValues.get(outputmethod);
		} else {
			throw new OutputUnavailableException();
		}		
	}
	
	public void setValue(MetricOutput outputmethod, Number value) {
		values.put(outputmethod, value);
	}
	
	public void setMaxValue(MetricOutput outputmethod, Number value) {
		maxValues.put(outputmethod, value);
	}
	
	public void update(Object[] results) {
		myDescription.update(results, this);
	}

	public junglevision.gathering.MetricDescription getDescription() {
		return myDescription;
	}		
}