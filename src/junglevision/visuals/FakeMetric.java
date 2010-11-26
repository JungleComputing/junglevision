package junglevision.visuals;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import junglevision.Junglevision;

public class FakeMetric extends VisualAbstract implements Visual {
	private static final float WIDTH = 0.25f;
	private static final float HEIGHT = 1.00f;
	private GLU glu;
	
	private Float[] color;
	private float currentValue;
	private int glName;
	private int[] barPointer;
	
	FakeMetric(Junglevision jv, GLU glu, Float[] color) {
		super();
		
		this.glu = glu;		
		this.color = color;
		
		currentValue = 0.0f;
		barPointer = jv.getBarPointer();
		
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
			drawTube(gl, currentValue);
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
	}

	protected void drawBar(GL gl, float length, float maxLength) {	
		//Save the current modelview matrix
		gl.glPushMatrix();
		
		//Translate to the desired coordinates and rotate if desired
		gl.glTranslatef(location[0], location[1], location[2]);
		gl.glRotatef(rotation[0], 1.0f, 0.0f, 0.0f);
		gl.glRotatef(rotation[1], 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rotation[2], 0.0f, 0.0f, 1.0f);		
		
		if (maxLength == HEIGHT) {	
			int whichBar = (int) Math.floor(length*barPointer.length)/2;
			if (length >= 0.95f) {
				whichBar = (barPointer.length/2)-1;
			}
	
			gl.glColor4f(color[0], color[1], color[2], 1.0f);
			gl.glCallList(barPointer[(whichBar*2)]); 
			gl.glColor4f(color[0], color[1], color[2], 0.4f);
			gl.glCallList(barPointer[(whichBar*2)+1]);
		} else {			
			float alpha = 0.4f;
			 			
			float 	Xn = -0.5f*dimensions[0],
					Xp =  0.5f*dimensions[0],
					Yn = -0.5f*dimensions[1],
					Yp =  0.5f*dimensions[1],
					Zn = -0.5f*dimensions[2],
					Zp =  0.5f*dimensions[2];
	
			float Yf = 0.0f;
						
			Yf = (length*dimensions[1])-(0.5f*dimensions[1]);
			
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
		
		
		//Restore the old modelview matrix
		gl.glPopMatrix();
	}
	
	protected void drawTube(GL gl, float length) {
		//Save the current modelview matrix
		gl.glPushMatrix();
		
		final int SIDES = 12;
		final float EDGE_SIZE = 0.01f;
		
		//use nice variables, so that the ogl code is readable
		float o = 0.0f;			//(o)rigin
		float x = HEIGHT;		//(x) maximum coordinate
		float y = HEIGHT;			//(y) maximum coordinate
		float z = WIDTH;		//(z) maximum coordinate	
		float f = length * y; 	//(f)illed area
		float r = y - f;		//(r)est area (non-filled, up until the maximum) 
		float alpha = 0.4f;
		
		float line_color_r = 0.8f;
		float line_color_g = 0.8f;
		float line_color_b = 0.8f;
		
		float quad_color_r = color[0];
		float quad_color_g = color[1];
		float quad_color_b = color[2];
		
		float radius = Math.max(x,z) / 2;
		
		//Translate to the desired coordinates
		gl.glTranslatef(location[0], location[1], location[2]);
		
		//Center the drawing startpoint
		gl.glTranslatef(-0.5f*x, -0.5f*y, -0.5f*z);		

		//Rotate to align with the y axis instead of the default z axis
		gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
		
		//Make a new quadratic object
		GLUquadric qobj = glu.gluNewQuadric();
				
		//The Solid Element
			//Bottom disk
			gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);
			glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
						
			//Sides
			glu.gluCylinder(qobj, radius, radius, f, SIDES, 1);			
			
			//Edge of bottom disk
			gl.glColor3f(line_color_r, line_color_g, line_color_b);
			glu.gluCylinder(qobj, radius, radius, EDGE_SIZE, SIDES, 1);
			
			gl.glTranslatef(o, o, f);
			
			//Top disk
			gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);
			glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
			
			//Edge of top disk
			gl.glColor3f(line_color_r, line_color_g, line_color_b);
			glu.gluCylinder(qobj, radius, radius, EDGE_SIZE, SIDES, 1);
		
		//The shadow Element				
			//Bottom disk left out, since it's the top disk of the solid
										
			//Sides
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);
			glu.gluCylinder(qobj, radius, radius, r, SIDES, 1);			
			
			//Edge of bottom disk also left out
						
			gl.glTranslatef(o, o, r);
			
			//Top disk
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);
			glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
			
			//Edge of top disk
			gl.glColor4f(line_color_r, line_color_g, line_color_b, alpha);
			glu.gluCylinder(qobj, radius, radius, EDGE_SIZE, SIDES, 1);		
		
		//Cleanup
		glu.gluDeleteQuadric(qobj);
		
		//Restore the old modelview matrix
		gl.glPopMatrix();
	}
}
