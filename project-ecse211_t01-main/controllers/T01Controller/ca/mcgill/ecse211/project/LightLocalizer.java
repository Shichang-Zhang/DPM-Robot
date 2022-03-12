package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Navigation.*;
import static ca.mcgill.ecse211.project.Resources.*;

import java.util.Arrays;
import ca.mcgill.ecse211.playingfield.Point;
import simlejos.ExecutionController;

/**
 * The light localizer for the project.
 * This class Contain methods which can use light sensors to localize(i.e. correct) the robot to a specific point.
 * @author Junjian Chen
 *
 */
public class LightLocalizer {
  private static final int SLEEP_INT = 10;
  private static final int NSAMPLES = 100;
  private static final double DISTANCE_WHEEL_COLOR = 0.03;
  public static final double DISTANCE_WHEEL_ROBOTCENTER = 0.0405;
  private static float[] colorData1 = new float[Resources.leftColorSensor.sampleSize()];
  private static float[] colorData2 = new float[Resources.rightColorSensor.sampleSize()];
  public static int curretSample;
  private static int[] sampleArray1;
  private static int[] sampleArray2;
  private static final int sampleArraySize = 5;
  private static boolean isTouch1 = false;
  private static boolean isTouch2 = false;
  
  /**
   * Localizes the robot to (x, y, theta) = (1, 1, 0).
   */
  public static void localize() {
    UltrasonicLocalizer.turnBy(90);
    initialize();
    UltrasonicLocalizer.setSpeed(LOCALIZE_SPEED);
    leftMotor.forward();
    rightMotor.forward();
    boolean rolling = true;
    isTouch1=false;
    isTouch2=false;
    while (rolling) {
      if (readColor1() <100) {
        isTouch1 = true;
      } else {
        isTouch1 = false;
      }
      if (readColor2() <100) {
        isTouch2 = true;
      } else {
        isTouch2 = false;
      }
      if(isTouch1==true) {
        Resources.leftMotor.setSpeed(0);
      }
      if(isTouch2==true) {
        Resources.rightMotor.setSpeed(0);
      }
      if (isTouch1 && isTouch2) {
        rolling = false;
      }
    }
    UltrasonicLocalizer.setSpeed(0);
    UltrasonicLocalizer.moveStraightFor(DISTANCE_WHEEL_COLOR);
    UltrasonicLocalizer.turnBy(-90);
    rolling=true;
    UltrasonicLocalizer.setSpeed(LOCALIZE_SPEED);
    isTouch1=false;
    isTouch2=false;
    initialize();
    leftMotor.forward();
    rightMotor.forward();
    while (rolling) {
      if (readColor1() <100 ) {
        isTouch1 = true;
      } else {
        isTouch1 = false;
      }
      if (readColor2() <100) {
        isTouch2 = true;
      } else {
        isTouch2 = false;
      }
      if(isTouch1==true) {
        Resources.leftMotor.setSpeed(0);
      }
      if(isTouch2==true) {
        Resources.rightMotor.setSpeed(0);
      }
      if (isTouch1==true && isTouch2==true) {
        rolling = false;
      }
    }
    UltrasonicLocalizer.moveStraightFor(DISTANCE_WHEEL_COLOR+DISTANCE_WHEEL_ROBOTCENTER);
    waitFor(WAIT_PERIOD); 
    
  }
  /**
   * Localize the robot to a point around, depends on the input angle.
   * @param angle The angle robot turn by after first line detection.
   */
  public static void localizeToNearest(double angle) {
    initialize();
    //UltrasonicLocalizer.turnBy(45);
    UltrasonicLocalizer.setSpeed(LOCALIZE_SPEED);
    leftMotor.forward();
    rightMotor.forward();
    boolean rolling = true;
    isTouch1=false;
    isTouch2=false;
    while (rolling) {
      if(isTouch1==false) {
        if (readColor1() <100) {
          isTouch1 = true;
        } else {
          isTouch1 = false;
        }
      }
      if(isTouch2==false) {
        if (readColor2() <100) {
          isTouch2 = true;
        } else {
          isTouch2 = false;
        }
      }
      
//      System.out.println("  1: "+readColor1()+"  2:  "+readColor2());
//      System.out.println("  1: "+isTouch1+"  2:  "+isTouch2);
      if(isTouch1==true) {
        Resources.leftMotor.setSpeed(0);
      }
      if(isTouch2==true) {
        Resources.rightMotor.setSpeed(0);
      }
      if (isTouch1 && isTouch2) {
        rolling = false;
      }
    }
    UltrasonicLocalizer.setSpeed(0);
    UltrasonicLocalizer.moveStraightFor(DISTANCE_WHEEL_COLOR);
    UltrasonicLocalizer.turnBy(angle);//!!! angle after first localize turn By
    rolling=true;
    UltrasonicLocalizer.setSpeed(LOCALIZE_SPEED);
    isTouch1=false;
    isTouch2=false;
    initialize();
    leftMotor.forward();
    rightMotor.forward();
    while (rolling) {
      if(isTouch1==false) {
        if (readColor1() <100) {
          isTouch1 = true;
        } else {
          isTouch1 = false;
        }
      }
      if(isTouch2==false) {
        if (readColor2() <100) {
          isTouch2 = true;
        } else {
          isTouch2 = false;
        }
      }
      if(isTouch1==true) {
        Resources.leftMotor.setSpeed(0);
      }
      if(isTouch2==true) {
        Resources.rightMotor.setSpeed(0);
      }
      if (isTouch1==true && isTouch2==true) {
        rolling = false;
      }
    }
    UltrasonicLocalizer.moveStraightFor(DISTANCE_WHEEL_COLOR);
    waitFor(WAIT_PERIOD);
  }
  /**
   * Use median filter to return the correct value of sample.
   * @return flitered sample from ColorSenosr2
   */
  public static int readColor1() {
    try {
      Thread.sleep(SLEEP_INT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    int currDistance = getSample1();
    int[] tempArr = new int [sampleArraySize];
    for (int i = 1; i < sampleArraySize; i++) {
      sampleArray1[i - 1] = sampleArray1[i];
    }
    sampleArray1[sampleArraySize - 1] = currDistance;
    //copy the sampleArray
    for (int i = 0; i < sampleArraySize; i++) {
      tempArr[i] = sampleArray1[i];
    }
    int median = UltrasonicLocalizer.median(tempArr);
    return median;
    
  }
  /**
   * Use median filter to return the correct value of sample.
   * @return flitered sample from ColorSenosr2
   */
  
  public static int readColor2() {
    try {
      Thread.sleep(SLEEP_INT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    int currDistance = getSample2();
    int[] tempArr = new int [sampleArraySize];
    for (int i = 1; i < sampleArraySize; i++) {
      sampleArray2[i - 1] = sampleArray2[i];
    }
    sampleArray2[sampleArraySize - 1] = currDistance;
    //copy the sampleArray
    for (int i = 0; i < sampleArraySize; i++) {
      tempArr[i] = sampleArray2[i];
    }
    int median = UltrasonicLocalizer.median(tempArr);
    return median;
    
  }
  
  /**
   * Initialize two sampleArrays.
   */  
  public static void initialize() {
    try {
      Thread.sleep(SLEEP_INT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    sampleArray1 = new int[sampleArraySize];
    sampleArray2 = new int[sampleArraySize];
    for (int i = 0; i < sampleArraySize; i++) {
      sampleArray1[i] = 150;//getSample1();
      sampleArray2[i] = 150;//getSample2();
    }
    
  }
  
  /**
   * Get sample from colorSensor1.
   * @return sample from colorSensor1
   */
  
  public static int getSample1() {
    Resources.leftColorSensor.fetchSample(colorData1, 0);
    return (int) colorData1[0];
    
  }
  
  /**
   * Get sample from colorSensor2.
   * @return sample from colorSensor2
   */ 
  public static int getSample2() {
    Resources.rightColorSensor.fetchSample(colorData2, 0);
    return (int) colorData2[0];
  }
  /**
   * Wait for a given time
   * @param time The number of physical steps to wait
   */
  public static void waitFor(int time) {
    for (int i = 0; i < time; i++) {
      ExecutionController.waitUntilNextStep();
    }
  }  
  
  /**
   * This method is ONLY used for the robot to localize to the tnLocalizationPoint after it passes the tunnel.
   * @param p The point the robot is going to localize to (A tnLocalizationPoint)
   */
  public static void smallDistanceLocalization(Point p) {
    Point currPoint = new Point(odometer.getXyt()[0] / TILE_SIZE, odometer.getXyt()[1] / TILE_SIZE);
    double angle=getDestinationAngle(currPoint, p);
    double currAngle=odometer.getXyt()[2];
    turnTo(angle);
    moveStraightFor(DISTANCE_WHEEL_ROBOTCENTER/TILE_SIZE);
    turnTo(currAngle);
    if(angle>=0&&angle<90) {
      turnTo(0);
      localizeToNearest(90);
      odometer.setXyt(p.x*TILE_SIZE, p.y*TILE_SIZE, 90);
    }else if(angle>=90&&angle<180) {
      turnTo(90);
      localizeToNearest(90);
      odometer.setXyt(p.x*TILE_SIZE, p.y*TILE_SIZE, 180);
    }else if(angle>=180&&angle<270) {
      turnTo(180);
      localizeToNearest(90);
      odometer.setXyt(p.x*TILE_SIZE, p.y*TILE_SIZE, 270);
    }else {
      turnTo(270);
      localizeToNearest(90);
      odometer.setXyt(p.x*TILE_SIZE, p.y*TILE_SIZE, 0);
    }    
  }
}
