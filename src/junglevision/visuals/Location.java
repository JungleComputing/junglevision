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
			children.add(new Location(jv, glu, datachild));
		}
		
		ArrayList<junglevision.gathering.Ibis> dataIbises = dataLocation.getIbises();
		
		for (junglevision.gathering.Ibis dataIbis : dataIbises) {
			children.add(new Ibis(jv, glu, dataIbis));
		}
						
		constructDimensions();
	}
}
