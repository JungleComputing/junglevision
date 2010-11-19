import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class FakeMetric {
	private GLU glu;
	
	public enum Shape { BAR, TUBE }  
	private Float[] color;
	private Float[] location;
	private Shape currentShape;
	private float currentValue;
	private int glName;
	
	FakeMetric(Junglevision jv, GLU glu, Float[] color) {
		this.glu = glu;		
		this.location = new Float[3];
		this.color = color;
		this.currentShape = Shape.BAR;
		this.currentValue = 0.0f;
		
		glName = jv.registerGLName(this);
	}
	
	public void drawThis(GL gl, int renderMode, int selectedItem) {
		if (renderMode == GL.GL_SELECT) { gl.glLoadName(glName); }
		if (currentShape == Shape.BAR) {
			drawBar(gl, currentValue);
		} else if (currentShape == Shape.TUBE) {
			drawTube(gl, currentValue);
		}		
	}
	
	public void setLocation(Float[] newLocation) {
		this.location[0] = newLocation[0];
		this.location[1] = newLocation[1];
		this.location[2] = newLocation[2];		
	}
	
	public Float[] getLocation() {
		return location;
	}
	
	public void setShape(Shape newShape) {
		this.currentShape = newShape;
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

	protected void drawBar(GL gl, float length) {	
		//Save the current modelview matrix
		gl.glPushMatrix();
		
		//use nice variables, so that the ogl code is readable
		float o = 0.0f;			//(o)rigin
		float x = 0.25f;		//(x) maximum coordinate
		float y = 1.0f;			//(y) maximum coordinate
		float z = 0.25f;		//(z) maximum coordinate	
		float f = length * y; 	//(f)illed area
		float r = y - f;		//(r)est area (non-filled, up until the maximum) 
		float alpha = 0.4f;
		
		//Transparency
		float lineAlpha = alpha;
		
		//Color for the lines around the box
		float line_color_r = 0.8f;
		float line_color_g = 0.8f;
		float line_color_b = 0.8f;
					
		float quad_color_r = color[0];
		float quad_color_g = color[1];
		float quad_color_b = color[2];
								
		//Translate to the desired coordinates
		gl.glTranslatef(location[0], location[1], location[2]);
		
		//Center the drawing startpoint
		gl.glTranslatef(-0.5f*x, -0.5f*y, -0.5f*z);	
		
		//The solid Element
			gl.glBegin(GL.GL_LINE_LOOP);
				//TOP of filled area
				gl.glColor3f(line_color_r,line_color_g,line_color_b);			
				gl.glVertex3f( x, f, o);			
				gl.glVertex3f( o, f, o);			
				gl.glVertex3f( o, f, z);			
				gl.glVertex3f( x, f, z);			
			gl.glEnd();		
			
			gl.glBegin(GL.GL_LINE_LOOP);
				//BOTTOM
				gl.glColor3f(line_color_r,line_color_g,line_color_b);			
				gl.glVertex3f( x, o, z);			
				gl.glVertex3f( o, o, z);			
				gl.glVertex3f( o, o, o);			
				gl.glVertex3f( x, o, o);			
			gl.glEnd();	
			
			gl.glBegin(GL.GL_LINE_LOOP);
				//FRONT
				gl.glColor3f(line_color_r,line_color_g,line_color_b);			
				gl.glVertex3f( x, f, z);			
				gl.glVertex3f( o, f, z);			
				gl.glVertex3f( o, o, z);			
				gl.glVertex3f( x, o, z);			
			gl.glEnd();
			
			gl.glBegin(GL.GL_LINE_LOOP);
				//BACK
				gl.glColor3f(line_color_r,line_color_g,line_color_b);			
				gl.glVertex3f( x, o, o);			
				gl.glVertex3f( o, o, o);			
				gl.glVertex3f( o, f, o);			
				gl.glVertex3f( x, f, o);			
			gl.glEnd();	
			
			gl.glBegin(GL.GL_LINE_LOOP);
				//LEFT
				gl.glColor3f(line_color_r,line_color_g,line_color_b);			
				gl.glVertex3f( o, f, z);			
				gl.glVertex3f( o, f, o);			
				gl.glVertex3f( o, o, o);			
				gl.glVertex3f( o, o, z);			
			gl.glEnd();
			
			gl.glBegin(GL.GL_LINE_LOOP);
				//RIGHT
				gl.glColor3f(line_color_r,line_color_g,line_color_b);			
				gl.glVertex3f( x, f, o);			
				gl.glVertex3f( x, f, z);			
				gl.glVertex3f( x, o, z);			
				gl.glVertex3f( x, o, o);
			gl.glEnd();
				
			gl.glBegin(GL.GL_QUADS);		
				//TOP
				gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);			
				gl.glVertex3f( x, f, o);			
				gl.glVertex3f( o, f, o);			
				gl.glVertex3f( o, f, z);			
				gl.glVertex3f( x, f, z);
				
				//BOTTOM
				gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);			
				gl.glVertex3f( x, o, z);			
				gl.glVertex3f( o, o, z);			
				gl.glVertex3f( o, o, o);			
				gl.glVertex3f( x, o, o);
				
				//FRONT
				gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);			
				gl.glVertex3f( x, f, z);			
				gl.glVertex3f( o, f, z);			
				gl.glVertex3f( o, o, z);			
				gl.glVertex3f( x, o, z);
				
				//BACK
				gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);			
				gl.glVertex3f( x, o, o);			
				gl.glVertex3f( o, o, o);			
				gl.glVertex3f( o, f, o);			
				gl.glVertex3f( x, f, o);
				
				//LEFT
				gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);			
				gl.glVertex3f( o, f, z);			
				gl.glVertex3f( o, f, o);			
				gl.glVertex3f( o, o, o);			
				gl.glVertex3f( o, o, z);
				
				//RIGHT
				gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);			
				gl.glVertex3f( x, f, o);			
				gl.glVertex3f( x, f, z);			
				gl.glVertex3f( x, o, z);			
				gl.glVertex3f( x, o, o);
			gl.glEnd();	
		
		//The shadow (nonfilled) element		
		gl.glTranslatef(0.0f, f, 0.0f);		
		
		gl.glBegin(GL.GL_LINE_LOOP);
			//TOP of shadow area
			gl.glColor4f(line_color_r,line_color_g,line_color_b, lineAlpha);			
			gl.glVertex3f( x, r, o);			
			gl.glVertex3f( o, r, o);			
			gl.glVertex3f( o, r, z);			
			gl.glVertex3f( x, r, z);			
		gl.glEnd();		
		
		//Bottom left out, since it's the top of the solid area
		
		gl.glBegin(GL.GL_LINE_LOOP);
			//FRONT
			gl.glColor4f(line_color_r,line_color_g,line_color_b, lineAlpha);			
			gl.glVertex3f( x, r, z);			
			gl.glVertex3f( o, r, z);			
			gl.glVertex3f( o, o, z);			
			gl.glVertex3f( x, o, z);			
		gl.glEnd();
		
		gl.glBegin(GL.GL_LINE_LOOP);
			//BACK
			gl.glColor4f(line_color_r,line_color_g,line_color_b, lineAlpha);			
			gl.glVertex3f( x, o, o);			
			gl.glVertex3f( o, o, o);			
			gl.glVertex3f( o, r, o);			
			gl.glVertex3f( x, r, o);			
		gl.glEnd();	
		
		gl.glBegin(GL.GL_LINE_LOOP);
			//LEFT
			gl.glColor4f(line_color_r,line_color_g,line_color_b, lineAlpha);			
			gl.glVertex3f( o, r, z);			
			gl.glVertex3f( o, r, o);			
			gl.glVertex3f( o, o, o);			
			gl.glVertex3f( o, o, z);			
		gl.glEnd();
		
		gl.glBegin(GL.GL_LINE_LOOP);
			//RIGHT
			gl.glColor4f(line_color_r,line_color_g,line_color_b, lineAlpha);			
			gl.glVertex3f( x, r, o);			
			gl.glVertex3f( x, r, z);			
			gl.glVertex3f( x, o, z);			
			gl.glVertex3f( x, o, o);
		gl.glEnd();
			
		gl.glBegin(GL.GL_QUADS);		
			//TOP
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);			
			gl.glVertex3f( x, r, o);			
			gl.glVertex3f( o, r, o);			
			gl.glVertex3f( o, r, z);			
			gl.glVertex3f( x, r, z);
			
			//BOTTOM left out
			
			//FRONT
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);			
			gl.glVertex3f( x, r, z);			
			gl.glVertex3f( o, r, z);			
			gl.glVertex3f( o, o, z);			
			gl.glVertex3f( x, o, z);
			
			//BACK
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);			
			gl.glVertex3f( x, o, o);			
			gl.glVertex3f( o, o, o);			
			gl.glVertex3f( o, r, o);			
			gl.glVertex3f( x, r, o);
			
			//LEFT
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);			
			gl.glVertex3f( o, r, z);			
			gl.glVertex3f( o, r, o);			
			gl.glVertex3f( o, o, o);			
			gl.glVertex3f( o, o, z);
			
			//RIGHT
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);			
			gl.glVertex3f( x, r, o);			
			gl.glVertex3f( x, r, z);			
			gl.glVertex3f( x, o, z);			
			gl.glVertex3f( x, o, o);
		gl.glEnd();
		
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
		float x = 0.25f;		//(x) maximum coordinate
		float y = 1.0f;			//(y) maximum coordinate
		float z = 0.25f;		//(z) maximum coordinate	
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
