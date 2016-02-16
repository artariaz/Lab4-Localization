package localization;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigator extends Thread {
	private EV3LargeRegulatedMotor rightMotor;
	private EV3LargeRegulatedMotor leftMotor;
	private State state = State.IDLE;
	private double rotationalAngle;
	private double destX, destY;
	private Odometer odo;
	private int error = 2;
	private int SPEED = 50;
	private int SLOW = 50;
	private double DEG_ERR = 5.0;
	private boolean isRotating;
	public enum State {
		IDLE, ROTATECW, ROTATECCW, ROTATETO, TRAVELLING
	}

	// Constructor for Navigator class
	public Navigator(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, Odometer odo) {
		this.rightMotor = rightMotor;
		this.leftMotor = leftMotor;
		this.odo = odo;
	}

	public void run() {
		int i = 0;
		while (true) {
			switch (this.state) {
			case ROTATECW:
				rotateCW();
				break;
			case ROTATECCW:
				rotateCCW();
				break;
			case ROTATETO:
				isRotating = true;
				turnTo();
				isRotating = false;
				this.state = State.IDLE;
				break;
			case TRAVELLING:
				if (checkIfDone(odo.getX(), odo.getY())) {
					state = State.IDLE;
				} else {
					if (i < 50) {
						i++;
						goForward();
					} else {
						state = State.ROTATETO;
						i = 0;
					}
				}
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
		double angle = odo.fixDegAngle(getRotAngle()); // WHY IS getRotAngle()
														// CALLED?
														// rotationalAngle is a
														// class variable.
		double error = angle - odo.fixDegAngle(this.odo.getAng());

		while (Math.abs(error) > DEG_ERR) {

			error = angle - odo.fixDegAngle(this.odo.getAng());

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

	public void getDestAngle() {
		double currentX = this.odo.getX();
		double currentY = this.odo.getY();
		double deltaX, deltaY;
		double angle;

		deltaX = destX - currentX;
		deltaY = destY - currentY;

		// Using the difference between the desired coordinates and the current
		// coordinates, calculate
		// the angle. (Pythagorean Theorem).

		// When either deltaX or deltaY are 0, they are special and must be
		// handled differently.
		if (Math.abs(deltaX) <= error && deltaY > 0) {
			angle = 0;
		} else if (Math.abs(deltaX) <= error && deltaY < 0) {
			angle = 180;
		} else if (Math.abs(deltaY) <= error && deltaX > 0) {
			angle = 90;
		} else if (Math.abs(deltaY) <= error && deltaX < 0) {
			angle = 270;
		}
		// If we do not have to deal with a special case, we simply calculate
		// the angle using the arctan function.
		else {
			angle = 90 - ((Math.atan(deltaY / deltaX)) * (180 / Math.PI));
		}

		// Once the angle has been calculated, we set destAngle to it.
		this.rotationalAngle = angle;
	}

	public boolean checkIfDone(double x, double y) {
		// x and y are the odometer's readings
		// Compare with destX and destY with a degree of tolerance
		// And return true if they are close to the desired values
		if ((destX + error >= x && destX - error <= x)
				&& (destY + error >= y && destY - error <= y)) {

			return true;
		} else
			return false;
	}

	public boolean faceDest() {
		double currentAngle = this.odo.getAng();

		// If angle is near the desired angle given a tolerance error, return
		// true
		if (rotationalAngle + error >= currentAngle
				|| rotationalAngle - error <= currentAngle) {
			return true;
		} else
			return false;
	}

	public void setDestination(double destX, double destY) {
		this.destX = destX;
		this.destY = destY;
		getDestAngle();
	}

	public void setState(State state) {
		this.state = state;
	}

	public double getRotAngle() {
		return rotationalAngle;
	}
	
	public boolean isRotating() {
		return isRotating;
	}
}
