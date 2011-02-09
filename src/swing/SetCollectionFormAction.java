package swing;

import java.awt.event.ActionEvent;

import junglevision.visuals.Visual;
import junglevision.visuals.Visual.CollectionShape;

public class SetCollectionFormAction implements GoggleAction {
	private static final long serialVersionUID = 7987449048219770239L;
	
	Visual caller;
	CollectionShape myCollections;
	
	public SetCollectionFormAction(Visual caller, String label) {
		this.caller = caller;
		
		if (label.compareTo("Cityscape") 	== 0) myCollections = Visual.CollectionShape.CITYSCAPE;
		else if (label.compareTo("Cube") 	== 0) myCollections = Visual.CollectionShape.CUBE;
		else if (label.compareTo("Sphere") 	== 0) myCollections = Visual.CollectionShape.SPHERE;
	}

	public void actionPerformed(ActionEvent e) {		
		caller.setCollectionShape(myCollections);			
	}
	
	public GoggleAction clone(String label) {
		return new SetCollectionFormAction(caller, label);		
	}
}
