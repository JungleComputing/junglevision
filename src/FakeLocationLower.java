import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class FakeLocationLower {
	static final float SEPARATION = 1.0f;
	
	public enum Shape { CITYSCAPE, SPHERE, CUBE }  
	
	private FakeIbis[] ibises;	
	private Float[] location;
	private Shape currentShape;
	private float maxChildWidth, maxChildHeight;
	
	FakeLocationLower(Junglevision jv, GLU glu, int numberOfIbises) {		
		this.ibises = new FakeIbis[numberOfIbises];
		this.location = new Float[3];		
		this.currentShape = Shape.CUBE;
		this.maxChildWidth  = 0.0f;
		this.maxChildHeight = 0.0f;
		
		for (int i=0; i<numberOfIbises; i++) {
			ibises[i] = new FakeIbis(jv, glu, (int) (Math.random()*9));
		}		
	}
	
	public void setLocation(Float[] newLocation) {		 
		this.location[0] = newLocation[0];		
		this.location[1] = newLocation[1];
		this.location[2] = newLocation[2];		
		
		if (currentShape == Shape.CITYSCAPE) {		
			//get the breakoff point for rows and columns
			int metricsCount = ibises.length;
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
			for (FakeIbis node : ibises) {
				row = i % rows;
				
				//Move to next row (if applicable)
				if (i != 0 && row == 0) {
					column++;						
				}
								
				//cascade the new location
				metricLocation[0] = shiftedLocation[0] + xzShiftPerChild*row;
				metricLocation[1] = shiftedLocation[1];
				metricLocation[2] = shiftedLocation[2] + xzShiftPerChild*column;
				
				node.setLocation(metricLocation);
				    
				i++;
			}
		} else if (currentShape == Shape.SPHERE) {							
			double dlong = Math.PI*(3-Math.sqrt(5));
			double olong = 0.0;
			double dz    = 2.0/ibises.length;
			double z     = 1 - (dz/2);
			Float[][] pt = new Float[ibises.length][3]; 
			double r = 0;
			
			for (int k=0;k<ibises.length;k++) {
				r = Math.sqrt(1-(z*z));
				pt[k][0] = location[0] + 4*((float) (Math.cos(olong)*r));
				pt[k][1] = location[1] + 4*((float) (Math.sin(olong)*r));
				pt[k][2] = location[2] + 4*((float) z);
				z = z -dz;
				olong = olong +dlong;				
			}	
			
			int k=0;				
			for (FakeIbis node : ibises) {						
				//set the location						
				node.setLocation(pt[k]);							
				k++;
			}			
		} else if (currentShape == Shape.CUBE) {		
			//get the breakoff point for rows and columns
			int metricsCount = ibises.length;
			int rows 		= (int)Math.ceil(Math.pow(metricsCount,  (1.0/3.0)));
			int columns 	= (int)Math.ceil(Math.pow(metricsCount, (1.0/3.0)));
			int layers		= (int)Math.floor(Math.pow(metricsCount, (1.0/3.0)));
						
			float xzShiftPerChild = maxChildWidth  + SEPARATION;
			float yShiftPerChild  = maxChildHeight + SEPARATION;
			
			//Center the drawing around the location	
			Float[] shiftedLocation = new Float[3];
			shiftedLocation[0] = location[0] - ((xzShiftPerChild*rows   )-SEPARATION) * 0.5f;
			shiftedLocation[1] = location[1] - (( yShiftPerChild*layers )-SEPARATION) * 0.5f;
			shiftedLocation[2] = location[2] - ((xzShiftPerChild*columns)-SEPARATION) * 0.5f;
			
			Float[] metricLocation = new Float[3];
			
			int row = 0, column = 0, layer = 0;
			for (FakeIbis node : ibises) {								
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
				
				node.setLocation(metricLocation);
				    
				row++;
			}
		}
	}
	
	public void setShape(Shape newShape) {
		this.currentShape = newShape;
	}
	
	public float getSizeXZ() {
		float childWidth;
		for (FakeIbis ibis : ibises) {
			childWidth = ibis.getSizeXZ();			
			if (childWidth > maxChildWidth) {
				maxChildWidth = childWidth;
			}
		}
		
		return ((maxChildWidth+SEPARATION) * (int) Math.ceil(Math.sqrt(ibises.length))-SEPARATION);
	}
	
	public float getSizeY() {
		float childHeight;
		for (FakeIbis ibis : ibises) {	
			childHeight = ibis.getSizeY();
			if (childHeight > maxChildHeight) {
				maxChildHeight = childHeight;
			}
		}
		
		return ((maxChildHeight+SEPARATION) * (int) Math.ceil(Math.sqrt(ibises.length))-SEPARATION);
	}
	
	public void update() {
		for (FakeIbis node : ibises) {
			node.update();
		}
	}
	
	public void drawThis(GL gl, int renderMode, int selectedItem) {
		for (FakeIbis node : ibises) {
			node.drawThis(gl, renderMode, selectedItem);
		}
	}
}
