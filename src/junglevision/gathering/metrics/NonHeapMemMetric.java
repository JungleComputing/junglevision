package junglevision.gathering.metrics;

import javax.management.openmbean.CompositeData;

import junglevision.gathering.Metric;
import ibis.ipl.support.management.AttributeDescription;

public class NonHeapMemMetric extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	public NonHeapMemMetric() {
		super();
		
		name = "MEM_NONHEAP";		
				
		color[0] = 0.5f;
		color[1] = 1.0f;
		color[2] = 0.0f;
				
		necessaryAttributes.add(new AttributeDescription("java.lang:type=Memory", "NonHeapMemoryUsage"));
		
		outputTypes.add(MetricOutput.PERCENT);
		outputTypes.add(MetricOutput.RPOS);
	}
	
	public void update(Object[] results, Metric metric) {
		CompositeData mem_nonheap_recvd	= (CompositeData) results[0];
		
		Long mem_nonheap_max 	= (Long) mem_nonheap_recvd.get("max");
		Long mem_nonheap_used 	= (Long) mem_nonheap_recvd.get("used");
		
		metric.setValue(MetricOutput.PERCENT, ((float) mem_nonheap_used / (float) mem_nonheap_max));
		metric.setValue(MetricOutput.RPOS, (float) mem_nonheap_used);
		metric.setMaxValue(MetricOutput.RPOS, (float) mem_nonheap_max);
	}
}
