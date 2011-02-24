package junglevision.gathering.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Metric;
import junglevision.gathering.Metric.MetricModifier;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import junglevision.gathering.exceptions.IncorrectParametersException;
import junglevision.gathering.exceptions.OutputUnavailableException;

public class Random extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.metrics.Random");

	public Random() {
		super();

		name = "RANDOM";
		type = MetricType.NODE;

		color[0] = (float)Math.random();
		color[1] = (float)Math.random();
		color[2] = (float)Math.random();

		outputTypes.add(MetricOutput.PERCENT);
	}

	public void update(Object[] results, Metric metric) throws IncorrectParametersException {
		junglevision.gathering.impl.Metric castMetric = ((junglevision.gathering.impl.Metric)metric);
		if (results[0] == null) {
			float currentValue;
			try {
				currentValue = (Float) metric.getValue(MetricModifier.NORM, MetricOutput.PERCENT);
				if (Math.random() > 0.5) {
					currentValue += Math.random()/10;
				} else {
					currentValue -= Math.random()/10;
				}
	
				currentValue = Math.max(0.0f, currentValue);
				currentValue = Math.min(1.0f, currentValue);
	
				try {
					castMetric.setValue(MetricModifier.NORM, MetricOutput.PERCENT, currentValue);
				} catch (BeyondAllowedRangeException e) {
					logger.debug(name +" metric failed trying to set value out of bounds.");
				}
	
			} catch (OutputUnavailableException e) {
				//This shouldn't happen if the metric is well defined
				e.printStackTrace();
			}
		} else {
			logger.error("Parameter is not of the required type.");
			throw new IncorrectParametersException();
		}
	}
}
