package junglevision.test;

public class UpdateTimer implements Runnable {
	private FakeRegistryService reg;
	
	public UpdateTimer(FakeRegistryService reg) {
		this.reg = reg;
	}
	
	public void run() {
		while (true) {
			reg.doUpdate();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {				
				break;
			}
		}
	}
	
}
