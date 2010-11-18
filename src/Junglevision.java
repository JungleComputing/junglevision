
import java.awt.*;
import java.awt.event.*;
import java.nio.IntBuffer;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.FPSAnimator;

class Junglevision implements GLEventListener {
    GLU glu = new GLU();
    
    //Perspective variables 
    private double fovy, aspect, width, height, zNear, zFar;
    
    //View variables
    private float viewDist; 
    private Float[] viewTranslation, viewRotation;
    
    //picking
    private boolean pickRequest;
    private Point pickPoint;
    private int selectedItem;

    /**
     * Constructor for your program, this sets up the
     * window (Frame), creates a GLCanvas and starts the Animator
     */
    public Junglevision() {
    	//Standard capabilities
		GLCapabilities glCapabilities = new GLCapabilities();		
		glCapabilities.setDoubleBuffered(true);
		glCapabilities.setHardwareAccelerated(true);
		
		//Anti-Aliasing
		glCapabilities.setSampleBuffers(true);
		glCapabilities.setNumSamples(4);
    	
    	GLCanvas canvas = new GLCanvas(glCapabilities);    	
		canvas.addGLEventListener(this);
		
		//Add Mouse event listener
		MouseHandler mouseHandler = new MouseHandler(this);
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseHandler);
		canvas.addMouseWheelListener(mouseHandler);
		
		//Set up the window
		Frame frame = new Frame("Maarten's Skeleton");
		frame.add(canvas);
		frame.setSize(800, 600);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(0); }
			});
		frame.setVisible(true);
		
		//Initial perspective
		fovy = 45.0f; 
		aspect = (this.width / this.height); 
		zNear = 0.1f;
		zFar = 100.0f;	
		
		//Initial view
		viewDist = -6;
		viewRotation = new Float[3];
		viewTranslation = new Float[3];
		for (int i=0; i<3; i++) {
			viewRotation[i] = 0.0f;
			viewTranslation[i] = 0.0f;
		}
		
		//Additional initializations
		pickRequest = false;
		pickPoint = new Point();
		
		FPSAnimator animator = new FPSAnimator(canvas,60);
		animator.start();
    }
    
    /**
     * Init() will be called when your program starts
     */
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		
		//Shader Model
		gl.glShadeModel(GL.GL_SMOOTH);
		
		//Anti-Aliasing
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_POLYGON_SMOOTH); 
		gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
	    	    
	    //Depth testing
	    gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glClearDepth(1.0f);		
		
		//Enable Blending (needed for both Transparency and Anti-Aliasing
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);				
		gl.glEnable(GL.GL_BLEND);
				
		//General hint for optimum color quality
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		
		//Enable Vertical Sync
		gl.setSwapInterval(1);
		
		//Set black as background color
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	    
	    //and set the matrix mode to the modelview matrix in the end
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	    gl.glLoadIdentity();
	}

	/**
	 * display() will be called repeatedly by the Animator
	 * when Animator is done it will swap buffers and update
	 * the display.
	 */
	public void display(GLAutoDrawable drawable) {
	    GL gl = drawable.getGL();

	    //Added GL.GL_DEPTH_BUFFER_BIT
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	    
	    //Reset the modelview matrix
	    gl.glLoadIdentity();
	    
	    //Change the view according to mouse input
		gl.glTranslatef(viewTranslation[0], viewTranslation[1], viewDist);
		gl.glRotatef(viewRotation[0], 1,0,0);
		gl.glRotatef(viewRotation[1], 0,1,0);
		
		
		//Draw the current state of the universe
		drawUniverse(gl, GL.GL_RENDER, selectedItem);
		
		//Start the rendering process so that it runs in parallel with the 
		//computations we need to do for the NEXT frame
		gl.glFlush();
		
		//Handle input (picking etc)		
		if (pickRequest) {
			selectedItem = pick(gl, pickPoint);
			pickRequest = false;
			
			System.out.println("picked: " + selectedItem);
		}		
		
		//Update visuals
		
		//wait for, and show the output
		drawable.swapBuffers();
		
	}
	
	private void drawUniverse(GL gl, int renderMode, int selectedItem) {
		drawFakeNode(gl, renderMode, selectedItem);
		
		gl.glTranslatef( 1.5f, 0, 0);
		drawFakeNode(gl, renderMode, selectedItem);
		
		gl.glTranslatef( 0, 0, 1.5f);
		drawFakeNode(gl, renderMode, selectedItem);
		
		gl.glTranslatef(-1.5f, 0, 0);
		drawFakeNode(gl, renderMode, selectedItem);
	}
	
	private void drawFakeNode(GL gl, int renderMode, int selectedItem) {
		gl.glPushMatrix();
		
		if (renderMode == GL.GL_SELECT) gl.glLoadName(1);
		drawBar(gl, (float) Math.random(), 1.0f, 0.0f, 0.0f);
				
		gl.glTranslatef(0.3f, 0, 0);
		if (renderMode == GL.GL_SELECT) gl.glLoadName(2);
		drawBar(gl, (float) Math.random(), 0.0f, 1.0f, 0.0f);
		
		gl.glTranslatef(0, 0, 0.3f);
		if (renderMode == GL.GL_SELECT) gl.glLoadName(3);
		drawBar(gl, (float) Math.random(), 0.0f, 0.0f, 1.0f);
		
		gl.glTranslatef(-0.3f, 0, 0);
		if (renderMode == GL.GL_SELECT) gl.glLoadName(4);
		drawBar(gl, (float) Math.random(), 0.5f, 1.0f, 0.0f);
		
		gl.glPopMatrix();
	}
	
	private int pick(GL gl, Point pickPoint) {
		final int BUFSIZE = 512;
		
		int[] selectBuf = new int[BUFSIZE];
	    IntBuffer selectBuffer = BufferUtil.newIntBuffer(BUFSIZE);
	    int hits;
	    
	    //Save the current viewport
	    int viewport[] = new int[4];	    
	    gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

	    //Switch to selection mode
	    gl.glSelectBuffer(BUFSIZE, selectBuffer);
	    gl.glRenderMode(GL.GL_SELECT);

	    gl.glInitNames();
	    gl.glPushName(-1);

	    //Switch to Projection mode and save the current projection matrix
	    gl.glMatrixMode(GL.GL_PROJECTION);
	    gl.glPushMatrix();
	    gl.glLoadIdentity();
	    
	    // create 5x5 pixel picking region near cursor location
	    glu.gluPickMatrix((double) pickPoint.x,
	        (double) (viewport[3] - pickPoint.y), 
	        5.0, 5.0, viewport, 0);
	    	    
	    //Multiply by the perspective
	    glu.gluPerspective(fovy, aspect, zNear, zFar);	    
	    
	    //Draw the models in selection mode
	    gl.glMatrixMode(GL.GL_MODELVIEW);	    
	    drawUniverse(gl, GL.GL_SELECT, selectedItem);
	    
	    //Restore the original projection matrix
	    gl.glMatrixMode(GL.GL_PROJECTION);
	    gl.glPopMatrix();
	    
	    //Switch back to modelview and make sure there are no stragglers
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	    gl.glFlush();
	    
	    //Process the hits
	    hits = gl.glRenderMode(GL.GL_RENDER);
	    selectBuffer.get(selectBuf);
	    int selection = processHits(hits, selectBuf);	    
	    
	    return selection;
	}
	
	private int processHits(int hits, int buffer[]) {	
		int selection = -1;
		
	    if (hits > 0) {
	    	//select the last (top) item
	    	selection = buffer[3+4*(hits-1)];
	    }
	    
	    return selection;
	}
	
	protected void drawBar(GL gl, float length, float rC, float gC, float bC) {	
		//Save the current modelview matrix
		gl.glPushMatrix();
		
		//use nice variables, so that the ogl code is readable
		float o = 0.0f;			//(o)rigin
		float x = 0.25f;		//(x) maximum coordinate
		float y = 1.0f;		//(y) maximum coordinate
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
					
		float quad_color_r = rC;
		float quad_color_g = gC;
		float quad_color_b = bC;
						
		//Center the drawing startpoint
		gl.glTranslatef(-0.5f*x, 0.0f, -0.5f*z);		
		
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
	
	protected void drawTube(GL gl, float length, float rC, float gC, float bC) {
		//Save the current modelview matrix
		gl.glPushMatrix();
		
		final int SIDES = 12;
		
		float line_color_r = 0.8f;
		float line_color_g = 0.8f;
		float line_color_b = 0.8f;
		
		float quad_color_r = rC;
		float quad_color_g = gC;
		float quad_color_b = bC;
		float alpha = 0.4f;
		
		float radius = 0.25f /2;
		
		float f = 1.0f * length;
		
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
			glu.gluCylinder(qobj, radius, radius, 0.01f, SIDES, 1);
			
			gl.glTranslatef(0.0f, 0.0f, f);
			
			//Top disk
			gl.glColor3f(quad_color_r, quad_color_g, quad_color_b);
			glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
			
			//Edge of top disk
			gl.glColor3f(line_color_r, line_color_g, line_color_b);
			glu.gluCylinder(qobj, radius, radius, 0.01f, SIDES, 1);
		
		//The shadow Element				
			//Bottom disk left out, since it's the top disk of the solid
										
			//Sides
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);
			glu.gluCylinder(qobj, radius, radius, 1.0f-f, SIDES, 1);			
			
			//Edge of bottom disk also left out
						
			gl.glTranslatef(0.0f, 0.0f, 1.0f-f);
			
			//Top disk
			gl.glColor4f(quad_color_r, quad_color_g, quad_color_b, alpha);
			glu.gluDisk(qobj, 0.0, radius, SIDES, 1);
			
			//Edge of top disk
			gl.glColor4f(line_color_r, line_color_g, line_color_b, alpha);
			glu.gluCylinder(qobj, radius, radius, 0.01f, SIDES, 1);		
		
		//Cleanup
		glu.gluDeleteQuadric(qobj);
		
		//Restore the old modelview matrix
		gl.glPopMatrix();
	}

	/**
	 * reshape() specifies what happens when the user reshapes
	 * the window.
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		GL gl = drawable.getGL();
		
		//Set the new viewport
		gl.glViewport(0, 0, w, h);

		//Change to the projection mode
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();

		//Calculate and set the new perspective
		this.width = (double) w;
		this.height = (double) h;
		
		aspect = this.width / this.height;
		
		glu.gluPerspective(fovy, aspect, zNear, zFar);
		
		//Return to normal mode
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	/**
	 * Called by the drawable when the display mode or the
	 * display device associated with the GLAutoDrawable
	 * has changed. However, you may leave this method
	 * empty during your project.
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		
	}
	
	public void setRotation(Float[] newViewRotation) {
		viewRotation = newViewRotation;
	}
	
	public void setTranslation(Float[] newViewTranslation) {
		viewTranslation = newViewTranslation;
	}
	
	public void setViewDist(float newViewDist) {
		viewDist = newViewDist;
	}
	
	public void doPickRequest(Point p) {
		pickRequest = true;
		pickPoint = p;
	}

	/**
	 * Your program starts here
	 */
    public static void main(String[] args) {
    	new Junglevision();		
	}
}