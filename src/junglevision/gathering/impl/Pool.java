package junglevision.gathering.impl;

import java.util.ArrayList;

/**
 * The data gathering module's representation of an Ibis Pool. 
 * @author Maarten van Meersbergen
 */
public class Pool implements junglevision.gathering.Pool {
	private String name;
	private ArrayList<junglevision.gathering.Ibis> ibises;
	
	public Pool(String name) {
		this.name = name;
		
		ibises = new ArrayList<junglevision.gathering.Ibis>();
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<junglevision.gathering.Ibis> getIbises() {
		return ibises;
	}
	
	public void addIbis(junglevision.gathering.Ibis newIbis) {
		ibises.add(newIbis);
	}
	
	public void removeIbis(junglevision.gathering.Ibis newIbis) {
		ibises.remove(newIbis);
	}
}