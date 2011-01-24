package junglevision.visuals;

public class Timer implements Runnable {
	private Mover m;
	
	private long startTime = 0;
    private long stopTime = 0;    
    
    public Timer(Mover m) {
    	this.m = m;
    }

    public void startTiming(long timeToComplete) {
    	this.startTime = System.currentTimeMillis();
    	this.stopTime = startTime + timeToComplete;
        
    }

	public void run() {
		while(true) {
			long currentTime = System.currentTimeMillis();
			if (currentTime < stopTime) {
				long elapsedTime = currentTime - startTime;
				float fractionCompleted = (float) elapsedTime / (float) (stopTime-startTime);
				m.doMoveFraction(fractionCompleted);
			}
			
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
	}

}
