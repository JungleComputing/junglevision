package junglevision.gathering.impl;

import junglevision.gathering.exceptions.SingletonObjectNotInstantiatedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker extends Thread {	
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Worker");
	
	private Collector c;
		
	public Worker() {
		try {
			this.c = Collector.getCollector();
		} catch (SingletonObjectNotInstantiatedException e) {
			logger.error("Collector not instantiated properly.");
		}
	}
	
	public void run() {
		junglevision.gathering.Element element;
		while (true) {
			element = c.getWork();
			element.update();			
		}
	}
}
