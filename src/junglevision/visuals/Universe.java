package junglevision.visuals;

import java.util.ArrayList;

import javax.media.opengl.glu.gl2.GLUgl2;


public class Universe extends VisualAbstract implements Visual {	
	public Universe(JungleGoggles jv, GLUgl2 glu, junglevision.gathering.Location root) {
		super();
		separation = 16.0f;
		cShape = CollectionShape.CUBE;
		
		jv.registerVisual(root, this);
		
		ArrayList<junglevision.gathering.Location> dataChildren = root.getChildren();
		
		for (junglevision.gathering.Location datachild : dataChildren) {
			locations.add(new Location(jv,glu,datachild));
		}
		
		constructDimensions();
	}	
}