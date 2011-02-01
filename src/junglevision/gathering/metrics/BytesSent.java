package junglevision.gathering.metrics;

import java.util.HashMap;
import java.util.Map;

import ibis.ipl.support.management.AttributeDescription;
import ibis.ipl.IbisIdentifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Metric;
import junglevision.gathering.Metric.MetricModifier;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;

public class BytesSent extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.metrics.BytesSent");
		
	public BytesSent() {
		super();
		
		name = "Bytes_Sent";		
		type = MetricType.NODE;
		
		color[0] = 0.0f;
		color[1] = 0.5f;
		color[2] = 0.5f;
				
		necessaryAttributes.add(new AttributeDescription("ibis", "sentBytesPerIbis"));
		
		outputTypes.add(MetricOutput.N);
	}
		
	public void update(Object[] results, Metric metric) {
		@SuppressWarnings("unchecked")
		Map<IbisIdentifier, Long> sent = (Map<IbisIdentifier, Long>) results[0];
		
		HashMap<IbisIdentifier, Number> result = new HashMap<IbisIdentifier, Number>();
		long total = 0;
		for (Map.Entry<IbisIdentifier, Long> entry : sent.entrySet()) {
			long value = (Long) entry.getValue();
			result.put(entry.getKey(), value);
			total += value;
		}
		
		try {
			metric.setValue(MetricModifier.NORM, MetricOutput.N, total);
			metric.setValue(MetricModifier.NORM, MetricOutput.N, result);
		} catch (BeyondAllowedRangeException e) {
			logger.debug(name +" metric failed trying to set value out of bounds.");
		}
	}
}
