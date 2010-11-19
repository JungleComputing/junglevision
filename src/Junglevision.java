import java.awt.*;
import java.awt.event.*;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.FPSAnimator;

class Junglevision implements GLEventListener {	
    GLU glu = new GLU();
    MouseHandler mouseHandler;
    
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
    private FakeLocationUpper location;

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
		
		//Additional initializations
		pickRequest = false;
		updateRequest = true;
		recenterRequest = false;
		pickPoint = new Point();
		namesToVisuals = new HashMap<Integer, FakeMetric>();
				
		//Universe initializers
		location = new FakeLocationUpper(this, glu, 100);
		Float[] initialLocation = {0.0f,0.0f,0.0f};
		location.setLocation(initialLocation);
		
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
		
		//Handle input (picking, recentering etc)
		if (pickRequest) {
			selectedItem = pick(gl, pickPoint);
			pickRequest = false;
			
			System.out.println("picked: " + selectedItem);
		}
		if (recenterRequest) {
			Float[] newCenter = namesToVisuals.get(selectedItem).getLocation();
			Float[] oldLocation = location.getLocation();
			Float[] newLocation = new Float[3];
			newLocation[0] = oldLocation[0] - newCenter[0];
			newLocation[1] = oldLocation[1] - newCenter[1];
			newLocation[2] = oldLocation[2] - newCenter[2];
			location.setLocation(newLocation);
			recenterRequest = false;
		}
		
		//Update visuals
		if (updateRequest) {
			location.update();
			updateRequest = false;
		}
		
		//wait for, and show the output
		drawable.swapBuffers();
		
	}
	
	private void drawUniverse(GL gl, int renderMode, int selectedItem) {
		location.drawThis(gl, renderMode, selectedItem);
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
	
	int registerGLName(FakeMetric metric) {
		int key = namesToVisuals.size();
		namesToVisuals.put(key, metric);
		return key;		
	}
	
	private int processHits(int hits, int buffer[]) {	
		int selection = -1;
		
	    if (hits > 0) {
	    	//select the last (top) item
	    	selection = buffer[3+4*(hits-1)];
	    }
	    
	    return selection;
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
	
	public Float[] getTranslation() {
		Float[] translation = new Float[3];
		translation[0] = viewTranslation[0];
		translation[1] = viewTranslation[1];
		translation[2] = viewTranslation[2];
		return translation;
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

	/**
	 * Your program starts here
	 */
    public static void main(String[] args) {
    	new Junglevision();		
	}
}