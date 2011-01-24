package junglevision.gathering.metrics;

import java.util.HashMap;
import java.util.Map;

import ibis.ipl.support.management.AttributeDescription;
import ibis.ipl.IbisIdentifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Metric;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;

public class BytesSentPerSecond extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.metrics.BytesSentPerSecond");
	
	private long time_prev = 0;
	
	public BytesSentPerSecond() {
		super();
		
		name = "Bytes_Sent";		
		type = MetricType.LINK;
		
		color[0] = 0.0f;
		color[1] = 0.5f;
		color[2] = 0.5f;
				
		necessaryAttributes.add(new AttributeDescription("ibis", "sentBytesPerIbis"));
		
		outputTypes.add(MetricOutput.PERCENT);
	}
		
	public void update(Object[] results, Metric metric) {
		long time_now = System.currentTimeMillis();
		long time_elapsed = time_now - time_prev;
		float time_seconds = (float)time_elapsed / 1000.0f;
		
		@SuppressWarnings("unchecked")
		Map<IbisIdentifier, Long> sent = (Map<IbisIdentifier, Long>) results[0];
		
		HashMap<IbisIdentifier, Number> result = new HashMap<IbisIdentifier, Number>();
		for (Map.Entry<IbisIdentifier, Long> entry : sent.entrySet()) {
			result.put(entry.getKey(), (int) ((float)entry.getValue() / time_seconds));
		}
		
		try {			 
			metric.setValue(MetricOutput.N, result);
		} catch (BeyondAllowedRangeException e) {
			logger.debug(name +" metric failed trying to set value out of bounds.");
		}
	}
}
