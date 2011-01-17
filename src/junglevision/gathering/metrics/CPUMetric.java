package junglevision.gathering.metrics;

import junglevision.gathering.Metric;
import ibis.ipl.support.management.AttributeDescription;

public class CPUMetric extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	public static final String NAME = "CPU";
	
	//Metric-specific variables
	private long cpu_prev, upt_prev;
	
	public CPUMetric() {
		super();
		
		name = "CPU";		
				
		color[0] = 1.0f;
		color[1] = 0.0f;
		color[2] = 0.0f;
				
		necessaryAttributes.add(new AttributeDescription("java.lang:type=OperatingSystem", "ProcessCpuTime"));
		necessaryAttributes.add(new AttributeDescription("java.lang:type=Runtime", "Uptime"));
		necessaryAttributes.add(new AttributeDescription("java.lang:type=OperatingSystem", "AvailableProcessors"));
		
		outputTypes.add(MetricOutput.PERCENT);
	}
	
	public void update(Object[] results, Metric metric) {
		long cpu_elapsed 	= (Long)	results[0] - cpu_prev;
		long upt_elapsed	= (Long)	results[1] - upt_prev;
		int num_cpus		= (Integer) results[2];
		
		// Found at http://forums.sun.com/thread.jspa?threadID=5305095 to be the correct calculation for CPU usage
		float cpuUsage = Math.min(99F, cpu_elapsed / (upt_elapsed * 10000F * num_cpus));
		
		cpu_prev = cpu_elapsed;
		upt_prev = upt_elapsed;
		
		metric.setValue(MetricOutput.PERCENT, (cpuUsage / 100));
	}
}
