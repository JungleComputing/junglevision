package junglevision.visuals;

import javax.media.opengl.glu.GLU;

import junglevision.Junglevision;

public class FakeLocationUpper extends VisualAbstract implements Visual {
	
	public FakeLocationUpper(Junglevision jv, GLU glu, int numberOfLocations) {
		super();
		separation = 3.0f;
		cShape = CollectionShape.SPHERE;
		
		for (int i=0; i<numberOfLocations; i++) {
			children.add(new FakeLocationLower(jv, glu, (int) (Math.random()*64)));
		}
		
		constructDimensions();
	}	
}