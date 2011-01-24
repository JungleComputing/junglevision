package junglevision.gathering.metrics;

import javax.management.openmbean.CompositeData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Metric;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import ibis.ipl.support.management.AttributeDescription;

public class NonHeapMemory extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.metrics.NonHeapMemory");

	public NonHeapMemory() {
		super();

		name = "MEM_NONHEAP";	
		type = MetricType.NODE;

		color[0] = 0.5f;
		color[1] = 0.5f;
		color[2] = 0.0f;

		necessaryAttributes.add(new AttributeDescription("java.lang:type=Memory", "NonHeapMemoryUsage"));

		outputTypes.add(MetricOutput.PERCENT);
		outputTypes.add(MetricOutput.RPOS);
	}

	public void update(Object[] results, Metric metric) {
		CompositeData mem_nonheap_recvd	= (CompositeData) results[0];

		Long mem_nonheap_max 	= (Long) mem_nonheap_recvd.get("max");
		Long mem_nonheap_used 	= (Long) mem_nonheap_recvd.get("used");

		try {
			metric.setValue(MetricOutput.PERCENT, ((float) mem_nonheap_used / (float) mem_nonheap_max));
			metric.setValue(MetricOutput.RPOS, (float) mem_nonheap_used);
			metric.setMaxValue(MetricOutput.RPOS, (float) mem_nonheap_max);
		} catch (BeyondAllowedRangeException e) {
			logger.debug(name +" metric failed trying to set value out of bounds.");
		}
	}
}
