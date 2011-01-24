package junglevision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.ipl.server.ManagementServiceInterface;
import ibis.ipl.server.RegistryServiceInterface;
import junglevision.gathering.Collector;
import junglevision.test.FakeManagementService;
import junglevision.test.FakeRegistryService;
import junglevision.visuals.JungleGoggles;

public class Junglevision {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.Junglevision");
	
    public Junglevision() {
    	
    }
	
    /**
     * main function for the standalone Junglevision program. Not to be used once integrated into deploy.
     */
    public static void main(String[] args) {
    	//Ibis/JMX variables
    	FakeRegistryService regInterface = new FakeRegistryService();    	
    	ManagementServiceInterface manInterface = new FakeManagementService(regInterface.getIbises());
    	RegistryServiceInterface myReg = regInterface;
    	
    	//Data interface
        Collector collector = junglevision.gathering.impl.Collector.getCollector(manInterface, myReg);
		new Thread(collector).start();
		
    	new JungleGoggles(collector);		
	}
}