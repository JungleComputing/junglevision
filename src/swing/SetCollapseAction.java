package swing;

import java.awt.event.ActionEvent;

import junglevision.visuals.Visual;
import junglevision.visuals.Visual.FoldState;

public class SetCollapseAction implements GoggleAction {
	private static final long serialVersionUID = 7987449048219770239L;
	
	Visual caller;
	FoldState myState;
	
	public SetCollapseAction(Visual caller, String label) {
		this.caller = caller;
		
		if (label.compareTo("Collapse") 	== 0) myState = Visual.FoldState.COLLAPSED;
		else if (label.compareTo("Unfold") 	== 0) myState = Visual.FoldState.UNFOLDED;
	}

	public void actionPerformed(ActionEvent e) {		
		caller.setFoldState(myState);			
	}
	
	public GoggleAction clone(String label) {
		return new SetCollapseAction(caller, label);		
	}
}
