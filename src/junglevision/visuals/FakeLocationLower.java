package junglevision.visuals;

import javax.media.opengl.glu.GLU;

import junglevision.Junglevision;

public class FakeLocationLower extends VisualAbstract implements Visual {
	
	public FakeLocationLower(Junglevision jv, GLU glu, int numberOfIbises) {
		super();
		separation = 0.5f;
		cShape = CollectionShape.CUBE;
		
		for (int i=0; i<numberOfIbises; i++) {
			children.add(new FakeIbis(jv, glu, (int) (Math.random()*9)));
		}
		
		constructDimensions();
	}
}
