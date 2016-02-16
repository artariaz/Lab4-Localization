package localization;

import lejos.robotics.SampleProvider;

public class LightLocalizer extends Thread {

	private Navigator nav;
	private SampleProvider colorSensor;
	private float[] colorData;
	private Odometer odo;
	private USLocalizer usl;

	public LightLocalizer(Odometer odo, SampleProvider colorSensor,
			float[] colorData, USLocalizer usl) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.usl = usl;
	}

	public void run() {

		// Rotate the robot to 90 degrees so that it can determine the x
		// coordinate
		nav.setRotateTo(90);
		nav.setState(Navigator.State.ROTATETO);

		// Wait until robot finishes rotating before setting the x coordinate
		if (nav.isRotating() == false) {
			// Get the distance from the light sensor
			// The x coordinate should be negative and since the squares are
			// 30.48cm squares, the position is the distance - 30.48
			double currX = (double) usl.getFilteredData() - 30.48;
			odo.setX(currX);
		}

		// Rotate the robot to 180 degrees so that it can determine the y
		// coordinate
		nav.setRotateTo(180);
		nav.setState(Navigator.State.ROTATETO);

		// Wait until robot finishes rotating before setting the y coordinate
		if (nav.isRotating() == false) {
			// Get the distance from the light sensor
			// The y coordinate should be negative and since the squares are
			// 30.48cm squares, the coordinate is the distance - 30.48
			double currY = (double) usl.getFilteredData() - 30.48;
			odo.setY(currY);
		}

		// Move to the point (0,0)
		nav.setDestination(0, 0);
		nav.setState(Navigator.State.ROTATETO);
		// Move forward to the desired point after the robot has rotated to the
		// correct angle
		if (nav.isRotating() == false) {
			nav.setState(Navigator.State.TRAVELLING);
		}
	}

	//This method simplifies the code for sleeping threads
	public void sleepThread(int amount) {
		try {
			Thread.sleep(amount);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
