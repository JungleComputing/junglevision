package junglevision.gathering.impl;

import java.util.concurrent.TimeoutException;

import junglevision.gathering.exceptions.SingletonObjectNotInstantiatedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker extends Thread {	
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Worker");

	private Collector c;
	junglevision.gathering.Element element;

	public Worker() {
		try {
			this.c = Collector.getCollector();
		} catch (SingletonObjectNotInstantiatedException e) {
			logger.error("Collector not instantiated properly.");
		}
	}

	public void run() {		
		while (true) {
			element = c.getWork(this);

			if (element instanceof Location) {
				((Location)element).update();
			} else if (element instanceof Ibis) {
				try {
					((Ibis)element).update();
				} catch (TimeoutException e) {
					logger.debug("timed out.");
				}
			} else {
				logger.error("Wrong type in work queue.");
			}
		}
	}
}
