package particles;

import java.awt.*;
import java.awt.event.*;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.GLUT;

public class Junglevision implements GLEventListener {
	private final static int MAX_PARTICLES = 10000;
	private final static float WORLD_LENGTH = 10.0f;
	private final static float WORLD_WIDTH = 10.0f;
	private final static float WORLD_HEIGHT = 10.0f;
	
	GL gl;
    GLU glu = new GLU();
    GLUT glut = new GLUT();
    MouseHandler mouseHandler;
    KeyHandler keyHandler;
    
    //Perspective variables 
    private double fovy, aspect, width, height, zNear, zFar;
    
    //View variables
    private float viewDist; 
    private Float3d viewTranslation, viewRotation;
    
    //picking
    private boolean pickRequest, updateRequest, recenterRequest;
    private Point pickPoint;
    private int selectedItem;
     
    //FPS counter
    private int framesPassed, fps;
    
    //Particles
    Timer timer;
    Particle[] particleList;
    
    //Solid Universe
    Visual[] visuals;

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
    	
		//Canvas Initialization
    	GLCanvas canvas = new GLCanvas(glCapabilities);    	
		canvas.addGLEventListener(this);
		
		//Add Mouse event listener
		mouseHandler = new MouseHandler(this);
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseHandler);
		canvas.addMouseWheelListener(mouseHandler);
		
		//Add key event listener
		keyHandler = new KeyHandler(this);
		canvas.addKeyListener(keyHandler);
		
		//Set up the window
		Frame frame = new Frame("Maarten's Skeleton");
		frame.add(canvas);
		frame.setSize(1800, 1100);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(0); }
			});
		frame.setVisible(true);
		
		//Initial perspective
		fovy = 45.0f; 
		aspect = (this.width / this.height); 
		zNear = 0.1f;
		zFar = 1500.0f;	
		
		//Initial view
		viewDist = -6;
		viewRotation = new Float3d();
		viewTranslation = new Float3d();		
		
		//Particle initializers		
		particleList = new Particle[MAX_PARTICLES];
		
		Float3d initialParticleLocation = new Float3d(0.0f, 0.2f, 0.0f);
		
		for (int i=0;i<MAX_PARTICLES;i++) {			
			particleList[i] = new Particle(initialParticleLocation);
		}
		
		timer = new Timer(this);
		new Thread(timer).start();
				
		//Additional initializations
		pickRequest = false;
		updateRequest = true;
		recenterRequest = false;
		pickPoint = new Point();
		visuals = new Visual[1];
		
		Float3d[] points = new Float3d[4];
		points[0] = new Float3d( -10f, 0f, -10f);
		points[1] = new Float3d( -10f, 0f,  10f);
		points[2] = new Float3d(  10f, 0f,  10f);
		points[3] = new Float3d(  10f, 0f, -10f);
		visuals[0] = new Plane(new Float3d(0f, 0f, 0f), points);
		
		new javax.swing.Timer(1000, fpsRecorder).start();
								
		FPSAnimator animator = new FPSAnimator(canvas,60);
		animator.start();
    }
    
    /**
     * Init() will be called when your program starts
     */
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL();
		
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
		
		//Culling
		gl.glEnable(GL.GL_CULL_FACE);
		
		//Enable Blending (needed for both Transparency and Anti-Aliasing
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);				
		gl.glEnable(GL.GL_BLEND);
		
		//Lighting test
		//gl.glEnable(GL.GL_LIGHT0);
	    //gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_COLOR_MATERIAL);
				
		//General hint for optimum color quality
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		
		//Enable Vertical Sync
		gl.setSwapInterval(1);
		
		//Set black as background color
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);	 
	    
	    //Initialize particles
	    initAllParticles();
				
		//Universe initializers
	    resetUniverse();	    
	    
	    //and set the matrix mode to the modelview matrix in the end
	    gl.glMatrixMode(GL.GL_MODELVIEW);
	    gl.glLoadIdentity();
	}
	
	void initAllParticles() {
		for (int i=0;i<MAX_PARTICLES;i++) {
			Float3d minLocation = new Float3d (-WORLD_WIDTH, 0.0f,        -WORLD_LENGTH);
			Float3d maxLocation = new Float3d ( WORLD_WIDTH, WORLD_HEIGHT, WORLD_LENGTH);
			particleList[i].init(minLocation, maxLocation);
		}
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
		gl.glTranslatef(viewTranslation.x, viewTranslation.y, viewDist);
		gl.glRotatef(viewRotation.x, 1,0,0);
		gl.glRotatef(viewRotation.y, 0,1,0);
	    
		//Draw the current state of the universe
		drawUniverse(gl, GL.GL_RENDER);
		
		//Draw particles
		drawParticles(gl, GL.GL_RENDER);
		
		//Draw the Heads Up Display
		drawHud(gl);
		
		//Start the rendering process so that it runs in parallel with the 
		//computations we need to do for the NEXT frame
		gl.glFlush();
		
		//Handle input (picking, recentering etc)
		if (pickRequest) {
			selectedItem = pick(gl, pickPoint);
			pickRequest = false;
		}		
	}
	
	private void drawUniverse(GL gl, int renderMode) {		
		//Plane		
		for (Visual v : visuals) {
			v.draw(gl, renderMode);
		}
				
		float alpha = 0.4f;
		
		float 	Xn = -0.5f*0.25f,
				Xp =  0.5f*0.25f,
				Yn = -0.5f*1.0f,
				Yp =  0.5f*1.0f,
				Zn = -0.5f*0.25f,
				Zp =  0.5f*0.25f;

		float Yf = 0.0f;
					
		Yf = (0.6f*1.0f)-(0.5f*1.0f);
		
		//The solid area
		gl.glBegin(GL.GL_QUADS);	
			gl.glColor3f(1.0f,0.0f,0.0f);
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
		
		/*
		//The transparent area			
		gl.glBegin(GL.GL_QUADS);
		gl.glColor4f(1.0f,0.0f,0.0f, alpha);
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
		*/
	}
	
	private void drawParticles(GL gl, int renderMode) {
		for (Particle p : particleList) {			
			p.draw(gl, renderMode);
		}
	}
		
	private void drawHud(GL gl) {
		framesPassed++;
		
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -1.0f);
		
		gl.glColor3f(1.0f,1.0f,1.0f);
		
		gl.glRasterPos2f(0.45f, -0.35f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "FPS: " + fps);				
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
	        3.0, 3.0, viewport, 0);
	    	    
	    //Multiply by the perspective
	    glu.gluPerspective(fovy, aspect, zNear, zFar);	    
	    
	    //Draw the models in selection mode
	    gl.glMatrixMode(GL.GL_MODELVIEW);	    
	    drawUniverse(gl, GL.GL_SELECT);
	    
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
		int depth;
		
		if (hits > 0) {
			selection = buffer[3];
			depth = buffer[1];
			
			for (int i=0; i<hits;i++) {
				if (buffer[i*4+1] < depth) {
					selection = buffer[i*4+3];
					depth = buffer[i*4+1];
				}
			}
		}
	    
	    return selection;
	}	

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

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		
	}
	
	public void setRotation(Float3d newViewRotation) {
		viewRotation = newViewRotation.clone();
	}
	
	public void setViewDist(float newViewDist) {
		viewDist = newViewDist;
	}
	
	public void doPickRequest(Point p) {
		pickRequest = true;
		pickPoint = p;
	}
	
	public void doUpdateRequest() {
		updateRequest = true;
	}
	
	public void doRecenterRequest() {
		recenterRequest = true;
	}
	
	public void doParticleMoves(Float fraction) {
		for (Particle p : particleList) {
			p.doMove(fraction);
		}		
	}
	
	public void increaseWind(Float3d increase) {
		for (Particle p : particleList) {
			p.alterExtraneousSpeed(increase);			
		}
	}
	
	public void increasePower(Float3d increase) {
		/*
		for (Particle p : particleList) {
			Float3d oldMinSpeed = p.getMinSpeed();
			Float3d oldMaxSpeed = p.getMaxSpeed();			
			
			Float3d newMinSpeed = new Float3d();
			Float3d newMaxSpeed = new Float3d();
			
			newMinSpeed.set(oldMinSpeed.x - increase.x, 
							oldMinSpeed.y + increase.y, 
							oldMinSpeed.z - increase.z);
			
			newMaxSpeed.set(oldMaxSpeed.x + increase.x, 
							oldMaxSpeed.y + increase.y, 
							oldMaxSpeed.z + increase.z);
			
			p.setSpeed(newMinSpeed,newMaxSpeed);
		}
		*/
	}
			
	public void resetUniverse() {
		
	}
	
    public static void main(String[] args) {
    	new Junglevision();		
	}

	ActionListener fpsRecorder = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			fps = framesPassed;
			framesPassed = 0;
		}
	};
}