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
		
		for (int i=0; i<numberOfMetrics; i++) {			
			children.add(new LinkMetric(jv, glu, new junglevision.gathering.metrics.RandomMetric().getMetric()));
		}
		
		constructDimensions();
	}
		
	public void setLocation(Float[] newLocation) {	
		this.location = newLocation.clone();		
		
		Float[] newDimensions 	= new Float[3];		
		Float[] newRotation 	= new Float[3];
		
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
		newLocation[0] = origin[0] + (0.5f * (destination[0] - origin[0]));
		newLocation[1] = origin[1] + (0.5f * (destination[1] - origin[1]));
		newLocation[2] = origin[2] + (0.5f * (destination[2] - origin[2]));
		location = newLocation;
		
		//Set the object rotation, x is not used, we need an extra -90 on the z-axis for alignment
		newRotation[0] = 0.0f;
		newRotation[1] = yAngle;
		newRotation[2] = zAngle - 90.0f;		
		
		
		
		//get the breakoff point for rows and columns
		int number_of_children = children.size();
		int rows 		= (int)Math.ceil(Math.sqrt(number_of_children));
		int columns 	= (int)Math.floor(Math.sqrt(number_of_children));
		float xShiftPerChild = maxChildDimensions[0] + separation;
		float zShiftPerChild = maxChildDimensions[2] + separation;
		
		//Center the drawing around the location	
		Float[] shiftedLocation = new Float[3];
		shiftedLocation[0] = location[0] - ((xShiftPerChild*rows   )-separation) * 0.5f;
		shiftedLocation[1] = location[1];
		shiftedLocation[2] = location[2] - ((zShiftPerChild*columns)-separation) * 0.5f;
		
		Float[] metricLocation = new Float[3];
		
		int row = 0, column = 0, i = 0;
		for (Visual metric : children) {
			row = i % rows;
			
			//Move to next row (if applicable)
			if (i != 0 && row == 0) {
				column++;						
			}
							
			//cascade the new location
			metricLocation[0] = shiftedLocation[0] + xShiftPerChild*row;
			metricLocation[1] = shiftedLocation[1];
			metricLocation[2] = shiftedLocation[2] + zShiftPerChild*column;
			
			metric.setLocation(metricLocation);
			    
			i++;
			
			newDimensions[0] = metric.getDimensions()[0];
			//reduce the length by the radii of the origin and destination objects
			newDimensions[1] = length - (source.getRadius() + this.destination.getRadius());
			newDimensions[2] = metric.getDimensions()[2];
			
			metric.setRotation(newRotation);
			metric.setDimensions(newDimensions);
		}		
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
	    if (other == this) return true;
	    if (this.getClass() != other.getClass())return false;
	    FakeLink otherLink = (FakeLink) other;

		if ((source == otherLink.source && destination == otherLink.destination) ||
			(source == otherLink.destination && destination == otherLink.source)) {
			return true;
		} else {
			return false;
		}			
	}
}
