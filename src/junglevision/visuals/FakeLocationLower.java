package junglevision.visuals;

import javax.media.opengl.glu.GLU;

import junglevision.Junglevision;

public class FakeLocationLower extends VisualAbstract implements Visual {
	
	public FakeLocationLower(Junglevision jv, GLU glu, int numberOfIbises, int numberOfLinks) {
		super();
		separation = 1.0f;
		cShape = CollectionShape.SPHERE;
		
		for (int i=0; i<numberOfIbises; i++) {
			children.add(new FakeIbis(jv, glu, (int) (Math.random()*9)));
		}
		
		
		numberOfLinks = (int) (Math.min(numberOfLinks, ((numberOfIbises*(numberOfIbises-1))/2)) );
		//if (numberOfLinks > 0) {
		//	Visual source 		= children.get(0);
		//	Visual destination 	= children.get(1);
		//	links.add(new FakeLink(jv, glu, (int) (Math.random()*9), source , destination));
		//}
		
		
		for (int i=0; i<numberOfLinks; i++) {
			Visual source = null, destination = source;
			while (source == destination) {
				source 		= children.get(Math.min( numberOfIbises,(int) (Math.random()*numberOfIbises))); 
				destination	= children.get(Math.min( numberOfIbises,(int) (Math.random()*numberOfIbises)));
			}
			
			if (source == null) { System.out.println("Paniek!"); }
			
			links.add(new FakeLink(jv, glu, (int) (Math.random()*9), source , destination));
		}
				
		constructDimensions();
	}
}
