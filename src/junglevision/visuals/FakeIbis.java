package junglevision.visuals;

import javax.media.opengl.glu.GLU;

import junglevision.Junglevision;
import junglevision.gathering.metrics.RandomMetric;

public class FakeIbis extends VisualAbstract implements Visual  {	
	
	public FakeIbis(Junglevision jv, GLU glu, int numberOfMetrics) {
		super();		
		separation = 0.05f;
		
		jv.registerVisual(this);
		
		for (int i=0; i<numberOfMetrics; i++) {			
			children.add(new Metric(jv, glu, new RandomMetric().getMetric()));
		}
		
		constructDimensions();
	}
}
