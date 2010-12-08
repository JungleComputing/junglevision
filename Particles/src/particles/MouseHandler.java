package particles;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener{	
	private Junglevision jv;
		
	private float viewDist = -6; 
	private float dragCoefficient = 10.0f;
	
	private Float3d rotation;
	private Float3d translation;
	
	private float dragRightXorigin;
	private float rotationXorigin = 0; 
	private float rotationX;
	
	private float dragRightYorigin;
	private float rotationYorigin = 0; 
	private float rotationY;
	
	private float dragLeftXorigin;
	private float translationXorigin = 0; 
	private float translationX = 0;
	
	private float dragLeftYorigin;
	private float translationYorigin = 0; 
	private float translationY = 0;
	
	MouseHandler(Junglevision jv) {
		this.jv = jv;
		
		rotation = new Float3d();
		translation = new Float3d();	
	}
	
	public void mouseClicked(MouseEvent e) {		
		if (SwingUtilities.isLeftMouseButton(e)) {
			jv.doPickRequest(e.getPoint());
			if (e.getClickCount() != 1) {
				jv.doRecenterRequest();
			}			
		} else if (SwingUtilities.isMiddleMouseButton(e)) {
			//Nothing yet
		} else if (SwingUtilities.isRightMouseButton(e)) {
			//Nothing yet
		}		
	}

	public void mouseEntered(MouseEvent e) {
		//Empty - unneeded		
	}

	public void mouseExited(MouseEvent e) {
		//Empty - unneeded		
	}

	public void mousePressed(MouseEvent e) {		
		if (SwingUtilities.isLeftMouseButton(e)) {			
			dragLeftXorigin = e.getPoint().x;
			dragLeftYorigin = e.getPoint().y;
		}
	}

	public void mouseReleased(MouseEvent e) {
		rotationXorigin = rotationX;
		rotationYorigin = rotationY;	
				
		translationXorigin = translationX;
		translationYorigin = -translationY;
	}

	public void mouseDragged(MouseEvent e) { 
		if (SwingUtilities.isLeftMouseButton(e)) {
			// x/y reversed because of axis orientation
			rotationY = ((e.getPoint().x - dragLeftXorigin) + rotationYorigin) % 360;
			rotationX = ((e.getPoint().y - dragLeftYorigin) + rotationXorigin) % 360;
			rotation.set(rotationX, rotationY, 0f);			
			jv.setRotation(rotation);
		}
	}

	public void mouseMoved(MouseEvent e) {
		//Empty - unneeded		
	}

	public void mouseWheelMoved(MouseWheelEvent e) {	
		viewDist += e.getWheelRotation();
		if (viewDist > 0) { viewDist = 0; }
		
		dragCoefficient = 100* 1/(100 - viewDist);
		
		//System.out.println("viewdist "+ viewDist + " dragCoefficient " + dragCoefficient);
		jv.setViewDist(viewDist);
	}
}
