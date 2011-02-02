package junglevision.gathering.derivedmetrics;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junglevision.gathering.Element;
import junglevision.gathering.Metric;
import junglevision.gathering.Metric.MetricModifier;
import junglevision.gathering.MetricDescription;
import junglevision.gathering.exceptions.BeyondAllowedRangeException;
import junglevision.gathering.exceptions.OutputUnavailableException;
import junglevision.gathering.exceptions.SourceNotProvidedException;

public class BytesReceivedPerSecond extends junglevision.gathering.impl.MetricDescription implements junglevision.gathering.MetricDescription {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.derivedmetrics.BytesReceivedPerSecond");
	
	HashMap<String, MetricDescription> derivedFrom;
	
	public BytesReceivedPerSecond(MetricDescription receivedBytes) {
		super();
		
		name = "Bytes_Received_Per_Second";		
		type = MetricType.DERIVED_NODE;
		
		color[0] = 0.0f;
		color[1] = 0.5f;
		color[2] = 0.5f;
				
		derivedFrom.put("received_bytes", receivedBytes);
		
		outputTypes.add(MetricOutput.RPOS);
	}
		
	public void update(Object[] results, Metric metric, ArrayList<Element> sources ) throws SourceNotProvidedException {
		if (sources == null || sources.size() == 0) {
			throw new SourceNotProvidedException(); 
		}
		
		long time_now = System.currentTimeMillis();
		long time_elapsed = time_now - (Long)metric.getHelperVariable("time_prev");
		metric.setHelperVariable("time_prev", time_now);
		
		float time_seconds = (float)time_elapsed / 1000.0f;
		long total = 0;
		
		try {
			for (Element source : sources) {
				Metric sourceMetric = source.getMetric(derivedFrom.get("received_bytes"));			
				total += (Long)sourceMetric.getValue(MetricModifier.NORM, MetricOutput.N);			
			}
		} catch (OutputUnavailableException e) {
			logger.error("Source metric was not available while derived metric was requested.");
		}
		
		try {
			metric.setValue(MetricModifier.NORM, MetricOutput.RPOS, (float)total / time_seconds);
		} catch (BeyondAllowedRangeException e) {
			logger.debug(name +" metric failed trying to set value out of bounds.");
		}
	}
}
