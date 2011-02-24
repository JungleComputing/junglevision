package junglevision.gathering;

import java.util.ArrayList;


/**
 * The interface for the data gathering module's representation of an Ibis Pool. 
 * @author Maarten van Meersbergen
 */
public interface Pool {
	
	public String getName();
	
	public ArrayList<junglevision.gathering.Ibis> getIbises();
	
	//public void addIbis(junglevision.gathering.Ibis newIbis);
	
	//public void removeIbis(junglevision.gathering.Ibis newIbis);
}