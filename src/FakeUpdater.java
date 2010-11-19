
public class FakeUpdater implements Runnable {
	private Junglevision jv;
	
	public FakeUpdater(Junglevision jv) {
		this.jv = jv;
	}
	
	public void run() {
		while (true) {
			jv.doUpdateRequest();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {				
				break;
			}
		}
	}
	
}
