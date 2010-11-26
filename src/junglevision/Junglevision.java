package junglevision;

import java.awt.*;
import java.awt.event.*;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import junglevision.visuals.FakeLink;
import junglevision.visuals.FakeLocationUpper;
import junglevision.visuals.FakeMetric;
import junglevision.visuals.Visual;


import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.GLUT;

public class Junglevision implements GLEventListener {
	private final static int MAX_NUMBER_OF_CHILDREN = 16;
	private final static int MAX_NUMBER_OF_LINKS = 300;
	private final static int MAX_METRICS_PER_LINK = 3;
	
    GLU glu = new GLU();
    GLUT glut = new GLUT();
    MouseHandler mouseHandler;
    KeyHandler keyHandler;
    
    //Perspective variables 
    private double fovy, aspect, width, height, zNear, zFar;
    
    //View variables
    private float viewDist; 
    private Float[] viewTranslation, viewRotation;
    
    //picking
    private boolean pickRequest, updateRequest, recenterRequest;
    private Point pickPoint;
    private int selectedItem;
    private HashMap<Integer, FakeMetric> namesToVisuals;
    
    //Universe
    private int[] barPointer;
    private FakeLocationUpper location;
    private ArrayList<Visual> visualRegistry;
    private ArrayList<FakeLink> linkList;
    
    //Location
    Mover m;
    
    //FPS counter
    private int framesPassed, fps;

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
		zFar = 400.0f;	
		
		//Initial view
		viewDist = -6;
		viewRotation = new Float[3];
		viewTranslation = new Float[3];
		for (int i=0; i<3; i++) {
			viewRotation[i] = 0.0f;
			viewTranslation[i] = 0.0f;
		}
		
		
		//Universe initializers
		visualRegistry = new ArrayList<Visual>();
		linkList = new ArrayList<FakeLink>();
		
		//Additional initializations
		pickRequest = false;
		updateRequest = true;
		recenterRequest = false;
		pickPoint = new Point();
		namesToVisuals = new HashMap<Integer, FakeMetric>();
		new javax.swing.Timer(1000, fpsRecorder).start();
				
		this.m = new Mover();
		
		//Visual updater definition
		FakeUpdater updater = new FakeUpdater(this);
		new Thread(updater).start();		
		
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
	    
	    //Initialize display lists
	    DisplayListBuilder listBuilder = new DisplayListBuilder(gl);
	    barPointer = listBuilder.getBarPointers();
				
		//Universe initializers
	    resetUniverse();
		//location = new FakeLocationUpper(this, glu, MAX_NUMBER_OF_CHILDREN);
		//location.setLocation(m.getCurrentLocation());
		//createLinks();
	    
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
	    		
		//Draw the current state of the universe
		drawUniverse(gl, GL.GL_RENDER);
		
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
		if (recenterRequest) {
			if (namesToVisuals.get(selectedItem) != null) {
				Float[] newCenter = namesToVisuals.get(selectedItem).getLocation();
				m.moveTo(newCenter);
			}
			
			recenterRequest = false;
		}
		
		//Update visuals
		if (m.locationChanged()) {
			location.setLocation(m.getCurrentLocation().clone());
			for (FakeLink link : linkList) {
				link.setLocation(m.getCurrentLocation().clone());
			}
		}
		
		if (updateRequest) {
			location.update();	
			for (FakeLink link : linkList) {
				link.update();
			}
			updateRequest = false;
		}
	}
	
	private void drawUniverse(GL gl, int renderMode) {
		//Reset the modelview matrix
	    gl.glLoadIdentity();
	    
	    //Change the view according to mouse input
		gl.glTranslatef(viewTranslation[0], viewTranslation[1], viewDist);
		gl.glRotatef(viewRotation[0], 1,0,0);
		gl.glRotatef(viewRotation[1], 0,1,0);
		
		location.drawThis(gl, renderMode);
		
		for (FakeLink link : linkList) {
			link.drawThis(gl, renderMode);
		}
	}
	
	public void registerVisual(Visual newVisual) {
		visualRegistry.add(newVisual);
	}
	
	private void createLinks() {
		linkList.clear();
		int numberOfLinks = (int) (Math.min(MAX_NUMBER_OF_LINKS, ((visualRegistry.size()*(visualRegistry.size()-1))/2)) );		
		
		for (int i=0; i<numberOfLinks; i++) {
			Visual source = null, destination = source;
			FakeLink newLink;
			
			do {
				source 		= visualRegistry.get(Math.min( visualRegistry.size(),(int) (Math.random()*visualRegistry.size()))); 
				destination	= visualRegistry.get(Math.min( visualRegistry.size(),(int) (Math.random()*visualRegistry.size())));
				newLink = new FakeLink(this, glu, (int) (Math.random()*MAX_METRICS_PER_LINK), source, destination);
			} while (source == destination || linkList.contains(newLink));
			
			linkList.add(newLink);
			
			newLink.setMetricShape(Visual.MetricShape.TUBE);
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
	
	public int registerGLName(FakeMetric metric) {
		int key = namesToVisuals.size();
		namesToVisuals.put(key, metric);
		return key;		
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
	
	public void setRotation(Float[] newViewRotation) {
		viewRotation = newViewRotation;
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
	
	public int[] getBarPointer() {
		return barPointer;
	}
	
	public void resetUniverse() {
		visualRegistry.clear();
		
		location = new FakeLocationUpper(this, glu, MAX_NUMBER_OF_CHILDREN);
		location.setLocation(m.getCurrentLocation().clone());
		createLinks();
		for (FakeLink link : linkList) {
			link.setLocation(m.getCurrentLocation().clone());
		}
		
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