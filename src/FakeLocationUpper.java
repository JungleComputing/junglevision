import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class FakeLocationUpper {
	static final float SEPARATION = 3.0f;
	
	public enum Shape { CITYSCAPE, SPHERE, CUBE }  
	
	private FakeLocationLower[] locations;	
	private Float[] location;
	private Shape currentShape;
	private float maxChildWidth, maxChildHeight;
	
	FakeLocationUpper(Junglevision jv, GLU glu, int numberOfLocations) {		
		this.locations = new FakeLocationLower[numberOfLocations];
		this.location = new Float[3];		
		this.currentShape = Shape.CITYSCAPE;
		this.maxChildWidth  = 0.0f;
		this.maxChildHeight = 0.0f;
		
		for (int i=0; i<numberOfLocations; i++) {
			locations[i] = new FakeLocationLower(jv, glu, (int) (Math.random()*64));
		}		
	}
	
	public void setLocation(Float[] newLocation) {		 
		this.location[0] = newLocation[0];		
		this.location[1] = newLocation[1];
		this.location[2] = newLocation[2];
		
		float childWidth, childHeight;
		for (FakeLocationLower location : locations) {
			childWidth = location.getSizeXZ();			
			if (childWidth > maxChildWidth) {
				maxChildWidth = childWidth;
			}
			
			childHeight = location.getSizeY();
			if (childHeight > maxChildHeight) {
				maxChildHeight = childHeight;
			}
		}
		
		if (currentShape == Shape.CITYSCAPE) {		
			//get the breakoff point for rows and columns
			int metricsCount = locations.length;
			int rows 		= (int)Math.ceil(Math.sqrt(metricsCount));
			int columns 	= (int)Math.floor(Math.sqrt(metricsCount));
			float xzShiftPerChild = maxChildWidth + SEPARATION;
			
			//Center the drawing around the location	
			Float[] shiftedLocation = new Float[3];
			shiftedLocation[0] = location[0] - ((xzShiftPerChild*rows   )-SEPARATION) * 0.5f;
			shiftedLocation[1] = location[1];
			shiftedLocation[2] = location[2] - ((xzShiftPerChild*columns)-SEPARATION) * 0.5f;
			
			Float[] metricLocation = new Float[3];
			
			int row = 0, column = 0, i = 0;
			for (FakeLocationLower location : locations) {
				row = i % rows;
				
				//Move to next row (if applicable)
				if (i != 0 && row == 0) {
					column++;						
				}
								
				//cascade the new location
				metricLocation[0] = shiftedLocation[0] + xzShiftPerChild*row;
				metricLocation[1] = shiftedLocation[1];
				metricLocation[2] = shiftedLocation[2] + xzShiftPerChild*column;
				
				location.setLocation(metricLocation);
				    
				i++;
			}
		} else if (currentShape == Shape.SPHERE) {							
			double dlong = Math.PI*(3-Math.sqrt(5));
			double olong = 0.0;
			double dz    = 2.0/locations.length;
			double z     = 1 - (dz/2);
			Float[][] pt = new Float[locations.length][3]; 
			double r = 0;
			
			for (int k=0;k<locations.length;k++) {
				r = Math.sqrt(1-(z*z));
				pt[k][0] = location[0] + 40*((float) (Math.cos(olong)*r));
				pt[k][1] = location[1] + 40*((float) (Math.sin(olong)*r));
				pt[k][2] = location[2] + 40*((float) z);
				z = z -dz;
				olong = olong +dlong;				
			}	
			
			int k=0;				
			for (FakeLocationLower location : locations) {						
				//set the location						
				location.setLocation(pt[k]);							
				k++;
			}			
		} else if (currentShape == Shape.CUBE) {		
			//get the breakoff point for rows and columns
			int metricsCount = locations.length;
			int rows 		= (int)Math.ceil(Math.pow(metricsCount, (1.0/3.0)));
			int columns 	= (int)Math.ceil(Math.pow(metricsCount, (1.0/3.0)));
			int layers		= (int)Math.floor(Math.pow(metricsCount,(1.0/3.0)));
			
			float xzShiftPerChild = maxChildWidth  + SEPARATION;
			float yShiftPerChild  = maxChildHeight + SEPARATION;
			
			//Center the drawing around the location	
			Float[] shiftedLocation = new Float[3];
			shiftedLocation[0] = location[0] - ((xzShiftPerChild*rows   )-SEPARATION) * 0.5f;
			shiftedLocation[1] = location[1] - (( yShiftPerChild*layers )-SEPARATION) * 0.5f;
			shiftedLocation[2] = location[2] - ((xzShiftPerChild*columns)-SEPARATION) * 0.5f;
			
			Float[] metricLocation = new Float[3];
			
			int row = 0, column = 0, layer = 0, i = 0;
			for (FakeLocationLower location : locations) {								
				if (row == rows) {
					row = 0;
					column++;
				}
				if (column == columns) {
					column = 0;
					layer++;
				}			
								
				//cascade the new location
				metricLocation[0] = shiftedLocation[0] + xzShiftPerChild*row;
				metricLocation[1] = shiftedLocation[1] +  yShiftPerChild*layer;
				metricLocation[2] = shiftedLocation[2] + xzShiftPerChild*column;
				
				location.setLocation(metricLocation);
				    
				row++;
			}
		}
	}
	
	public Float[] getLocation() {
		return location;
	}
	
	public void setShape(Shape newShape) {
		this.currentShape = newShape;
	}
	
	public void update() {	
		for (FakeLocationLower location : locations) {
			location.update();
		}
	}
	
	public void drawThis(GL gl, int renderMode, int selectedItem) {
		for (FakeLocationLower location : locations) {
			location.drawThis(gl, renderMode, selectedItem);		
		}
	}
}
