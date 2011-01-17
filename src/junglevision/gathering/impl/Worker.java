package junglevision.gathering.impl;

public class Worker extends Thread {	
	private Collector collector;
		
	public Worker(Collector collector) {
		this.collector = collector;		
	}
	
	public void run() {
		junglevision.gathering.Ibis ibis;
		while (true) {
			ibis = collector.getWork();
			ibis.update();			
		}
	}
}
