package junglevision.visuals;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import junglevision.DisplayListBuilder;
import junglevision.Junglevision;

public class FakeMetric extends VisualAbstract implements Visual {
	private static final float WIDTH = 0.25f;
	private static final float HEIGHT = 1.00f;
	private GLU glu;
	
	private Float[] color;
	private float currentValue;
	private int glName;
	private int[] barPointer;
	private int[] barAndOutlinePointer;
	
	private DisplayListBuilder.DisplayList currentDL;
	
	FakeMetric(Junglevision jv, GLU glu, Float[] color) {
		super();
		
		this.glu = glu;
		this.color = color;
		
		currentValue = 0.0f;		
		
		barAndOutlinePointer = jv.getDisplayListPointer(DisplayListBuilder.DisplayList.BAR_AND_OUTLINE);
		barPointer = jv.getDisplayListPointer(DisplayListBuilder.DisplayList.BAR);
		currentDL = DisplayListBuilder.DisplayList.BAR;
		
		dimensions[0] = WIDTH;
		dimensions[1] = HEIGHT;
		dimensions[2] = WIDTH;
				
		glName = jv.registerGLName(this);
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
		//listBuilt = false;	
		
		if (Math.random()>0.5) {
			currentValue += Math.random()/10;
		} else {
			currentValue -= Math.random()/10;
		}
		
		currentValue = Math.max(0.0f, currentValue);
		currentValue = Math.min(1.0f, currentValue);
	}

	protected void drawBar(GL gl, float length, float maxLength) {
		//Save the current modelview matrix
		gl.glPushMatrix();
		
		//Translate to the desired coordinates and rotate if desired
		gl.glTranslatef(location[0], location[1], location[2]);
		gl.glRotatef(rotation[0], 1.0f, 0.0f, 0.0f);
		gl.glRotatef(rotation[1], 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rotation[2], 0.0f, 0.0f, 1.0f);		
		
		if (currentDL == DisplayListBuilder.DisplayList.BAR_AND_OUTLINE && maxLength == HEIGHT) {	//BAR AND OUTLINE
			int whichBar = (int) Math.floor(length*barAndOutlinePointer.length)/2;
			if (length >= 0.95f) {
				whichBar = (barAndOutlinePointer.length/2)-1;
			}
	
			gl.glColor4f(color[0], color[1], color[2], 1.0f);
			gl.glCallList(barAndOutlinePointer[(whichBar*2)]); 
			gl.glColor4f(color[0], color[1], color[2], 0.4f);
			gl.glCallList(barAndOutlinePointer[(whichBar*2)+1]);
		} else if (currentDL == DisplayListBuilder.DisplayList.BAR && maxLength == HEIGHT) {	//BAR
			int whichBar = (int) Math.floor(length*barPointer.length);
			if (length >= 0.95f) {
				whichBar = (barPointer.length)-1;
			}
	
			gl.glColor4f(color[0], color[1], color[2], 1.0f);
			gl.glCallList(barPointer[whichBar]);
		} else {
			
		}
		
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
				
		final int SIDES = 12;
		final float EDGE_SIZE = 0.01f;			
		
		float alpha = 0.4f;
		 			
		float 	Yn = -0.5f*maxLength,
				Yp =  0.5f*maxLength;

		float Yf = 0.0f;
					
		Yf = (length*maxLength)-(0.5f*maxLength);
		
		float quad_color_r = color[0];
		float quad_color_g = color[1];
		float quad_color_b = color[2];
		
		float radius = dimensions[0] / 2;
							
		//Make a new quadratic object
		GLUquadric qobj = glu.gluNewQuadric();
				
		//The Solid Element
			gl.glTranslatef(0.0f, Yn, 0.0f);
			
			//Bottom disk
			gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);
			glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
						
			//Sides
			glu.gluCylinder(qobj, radius, radius, Yf, SIDES, 1);			
			
			//Edge of bottom disk
			gl.glColor3f(0.8f,0.8f,0.8f);
			glu.gluCylinder(qobj, radius, radius, EDGE_SIZE, SIDES, 1);
			
			gl.glTranslatef(0.0f, Yf, 0.0f);
			
			//Top disk
			gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);
			glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
			
			//Edge of top disk
			gl.glColor3f(0.8f,0.8f,0.8f);
			glu.gluCylinder(qobj, radius, radius, EDGE_SIZE, SIDES, 1);
		
		//The shadow Element				
			//Bottom disk left out, since it's the top disk of the solid
										
			//Sides
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);
			glu.gluCylinder(qobj, radius, radius, Yp-Yf, SIDES, 1);			
			
			//Edge of bottom disk also left out
						
			gl.glTranslatef(0.0f, Yp-Yf, 0.0f);
			
			//Top disk
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);
			glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
			
			//Edge of top disk
			gl.glColor4f(0.8f,0.8f,0.8f, alpha);
			glu.gluCylinder(qobj, radius, radius, EDGE_SIZE, SIDES, 1);		
		
		//Cleanup
		glu.gluDeleteQuadric(qobj);
		
		//Restore the old modelview matrix
		gl.glPopMatrix();
	}
}
