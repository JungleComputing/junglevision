package junglevision.visuals;

import javax.media.opengl.glu.GLU;

import junglevision.Junglevision;

public class FakeIbis extends VisualAbstract implements Visual  {	
	
	public FakeIbis(Junglevision jv, GLU glu, int numberOfMetrics) {
		super();		
		separation = 0.05f;
		
		jv.registerVisual(this);
		
		for (int i=0; i<numberOfMetrics; i++) {
			Float[] color = {(float)Math.random(), (float)Math.random(), (float)Math.random()};
			children.add(new FakeMetric(jv, glu, color));
		}
		
		constructDimensions();
	}
}
