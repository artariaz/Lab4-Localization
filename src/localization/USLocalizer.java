package localization;

import lejos.robotics.SampleProvider;

public class USLocalizer extends Thread {

	private Navigator nav;
	private boolean isFacingWall;
	private SampleProvider usSensor;
	private float[] usData;

	public USLocalizer(Navigator nav, SampleProvider usSensor, float[] usData) {
		this.nav = nav;
		this.usSensor = usSensor;
		this.usData = usData;
	}

	public void run() {
		nav.start();
		initialize();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private float getFilteredData() {
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
			while (facingWall(getFilteredData())) {
				nav.setState(Navigator.State.ROTATE);
			}
			// No longer facing a wall, stop the motors
			nav.setState(Navigator.State.IDLE);
		}

		// If it is not facing a wall
		else {
			while (!(facingWall(getFilteredData()))) {
				nav.setState(Navigator.State.ROTATE);
			}
			nav.setState(Navigator.State.IDLE);
		}
	}

}
