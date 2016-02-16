package localization;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class USLocalizer extends Thread {

	private Navigator nav;
	private SampleProvider usSensor;
	private float[] usData;
	private Odometer odo;

	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	private LocalizationType locType;

	// Constructor for USLocalizer
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

	// Filter the data from the light sensor
	public float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * 100;
		if (distance > 50) {
			distance = 50;
		}
		return distance;
	}

	// This method checks if the robot is facing the wall by checking the
	// distance from the wall
	private boolean facingWall(float distance) {
		if (distance < 28) {
			return true;
		} else {
			return false;
		}
	}

	private void initialize(LocalizationType loctype) {
		// For falling edge, the robot must be facing away from the wall to
		// start
		if (loctype == LocalizationType.FALLING_EDGE) {
			if (facingWall(getFilteredData())) {
				while (facingWall(getFilteredData())) {
					nav.setState(Navigator.State.ROTATECW);
				}
				// When the robot is no longer facing a wall, stop the motors
				nav.setState(Navigator.State.IDLE);
				Sound.twoBeeps();
				sleepThread(250);
			}
		}
		// For rising edge, the robot must be facing the wall initially
		if (loctype == LocalizationType.RISING_EDGE) {
			if (!(facingWall(getFilteredData()))) {
				while (!(facingWall(getFilteredData()))) {
					nav.setState(Navigator.State.ROTATECW);
				}
				// When the robot is facing a wall, stop the motors
				nav.setState(Navigator.State.IDLE);
				Sound.twoBeeps();
				sleepThread(250);
			}
		}

	}

	// This method simplifies the code for sleeping threads
	public void sleepThread(int amount) {
		try {
			Thread.sleep(amount);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// This method changes rotates the robot so that it is 0 degrees in the y
	// direction in the falling edge localization routine
	public void fallingEdge() {
		double angleA;
		double angleB;
		// Rotate clockwise until it reaches the first wall
		while (!(facingWall(getFilteredData()))) {
			nav.setState(Navigator.State.ROTATECW);
		}
		Sound.beep();

		// Stop when you see a wall
		nav.setState(Navigator.State.IDLE);

		// Record the angle
		sleepThread(1000);
		angleA = odo.getAng();

		// Rotate counterclockwise until another wall is reached
		// Sleep for 2.5s so that it does not detect the wall that it is already
		// at
		try {
			nav.setState(Navigator.State.ROTATECCW);
			Thread.sleep(2500);
			nav.setState(Navigator.State.IDLE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Rotate counterclockwise until it reaches the next wall
		while (!(facingWall(getFilteredData()))) {
			nav.setState(Navigator.State.ROTATECCW);
		}
		Sound.beep();

		// Stop when you see a wall
		nav.setState(Navigator.State.IDLE);

		// Record the angle
		sleepThread(1000);
		angleB = odo.getAng();

		// Calculate the midpoint angle from angleA to angleB
		// This should be around 45 degrees
		double desiredAngle = handleAngles(angleA, angleB);

		// Rotate the robot so that it is pointing north
		desiredAngle = desiredAngle + 45;
		nav.setRotateTo(desiredAngle);
		nav.setState(Navigator.State.ROTATETO);
		sleepThread(15000);

		// Set the angle to 0 when it has finished rotating
		odo.setAng(0.0);
	}

	// This method changes rotates the robot so that it is 0 degrees in the y
	// direction in the rising edge localization routine
	public void risingEdge() {
		double angleA;
		double angleB;

		// Rotate clockwise until a wall is reached
		while (facingWall(getFilteredData())) {
			nav.setState(Navigator.State.ROTATECW);
		}
		Sound.beep();

		// Stop when you see a wall
		nav.setState(Navigator.State.IDLE);

		// Record the angle
		sleepThread(1000);
		angleA = odo.getAng();

		// Rotate counterclockwise until another wall is reached
		// Sleep for 2.5s so that it does not detect the wall that it is already
		// at
		try {
			nav.setState(Navigator.State.ROTATECCW);
			Thread.sleep(2500);
			nav.setState(Navigator.State.IDLE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Rotate in counterclockwise to get to the next wall
		while (facingWall(getFilteredData())) {
			nav.setState(Navigator.State.ROTATECCW);
		}
		Sound.beep();

		// Stop when you see a wall
		nav.setState(Navigator.State.IDLE);

		// Record the angle
		sleepThread(1000);
		angleB = odo.getAng();

		// Calculate the midpoint angle of angle A and angle B
		double desiredAngle = handleAngles(angleA, angleB);
		// Rotate the angle until it is pointing North
		desiredAngle = desiredAngle + 225;
		nav.setRotateTo(desiredAngle);
		nav.setState(Navigator.State.ROTATETO);
		sleepThread(15000);
		odo.setAng(0.0);
	}

	// Calculate the midpoint angle between 2 angles
	public double handleAngles(double angleA, double angleB) {
		double angle = (angleA + angleB) / 2.0;

		return angle;
	}
}