package localization;

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

	public enum Location {
		LEFT, BOTTOM
	};

	private LocalizationType locType;
	private Location location;

	public USLocalizer(Navigator nav, SampleProvider usSensor, float[] usData, LocalizationType locType, Odometer odo) {
		this.nav = nav;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.odo = odo;
	}

	public void run() {
		nav.start();

		// Set up a starting point
		initialize();

		// Look for what algorithm to follow (Rising Edge or Falling Edge)
		switch (locType) {
		case FALLING_EDGE:
			switch (location) {
			case LEFT:
			handleLFE();
				break;
			case BOTTOM:
			handleBFE();
				break;
			}
			break;
		case RISING_EDGE:
			switch (location) {
			case LEFT:
			handleLRE();
				break;
			case BOTTOM:
			handleBRE();
				break;
			}
			break;

		}

	}

	public float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * 100;
		return distance;
	}

	private boolean facingWall(float distance) {
		if (distance < 30) {
			return true;
		} else {
			return false;
		}
	}

	private void initialize() {
		// If it is facing wall
		if (facingWall(getFilteredData())) {
			this.location = Location.LEFT;
			while (facingWall(getFilteredData())) {
				nav.setState(Navigator.State.ROTATECW);
			}
			// No longer facing a wall, stop the motors
			nav.setState(Navigator.State.IDLE);
			sleepThread(250);
		}

		// If it is not facing a wall
		else {
			this.location = Location.BOTTOM;
			while (!(facingWall(getFilteredData()))) {
				nav.setState(Navigator.State.ROTATECW);
			}
			nav.setState(Navigator.State.IDLE);
			sleepThread(250);
		}
	}

	public void sleepThread(int amount) {
		try {
			Thread.sleep(amount);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handleLFE() {
		double angleA;
		double angleB;
		// Rotate clockwise until a wall is reached
		while (!(facingWall(getFilteredData()))) {
			nav.setState(Navigator.State.ROTATECW);
		}
		// Stop when you see a wall
		nav.setState(Navigator.State.IDLE);
		// Record the angle
		angleA = odo.getTheta();
		// Rotate counter clockwise until another wall is reached
		try {
			nav.setState(Navigator.State.ROTATECCW);
			Thread.sleep(1000);
			nav.setState(Navigator.State.IDLE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (!(facingWall(getFilteredData()))) {
			nav.setState(Navigator.State.ROTATECCW);
		}
		// Stop when you see a wall
		nav.setState(Navigator.State.IDLE);
		// Record the angle
		angleB = odo.getTheta();
		double desiredAngle = averageAngles(angleA,angleB);
		nav.setRotateTo(desiredAngle);
		nav.setState(Navigator.State.ROTATETO);
	}

	public void handleBFE() {

	}

	public void handleLRE() {

	}

	public void handleBRE() {

	}

	public double averageAngles(double angleOne, double angleTwo) {
		double averageAngle = (angleOne + angleTwo) / 2.0;

		return averageAngle;
	}
}
