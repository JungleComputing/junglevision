package junglevision.gathering.metrics;

import junglevision.gathering.Metric;
import junglevision.gathering.exceptions.OutputUnavailableException;

public class RandomMetric extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	public RandomMetric() {
		super();
		
		name = "RANDOM";		
				
		color[0] = (float)Math.random();
		color[1] = (float)Math.random();
		color[2] = (float)Math.random();
		
		outputTypes.add(MetricOutput.PERCENT);
	}
	
	public void update(Object[] results, Metric metric) {
		float currentValue;
		try {
			currentValue = (Float) metric.getCurrentValue(MetricOutput.PERCENT);
			if (Math.random() > 0.5) {
				currentValue += Math.random()/10;
			} else {
				currentValue -= Math.random()/10;
			}
			
			currentValue = Math.max(0.0f, currentValue);
			currentValue = Math.min(1.0f, currentValue);
			
			metric.setValue(MetricOutput.PERCENT, currentValue);
			
		} catch (OutputUnavailableException e) {
			//This shouldn't happen if the metric is well defined
			e.printStackTrace();
		}		
	}
}
