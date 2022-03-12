package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;

import java.util.Arrays;

/**
 * The ultrasonic localizer. TODO: Give an overview of your ultrasonic localizer here, 
 * in the Javadoc comment.
 */
public class UltrasonicLocalizer {


  private static final int WALL_DIST_ERR_THRESH = 7;
  // Other class variables
  private static final int faceDistance = 70;

  private static final int safeDistance = 20;

  private static int[] sampleArray;
  private static final int sample_array_size = 5;
  private static final int SLEEP_INT = 10;

  /** The distance remembered by the filter() method. */
  private static int prevDistance;
  /** The number of invalid samples seen by filter() so far. */
  private static int invalidSampleCount;

  // These arrays are class variables to avoid creating new ones at each iteration.
  /** Buffer (array) to store US samples. */
  private static float[] usData = new float[Resources.usSensor.sampleSize()];

  /**
   * Localizes the robot to theta = 0.
   */
  public static void localize() {
    println("UltrasonicLocalizer Starts!");
    // initialize the sampleArray
    try {
      Thread.sleep(SLEEP_INT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    init();
    //Let the midpoint of two wheels go to the center of the robot
    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);
    moveStraightFor(-LightLocalizer.DISTANCE_WHEEL_ROBOTCENTER);
    //avaerage two angles
    average();
    //turn left by 45, so it is on 0
    turnBy(135);
    println("The Robot has turned to 90 degree! UltrasonicLocalizer Finish......");
    odometer.setTheta(180);

    // TODO
  }
  // ----------------------------------------------------Lab3 Helper Method

  /**
   * By checking two angles outerward, turn to the half of their sum.
   * ie.45 degree
   */
  public static void average() {
      init();
      leftMotor.forward();
      rightMotor.backward();
      while (true) {
        //System.out.println("US distance reading:  "+readUsDistance2());
        if (readUsDistance2() <= safeDistance) {
            println("The Robot is within the safe distance! Next Step:Turn Left to exceed the face distnace");
           break;
      }
    }
      leftMotor.stop();
      rightMotor.stop();
      odometer.setTheta(0);
      turnToDistanceLeft();
      println("The Robot has turned left to exceed the face distance! Next Step:Turn Back to original angle");
      double angleLeft;
      angleLeft = 360-odometer.getXyt()[2];
      turnBy(angleLeft);
      println("The Robot has turned back to original angle! Next Step: Next Step:Turn Right to exceed the face distance");
      init();
      odometer.setTheta(0);
      turnToDistanceRight();
      println("The Robot has turned right to exceed the face distance! Next Step:Turn to -45 degree");
      double angleRight;
      angleRight=odometer.getXyt()[2];
      double angleNeedToTurn=(angleLeft+angleRight)/2;
      turnBy(-angleNeedToTurn);
      println("The Robot has turned to -45 degree! Next Step:Turn to 90 degree");
  }
  
  /**
   * turn right until it gets faceDistance.
   */
  public static void turnToDistanceRight() {
    setSpeed(ROTATE_SPEED);
    leftMotor.forward();
    rightMotor.backward();
    while (true) {
   //   System.out.println("angle: " + odometer.getXyt()[2] + "  distance  " + readUsDistance2());
      if (readUsDistance2() >faceDistance) {
        break;
      }
    }
    leftMotor.stop();
    rightMotor.stop();
  }
  
  /**
   *  turn left until it gets faceDistance.
   */
  public static void turnToDistanceLeft() {
    setSpeed(ROTATE_SPEED);
    leftMotor.backward();
    rightMotor.forward();
    while (true) {
    //  System.out.println("angle: " + odometer.getXyt()[2] + "  distance  " + readUsDistance2());
      if (readUsDistance2() >faceDistance) {
        break;
      }
    }
    leftMotor.stop();
    rightMotor.stop();
  }

  // ----------------------------------------------------Lab1 Method and Sampling Correction

  /**
   * Use the median filter to get a correct sample.
   * @return the distance filtered.
   */
  public static int readUsDistance2() {
    try {
      Thread.sleep(SLEEP_INT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    int currDistance = getSample();
    int[] tempArr = new int[sample_array_size];
    for (int i = 1; i < sample_array_size; i++) {
      sampleArray[i - 1] = sampleArray[i];
    }
    sampleArray[sample_array_size - 1] = currDistance;
    // copy the sampleArray
    for (int i = 0; i < sample_array_size; i++) {
      tempArr[i] = sampleArray[i];
    }
    int median = median(tempArr);
    return median;

  }
  
  /**
   * Use the average filter to get a correct sample.
   * @return the distance filtered.
   */
  public static int readUsDistance1() {
    init();
    try {
      Thread.sleep(SLEEP_INT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    int currDistance = getSample();
    int[] tempArr = new int[sample_array_size];
    for (int i = 1; i < sample_array_size; i++) {
      sampleArray[i - 1] = sampleArray[i];
    }
    sampleArray[sample_array_size - 1] = currDistance;
    // copy the sampleArray
    for (int i = 0; i < sample_array_size; i++) {
      tempArr[i] = sampleArray[i];
    }
    int average = getAverage(tempArr);
    return average;
  }

  /**
   * 
   * @return distance read from the USsensor.
   */
  public static int getSample() {
    Resources.usSensor.fetchSample(usData, 0);
    // extract from buffer, convert to cm, cast to int, and filter
    return filter((int) (usData[0] * 100.0));
  }

  /**
  * Get a median of an array with an odd length, even length doesn't work.
  * @param Oddarr (odd length)
  * @return median of the array
  */    
  public static int median(int[] Oddarr) {
    Arrays.sort(Oddarr);
    int median = Oddarr[(Oddarr.length - 1) / 2];
    return median;
  }
  
  /**
   * get an average of input array eliminating lowest and highest value.
   * @param array input
   * @return average of the array eliminating lowest and highest value
   */
  public static int getAverage(int[] array) {
    Arrays.sort(array);
    int sum = 0;
    for (int i = 1; i < array.length - 1; i++) {
      sum += array[i];
    }
    return sum / (array.length - 2);
  }


  static int filter(int distance) {
    if (distance >= MAX_SENSOR_DIST && invalidSampleCount < INVALID_SAMPLE_LIMIT) {
      // bad value, increment the filter value and return the distance remembered from before
      invalidSampleCount++;
      return prevDistance;
    } else {
      if (distance < MAX_SENSOR_DIST) {
        // distance went below MAX_SENSOR_DIST: reset filter and remember the input distance.
        invalidSampleCount = 0;
      }
      prevDistance = distance;
      return distance;
    }
  }
  
  public static void init() {
    sampleArray = new int[sample_array_size];
    for (int i = 0; i < sample_array_size; i++) {
      sampleArray[i] = getSample();
    }
  }
  

  // ----------------------------------------------------SquareDriver Method
  // turnBy()
  // moveStraightFor()
  /**
   * Move straight for a given distance in meters.
   * @param distance  distance in meter
   */
  public static void moveStraightFor(double distance) {

    int WRotationAngle = convertDistance(distance);
    setSpeed(FORWARD_SPEED);
    leftMotor.rotate(WRotationAngle, true);
    rightMotor.rotate(WRotationAngle, false);
  }

  /**
   * Turns the robot by a specified angle. Note that this method is 
   * different from {@code Navigation.turnTo()}. For example, if the robot 
   * is facing 90 degrees, calling {@code turnBy(90)} will make the robot 
   * turn to 180 degrees, but calling {@code Navigation.turnTo(90)} should 
   * do nothing (since the robot is already at 90 degrees).
   *
   * @param angle the angle by which to turn, in degrees
   */
  public static void turnBy(double angle) {
    int CRotationAngle = convertAngle(angle);
    setSpeed(ROTATE_SPEED);
    leftMotor.rotate(CRotationAngle, true);
    rightMotor.rotate(-CRotationAngle, false);
  }

  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   *
   * @param distance the input distance in meters
   * @return the wheel rotations necessary to cover the distance in degrees
   */
  public static int convertDistance(double distance) {
    int wheelRotation = 0;
    wheelRotation = (int) ((180 * distance) / (WHEEL_RAD * Math.PI));
    return wheelRotation;
  }

  /**
   * Converts input angle to the total rotation of each wheel 
   * needed to rotate the robot by that angle.
   *
   * @param angle the input angle in degrees
   * @return the wheel rotations necessary to rotate the robot by the angle in degrees
   */
  public static int convertAngle(double angle) {
    double distance = (BASE_WIDTH * Math.PI * angle) / 360;
    int wheelRotation = convertDistance(distance);
    return wheelRotation;
  }

  /**
   * Stops both motors.
   */
  public static void stopMotors() {
    leftMotor.stop();
    rightMotor.stop();
  }

  /**
   * Sets the speed of both motors to the same values.
   *
   * @param speed the speed in degrees per second
   */
  public static void setSpeed(int speed) {
    setSpeeds(speed, speed);
  }

  /**
   * Sets the speed of both motors to different values.
   *
   * @param leftSpeed the speed of the left motor in degrees per second
   * @param rightSpeed the speed of the right motor in degrees per second
   */
  public static void setSpeeds(int leftSpeed, int rightSpeed) {
    leftMotor.setSpeed(leftSpeed);
    rightMotor.setSpeed(rightSpeed);
  }

  /**
   * Sets the acceleration of both motors.
   *
   * @param acceleration the acceleration in degrees per second squared
   */
  public static void setAcceleration(int acceleration) {
    leftMotor.setAcceleration(acceleration);
    rightMotor.setAcceleration(acceleration);
  }
}
