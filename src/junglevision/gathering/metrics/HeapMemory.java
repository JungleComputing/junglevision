package junglevision.gathering.metrics;

import javax.management.openmbean.CompositeData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Metric;
import junglevision.gathering.Metric.MetricModifier;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import junglevision.gathering.exceptions.IncorrectParametersException;
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
	
	public void update(Object[] results, Metric metric) throws IncorrectParametersException {
		if (results[0] instanceof CompositeData) {
			CompositeData received	= (CompositeData) results[0];
			
			long mem_max  = (Long) received.get("max");
			long mem_used = (Long) received.get("used");
					
			try {			 
				metric.setValue(MetricModifier.NORM, MetricOutput.PERCENT, (float) mem_used / (float) mem_max);
				metric.setValue(MetricModifier.NORM, MetricOutput.RPOS, (float) mem_used);
				metric.setValue(MetricModifier.MAX, MetricOutput.RPOS, (float) mem_max);
			} catch (BeyondAllowedRangeException e) {
				logger.debug(name +" metric failed trying to set value out of bounds.");
			}
		} else {
			logger.error("Parameter is not of the required type.");
			throw new IncorrectParametersException();
		}
	}
}
