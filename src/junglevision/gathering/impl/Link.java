package junglevision.gathering.impl;

import java.util.ArrayList;

/**
 * An interface for a link between two elements that exist within the managed universe.
 * @author Maarten van Meersbergen 
 */
public class Link extends Element implements junglevision.gathering.Link {
	private ArrayList<junglevision.gathering.Link> children;
	private Location location;
	
	public Link(Location location) {
		this.location = location;
		children = new ArrayList<junglevision.gathering.Link>();
	}
	
	public Location getLocation() {
		return location;
	}
	
	public ArrayList<junglevision.gathering.Link> getChildren() {
		return children;
	}
	
	public void addChild(junglevision.gathering.Link link) {
		children.add(link);
	}
	
	public void removeChild(junglevision.gathering.Link link) {
		children.remove(link);
	}
}