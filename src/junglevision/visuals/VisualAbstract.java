package junglevision.visuals;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

public abstract class VisualAbstract implements Visual {
	private final static float CUBE_RADIUS_MULTIPLIER = 0.075f;
	
	protected List<Visual> locations;
	protected List<Visual> ibises;
	protected List<Visual> metrics;
	protected List<Visual> links;
	
	protected Float[] coordinates;
	protected Float[] rotation;
	protected Float[] dimensions, maxChildDimensions;
	
	protected float maxAllChildDimensions;
	protected CollectionShape cShape;
	protected FoldState foldState;
	protected MetricShape mShape;
	protected float separation;
	
	public VisualAbstract() {
		locations = new ArrayList<Visual>();
		ibises = new ArrayList<Visual>();
		metrics = new ArrayList<Visual>();
		links = new ArrayList<Visual>();
		
		coordinates   = new Float[3];
		coordinates[0] = 0.0f;
		coordinates[1] = 0.0f;
		coordinates[2] = 0.0f;
		
		rotation   = new Float[3];		
		rotation[0] = 0.0f;
		rotation[1] = 0.0f;
		rotation[2] = 0.0f;
		
		dimensions = new Float[3];
		dimensions[0] = 0.0f;
		dimensions[1] = 0.0f;
		dimensions[2] = 0.0f;
		
		maxChildDimensions = new Float[3];
		maxAllChildDimensions = 0.0f;
		cShape = CollectionShape.CUBE;
		foldState = FoldState.UNFOLDED;
		mShape = MetricShape.BAR;
		separation = 0.0f;
	}
	
	public void init(GL2 gl) {
		for (Visual child : locations) {
			child.init(gl);
		}
		for (Visual ibis : ibises) {
			ibis.init(gl);
		}	
		for (Visual metric : metrics) {
			metric.init(gl);
		}
		for (Visual link : links) {
			link.init(gl);
		}		
	}
	
	public void setCoordinates(Float[] newCoordinates) {		 
		this.coordinates[0] = newCoordinates[0];		
		this.coordinates[1] = newCoordinates[1];
		this.coordinates[2] = newCoordinates[2];
		
		
		
		if (locations.size() > 0) {
			maxAllChildDimensions = Math.max(Math.max(maxChildDimensions[0], maxChildDimensions[1]), maxChildDimensions[2]);
			
			if (cShape == CollectionShape.CITYSCAPE) {		
				//get the breakoff point for rows and columns
				int number_of_children = locations.size();
				int rows 		= (int)Math.ceil(Math.sqrt(number_of_children));
				int columns 	= (int)Math.floor(Math.sqrt(number_of_children));
				float xShiftPerChild = maxChildDimensions[0] + separation;
				float zShiftPerChild = maxChildDimensions[2] + separation;
				
				//Center the drawing around the location	
				Float[] shiftedLocation = new Float[3];
				shiftedLocation[0] = coordinates[0] - ((xShiftPerChild*rows   )-separation) * 0.5f;
				shiftedLocation[1] = coordinates[1];
				shiftedLocation[2] = coordinates[2] - ((zShiftPerChild*columns)-separation) * 0.5f;
				
				Float[] metricLocation = new Float[3];
				
				int row = 0, column = 0, i = 0;
				for (Visual metric : locations) {
					row = i % rows;
					
					//Move to next row (if applicable)
					if (i != 0 && row == 0) {
						column++;						
					}
									
					//cascade the new location
					metricLocation[0] = shiftedLocation[0] + xShiftPerChild*row;
					metricLocation[1] = shiftedLocation[1];
					metricLocation[2] = shiftedLocation[2] + zShiftPerChild*column;
					
					metric.setCoordinates(metricLocation);
					    
					i++;
				}
			} else if (cShape == CollectionShape.SPHERE) {							
				double dlong = Math.PI*(3-Math.sqrt(5));
				double olong = 0.0;
				double dz    = 2.0/locations.size();
				double z     = 1 - (dz/2);
				Float[][] pt = new Float[locations.size()][3]; 
				double r = 0;
				float radius = CUBE_RADIUS_MULTIPLIER * (maxAllChildDimensions+separation) * locations.size();
				
				for (int k=0;k<locations.size();k++) {
					r = Math.sqrt(1-(z*z));
					pt[k][0] = coordinates[0] + radius*((float) (Math.cos(olong)*r));
					pt[k][1] = coordinates[1] + radius*((float) (Math.sin(olong)*r));
					pt[k][2] = coordinates[2] + radius*((float) z);
					z = z -dz;
					olong = olong +dlong;				
				}	
				
				int k=0;				
				for (Visual node : locations) {						
					//set the location						
					node.setCoordinates(pt[k]);							
					k++;
				}			
			} else if (cShape == CollectionShape.CUBE) {		
				//get the breakoff point for rows and columns
				int ibisCount = locations.size();
				int rows 		= (int)Math.ceil(Math.pow(ibisCount,  (1.0/3.0)));
				int columns 	= (int)Math.ceil(Math.pow(ibisCount, (1.0/3.0)));
				int layers		= (int)Math.floor(Math.pow(ibisCount, (1.0/3.0)));
							
				float xShiftPerChild = maxChildDimensions[0] + separation;
				float yShiftPerChild = maxChildDimensions[1] + separation;			
				float zShiftPerChild = maxChildDimensions[2] + separation;
				
				//Center the drawing around the location	
				Float[] shiftedLocation = new Float[3];
				shiftedLocation[0] = coordinates[0] - ((xShiftPerChild*rows   )-separation) * 0.5f;
				shiftedLocation[1] = coordinates[1] - ((yShiftPerChild*layers )-separation) * 0.5f;
				shiftedLocation[2] = coordinates[2] - ((zShiftPerChild*columns)-separation) * 0.5f;
				
				Float[] metricLocation = new Float[3];
				
				int row = 0, column = 0, layer = 0;
				for (Visual node : locations) {								
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
					
					node.setCoordinates(metricLocation);
					    
					row++;
				}
			}
		}
		
		for (Visual metric : metrics) {
			metric.setCoordinates(newCoordinates);
		}
		
		for (Visual link : links) {
			link.setCoordinates(newCoordinates);
		}
		
		
	}
	
	public void setRotation(Float[] newRotation) {
		rotation[0] = newRotation[0];
		rotation[1] = newRotation[1];
		rotation[2] = newRotation[2];
	}
	
	public void setDimensions(Float[] newDimensions) {
		dimensions[0] = newDimensions[0];
		dimensions[1] = newDimensions[1];
		dimensions[2] = newDimensions[2];		
	}
	
	public void setCollectionShape(CollectionShape newShape) {		
		cShape = newShape;
		constructDimensions();
	}
	
	public void setFoldState(FoldState newFoldState) {
		foldState = newFoldState;		
	}
	
	public void setMetricShape(MetricShape newShape) {
		mShape = newShape;
		for (Visual metric : metrics) {
			metric.setMetricShape(newShape);
		}		
	}
	
	public Float[] getCoordinates() {
		Float[] myCoordinates = new Float[3];
		myCoordinates[0] = coordinates[0];
		myCoordinates[1] = coordinates[1];
		myCoordinates[2] = coordinates[2];
		
		return myCoordinates;
	}
	
	public Float[] getDimensions() {
		Float[] myDimensions = new Float[3];
		myDimensions[0] = dimensions[0];
		myDimensions[1] = dimensions[1];
		myDimensions[2] = dimensions[2];
		
		return myDimensions;
	}
	
	public float getRadius() {
		return maxAllChildDimensions;
	}
	
	public void update() {
		for (Visual child : locations) {
			child.update();
		}
		for (Visual ibis : ibises) {
			ibis.update();
		}
		for (Visual metric : metrics) {
			metric.update();
		}
		for (Visual link : links) {
			link.update();
		}
	}
	
	public void drawThis(GL2 gl, int renderMode) {
		if (foldState == FoldState.UNFOLDED) {
			for (Visual ibis : locations) {
				ibis.drawThis(gl, renderMode);
			}		
			for (Visual ibis : ibises) {
				ibis.drawThis(gl, renderMode);
			}
			for (Visual metric : metrics) {
				metric.drawThis(gl, renderMode);
			}
			for (Visual link : links) {
				link.drawThis(gl, renderMode);
			}			
		} else {
			
		}		
	}
	
	protected void constructDimensions() {
		Float[] childDimensions;
		maxChildDimensions[0] = 0.0f;
		maxChildDimensions[1] = 0.0f;
		maxChildDimensions[2] = 0.0f;
		
		for (Visual child : locations) {
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
		
		maxAllChildDimensions = Math.max(Math.max(maxChildDimensions[0], maxChildDimensions[1]), maxChildDimensions[2]);
		
		if (cShape == CollectionShape.CITYSCAPE) {
			dimensions[0] = (maxChildDimensions[0]+separation) * (int) Math.ceil(Math.sqrt(locations.size()))-separation; 
			dimensions[1] = (maxChildDimensions[1]+separation);
			dimensions[2] = (maxChildDimensions[2]+separation) * (int) Math.floor(Math.sqrt(locations.size()))-separation;
		} else if (cShape == CollectionShape.SPHERE) {
			dimensions[0] = (maxAllChildDimensions+separation)*CUBE_RADIUS_MULTIPLIER * locations.size()*2f;
			dimensions[1] = (maxAllChildDimensions+separation)*CUBE_RADIUS_MULTIPLIER * locations.size()*2f;
			dimensions[2] = (maxAllChildDimensions+separation)*CUBE_RADIUS_MULTIPLIER * locations.size()*2f;
		} else if (cShape == CollectionShape.CUBE) {
			dimensions[0] = (maxChildDimensions[0]+separation) * (int) Math.ceil(Math.pow(locations.size(),(1.0/3.0)))-separation; 
			dimensions[1] = (maxChildDimensions[1]+separation) * (int) Math.floor(Math.pow(locations.size(),(1.0/3.0)))-separation;
			dimensions[2] = (maxChildDimensions[2]+separation) * (int) Math.ceil(Math.pow(locations.size(),(1.0/3.0)))-separation;
		}
	}
}
