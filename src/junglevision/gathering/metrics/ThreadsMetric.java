package junglevision.gathering.metrics;

import junglevision.gathering.Metric;
import ibis.ipl.support.management.AttributeDescription;

public class ThreadsMetric extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private int thread_max;
	
	public ThreadsMetric() {
		super();
		
		name = "THREADS";		
				
		color[0] = 0.5f;
		color[1] = 0.5f;
		color[2] = 0.5f;
		
		necessaryAttributes.add(new AttributeDescription("java.lang:type=Threading", "ThreadCount"));
		
		outputTypes.add(MetricOutput.N);
	}
	
	public void update(Object[] results, Metric metric) {		
		int num_threads		= (Integer) results[0];
		thread_max = Math.max(thread_max, num_threads);		
		
		metric.setValue(MetricOutput.N, num_threads);
	}
}
