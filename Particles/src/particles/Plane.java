package particles;

import javax.media.opengl.GL;

public class Plane implements Visual {
	private Float3d location;
	private Float3d[] points;
	
	public Plane(Float3d location, Float3d[] points) {
		this.location = location.clone();
		
		this.points = new Float3d[4];
		this.points[0] = points[0].clone();
		this.points[1] = points[1].clone();
		this.points[2] = points[2].clone();
		this.points[3] = points[3].clone();
	}

	public void draw(GL gl, int glMode) {
		gl.glBegin(GL.GL_QUADS);		
			gl.glColor3f(1.0f,1.0f,1.0f);
			//TOP
			gl.glVertex3f(points[0].x, points[0].y, points[0].z);
			gl.glVertex3f(points[1].x, points[1].y, points[1].z);
			gl.glVertex3f(points[2].x, points[2].y, points[2].z);
			gl.glVertex3f(points[3].x, points[3].y, points[3].z);
		gl.glEnd();		
	}
	
	//public boolean collisionDetect(Float3d objectLocation) {
		
	//}

	public Float3d getNormal() {
		return (new Float3d(0f,10f,0)).unit();
	}

}
