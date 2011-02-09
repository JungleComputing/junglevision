package swing;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import junglevision.visuals.Visual;

public class ContextSensitiveMenu {	
	private JPopupMenu myMenu;
	
	public ContextSensitiveMenu(Visual caller) {		
		myMenu = new JPopupMenu();
			String[] collectionItems = {"Sphere","Cube","Cityscape"};
			GoggleAction al = new SetCollectionFormAction(caller, "");
		myMenu.add(makeRadioMenu("Collection Form", collectionItems, al));
		
	}
	
	private JMenu makeRadioMenu(String name, String[] labels, GoggleAction al) {
		JMenu result = new JMenu(name);
		
		for (String label : labels) {
			JRadioButtonMenuItem current = new JRadioButtonMenuItem(label);
			current.addActionListener(al.clone(label));
			result.add(current);
		}
		
		return result;
	}
	
	public JPopupMenu getMenu(Visual caller) {		
		return myMenu;
	}
}
