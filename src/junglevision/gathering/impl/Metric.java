package junglevision.gathering.impl;

import ibis.ipl.IbisIdentifier;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.MetricDescription.MetricOutput;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import junglevision.gathering.exceptions.IncorrectParametersException;
import junglevision.gathering.exceptions.OutputUnavailableException;
import junglevision.gathering.exceptions.SingletonObjectNotInstantiatedException;

/**
 * The abstract implementation of the interface for metrics, used in the gathering module
 * @author Maarten van Meersbergen
 *
 */
public class Metric implements junglevision.gathering.Metric {	
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Metric");

	protected junglevision.gathering.impl.Collector c;
	protected junglevision.gathering.Element element;
	protected junglevision.gathering.MetricDescription myDescription;
	protected HashMap<MetricOutput, Number> values, maxValues, minValues;
	protected HashMap<MetricOutput, HashMap<junglevision.gathering.Element, Number>> linkValues, maxLinkValues, minLinkValues;
	
	protected HashMap<String, Number> helperVariables;
	
	public Metric(junglevision.gathering.Element element, junglevision.gathering.MetricDescription desc) {
		try {
			this.c = Collector.getCollector();
		} catch (SingletonObjectNotInstantiatedException e) {
			logger.error("Collector not instantiated properly.");
		}
		
		this.element = element;
		this.myDescription = desc;

		values = new HashMap<MetricOutput, Number>();
		maxValues = new HashMap<MetricOutput, Number>();
		minValues = new HashMap<MetricOutput, Number>();
		for (MetricOutput current : desc.getOutputTypes()) {
			if (current == MetricOutput.PERCENT) {
				values.put(current, 0.0f);
				maxValues.put(current, 0.0f);
				minValues.put(current, 0.0f);
			} else if (current == MetricOutput.RPOS) {
				values.put(current, 0.0f);
				maxValues.put(current, 0.0f);
				minValues.put(current, 0.0f);
			} else if (current == MetricOutput.R) {
				values.put(current, 0.0f);
				maxValues.put(current, 0.0f);
				minValues.put(current, 0.0f);
			} else if (current == MetricOutput.N) {
				values.put(current, 0);
				maxValues.put(current, 0);
				minValues.put(current, 0);
			} 
		}
		
		linkValues = new HashMap<MetricOutput, HashMap<junglevision.gathering.Element, Number>>();
		maxLinkValues = new HashMap<MetricOutput, HashMap<junglevision.gathering.Element, Number>>();
		minLinkValues = new HashMap<MetricOutput, HashMap<junglevision.gathering.Element, Number>>();
		
		helperVariables = new HashMap<String, Number>();
	}

	public Number getHelperVariable(String name) {
		if (helperVariables.containsKey(name)) {
			return helperVariables.get(name);		
		} else {
			return (Number)0L;
		}
	}
	
	public void setHelperVariable(String name, Number value) {
		helperVariables.put(name, value);
	}	

	public Number getValue(MetricModifier mod, MetricOutput outputmethod) throws OutputUnavailableException {
		if (values.containsKey(outputmethod)) {
			if (mod == MetricModifier.NORM) {
				return values.get(outputmethod);
			} else if (mod == MetricModifier.MAX) {
				return maxValues.get(outputmethod);
			} else {
				return minValues.get(outputmethod);
			}
		} else {
			throw new OutputUnavailableException();
		}
	}
	
	public HashMap<junglevision.gathering.Element, Number> getLinkValue(MetricModifier mod, MetricOutput outputmethod) throws OutputUnavailableException {
		if (values.containsKey(outputmethod)) {
			if (mod == MetricModifier.NORM) {
				return linkValues.get(outputmethod);
			} else if (mod == MetricModifier.MAX) {
				return maxLinkValues.get(outputmethod);
			} else {
				return minLinkValues.get(outputmethod);
			}
		} else {
			throw new OutputUnavailableException();
		}
	}

	public void setValue(MetricModifier mod, MetricOutput outputmethod, Number value) throws BeyondAllowedRangeException {
		if (outputmethod == MetricOutput.PERCENT) {
			if (((Float)value) < 0f || ((Float)value) > 1f) {
				throw new BeyondAllowedRangeException();
			}
		} else if (outputmethod == MetricOutput.N) {
			if (((Long)value) < 0) {
				throw new BeyondAllowedRangeException();
			}
		} else if (outputmethod == MetricOutput.RPOS) {
			if (((Float)value) < 0f) {
				throw new BeyondAllowedRangeException();
			}
		}
		if (mod == MetricModifier.NORM) {
			values.put(outputmethod, value);
		} else if (mod == MetricModifier.MAX) {
			maxValues.put(outputmethod, value);
		} else if (mod == MetricModifier.MAX) {
			minValues.put(outputmethod, value);
		}
	}
	
	public void setValue(MetricModifier mod, MetricOutput outputmethod, HashMap<IbisIdentifier, Number> values) throws BeyondAllowedRangeException {
		HashMap<junglevision.gathering.Element, Number> result = new HashMap<junglevision.gathering.Element, Number>();
		
		for (Map.Entry<IbisIdentifier, Number> entry : values.entrySet()) {
			junglevision.gathering.Element ibis = c.getIbis(entry.getKey());
			Number value = 0;

			if (outputmethod == MetricOutput.PERCENT) {				
				value = (Float) entry.getValue();
				if (((Float)value) < 0f || ((Float)value) > 1f) {
					throw new BeyondAllowedRangeException();
				}
			} else if (outputmethod == MetricOutput.N) {	
				value = (Long) entry.getValue();
				if (((Long)value) < 0) {
					throw new BeyondAllowedRangeException();
				}	
			} else if (outputmethod == MetricOutput.RPOS) {	
				value = (Float) entry.getValue();
				if (((Float)value) < 0f) {
					throw new BeyondAllowedRangeException();
				}	
			}
			
			result.put(ibis, value);
		}
		
		if (mod == MetricModifier.NORM) {
			linkValues.put(outputmethod, result);
		} else if (mod == MetricModifier.MAX) {
			maxLinkValues.put(outputmethod, result);
		} else if (mod == MetricModifier.MAX) {
			minLinkValues.put(outputmethod, result);
		}		
	}
	
	public void update(Object[] results) {
		try {
			myDescription.update(results, this);
		} catch (IncorrectParametersException shouldnteverhappen) {
			//This is so bad, we're going to throw exceptions until someone fixes it.
			shouldnteverhappen.printStackTrace();
		}
	}

	public junglevision.gathering.MetricDescription getDescription() {
		return myDescription;
	}
}