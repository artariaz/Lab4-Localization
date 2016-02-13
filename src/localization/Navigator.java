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

	public enum State {
		INIT, IDLE, ROTATECW, ROTATECCW, ROTATETO, FORWARD
	}

	public Navigator(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Odometer odo,
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
				rotateTo();
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

	public void rotateCCW() {
		this.rightMotor.setSpeed(150);
		this.leftMotor.setSpeed(150);

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

	public void setRotateTo(double angle) {
		this.rotationalAngle = angle;
	}

	public void rotateTo() {
		double currentAngle = this.odo.getTheta();
		double rotationAngle;
		double smallestAngle;
		int ROTATE_SPEED = 150;
		if (currentAngle > rotationalAngle) {
			rotationAngle = currentAngle - rotationalAngle;
			if (rotationAngle > 180) {
				// Turn right by 360 - rotationAngle
				smallestAngle = 360 - rotationAngle;
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				leftMotor.rotate(convertAngle(leftRadius, width, smallestAngle), true);
				rightMotor.rotate(-convertAngle(rightRadius, width, smallestAngle), false);
			} else {
				// Turn left by rotationAngle
				smallestAngle = rotationAngle;
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				leftMotor.rotate(-convertAngle(leftRadius, width, smallestAngle), true);
				rightMotor.rotate(convertAngle(rightRadius, width, smallestAngle), false);
			}

		} else if (currentAngle < rotationalAngle) {
			rotationAngle = rotationalAngle - currentAngle;
			if (rotationAngle > 180) {
				// Turn left by 360 - rotationTheta
				smallestAngle = 360 - rotationAngle;
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				leftMotor.rotate(-convertAngle(leftRadius, width, smallestAngle), true);
				rightMotor.rotate(convertAngle(rightRadius, width, smallestAngle), false);
			} else {
				// Turn right by rotationTheta
				smallestAngle = rotationAngle;
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				leftMotor.rotate(convertAngle(leftRadius, width, smallestAngle), true);
				rightMotor.rotate(-convertAngle(rightRadius, width, smallestAngle), false);
			}
		}

		// Sets the thread to sleep for 200 ms once the robot finishes rotating.
		try {

			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public boolean faceDest()  {
		double currentAngle = this.odo.getTheta();

		// If angle is near the desired angle given a tolerance error, return
		// true
		if (rotationalAngle + error >= currentAngle || rotationalAngle - error <= currentAngle) {
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
