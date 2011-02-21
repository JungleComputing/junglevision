package swing;

import java.awt.event.ActionEvent;

import junglevision.visuals.JGVisual;
import junglevision.visuals.JGVisual.FoldState;

public class SetCollapseAction implements GoggleAction {
	private static final long serialVersionUID = 7987449048219770239L;
	
	JGVisual caller;
	FoldState myState;
	
	public SetCollapseAction(JGVisual caller, String label) {
		this.caller = caller;
		
		if (label.compareTo("Collapse") 	== 0) myState = JGVisual.FoldState.COLLAPSED;
		else if (label.compareTo("Unfold") 	== 0) myState = JGVisual.FoldState.UNFOLDED;
	}

	public void actionPerformed(ActionEvent e) {		
		caller.setFoldState(myState);			
	}
	
	public GoggleAction clone(String label) {
		return new SetCollapseAction(caller, label);		
	}
}
