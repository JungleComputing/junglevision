package junglevision.gathering;

/**
 * An interface for a link between two elements that exist within the managed universe.
 * @author Maarten van Meersbergen 
 */
public interface Link extends Element {
	public Element getSource();
	public Element getDestination();
	public int getNumberOfDescendants();
	
	public String debugPrint();
	
	public void addChild(Link childLink);
}