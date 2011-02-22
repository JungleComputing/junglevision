package junglevision.gathering.impl;

public class UpdateTimer implements Runnable {
	private int refreshrate;
	
	public UpdateTimer(int refreshrate) {
		this.refreshrate = refreshrate;
	}
	
	public void run() {
		while (true) {
			Collector.reInitialize();
			try {
				Thread.sleep(refreshrate);
			} catch (InterruptedException e) {				
				break;
			}
		}
	}
	
}
