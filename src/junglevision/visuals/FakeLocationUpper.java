package junglevision.visuals;

import javax.media.opengl.glu.GLU;

import junglevision.Junglevision;

public class FakeLocationUpper extends VisualAbstract implements Visual {
	private final static int MAX_NUMBER_OF_CHILDREN = 100;
	
	public FakeLocationUpper(Junglevision jv, GLU glu, int numberOfLocations) {
		super();
		separation = 3.0f;
		cShape = CollectionShape.CUBE;
		
		jv.registerVisual(this);
		
		for (int i=0; i<numberOfLocations; i++) {
			children.add(new FakeLocationLower(jv, glu, (int) (Math.random()*MAX_NUMBER_OF_CHILDREN)));
		}
		
		constructDimensions();
	}	
}