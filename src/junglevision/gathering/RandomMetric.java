package junglevision.gathering;

public class RandomMetric {
	private float currentValue;
	
	public RandomMetric() {
		currentValue = 0.0f;
	}
	
	public void update() {
		if (Math.random()>0.5) {
			currentValue += Math.random()/10;
		} else {
			currentValue -= Math.random()/10;
		}
		
		currentValue = Math.max(0.0f, currentValue);
		currentValue = Math.min(1.0f, currentValue);
	}
	
}
