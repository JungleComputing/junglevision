package junglevision.gathering.impl;

import ibis.ipl.IbisIdentifier;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.MetricDescription.MetricOutput;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import junglevision.gathering.exceptions.OutputUnavailableException;
import junglevision.gathering.exceptions.SingletonObjectNotInstantiatedException;

/**
 * The abstract implementation of the interface for metrics, used in the gathering module
 * @author Maarten van Meersbergen
 *
 */
public class Metric implements junglevision.gathering.Metric {	
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Metric");

	protected junglevision.gathering.Collector c;
	protected junglevision.gathering.Element element;
	protected junglevision.gathering.MetricDescription myDescription;
	protected HashMap<MetricOutput, Number> values, maxValues;

	public Metric(junglevision.gathering.Element element, junglevision.gathering.MetricDescription desc) {
		try {
			this.c = Collector.getCollector();
		} catch (SingletonObjectNotInstantiatedException e) {
			logger.error("Collector not instantiated properly.");
		}
		
		this.element = element;
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

	public void setValue(MetricOutput outputmethod, Number value) throws BeyondAllowedRangeException {
		if (outputmethod == MetricOutput.PERCENT) {
			if (((Float)value) < 0f || ((Float)value) > 1f) {
				throw new BeyondAllowedRangeException();
			}
		} else if (outputmethod == MetricOutput.N) {
			if (((Integer)value) < 0) {
				throw new BeyondAllowedRangeException();
			}
		} else if (outputmethod == MetricOutput.RPOS) {
			if (((Float)value) < 0f) {
				throw new BeyondAllowedRangeException();
			}
		}
		values.put(outputmethod, value);
	}

	public void setMaxValue(MetricOutput outputmethod, Number value) throws BeyondAllowedRangeException {
		if (outputmethod == MetricOutput.PERCENT) {
			if (((Float)value) < 0f || ((Float)value) > 1f) {
				throw new BeyondAllowedRangeException();
			}
		} else if (outputmethod == MetricOutput.N) {
			if (((Integer)value) < 0) {
				throw new BeyondAllowedRangeException();
			}
		} else if (outputmethod == MetricOutput.RPOS) {
			if (((Float)value) < 0f) {
				throw new BeyondAllowedRangeException();
			}
		}
		maxValues.put(outputmethod, value);
	}

	public void setValue(MetricOutput outputmethod, HashMap<IbisIdentifier, Number> values) throws BeyondAllowedRangeException {
		for (Map.Entry<IbisIdentifier, Number> entry : values.entrySet()) {
			junglevision.gathering.Ibis destination = c.getIbis(entry.getKey());
			junglevision.gathering.Link link = element.getLink(destination);
						
			Number value = 0;

			if (outputmethod == MetricOutput.PERCENT) {				
				value = (Float) entry.getValue();
				if (((Float)value) < 0f || ((Float)value) > 1f) {
					throw new BeyondAllowedRangeException();
				}
			} else if (outputmethod == MetricOutput.N) {	
				value = (Integer) entry.getValue();
				if (((Integer)value) < 0) {
					throw new BeyondAllowedRangeException();
				}	
			} else if (outputmethod == MetricOutput.RPOS) {	
				value = (Float) entry.getValue();
				if (((Float)value) < 0f) {
					throw new BeyondAllowedRangeException();
				}	
			}

			link.getMetric(myDescription).setValue(outputmethod, value);
		}
	}

	public void setMaxValue(MetricOutput outputmethod, HashMap<IbisIdentifier, Number> values) throws BeyondAllowedRangeException {
		for (Map.Entry<IbisIdentifier, Number> entry : values.entrySet()) {
			junglevision.gathering.Ibis destination = c.getIbis(entry.getKey());
			junglevision.gathering.Link link = element.getLink(destination);
						
			Number value = 0;

			if (outputmethod == MetricOutput.PERCENT) {				
				value = (Float) entry.getValue();
				if (((Float)value) < 0f || ((Float)value) > 1f) {
					throw new BeyondAllowedRangeException();
				}
			} else if (outputmethod == MetricOutput.N) {	
				value = (Integer) entry.getValue();
				if (((Integer)value) < 0) {
					throw new BeyondAllowedRangeException();
				}	
			} else if (outputmethod == MetricOutput.RPOS) {	
				value = (Float) entry.getValue();
				if (((Float)value) < 0f) {
					throw new BeyondAllowedRangeException();
				}	
			}

			link.getMetric(myDescription).setMaxValue(outputmethod, value);
		}
	}

	public void update(Object[] results) {
		myDescription.update(results, this);
	}

	public junglevision.gathering.MetricDescription getDescription() {
		return myDescription;
	}
}