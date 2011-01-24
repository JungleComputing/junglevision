package junglevision.test;

import ibis.ipl.IbisIdentifier;
import ibis.ipl.Location;

public class FakeIbisIdentifier implements ibis.ipl.IbisIdentifier {
	private static final long serialVersionUID = 1973096908454994055L;
	
	private Location location;
	
	public FakeIbisIdentifier(String locationString) {
		location = new ibis.ipl.impl.Location(locationString);
	}
	
	public Location location() {		
		return location;
	}
	
	
	/* ------------------------------------------------------------------------------------------------ 
	 *  The rest is unneeded by the Collector 
	 * */ 	
	
	public int compareTo(IbisIdentifier arg0) {
		//Not needed by the collector
		return 0;
	}

	public String name() {
		//Not needed by the collector
		return null;
	}

	public String poolName() {
		//Not needed by the collector
		return null;
	}

	public byte[] tag() {
		//Not needed by the collector
		return null;
	}

	public String tagAsString() {
		//Not needed by the collector
		return null;
	}

}
