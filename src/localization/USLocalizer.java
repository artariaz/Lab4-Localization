package localization;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class USLocalizer extends Thread {

	private Navigator nav;
	private boolean isFacingWall;
	private SampleProvider usSensor;
	private float[] usData;
	private Odometer odo;

	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};


	private LocalizationType locType;

	public USLocalizer(Navigator nav, SampleProvider usSensor, float[] usData,
			LocalizationType locType, Odometer odo) {
		this.nav = nav;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.odo = odo;
	}

	public void run() {
		nav.start();

		// Set up a starting point
		initialize(locType);

		// Look for what algorithm to follow (Rising Edge or Falling Edge)
		switch (locType) {
		case FALLING_EDGE:
			
			fallingEdge();

			break;
		case RISING_EDGE:
			risingEdge();
			break;

		}

	}

	public float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * 100;
		return distance;
	}

	private boolean facingWall(float distance) {
		if (distance < 32) {
			return true;
		} else {
			return false;
		}
	}

	private void initialize(LocalizationType loctype) {
		// If it is facing wall
		if (loctype == LocalizationType.FALLING_EDGE) {
			if (facingWall(getFilteredData())) {
				while (facingWall(getFilteredData())) {
					nav.setState(Navigator.State.ROTATECW);
				}
				// No longer facing a wall, stop the motors
				nav.setState(Navigator.State.IDLE);
				Sound.buzz();
				sleepThread(250);
			}
		}
		if (loctype == LocalizationType.RISING_EDGE) {
			// If it is not facing a wall
			if (!(facingWall(getFilteredData()))) {
				while (!(facingWall(getFilteredData()))) {
					nav.setState(Navigator.State.ROTATECW);
				}
				nav.setState(Navigator.State.IDLE);
				Sound.buzz();
				sleepThread(250);
			}
		}

	}

	public void sleepThread(int amount) {
		try {
			Thread.sleep(amount);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Falling edge - initial angle will be the left wall - originally facing
	// the wall
	// To turn to the next wall (back), rotate clockwise
	public void fallingEdge() {
		double angleA;
		double angleB;
		/*try {
			nav.setState(Navigator.State.ROTATECW);
			Thread.sleep(2500);
			nav.setState(Navigator.State.IDLE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		// Rotate clockwise until a wall is reached
		while (!(facingWall(getFilteredData()))) {
			nav.setState(Navigator.State.ROTATECW);
		}
		Sound.beep();
		// Stop when you see a wall
		nav.setState(Navigator.State.IDLE);
		// Record the angle
		sleepThread(250);
		angleA = odo.getTheta();
		// Rotate counter clockwise until another wall is reached
		try {
			nav.setState(Navigator.State.ROTATECCW);
			Thread.sleep(2500);
			nav.setState(Navigator.State.IDLE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (!(facingWall(getFilteredData()))) {
			nav.setState(Navigator.State.ROTATECCW);
		}
		// Stop when you see a wall
		nav.setState(Navigator.State.IDLE);
		Sound.beep();
		// Record the angle
		sleepThread(250);
		angleB = odo.getTheta();
		double desiredAngle = handleAngles(angleA, angleB);
		nav.setRotateTo(desiredAngle);
		nav.setState(Navigator.State.ROTATETO);
		odo.setTheta(0.0);
	}

	// Rising edge - initially at back wall, originally facing away from wall
	// Turn CW to get to next wall (left wall)
	public void risingEdge() { //has to start with seeing a wall 
		double angleA;
		double angleB;
		/*try {
			nav.setState(Navigator.State.ROTATECW);
			Thread.sleep(2500);
			nav.setState(Navigator.State.IDLE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		// Rotate clockwise until a wall is reached
		while (facingWall(getFilteredData())) {
			nav.setState(Navigator.State.ROTATECW);
		}
		Sound.beep();
		// Stop when you see a wall
		nav.setState(Navigator.State.IDLE);
		// Record the angle
		sleepThread(250);
		angleA = odo.getTheta();
		// Rotate counter clockwise until another wall is reached
		try {
			nav.setState(Navigator.State.ROTATECCW);
			Thread.sleep(2500);
			nav.setState(Navigator.State.IDLE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (facingWall(getFilteredData())) {
			nav.setState(Navigator.State.ROTATECCW);
		}
		// Stop when you see a wall
		nav.setState(Navigator.State.IDLE);
		// Record the angle
		Sound.beep();
		sleepThread(250);
		angleB = odo.getTheta();
		double desiredAngle = handleAngles(angleA, angleB);
		nav.setRotateTo(desiredAngle);
		nav.setState(Navigator.State.ROTATETO);
		odo.setTheta(0.0);
	}

	public double handleAngles(double angleA, double angleB) {
		// calculate the average angle using a+b/2
		double angle;
		angle = (angleA + angleB) / 2.0;
		return angle;
	}
}