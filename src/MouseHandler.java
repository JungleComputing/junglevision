import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener{	
	private Junglevision p;
		
	private float viewDistOrigin; 
	private float viewDist = -6; 
	
	private Float[] rotation;
	private Float[] translation;
	
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
	
	MouseHandler(Junglevision p) {
		this.p = p;
		
		rotation = new Float[3];
		translation = new Float[3];		
	}
	
	public void resetTranslation() {
		this.translation = new Float[3];
		this.translationX = 0.0f;
		this.translationY = 0.0f;
		
		this.translationXorigin = 0.0f;
		this.translationYorigin = 0.0f;
	}
	
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (e.getClickCount() == 1) {	
				p.doPickRequest(e.getPoint());				
			} else {
				//p.relocateOrigin(e.getPoint());
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
		} else if (SwingUtilities.isMiddleMouseButton(e)) {
			dragRightXorigin = e.getPoint().x;
			dragRightYorigin = e.getPoint().y;
		}
	}

	public void mouseReleased(MouseEvent e) {
		rotationXorigin = rotationX;
		rotationYorigin = rotationY;	
		
		translationXorigin = translationX;
		translationYorigin = -translationY;
	}

	public void mouseDragged(MouseEvent e) { 
		if (SwingUtilities.isMiddleMouseButton(e)) {
			// x/y reversed because of axis orientation
			rotationY = ((e.getPoint().x - dragRightXorigin) + rotationYorigin) % 360;
			rotationX = ((e.getPoint().y - dragRightYorigin) + rotationXorigin) % 360;
			rotation[0] = rotationX;
			rotation[1] = rotationY;
			rotation[2] = 0.0f;
			p.setRotation(rotation);
		} else if (SwingUtilities.isLeftMouseButton(e)) {
			// y direction reversed because window coordinates are read from top to bottom
			translationX =  ((e.getPoint().x - dragLeftXorigin)/150 + translationXorigin);
			translationY = -((e.getPoint().y - dragLeftYorigin)/150 + translationYorigin);
			translation[0] = translationX;				 
			translation[1] = translationY;
			translation[2] = 0.0f;
			p.setTranslation(translation);
		}
	}

	public void mouseMoved(MouseEvent e) {
		//Empty - unneeded		
	}

	public void mouseWheelMoved(MouseWheelEvent e) {	
		viewDist += viewDistOrigin + e.getWheelRotation();	
		p.setViewDist(viewDist);
	}

}
