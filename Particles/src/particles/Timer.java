package particles;

public class Timer implements Runnable {
	private static final int APS = 25; 
	private Junglevision jv;
	
	private long startTime = 0;
	private int waitTime;
    
    public Timer(Junglevision jv) {
    	this.jv = jv;
    	
    	waitTime = 1000 / APS;
    }

    public void startTiming(long timeToComplete) {
    	this.startTime = System.currentTimeMillis();
        
    }

	public void run() {
		while(true) {
			long currentTime = System.currentTimeMillis();
			long elapsed = currentTime - startTime;
			startTime = currentTime;
			
			float fraction = (float)elapsed / 1000f;
			jv.doParticleMoves(fraction);
			
			try {
				if (waitTime - elapsed > 0) { 
					Thread.sleep(waitTime - elapsed);
				} else {
					Thread.sleep(0);
				}
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
	}

}
