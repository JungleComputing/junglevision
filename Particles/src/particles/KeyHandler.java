package particles;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
	Junglevision jv;
	
	public KeyHandler(Junglevision jv) {
		this.jv =jv;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if (code == 82) {
			jv.initAllParticles();
		} else if (code == 38) { //UP
			Float3d increase = new Float3d(0.0f, 0.0f, -0.01f);
			jv.increaseWind(increase);
		} else if (code == 40) { //DOWN
			Float3d increase = new Float3d(0.0f, 0.0f,  0.01f);
			jv.increaseWind(increase);
		} else if (code == 37) { //LEFT
			Float3d increase = new Float3d(-0.01f, 0.0f, 0.0f);
			jv.increaseWind(increase);
		} else if (code == 39) { //RIGHT
			Float3d increase = new Float3d( 0.01f, 0.0f, 0.0f);
			jv.increaseWind(increase);
		} else if (code == 33) { //PAGEUP
			Float3d increase = new Float3d( 0.0f, 0.01f, 0.0f);
			jv.increasePower(increase);
		} else if (code == 34) { //PAGEDOWN
			Float3d increase = new Float3d( 0.0f,-0.01f, 0.0f);
			jv.increasePower(increase);
		} else if (code == 107) { //PLUS
			Float3d increase = new Float3d( 0.01f, 0.0f, 0.01f);
			jv.increasePower(increase);
		}	 else if (code == 109) { //MINUS
			Float3d increase = new Float3d(-0.01f, 0.0f,-0.01f);
			jv.increasePower(increase);
		}			
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
