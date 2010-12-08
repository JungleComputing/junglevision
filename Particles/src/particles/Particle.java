package particles;

import javax.media.opengl.GL;

public class Particle {
	Float3d location, minLocation, maxLocation, initialLocation, 
			speed, minInitialSpeed, maxInitialSpeed,
			extraneousSpeed, down;
	
	float gravity;
	
    //display list
    int list;
    boolean listBuilt = false;
	
	public Particle(Float3d initialLocation) {		
		this.minLocation = new Float3d();
		this.maxLocation = new Float3d();	
		
		this.initialLocation = initialLocation.clone();
		this.location 		 = initialLocation.clone();
		
		speed				= new Float3d();		
		minInitialSpeed 	= new Float3d(-0.2f, 0.4f,-0.2f);		
		maxInitialSpeed 	= new Float3d( 0.2f, 0.5f, 0.2f);
		
		extraneousSpeed = new Float3d();
		
		down = new Float3d(0f, -1.0f, 0f);		
		gravity = 0.981f;
	}
		
	public void init(Float3d minLocation, Float3d maxLocation) {
		this.minLocation = minLocation.clone();
		this.maxLocation = maxLocation.clone();		
		this.location = initialLocation.clone();
		
		speed.set(((float)Math.random()*(maxInitialSpeed.x - minInitialSpeed.x)) + minInitialSpeed.x,
				  ((float)Math.random()*(maxInitialSpeed.y - minInitialSpeed.y)) + minInitialSpeed.y,
				  ((float)Math.random()*(maxInitialSpeed.z - minInitialSpeed.z)) + minInitialSpeed.z);
	}
	
	public void alterExtraneousSpeed(Float3d alteration) {
		extraneousSpeed.add(alteration);		
	}
	
	public void alterInitialSpeed(Float3d alteration) {
		minInitialSpeed.add(alteration);
		maxInitialSpeed.add(alteration);
	}
	
	public void bounce(Float3d normal, float bounciness) {
		Float3d inUnit = speed.clone().unit();
		Float3d inInvert = inUnit.clone().invert();
		
		float newMag = speed.mag()* bounciness;
						
		speed = normal.mult(inInvert.dot(normal)).mult(2f).add(inUnit).mult(newMag);		 
		
		location.set(location.x,0.0f,location.z);
	}	
	
	public void doMove(float fraction) {
		Float3d gravityComponent = down.clone().mult(gravity*fraction);
		Float3d directionComponent = extraneousSpeed.clone().mult(fraction);
		speed.add(directionComponent).add(gravityComponent);
		
		location.add(gravityComponent).add(directionComponent).add(speed);
	}
	
	public void draw(GL gl, int renderMode) {
		//Save the current modelview matrix
		gl.glPushMatrix();
		
		if (location.x <= minLocation.x || location.x >= maxLocation.x ||			
			location.z <= minLocation.z || location.z >= maxLocation.z) {
			
			init(minLocation, maxLocation);				
		} if (location.y < minLocation.y) {
			bounce((new Float3d(0,1,0)).unit(), 0.6f);
		}
		
		gl.glTranslatef(location.x, location.y, location.z);
		if (listBuilt) {
			gl.glCallList(list);
		} else {
			listBuilt = true;
			list = gl.glGenLists(1);
		
			float 	Xn = -0.5f*0.05f,
					Xp =  0.5f*0.05f,
					Yn = -0.5f*0.05f,
					Zn = -0.5f*0.05f,
					Zp =  0.5f*0.05f;
			
			float Yf = 0.05f;
			
			gl.glNewList(list, GL.GL_COMPILE_AND_EXECUTE);
			
				gl.glBegin(GL.GL_QUADS);	
					gl.glColor4f(0.5f,0.5f,0.5f, 0.4f);
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
			
			gl.glEndList();
		}
		
		//Restore the old modelview matrix
		gl.glPopMatrix();
	}
}
