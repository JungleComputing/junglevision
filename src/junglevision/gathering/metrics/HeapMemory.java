package junglevision.gathering.metrics;

import javax.management.openmbean.CompositeData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Metric;
import junglevision.gathering.Metric.MetricModifier;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import ibis.ipl.support.management.AttributeDescription;

public class HeapMemory extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.metrics.HeapMemory");
	
	public HeapMemory() {
		super();
		
		name = "MEM_HEAP";	
		type = MetricType.NODE;
				
		color[0] = 0.0f;
		color[1] = 1.0f;
		color[2] = 0.0f;
		
		necessaryAttributes.add(new AttributeDescription("java.lang:type=Memory", "HeapMemoryUsage"));
		
		outputTypes.add(MetricOutput.PERCENT);
		outputTypes.add(MetricOutput.RPOS);
	}
	
	public void update(Object[] results, Metric metric) {
		CompositeData mem_heap_recvd	= (CompositeData) results[0];
		
		Long mem_heap_max 	= (Long) mem_heap_recvd.get("max");
		Long mem_heap_used 	= (Long) mem_heap_recvd.get("used");
				
		try {			 
			metric.setValue(MetricModifier.NORM, MetricOutput.PERCENT, (float) mem_heap_used / (float) mem_heap_max);
			metric.setValue(MetricModifier.NORM, MetricOutput.RPOS, (float) mem_heap_used);
			metric.setValue(MetricModifier.MAX, MetricOutput.RPOS, (float) mem_heap_max);
		} catch (BeyondAllowedRangeException e) {
			logger.debug(name +" metric failed trying to set value out of bounds.");
		}	
		
	}
}
