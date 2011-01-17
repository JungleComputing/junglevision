package junglevision.gathering;

import java.util.ArrayList;

/**
 * An interface for a link between two elements that exist within the managed universe.
 * @author Maarten van Meersbergen 
 */
public interface Link extends Element {
	
	public Location getLocation();
	
	public ArrayList<junglevision.gathering.Link> getChildren();
	
	public void addChild(junglevision.gathering.Link link);
	
	public void removeChild(junglevision.gathering.Link link);
}