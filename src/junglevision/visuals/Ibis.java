package junglevision.visuals;

import javax.media.opengl.glu.gl2.GLUgl2;


public class Ibis extends VisualAbstract implements Visual  {	
	
	public Ibis(JungleGoggles jv, GLUgl2 glu, junglevision.gathering.Ibis dataIbis) {
		super();		
		separation = 0.05f;
		
		jv.registerVisual(dataIbis, this);
		
		junglevision.gathering.Metric dataMetrics[] = dataIbis.getMetrics();
		
		for (junglevision.gathering.Metric dataMetric : dataMetrics) {			
			metrics.add(new Metric(jv, glu, dataMetric));
		}
		
		constructDimensions();
	}
}
