package localization;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Lab4 {
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("C"));
	public static final Port usPort = LocalEV3.get().getPort("S1");
	// public static final Port colorPort = LocalEV3.get().getPort("S2");

	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 15.6;

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");
		float[] usData = new float[usValue.sampleSize()];

		// SensorModes colorSensor = new EV3ColorSensor(colorPort);
		// SampleProvider colorValue = colorSensor.getMode("Red");
		// float[] colorData = new float[colorValue.sampleSize()]; // colorData
		// is
		// the buffer in
		// which data
		// are returned
		Odometer odo = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, TRACK);
		odo.start();

		final TextLCD t = LocalEV3.get().getTextLCD();
		t.clear();

		t.drawString("< Left | Right >", 0, 0);
		t.drawString("Falling| Rising>", 0, 1);
		t.drawString(" Edge  | Edge  >", 0, 2);
		int buttonChoice = Button.waitForAnyPress();

		while (buttonChoice != Button.ID_LEFT
				|| buttonChoice != Button.ID_RIGHT) {

			if (buttonChoice == Button.ID_LEFT) {
				

				// perform the ultrasonic localization
				Navigator nav = new Navigator(leftMotor, rightMotor);
				USLocalizer usl = new USLocalizer(nav, usSensor, usData, USLocalizer.LocalizationType.FALLING_EDGE);
				LCDInfo lcd = new LCDInfo(odo, usl);
				usl.start();
				
				
				/*
				 * perform the light sensor localization LightLocalizer lsl = new
				 * LightLocalizer(odo, colorValue, colorData); lsl.doLocalization();
				 */
			} else {
		

				// perform the ultrasonic localization
				Navigator nav = new Navigator(leftMotor, rightMotor);
				USLocalizer usl = new USLocalizer(nav, usSensor, usData, USLocalizer.LocalizationType.RISING_EDGE);
				LCDInfo lcd = new LCDInfo(odo, usl);
				usl.start();
				
				/*
				 * perform the light sensor localization LightLocalizer lsl = new
				 * LightLocalizer(odo, colorValue, colorData); lsl.doLocalization();
				 */
			}

			
		}
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);

	}

}
