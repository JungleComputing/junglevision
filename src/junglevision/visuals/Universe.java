package junglevision.visuals;

import java.util.ArrayList;

import javax.media.opengl.glu.GLU;


public class Universe extends VisualAbstract implements Visual {	
	public Universe(JungleGoggles jv, GLU glu, junglevision.gathering.Location root) {
		super();
		separation = 3.0f;
		cShape = CollectionShape.CUBE;
		
		jv.registerVisual(root, this);
		
		ArrayList<junglevision.gathering.Location> dataChildren = root.getChildren();
		
		for (junglevision.gathering.Location datachild : dataChildren) {
			locations.add(new Location(jv,glu,datachild));
		}
		
		constructDimensions();
	}	
}