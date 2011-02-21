package junglevision.visuals;

import java.util.ArrayList;

import javax.media.opengl.glu.gl2.GLUgl2;

import junglevision.gathering.Ibis;
import junglevision.gathering.Location;
import junglevision.gathering.Metric;


public class JGLocation extends JGVisualAbstract implements JGVisual {		
	public JGLocation(JungleGoggles jv, GLUgl2 glu, Location dataLocation) {
		super();
		separation = 1.0f;
		cShape = CollectionShape.SPHERE;
		
		jv.registerVisual(dataLocation, this);
				
		ArrayList<Location> dataChildren = dataLocation.getChildren();		
		for (Location datachild : dataChildren) {
			locations.add(new JGLocation(jv, glu, datachild));
		}
		
		ArrayList<Ibis> dataIbises = dataLocation.getIbises();		
		for (Ibis dataIbis : dataIbises) {
			ibises.add(new JGIbis(jv, glu, dataIbis));
		}
		
		Metric dataMetrics[] = dataLocation.getMetrics();		
		for (Metric dataMetric : dataMetrics) {			
			metrics.add(new JGMetric(jv, glu, dataMetric));
		}	
						
		constructDimensions();
	}
}
