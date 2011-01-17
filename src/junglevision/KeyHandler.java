package junglevision;

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
			jv.doReset();
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
