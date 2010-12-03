package junglevision.visuals;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import junglevision.Junglevision;

public class LinkMetric extends VisualAbstract implements Visual {
	private static final float WIDTH = 0.1f;
	private static final float HEIGHT = 1.00f;
	private static final float ALPHA = 0.4f;
	private static final int ACCURACY = 20;
	
	private GLU glu;
	
	private Float[] color;
	private float currentValue;
	private int glName;
		
	//On-demand generated displaylists
	private int[] onDemandList;
	private boolean[] onDemandListsBuilt;
	int whichList;
	
	LinkMetric(Junglevision jv, GLU glu, Float[] color) {
		super();
		
		this.glu = glu;
		this.color = color;
		
		currentValue = 0.0f;		
		
		dimensions[0] = WIDTH;
		dimensions[1] = HEIGHT;
		dimensions[2] = WIDTH;
		
		onDemandList 		= new int[ACCURACY+1];
		onDemandListsBuilt 	= new boolean[ACCURACY+1];	
		whichList = 0;
		
		glName = jv.registerGLName(this);
	}
	
	public void init(GL gl) {
		onDemandList[0] = gl.glGenLists(ACCURACY+1);
		
		for (int i=0; i<ACCURACY+1; i++) {
			onDemandListsBuilt[i] = false;
			onDemandList[i] = onDemandList[0]+i;
		}
	}
	
	public void setLocation(Float[] newLocation) {
		
		for (int i=0; i<ACCURACY+1; i++) {
			onDemandListsBuilt[i] = false;
		}
		
		super.setLocation(newLocation);
	}
	
	public void drawThis(GL gl, int renderMode) {
		if (renderMode == GL.GL_SELECT) { gl.glLoadName(glName); }
		if (mShape == MetricShape.BAR) {
			drawBar(gl, currentValue, dimensions[1]);
		} else if (mShape == MetricShape.TUBE) {
			drawTube(gl, currentValue, dimensions[1]);
		}		
	}
	
	public void update() {
		if (Math.random()>0.5) {
			currentValue += Math.random()/10;
		} else {
			currentValue -= Math.random()/10;
		}
		
		currentValue = Math.max(0.0f, currentValue);
		currentValue = Math.min(1.0f, currentValue);
		
		whichList = (int) Math.floor(currentValue*ACCURACY);			
	}

	protected void drawBar(GL gl, float length, float maxLength) {
		//Save the current modelview matrix
		gl.glPushMatrix();
		
		//Translate to the desired coordinates and rotate if desired
		gl.glTranslatef(location[0], location[1], location[2]);
		gl.glRotatef(rotation[0], 1.0f, 0.0f, 0.0f);
		gl.glRotatef(rotation[1], 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rotation[2], 0.0f, 0.0f, 1.0f);
		
		if (onDemandListsBuilt[whichList]) {
			gl.glCallList(onDemandList[whichList]);
		} else {
			onDemandListsBuilt[whichList] = true;
			
			float alpha = ALPHA;
			
			float 	Xn = -0.5f*dimensions[0],
					Xp =  0.5f*dimensions[0],
					Yn = -0.5f*maxLength,
					Yp =  0.5f*maxLength,
					Zn = -0.5f*dimensions[2],
					Zp =  0.5f*dimensions[2];
	
			float Yf = 0.0f;
						
			Yf = (length*maxLength)-(0.5f*maxLength);
			
			gl.glNewList(onDemandList[whichList], GL.GL_COMPILE_AND_EXECUTE);
				
				//The solid area
				gl.glBegin(GL.GL_QUADS);	
					gl.glColor3f(color[0],color[1],color[2]);
					//TOP
					gl.glVertex3f( Xn, Yf, Zn);
					gl.glVertex3f( Xn, Yf, Zp);
					gl.glVertex3f( Xp, Yf, Zp);
					gl.glVertex3f( Xp, Yf, Zn);
					
					//BOTTOM
					gl.glVertex3f( Xn, Yn, Zn);
					gl.glVertex3f( Xp, Yn, Zn);
					gl.glVertex3f( Xp, Yn, Zp);
					gl.glVertex3f( Xn, Yn, Zp);
					
					//FRONT
					gl.glVertex3f( Xn, Yf, Zp);
					gl.glVertex3f( Xn, Yn, Zp);
					gl.glVertex3f( Xp, Yn, Zp);
					gl.glVertex3f( Xp, Yf, Zp);
					
					//BACK
					gl.glVertex3f( Xp, Yf, Zn);
					gl.glVertex3f( Xp, Yn, Zn);
					gl.glVertex3f( Xn, Yn, Zn);
					gl.glVertex3f( Xn, Yf, Zn);
					
					//LEFT
					gl.glVertex3f( Xn, Yf, Zn);
					gl.glVertex3f( Xn, Yn, Zn);
					gl.glVertex3f( Xn, Yn, Zp);
					gl.glVertex3f( Xn, Yf, Zp);
					
					//RIGHT
					gl.glVertex3f( Xp, Yf, Zp);
					gl.glVertex3f( Xp, Yn, Zp);
					gl.glVertex3f( Xp, Yn, Zn);
					gl.glVertex3f( Xp, Yf, Zn);
				gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//TOP
					gl.glVertex3f( Xn, Yf, Zn);
					gl.glVertex3f( Xn, Yf, Zp);
					gl.glVertex3f( Xp, Yf, Zp);
					gl.glVertex3f( Xp, Yf, Zn);
				gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//BOTTOM
					gl.glVertex3f( Xn, Yn, Zn);
					gl.glVertex3f( Xp, Yn, Zn);
					gl.glVertex3f( Xp, Yn, Zp);
					gl.glVertex3f( Xn, Yn, Zp);
				gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//FRONT
					gl.glVertex3f( Xn, Yf, Zp);
					gl.glVertex3f( Xn, Yn, Zp);
					gl.glVertex3f( Xp, Yn, Zp);
					gl.glVertex3f( Xp, Yf, Zp);
				gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//BACK
					gl.glVertex3f( Xp, Yf, Zn);
					gl.glVertex3f( Xp, Yn, Zn);
					gl.glVertex3f( Xn, Yn, Zn);
					gl.glVertex3f( Xn, Yf, Zn);
				gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//LEFT
					gl.glVertex3f( Xn, Yf, Zn);
					gl.glVertex3f( Xn, Yn, Zn);
					gl.glVertex3f( Xn, Yn, Zp);
					gl.glVertex3f( Xn, Yf, Zp);
				gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//RIGHT
					gl.glVertex3f( Xp, Yf, Zp);
					gl.glVertex3f( Xp, Yn, Zp);
					gl.glVertex3f( Xp, Yn, Zn);
					gl.glVertex3f( Xp, Yf, Zn);
				gl.glEnd();
				
				//The transparent area			
				gl.glBegin(GL.GL_QUADS);
				gl.glColor4f(color[0],color[1],color[2], alpha);
					//TOP
					gl.glVertex3f( Xn, Yp, Zn);
					gl.glVertex3f( Xn, Yp, Zp);
					gl.glVertex3f( Xp, Yp, Zp);
					gl.glVertex3f( Xp, Yp, Zn);
					
					//BOTTOM LEFT OUT
					//gl.glVertex3f( Xn, Yn, Zn);
					//gl.glVertex3f( Xp, Yn, Zn);
					//gl.glVertex3f( Xp, Yn, Zp);
					//gl.glVertex3f( Xn, Yn, Zp);
					
					//FRONT
					gl.glVertex3f( Xn, Yp, Zp);
					gl.glVertex3f( Xn, Yf, Zp);
					gl.glVertex3f( Xp, Yf, Zp);
					gl.glVertex3f( Xp, Yp, Zp);
					
					//BACK
					gl.glVertex3f( Xp, Yp, Zn);
					gl.glVertex3f( Xp, Yf, Zn);
					gl.glVertex3f( Xn, Yf, Zn);
					gl.glVertex3f( Xn, Yp, Zn);
					
					//LEFT
					gl.glVertex3f( Xn, Yp, Zn);
					gl.glVertex3f( Xn, Yf, Zn);
					gl.glVertex3f( Xn, Yf, Zp);
					gl.glVertex3f( Xn, Yp, Zp);
					
					//RIGHT
					gl.glVertex3f( Xp, Yp, Zp);
					gl.glVertex3f( Xp, Yf, Zp);
					gl.glVertex3f( Xp, Yf, Zn);
					gl.glVertex3f( Xp, Yp, Zn);
				gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//TOP
					gl.glVertex3f( Xn, Yp, Zn);
					gl.glVertex3f( Xn, Yp, Zp);
					gl.glVertex3f( Xp, Yp, Zp);
					gl.glVertex3f( Xp, Yp, Zn);
				gl.glEnd();
				
				//gl.glBegin(GL.GL_LINE_LOOP);
					//gl.glColor3f(0.8f,0.8f,0.8f);
					//BOTTOM LEFT OUT
					//gl.glVertex3f( Xn, Yn, Zn);
					//gl.glVertex3f( Xp, Yn, Zn);
					//gl.glVertex3f( Xp, Yn, Zp);
					//gl.glVertex3f( Xn, Yn, Zp);
				//gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//FRONT
					gl.glVertex3f( Xn, Yp, Zp);
					gl.glVertex3f( Xn, Yf, Zp);
					gl.glVertex3f( Xp, Yf, Zp);
					gl.glVertex3f( Xp, Yp, Zp);
				gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//BACK
					gl.glVertex3f( Xp, Yp, Zn);
					gl.glVertex3f( Xp, Yf, Zn);
					gl.glVertex3f( Xn, Yf, Zn);
					gl.glVertex3f( Xn, Yp, Zn);
				gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//LEFT
					gl.glVertex3f( Xn, Yp, Zn);
					gl.glVertex3f( Xn, Yf, Zn);
					gl.glVertex3f( Xn, Yf, Zp);
					gl.glVertex3f( Xn, Yp, Zp);
				gl.glEnd();
				
				gl.glBegin(GL.GL_LINE_LOOP);
					gl.glColor3f(0.8f,0.8f,0.8f);
					//RIGHT
					gl.glVertex3f( Xp, Yp, Zp);
					gl.glVertex3f( Xp, Yf, Zp);
					gl.glVertex3f( Xp, Yf, Zn);
					gl.glVertex3f( Xp, Yp, Zn);
				gl.glEnd();
			}	

		gl.glEndList();
		
		//Restore the old modelview matrix
		gl.glPopMatrix();
	}
	
	protected void drawTube(GL gl, float length, float maxLength) {
		//Save the current modelview matrix
		gl.glPushMatrix();
		
		//Translate to the desired coordinates and rotate if desired
		gl.glTranslatef(location[0], location[1], location[2]);
		gl.glRotatef(rotation[0], 1.0f, 0.0f, 0.0f);
		gl.glRotatef(rotation[1], 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rotation[2], 0.0f, 0.0f, 1.0f);
		
		gl.glRotatef(-90f, 1.0f, 0.0f, 0.0f);
		
		if (onDemandListsBuilt[whichList]) {
			gl.glCallList(onDemandList[whichList]);
		} else {		
			final int SIDES = 12;
			final float EDGE_SIZE = 0.01f;			
			
			float alpha = ALPHA;
			 			
			float 	Yn = -0.5f*maxLength,
					Yp =  0.5f*maxLength;
	
			float Yf = (length*maxLength)-(0.5f*maxLength);
			
			float radius = Math.max(dimensions[0], dimensions[2]) / 2;
			
			//On-demand generated list			
			onDemandListsBuilt[whichList] = true;
			gl.glNewList(onDemandList[whichList], GL.GL_COMPILE_AND_EXECUTE);				
				//Make a new quadratic object
				GLUquadric qobj = glu.gluNewQuadric();
						
				//The Solid Element
					gl.glTranslatef(0.0f, Yn, 0.0f);
					
					//Bottom disk
					gl.glColor3f(color[0], color[1], color[2]);
					glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
								
					//Sides
					glu.gluCylinder(qobj, radius, radius, Yf, SIDES, 1);			
					
					//Edge of bottom disk
					gl.glColor3f(0.8f,0.8f,0.8f);
					glu.gluCylinder(qobj, radius, radius, EDGE_SIZE, SIDES, 1);
					
					gl.glTranslatef(0.0f, Yf, 0.0f);
					
					//Top disk
					gl.glColor3f(color[0], color[1], color[2]);
					glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
					
					//Edge of top disk
					gl.glColor3f(0.8f,0.8f,0.8f);
					glu.gluCylinder(qobj, radius, radius, EDGE_SIZE, SIDES, 1);
				
				//The shadow Element				
					//Bottom disk left out, since it's the top disk of the solid
												
					//Sides
					gl.glColor4f(color[0], color[1], color[2], alpha);
					glu.gluCylinder(qobj, radius, radius, Yp-Yf, SIDES, 1);			
					
					//Edge of bottom disk also left out
								
					gl.glTranslatef(0.0f, Yp-Yf, 0.0f);
					
					//Top disk
					gl.glColor4f(color[0], color[1], color[2], alpha);
					glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
					
					//Edge of top disk
					gl.glColor4f(0.8f,0.8f,0.8f, alpha);
					glu.gluCylinder(qobj, radius, radius, EDGE_SIZE, SIDES, 1);		
				
				//Cleanup
				glu.gluDeleteQuadric(qobj);
			gl.glEndList();
		}
		
		//Restore the old modelview matrix
		gl.glPopMatrix();
	}
}
