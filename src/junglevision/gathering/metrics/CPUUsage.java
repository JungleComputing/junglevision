package junglevision.gathering.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Metric;
import junglevision.gathering.Metric.MetricModifier;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import ibis.ipl.support.management.AttributeDescription;

public class CPUUsage extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.metrics.CPUUsage");
		
	public CPUUsage() {
		super();
		
		name = "CPU";
		type = MetricType.NODE;
				
		color[0] = 1.0f;
		color[1] = 0.0f;
		color[2] = 0.0f;
				
		necessaryAttributes.add(new AttributeDescription("java.lang:type=OperatingSystem", "ProcessCpuTime"));
		necessaryAttributes.add(new AttributeDescription("java.lang:type=Runtime", "Uptime"));
		necessaryAttributes.add(new AttributeDescription("java.lang:type=OperatingSystem", "AvailableProcessors"));
		
		outputTypes.add(MetricOutput.PERCENT);
	}
	
	public void update(Object[] results, Metric metric) {
		long cpu_elapsed 	= (Long)	results[0] - (Long) metric.getHelperVariable("cpu_prev");
		long upt_elapsed	= (Long)	results[1] - (Long) metric.getHelperVariable("upt_prev");
		int num_cpus		= (Integer) results[2];
		
		// Found at http://forums.sun.com/thread.jspa?threadID=5305095 to be the correct calculation for CPU usage
		float cpuUsage = Math.min(99F, cpu_elapsed / (upt_elapsed * 10000F * num_cpus));
		
		metric.setHelperVariable("cpu_prev", cpu_elapsed);
		metric.setHelperVariable("upt_prev", upt_elapsed);
		
		try {			 
			metric.setValue(MetricModifier.NORM, MetricOutput.PERCENT, (cpuUsage / 100));
		} catch (BeyondAllowedRangeException e) {
			logger.debug(name +" metric failed trying to set value out of bounds.");
		}
	}
}
