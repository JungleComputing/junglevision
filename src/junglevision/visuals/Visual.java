package junglevision.visuals;

import javax.media.opengl.GL;

public interface Visual {
	enum CollectionShape { CITYSCAPE, CUBE, SPHERE } 
	enum MetricShape { BAR, TUBE, SPHERE }
	
	public void setLocation(Float[] newLocation);
	public void setCollectionShape(CollectionShape newShape);
	public void setMetricShape(MetricShape newShape);
	
	public Float[] getLocation();	
	public Float[] getDimensions();	
		
	public void update();
	
	public void drawThis(GL gl, int renderMode);
}
