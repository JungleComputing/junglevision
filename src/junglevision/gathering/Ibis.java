package junglevision.gathering;

/**
 * A representation of a seperate Ibis instance within the data gathering universe
 */
public interface Ibis extends Element {
	
	public Location getLocation();
	
	public Pool getPool();
	
	//public void update() throws TimeoutException;
	
	//Tryout for steering
	public void kill();
}