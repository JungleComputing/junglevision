package junglevision.visuals;

import javax.media.opengl.glu.GLU;

import junglevision.Junglevision;

public class FakeLocationLower extends VisualAbstract implements Visual {
	private final static int MAX_NUMBER_OF_CHILDREN = 9;
		
	public FakeLocationLower(Junglevision jv, GLU glu, int numberOfIbises) {
		super();
		separation = 1.0f;
		cShape = CollectionShape.SPHERE;
		
		jv.registerVisual(this);
		
		for (int i=0; i<numberOfIbises; i++) {
			children.add(new FakeIbis(jv, glu, (int) Math.max(1,(Math.random()*MAX_NUMBER_OF_CHILDREN))));
		}		
				
		constructDimensions();
	}
}
