package swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface GoggleAction extends ActionListener {
	public void actionPerformed(ActionEvent e);
	public GoggleAction clone(String newLabel);
}
