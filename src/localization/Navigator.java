package localization;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigator extends Thread {
	private EV3LargeRegulatedMotor rightMotor;
	private EV3LargeRegulatedMotor leftMotor;
	private State state = State.IDLE;

	public enum State {
		INIT, IDLE, ROTATE, FORWARD
	}

	public Navigator(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		this.rightMotor = rightMotor;
		this.leftMotor = leftMotor;
	}

	public void run() {
		while (true) {
			switch (this.state) {
			case ROTATE:
				rotateCW();
				break;
			case FORWARD:
				goForward();
				break;
			case IDLE:
				goIdle();
				break;
			default:
				break;
			}
		}

	}

	// Rotate Clockwise
	public void rotateCW() {

		this.rightMotor.setSpeed(150);
		this.leftMotor.setSpeed(150);

		this.rightMotor.backward();
		this.leftMotor.forward();
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void goForward() {
		this.rightMotor.setSpeed(150);
		this.leftMotor.setSpeed(150);

		this.rightMotor.forward();
		this.leftMotor.forward();
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void goIdle() {
		this.rightMotor.setSpeed(0);
		this.leftMotor.setSpeed(0);
		this.rightMotor.forward();
		this.leftMotor.forward();
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setState(State state){
		this.state = state;
	}
}
