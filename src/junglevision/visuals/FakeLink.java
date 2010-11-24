package junglevision.visuals;

import javax.media.opengl.glu.GLU;

import junglevision.Junglevision;

public class FakeLink implements Visual {
	static final float SEPARATION = 0.05f;
	static final float METRIC_SIZE_XZ = 0.25f;
	
	public enum Shape { CITYSCAPE }  
	
	private FakeMetric[] metrics;	
	private Float[] location;
	private Shape currentShape;
	
	public FakeLink(Junglevision jv, GLU glu, int numberOfMetrics, Visual source, Visual destination) {		
		this.metrics = new FakeMetric[numberOfMetrics];
		this.location = new Float[3];		
		this.currentShape = Shape.CITYSCAPE;
		
		for (int i=0; i<numberOfMetrics; i++) {
			Float[] color = {(float)Math.random(), (float)Math.random(), (float)Math.random()};
			metrics[i] = new FakeMetric(jv, glu, color);
		}
	}
	
	public void setLocation(Float[] newLocation) {		 
		this.location[0] = newLocation[0];		
		this.location[1] = newLocation[1];
		this.location[2] = newLocation[2];
		
		if (currentShape == Shape.CITYSCAPE) {		
			//get the breakoff point for rows and columns
			int metricsCount = metrics.length;
			int rows 		= (int)Math.ceil(Math.sqrt(metricsCount));
			int columns 	= (int)Math.floor(Math.sqrt(metricsCount));
			float xzShiftPerChild = METRIC_SIZE_XZ + SEPARATION;
			
			//Center the drawing around the location	
			Float[] shiftedLocation = new Float[3];
			shiftedLocation[0] = location[0] - ((xzShiftPerChild*rows   )-SEPARATION) * 0.5f;
			shiftedLocation[1] = location[1];
			shiftedLocation[2] = location[2] - ((xzShiftPerChild*columns)-SEPARATION) * 0.5f;
			
			Float[] metricLocation = new Float[3];
			
			int row = 0, column = 0, i = 0;
			for (FakeMetric metric : metrics) {
				row = i % rows;
				
				//Move to next row (if applicable)
				if (i != 0 && row == 0) {
					column++;						
				}
								
				//cascade the new location
				metricLocation[0] = shiftedLocation[0] + xzShiftPerChild*row;
				metricLocation[1] = shiftedLocation[1];
				metricLocation[2] = shiftedLocation[2] + xzShiftPerChild*column;
				
				metric.setLocation(metricLocation);
				    
				i++;
			}
		}
	}
}
