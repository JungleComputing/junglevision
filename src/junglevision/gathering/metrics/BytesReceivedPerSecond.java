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

public class BytesReceivedPerSecond extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.metrics.BytesReceivedPerSecond");
		
	public BytesReceivedPerSecond() {
		super();
		
		name = "Bytes_Received";
		type = MetricType.LINK;
		direction = LinkDirection.DST_SRC;
				
		color[0] = 0.0f;
		color[1] = 0.5f;
		color[2] = 0.5f;
				
		necessaryAttributes.add(new AttributeDescription("ibis", "receivedBytesPerIbis"));
		
		outputTypes.add(MetricOutput.PERCENT);
	}
		
	public void update(Object[] results, Metric metric) {
		long time_now = System.currentTimeMillis();
		long time_elapsed = time_now - (Long)metric.getHelperVariable("time_prev");
		metric.setHelperVariable("time_prev", time_now);
		
		float time_seconds = (float)time_elapsed / 1000.0f;
		
		@SuppressWarnings("unchecked")
		Map<IbisIdentifier, Long> received = (Map<IbisIdentifier, Long>) results[0];
		
		HashMap<IbisIdentifier, Number> result = new HashMap<IbisIdentifier, Number>();
		long total = 0;
		for (Map.Entry<IbisIdentifier, Long> entry : received.entrySet()) {
			long value = (Long) entry.getValue();
			result.put(entry.getKey(), (value / time_seconds));
			total += value;
		}
		
		try {
			metric.setValue(MetricModifier.NORM, MetricOutput.N, total);
			metric.setValue(MetricModifier.NORM, MetricOutput.RPOS, result);
		} catch (BeyondAllowedRangeException e) {
			logger.debug(name +" metric failed trying to set value out of bounds.");
		}
	}
}