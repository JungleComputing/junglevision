package junglevision.visuals;

import javax.media.opengl.glu.GLU;

public class Ibis extends VisualAbstract implements Visual  {	
	
	public Ibis(JungleGoggles jv, GLU glu, junglevision.gathering.Ibis dataIbis) {
		super();		
		separation = 0.05f;
		
		jv.registerVisual(dataIbis, this);
		
		junglevision.gathering.Metric dataMetrics[] = dataIbis.getMetrics();
		
		for (junglevision.gathering.Metric dataMetric : dataMetrics) {			
			children.add(new Metric(jv, glu, dataMetric));
		}
		
		constructDimensions();
	}
}
