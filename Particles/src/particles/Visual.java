package particles;

import javax.media.opengl.GL;

public interface Visual {
	public void draw(GL gl, int glMode);
	public Float3d getNormal();
	
}
