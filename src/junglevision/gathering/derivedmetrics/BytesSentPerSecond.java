package junglevision.gathering.derivedmetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ibis.ipl.support.management.AttributeDescription;
import ibis.ipl.IbisIdentifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Element;
import junglevision.gathering.Metric;
import junglevision.gathering.Metric.MetricModifier;
import junglevision.gathering.MetricDescription;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import junglevision.gathering.exceptions.OutputUnavailableException;

public class BytesSentPerSecond extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.metrics.BytesSentPerSecond");
	
	HashMap<String, MetricDescription> derivedFrom;
	
	public BytesSentPerSecond(MetricDescription sentBytes) {
		super();
		
		name = "Bytes_Sent_Per_Second";		
		type = MetricType.NODE;
		
		color[0] = 0.0f;
		color[1] = 0.5f;
		color[2] = 0.5f;
				
		derivedFrom.put("sent_bytes", sentBytes);
		
		outputTypes.add(MetricOutput.RPOS);
	}
		
	public void update(Object[] results, Metric metric, ArrayList<Element> sources ) {
		long time_now = System.currentTimeMillis();
		long time_elapsed = time_now - (Long)metric.getHelperVariable("time_prev");
		metric.setHelperVariable("time_prev", time_now);
		
		float time_seconds = (float)time_elapsed / 1000.0f;
		long total = 0;
		
		try {
			for (Element source : sources) {
				Metric sourceMetric = source.getMetric(derivedFrom.get("sent_bytes"));			
				total += (Long)sourceMetric.getValue(MetricModifier.NORM, MetricOutput.N);			
			}
		} catch (OutputUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			metric.setValue(MetricModifier.NORM, MetricOutput.RPOS, (float)total / time_seconds);
		} catch (BeyondAllowedRangeException e) {
			logger.debug(name +" metric failed trying to set value out of bounds.");
		}
	}
}
