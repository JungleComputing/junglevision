package junglevision.visuals;

import javax.media.opengl.glu.GLU;

import junglevision.Junglevision;

public class FakeLink extends VisualAbstract implements Visual {
	private Visual source, destination;
	
	public FakeLink(Junglevision jv, GLU glu, int numberOfMetrics, Visual source, Visual destination) {		
		super();		
		separation = 0.05f;
		this.source = source;
		this.destination = destination;
		dimensions[0] = 0.25f;
		dimensions[1] = 1.0f;
		dimensions[2] = 0.25f;
		
		for (int i=0; i<numberOfMetrics; i++) {
			Float[] color = {(float)Math.random(), (float)Math.random(), (float)Math.random()};
			children.add(new FakeMetric(jv, glu, color));
		}
		
		constructDimensions();
	}
		
	public void initializeLinks() {	
		Float[] newLocation = new Float[3];
		
		//Calculate the angles we need to turn towards the destination
		Float[] origin = source.getLocation();
		Float[] destination = this.destination.getLocation();
		int xSign = 1, ySign = 1, zSign = 1;
		
		float xDist = destination[0] - origin[0];
		if (xDist<0) xSign = -1;
		xDist = Math.abs(xDist);
		
		float yDist = destination[1] - origin[1];
		if (yDist<0) ySign = -1;
		yDist = Math.abs(yDist);
		
		float zDist = destination[2] - origin[2];
		if (zDist<0) zSign = -1;
		zDist = Math.abs(zDist);
		
		//Calculate the length of this element : V( x^2 + y^2 + z^2 ) 
		float length  = (float) Math.sqrt(	Math.pow(xDist,2)
										  + Math.pow(yDist,2) 
										  + Math.pow(zDist,2));
		
		float xzDist =  (float) Math.sqrt(	Math.pow(xDist,2)
				  						  + Math.pow(zDist,2));
					
		float yAngle = 0.0f;
		if (xSign < 0) {
			yAngle = 180.0f + (zSign * (float) Math.toDegrees(Math.atan(zDist/xDist)));
		} else {
			yAngle = (-zSign * (float) Math.toDegrees(Math.atan(zDist/xDist)));
		}
		
		float zAngle = ySign * (float) Math.toDegrees(Math.atan(yDist/xzDist));
		
		//Calculate the midpoint of the link		
		newLocation[0] = origin[0] + 0.5f * (destination[0] - origin[0]);
		newLocation[1] = origin[1] + 0.5f * (destination[1] - origin[1]);
		newLocation[2] = origin[2] + 0.5f * (destination[2] - origin[2]);
		location = newLocation;
		
		//Set the object rotation, x is not used
		rotation[1] = yAngle;
		rotation[2] = zAngle;
		
		//reduce the length by the radii of the origin and destination objects
		dimensions[1] = length - (source.getRadius() + this.destination.getRadius());
		
		for (Visual metric : children) {
			metric.setLocation(location);
			metric.setRotation(rotation);
			metric.setDimensions(dimensions);
		}
	}
}
