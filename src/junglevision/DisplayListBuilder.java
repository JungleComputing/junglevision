package junglevision;
import javax.media.opengl.GL;


public class DisplayListBuilder {
	private static final int ACCURACY = 20;
	
	private int[] barPointer;
	
	public DisplayListBuilder(GL gl) {
		barPointer = new int[ACCURACY*2];
		buildBars(gl, ACCURACY);
	}
	
	public int[] getBarPointers() {
		return barPointer;
	}
	
	private void buildBars(GL gl, int amount) {
		final float WIDTH = 0.25f;
		final float HEIGHT = 1.0f;
		
		float 	Xn = -0.5f*WIDTH,
				Xp =  0.5f*WIDTH,
				Yn = -0.5f*HEIGHT,
				Yp =  0.5f*HEIGHT,
				Zn = -0.5f*WIDTH,
				Zp =  0.5f*WIDTH;
		
		float Yf = 0.0f;
				
		barPointer[0] = gl.glGenLists(amount*2);
		
		for (int i=0; i<(amount*2); i+=2) {
			barPointer[i]   = barPointer[0]+i;
			barPointer[i+1] = barPointer[0]+i+1;
			
			Yf = ((HEIGHT/amount)*(i/2))-(0.5f*HEIGHT);
			
			//The solid area
			gl.glNewList(barPointer[i], GL.GL_COMPILE);
				gl.glBegin(GL.GL_QUADS);					
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
			gl.glEndList();	
			
			//The transparent area
			gl.glNewList(barPointer[i+1], GL.GL_COMPILE);
				gl.glBegin(GL.GL_QUADS);					
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
			gl.glEndList();
		}
	}
}
