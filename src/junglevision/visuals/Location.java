package junglevision.visuals;

import java.util.ArrayList;

import javax.media.opengl.glu.GLU;

public class Location extends VisualAbstract implements Visual {		
	public Location(JungleGoggles jv, GLU glu, junglevision.gathering.Location dataLocation) {
		super();
		separation = 1.0f;
		cShape = CollectionShape.SPHERE;
		
		jv.registerVisual(dataLocation, this);
				
		ArrayList<junglevision.gathering.Location> dataChildren = dataLocation.getChildren();		
		for (junglevision.gathering.Location datachild : dataChildren) {
			locations.add(new Location(jv, glu, datachild));
		}
		
		ArrayList<junglevision.gathering.Ibis> dataIbises = dataLocation.getIbises();		
		for (junglevision.gathering.Ibis dataIbis : dataIbises) {
			ibises.add(new Ibis(jv, glu, dataIbis));
		}
		
		junglevision.gathering.Metric dataMetrics[] = dataLocation.getMetrics();		
		for (junglevision.gathering.Metric dataMetric : dataMetrics) {			
			metrics.add(new Metric(jv, glu, dataMetric));
		}	
						
		constructDimensions();
	}
}
