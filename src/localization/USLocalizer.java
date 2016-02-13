package localization;

import lejos.robotics.SampleProvider;

public class USLocalizer extends Thread {

	private Navigator nav;
	private boolean isFacingWall;
	private SampleProvider usSensor;
	private float[] usData;

	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	public enum Location {
		LEFT, BOTTOM
	};

	private LocalizationType locType;
	private Location location;

	public USLocalizer(Navigator nav, SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.nav = nav;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
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
				break;
			case BOTTOM:
				break;
			}
			break;
		case RISING_EDGE:
			switch (location) {
			case LEFT:
				break;
			case BOTTOM:
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
				nav.setState(Navigator.State.ROTATE);
			}
			// No longer facing a wall, stop the motors
			nav.setState(Navigator.State.IDLE);
			sleepThread(250);
		}

		// If it is not facing a wall
		else {
			this.location = Location.BOTTOM;
			while (!(facingWall(getFilteredData()))) {
				nav.setState(Navigator.State.ROTATE);
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
}
