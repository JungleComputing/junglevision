package junglevision.gathering.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Metric;
import junglevision.gathering.Metric.MetricModifier;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import junglevision.gathering.exceptions.IncorrectParametersException;
import ibis.ipl.support.management.AttributeDescription;

public class ThreadsCount extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.metrics.ThreadsCount");

	public ThreadsCount() {
		super();

		name = "THREADS";
		type = MetricType.NODE;

		color[0] = 0.5f;
		color[1] = 0.5f;
		color[2] = 0.5f;

		necessaryAttributes.add(new AttributeDescription("java.lang:type=Threading", "ThreadCount"));

		outputTypes.add(MetricOutput.N);
	}

	public void update(Object[] results, Metric metric) throws IncorrectParametersException {		
		if (results[0] instanceof Integer) {
			int num_threads		= (Integer) results[0];		
	
			try {
				metric.setValue(MetricModifier.NORM, MetricOutput.N, num_threads);
			} catch (BeyondAllowedRangeException e) {
				logger.debug(name +" metric failed trying to set value out of bounds.");
			}
		} else {
			logger.error("Parameter is not of the required type.");
			throw new IncorrectParametersException();
		}
	}
}
