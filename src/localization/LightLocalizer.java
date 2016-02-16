package localization;

import lejos.robotics.SampleProvider;

public class LightLocalizer extends Thread {

	private Navigator nav;
	private SampleProvider colorSensor;
	private float[] colorData;
	private Odometer odo;

	public LightLocalizer(Odometer odo, SampleProvider colorSensor,
			float[] colorData) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
	}

	public void run() {

	}
}
