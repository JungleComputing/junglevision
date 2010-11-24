package junglevision.visuals;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

public abstract class VisualAbstract implements Visual {
	private final static float CUBE_RADIUS_MULTIPLIER = 0.075f;
	
	protected List<Visual> children;
	protected Float[] location;
	protected Float[] dimensions, maxChildDimensions;
	protected CollectionShape cShape;
	protected MetricShape mShape;
	protected float separation;
	
	public VisualAbstract() {
		children = new ArrayList<Visual>();
		location   = new Float[3];
		dimensions = new Float[3];
		maxChildDimensions = new Float[3];
		cShape = CollectionShape.CITYSCAPE;
		mShape = MetricShape.BAR;
		separation = 0.00f;
	}
	
	public void setLocation(Float[] newLocation) {		 
		this.location[0] = newLocation[0];		
		this.location[1] = newLocation[1];
		this.location[2] = newLocation[2];
		
		if (children.size() > 0) {
			float maxAllDimensions = Math.max(Math.max(maxChildDimensions[0], maxChildDimensions[1]), maxChildDimensions[2]);
			
			if (cShape == CollectionShape.CITYSCAPE) {		
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
				}
			} else if (cShape == CollectionShape.SPHERE) {							
				double dlong = Math.PI*(3-Math.sqrt(5));
				double olong = 0.0;
				double dz    = 2.0/children.size();
				double z     = 1 - (dz/2);
				Float[][] pt = new Float[children.size()][3]; 
				double r = 0;
				float radius = CUBE_RADIUS_MULTIPLIER * (maxAllDimensions+separation) * children.size();
				
				for (int k=0;k<children.size();k++) {
					r = Math.sqrt(1-(z*z));
					pt[k][0] = location[0] + radius*((float) (Math.cos(olong)*r));
					pt[k][1] = location[1] + radius*((float) (Math.sin(olong)*r));
					pt[k][2] = location[2] + radius*((float) z);
					z = z -dz;
					olong = olong +dlong;				
				}	
				
				int k=0;				
				for (Visual node : children) {						
					//set the location						
					node.setLocation(pt[k]);							
					k++;
				}			
			} else if (cShape == CollectionShape.CUBE) {		
				//get the breakoff point for rows and columns
				int ibisCount = children.size();
				int rows 		= (int)Math.ceil(Math.pow(ibisCount,  (1.0/3.0)));
				int columns 	= (int)Math.ceil(Math.pow(ibisCount, (1.0/3.0)));
				int layers		= (int)Math.floor(Math.pow(ibisCount, (1.0/3.0)));
							
				float xShiftPerChild = maxChildDimensions[0] + separation;
				float yShiftPerChild = maxChildDimensions[1] + separation;			
				float zShiftPerChild = maxChildDimensions[2] + separation;
				
				//Center the drawing around the location	
				Float[] shiftedLocation = new Float[3];
				shiftedLocation[0] = location[0] - ((xShiftPerChild*rows   )-separation) * 0.5f;
				shiftedLocation[1] = location[1] - ((yShiftPerChild*layers )-separation) * 0.5f;
				shiftedLocation[2] = location[2] - ((zShiftPerChild*columns)-separation) * 0.5f;
				
				Float[] metricLocation = new Float[3];
				
				int row = 0, column = 0, layer = 0;
				for (Visual node : children) {								
					if (row == rows) {
						row = 0;
						column++;
					}
					if (column == columns) {
						column = 0;
						layer++;
					}			
									
					//cascade the new location
					metricLocation[0] = shiftedLocation[0] + xShiftPerChild*row;
					metricLocation[1] = shiftedLocation[1] + yShiftPerChild*layer;
					metricLocation[2] = shiftedLocation[2] + zShiftPerChild*column;
					
					node.setLocation(metricLocation);
					    
					row++;
				}
			}
		}
	}
	
	public void setCollectionShape(CollectionShape newShape) {		
		cShape = newShape;
		constructDimensions();
	}
	
	public void setMetricShape(MetricShape newShape) {
		mShape = newShape;
	}
	
	public Float[] getLocation() {
		Float[] myLocation = new Float[3];
		myLocation[0] = location[0];
		myLocation[1] = location[1];
		myLocation[2] = location[2];
		
		return myLocation;
	}
	
	public Float[] getDimensions() {
		Float[] myDimensions = new Float[3];
		myDimensions[0] = dimensions[0];
		myDimensions[1] = dimensions[1];
		myDimensions[2] = dimensions[2];
		
		return myDimensions;
	}
	
	public void update() {
		for (Visual child : children) {
			child.update();
		}
	}
	
	public void drawThis(GL gl, int renderMode) {
		for (Visual ibis : children) {
			ibis.drawThis(gl, renderMode);
		}
	}
	
	protected void constructDimensions() {
		Float[] childDimensions;
		maxChildDimensions[0] = 0.0f;
		maxChildDimensions[1] = 0.0f;
		maxChildDimensions[2] = 0.0f;
		
		for (Visual child : children) {
			childDimensions = child.getDimensions();			
			if (childDimensions[0] > maxChildDimensions[0]) {
				maxChildDimensions[0] = childDimensions[0];
			}
			if (childDimensions[1] > maxChildDimensions[1]) {
				maxChildDimensions[1] = childDimensions[1];
			}
			if (childDimensions[2] > maxChildDimensions[2]) {
				maxChildDimensions[2] = childDimensions[2];
			}
		}
		
		float maxAllDimensions = Math.max(Math.max(maxChildDimensions[0], maxChildDimensions[1]), maxChildDimensions[2]);
		
		if (cShape == CollectionShape.CITYSCAPE) {
			dimensions[0] = (maxChildDimensions[0]+separation) * (int) Math.ceil(Math.sqrt(children.size()))-separation; 
			dimensions[1] = (maxChildDimensions[1]+separation) * (int) Math.floor(Math.sqrt(children.size()))-separation;
			dimensions[2] = (maxChildDimensions[2]+separation);
		} else if (cShape == CollectionShape.SPHERE) {
			dimensions[0] = (maxAllDimensions+separation)*CUBE_RADIUS_MULTIPLIER * children.size()*2f;
			dimensions[1] = (maxAllDimensions+separation)*CUBE_RADIUS_MULTIPLIER * children.size()*2f;
			dimensions[2] = (maxAllDimensions+separation)*CUBE_RADIUS_MULTIPLIER * children.size()*2f;
		} else if (cShape == CollectionShape.CUBE) {
			dimensions[0] = (maxChildDimensions[0]+separation) * (int) Math.ceil(Math.pow(children.size(),(1.0/3.0)))-separation; 
			dimensions[1] = (maxChildDimensions[1]+separation) * (int) Math.floor(Math.pow(children.size(),(1.0/3.0)))-separation;
			dimensions[2] = (maxChildDimensions[2]+separation) * (int) Math.ceil(Math.pow(children.size(),(1.0/3.0)))-separation;
		}
	}
}
