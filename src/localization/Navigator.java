package localization;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigator extends Thread {
	private EV3LargeRegulatedMotor rightMotor;
	private EV3LargeRegulatedMotor leftMotor;
	private State state = State.IDLE;
	private double rotationalAngle;
	private Odometer odo;
	private double leftRadius;
	private double rightRadius;
	private double width;
	private int error = 5;
	private int SPEED = 50;
	private int SLOW = 50;
	private double DEG_ERR = 5.0;

	public enum State {
		INIT, IDLE, ROTATECW, ROTATECCW, ROTATETO, FORWARD
	}

	public Navigator(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, Odometer odo,
			double wheelRadius, double width) {
		this.rightMotor = rightMotor;
		this.leftMotor = leftMotor;
		this.odo = odo;
		this.leftRadius = wheelRadius;
		this.rightRadius = wheelRadius;
		this.width = width;
	}

	public void run() {
		while (true) {
			switch (this.state) {
			case ROTATECW:
				rotateCW();
				break;
			case ROTATECCW:
				rotateCCW();
				break;
			case ROTATETO:
				turnTo();
				if (faceDest()) {
					this.state = State.IDLE;
				}
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

	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void rotateCCW() {
		this.rightMotor.setSpeed(SPEED);
		this.leftMotor.setSpeed(SPEED);

		this.rightMotor.forward();
		this.leftMotor.backward();
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Rotate Clockwise
	public void rotateCW() {

		this.rightMotor.setSpeed(SPEED);
		this.leftMotor.setSpeed(SPEED);

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
		this.rightMotor.setSpeed(SPEED);
		this.leftMotor.setSpeed(SPEED);

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

	public void setRotateTo(double angle) {
		this.rotationalAngle = angle;
	}

	public void turnTo() {
		double desiredAngle = rotationalAngle;
		double currAngle = odo.getTheta();
		if (desiredAngle < 0) {
			desiredAngle = desiredAngle + 360;
		}

		if (odo.getTheta() < 0) {
			currAngle = odo.getTheta() + 360;
		}
		double error = desiredAngle - currAngle;

		while (Math.abs(error) > DEG_ERR) {
			if (desiredAngle < 0) {
				desiredAngle = desiredAngle + 360;
			}

			if (odo.getTheta() < 0) {
				currAngle = odo.getTheta() + 360;
			}
			error = desiredAngle - currAngle;

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		this.setSpeeds(0, 0);

	}

	public boolean faceDest() {
		double currentAngle = this.odo.getTheta();

		// If angle is near the desired angle given a tolerance error, return
		// true
		if (rotationalAngle + error >= currentAngle
				|| rotationalAngle - error <= currentAngle) {
			return true;
		} else
			return false;
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	public void setState(State state) {
		this.state = state;
	}
}
