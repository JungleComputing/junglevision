package junglevision.gathering.metrics;

import javax.management.openmbean.CompositeData;

import junglevision.gathering.Metric;
import ibis.ipl.support.management.AttributeDescription;

public class HeapMemMetric extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	public HeapMemMetric() {
		super();
		
		name = "MEM_HEAP";		
				
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
		
		metric.setValue(MetricOutput.PERCENT, ((float) mem_heap_used / (float) mem_heap_max));
		metric.setValue(MetricOutput.RPOS, (float) mem_heap_used);
		metric.setMaxValue(MetricOutput.RPOS, (float) mem_heap_max);
	}
}
