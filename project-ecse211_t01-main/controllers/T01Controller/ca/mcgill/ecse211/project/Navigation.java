package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import static ca.mcgill.ecse211.project.LightLocalizer.*;
import static ca.mcgill.ecse211.project.Navigation.moveStraightFor;
import static java.lang.Math.*;

import ca.mcgill.ecse211.playingfield.Point;
import java.nio.file.Path;


/**
 * The Navigation class is used to make the robot navigate around the playing field.
 */
public class Navigation {
  
  /** Do not instantiate this class. */
  private Navigation() {}
  
  /**
   * Travels to destination taking obstacles into account.
   *
   * <p>Describe your algorithm here in detail (then remove these instructions).
   */
  public static void travelTo(Point destination) {
    // Hint: One way to avoid an obstacle is to calculate a simple path around it and call
    // directTravelTo() to get to points on that path before returning to the original trajectory
    Point currPoint = new Point(odometer.getXyt()[0] / TILE_SIZE, odometer.getXyt()[1] / TILE_SIZE);
    println("Current Point: "+currPoint.toString());
    double relativeAngle = getDestinationAngle(currPoint, destination);
    turnTo(relativeAngle);
    double relativeDistance = distanceBetween(currPoint, destination);
    double relativeDistanceInMeter = relativeDistance * TILE_SIZE;   //relative distance in meter
    
    if(Math.round(currPoint.x)==Resources.overpassMileStone.x && Math.round(currPoint.y)==Resources.overpassMileStone.y ) {
        println("Prepare to cross the overpass!");
        //(6,5)->(9,1) cross bridge
        directTravelTo2(overpassStartLocalizationPoint);
        println("Now at:"+overpassStartLocalizationPoint.toString()+", enter the overpass!");
        turnTo(getDestinationAngle(Resources.overpassStartLocalizationPoint,Resources.overpassEndLocalizationPoint));
        
        //double angle = Navigation.getDestinationAngle(new Point(4,4.5), new Point(10,1.5));
        //turnTo(angle);
        UltrasonicLocalizer.setSpeed(300);
        int WRotationAngle=UltrasonicLocalizer.convertDistance(4*TILE_SIZE);
        leftMotor.rotate(WRotationAngle, true);
        rightMotor.rotate(WRotationAngle, false);
        UltrasonicLocalizer.setSpeed(100);
        leftMotor.forward();
        rightMotor.forward();
        LightLocalizer.initialize();
        while(true) {
          int sample1 = LightLocalizer.readColor1();
          int sample2 = LightLocalizer.readColor2();
          if(sample1<100||sample2<100) {
            break;
          }
        }
        println("Leave the overpass, now move straight for 0.7 feet");
        Navigation.moveStraightFor(0.7);      
        odometer.setTheta(overpassAngle);
             
        if(overpassAngle>=0 && overpassAngle<90) {
          println("After-Overpass-Correction, Mode: 0-90 degrees");
          turnTo(90);
          LightLocalizer.localizeToNearest(-90);
          odometer.setXyt((overpassEndPoint.x+1)*TILE_SIZE, (overpassEndPoint.y+1)*TILE_SIZE, 0);
        }else if(overpassAngle>=90 && overpassAngle<180) {
          println("After-Overpass-Correction, Mode: 90-180 degrees");
          turnTo(90);
          LightLocalizer.localizeToNearest(90);
          odometer.setXyt((overpassEndPoint.x+1)*TILE_SIZE, (overpassEndPoint.y-1)*TILE_SIZE, 180);
        }else if(overpassAngle>=180 && overpassAngle<270) {
          println("After-Overpass-Correction, Mode: 180-270 degrees");
          turnTo(270);
          LightLocalizer.localizeToNearest(-90);
          odometer.setXyt((overpassEndPoint.x-1)*TILE_SIZE, (overpassEndPoint.y-1)*TILE_SIZE, 180);       
        }else if(overpassAngle>=270 && overpassAngle<360) {
          println("After-Overpass-Correction, Mode: 270-360 degrees");
          turnTo(270);
          LightLocalizer.localizeToNearest(90);
          odometer.setXyt((overpassEndPoint.x-1)*TILE_SIZE, (overpassEndPoint.y+1)*TILE_SIZE, 0);
        }
        println("After-Overpass-Correction Finished, now at:"+"("+odometer.getXyt()[0]/TILE_SIZE+","+odometer.getXyt()[1]/TILE_SIZE+")"+"\n"+
                "Now Travel to"+destination.toString());
        
        directTravelTo2(destination);
        turnTo(relativeAngle);
        odometer.setXyt(destination.x*TILE_SIZE, destination.y*TILE_SIZE, relativeAngle);
        return;
    }else  if(Math.round(currPoint.x)==Resources.intersectMileStone.x && Math.round(currPoint.y)==Resources.intersectMileStone.y )  {
      //(8,4)->(4,1) cross tunnel from right to left
      println("Prepare to cross the underpass!!!!!!!!!!!!!!!!!!!!!");
      println(underpassStartLocalizationPoint.x+"  "+underpassStartLocalizationPoint.y);
      directTravelTo2(underpassStartLocalizationPoint);
      directTravelTo2(underpassEndLocalizationPoint);
      travelTo(destination);
      turnTo(relativeAngle);
      odometer.setXyt(destination.x*TILE_SIZE, destination.y*TILE_SIZE, relativeAngle);
      return;
    }
    
    double ultrasonicDistance = ((double)UltrasonicLocalizer.readUsDistance1() + (double)UltrasonicLocalizer.readUsDistance1()) / (2*100);     //ultrasonic distance in meter
    System.out.println("Ultrasonic reading: " + ultrasonicDistance);
    turnBy(15);
    double rightUltrasonicDistance = ((double)UltrasonicLocalizer.readUsDistance1() + (double)UltrasonicLocalizer.readUsDistance1()) / (2*100);     //ultrasonic distance in meter
    double rightDistance = rightUltrasonicDistance*Math.cos(15*Math.PI/180);
    turnBy(-30);
    double leftUltrasonicDistance = ((double)UltrasonicLocalizer.readUsDistance1() + (double)UltrasonicLocalizer.readUsDistance1()) / (2*100);     //ultrasonic distance in meter
    double leftDistance = leftUltrasonicDistance*Math.cos(15*Math.PI/180);
    turnBy(15);
    System.out.println("right and left distances  "+rightDistance+"  "+leftDistance);
    double ultrasonicSafeBound = 1.0;
    
    if (ultrasonicDistance > ultrasonicSafeBound && relativeDistanceInMeter > ultrasonicSafeBound) {
      if(rightDistance>ultrasonicSafeBound) {
        if(leftDistance>ultrasonicSafeBound) {
          moveStraightFor(ultrasonicSafeBound / TILE_SIZE);
          travelTo(destination);
        }else {
          avoidLeft(leftDistance);
          travelTo(destination);
        }
      }else {
        avoidRight(rightDistance);
        travelTo(destination);
      }
    }else if (ultrasonicDistance > ultrasonicSafeBound && relativeDistanceInMeter < ultrasonicSafeBound) {
      if(rightDistance>relativeDistanceInMeter) {
        if(leftDistance>relativeDistanceInMeter) {
          System.out.println("there is no obstacle");
          if(relativeDistance < 2.5) {
            directTravelTo2(destination);
          }else {
            directTravelTo(destination);
          }
        }else {
          avoidLeft(leftDistance);
          travelTo(destination);
        }
      }else {
        avoidRight(rightDistance);
        travelTo(destination);
      }
    }else {
      if (relativeDistanceInMeter > ultrasonicDistance) {
        System.out.println("there is an obstacle");
        avoidTravel(ultrasonicDistance, destination);
      } else {
        System.out.println("there is no obstacle");
        if(relativeDistance < 2.5) {
          directTravelTo2(destination);
        }else {
          directTravelTo(destination);
        }
      }
    }
    
    turnTo(relativeAngle);
    odometer.setXyt(destination.x*TILE_SIZE, destination.y*TILE_SIZE, relativeAngle);
  }

  /** Travels directly (in a straight line) to the given destination. */
/*  public static void directTravelTo(Point destination) {
    // Think carefully about how you would integrate line detection here, if necessary
    // Don't forget that destination.x and destination.y are in feet, not meters
    Point currPoint = new Point(odometer.getXyt()[0] / TILE_SIZE, odometer.getXyt()[1] / TILE_SIZE);
    System.out.println(currPoint.x + " " + currPoint.y);
    double relativeAngle = getDestinationAngle(currPoint, destination);
    turnTo(relativeAngle);
    double relativeDistance = distanceBetween(currPoint, destination);
    System.out.println("relative Angle:  " + relativeAngle + "  distance:  " + relativeDistance);
    moveStraightFor(relativeDistance);
    System.out.println("x: " + odometer.getXyt()[0] + "  y: "+odometer.getXyt()[1] + "  theta:  "+odometer.getXyt()[2]);
  }
*/
  
  /**
   * Travels directly (in a straight line) to the given destination. Finally a light localization will used to make the travel more accurate. 
   * @param destination destination point
   */
    public static void directTravelTo(Point destination) {
    // Think carefully about how you would integrate line detection here, if necessary
    // Don't forget that destination.x and destination.y are in feet, not meters
    Point currPoint = new Point(odometer.getXyt()[0] / TILE_SIZE, odometer.getXyt()[1] / TILE_SIZE);
    //moveStraightFor(-LightLocalizer.DISTANCE_WHEEL_ROBOTCENTER);
    double relativeAngle = getDestinationAngle(currPoint, destination);
    turnTo(relativeAngle);
    double distance = distanceBetween(currPoint, destination);
    
    boolean shallLocalize =false;
    
    if(distance > 0.7) distance -= 0.7;
    
    //System.out.println("Start moving straight for: "+ distance+" feets");
    //System.out.println("relative Angle:  " + relativeAngle + "  distance:  " + distance);
    moveStraightFor(distance);
    //System.out.println("Before localie  x: " + odometer.getXyt()[0] + "  y: "+odometer.getXyt()[1] + "  theta:  "+odometer.getXyt()[2]);
    localizeToPoint(relativeAngle);
    odometer.setXyt(destination.x*TILE_SIZE, destination.y*1*TILE_SIZE, odometer.getXyt()[2]);
    //System.out.println("After localize  x: " + odometer.getXyt()[0] + "  y: "+odometer.getXyt()[1] + "  theta:  "+odometer.getXyt()[2]);
    turnTo(relativeAngle);
    //moveStraightFor(DISTANCE_WHEEL_ROBOTCENTER/TILE_SIZE);
//    System.out.println("Turn to Minimun Angle: "+relativeAngle +" and Localize Robot's center to "+"("+destination.x+","+destination.y+"),");
//    System.out.println("Pause to check angle and position.......");
//    System.out.println("Coutinue moving, Localize Robot's wheels' center back to the point");
    //moveStraightFor(-DISTANCE_WHEEL_ROBOTCENTER/TILE_SIZE);
    odometer.setXyt(destination.x*TILE_SIZE, destination.y*TILE_SIZE, relativeAngle);
  }

  
  
  /**
   * Turns the robot with a minimal angle towards the given input angle in degrees, no matter what
   * its current orientation is. This method is different from {@code turnBy()}.
   */
  public static void turnTo(double angle) {
    turnBy(minimalAngle(odometer.getXyt()[2], angle));
    // Hint: You can do this in one line by reusing some helper methods declared in this class
  }

  /**
   * Returns the angle that the robot should point towards to face the destination in degrees.
   * This function does not depend on the robot's current theta.
   */
  public static double getDestinationAngle(Point current, Point destination) {
    double dx = destination.x - current.x;
    double dy = destination.y - current.y;
    //1st quadrant
    double angle = Math.atan(Math.abs(dx) / Math.abs(dy)) * 180 / Math.PI;              
    if (dx >= 0) {
      if (dy < 0) {                     
        angle = 180 - angle;                                            //2nd quadrant
      }
    } else {
      if (dy < 0) {
        angle += 180;                                                   //3rd quadrant
      } else {
        angle = 360 - angle;                                            //4th quadrant
      }
    }
    return angle; 
  }
  
  /**
   * Returns the signed minimal angle in degrees from initial angle to destination angle (deg).
   * @param initialAngle initial angle
   * @param destAngle final angle
   * @return
   */
  public static double minimalAngle(double initialAngle, double destAngle) {
    double minimalAngle = destAngle - initialAngle;
    if (Math.abs(minimalAngle) >= 180) {
      if (minimalAngle > 0) {
        minimalAngle -= 360;
      } else {
        minimalAngle += 360;
      }
    }
    return minimalAngle; 
  }
  
  /**
   * Returns the distance between the two points in tile lengths (feet).
   * @param p1 first point
   * @param p2 second point
   * @return
   */
  public static double distanceBetween(Point p1, Point p2) {
    double distance = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    return distance; 
  }
  
  
  
  //Bring Navigation-related helper methods from Labs 2 and 3 here
  // You can also add other helper methods here, but remember to document them with Javadoc (/**)!
  
  /**
   * Moves the robot straight for the given distance.
   *
   * @param distance in feet (tile sizes), may be negative
   */
  public static void moveStraightFor(double distance) {
    int rotationAngle = convertDistance(distance * TILE_SIZE);
    setSpeed(FORWARD_SPEED);
    leftMotor.rotate(rotationAngle, true);
    rightMotor.rotate(rotationAngle, false);

    //Set motor speeds and rotate them by the given distance.
    // This method should not return until the robot has finished moving.
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
    int rotationAngle = convertAngle(angle);
    setSpeed(ROTATE_SPEED);
    leftMotor.rotate(rotationAngle, true);
    rightMotor.rotate(-rotationAngle, false);
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
   * Sets the speed of both motors to the same values.
   *
   * @param speed the speed in degrees per second
   */
  public static void setSpeed(int speed) {
    setSpeeds(speed, speed);
  }
  
  /**
   * This method localize the robot to its nearest point.
   * @param angleDestPointTo The angle the robot should point toward to to get the destination
   */
  public static void localizeToPoint(double angleDestPointTo) {
    
    if(angleDestPointTo<5||angleDestPointTo>355) {
      //System.out.println("LocalizaToPoint mode: 0 degree");
      turnTo(90);
      moveStraightFor(0.25);
      turnTo(-90);
      localizeToNearest(90);
    }else if(angleDestPointTo<95&&angleDestPointTo>85) {
      //System.out.println("LocalizaToPoint mode: 90 degree");
      turnTo(180);
      moveStraightFor(0.25);
      turnTo(0);
      localizeToNearest(90);
    }else if(angleDestPointTo<185&&angleDestPointTo>175) {
      //System.out.println("LocalizaToPoint mode: 180 degree");
      turnTo(-90);
      moveStraightFor(0.25);
      turnTo(90);
      localizeToNearest(90);
    }else if(angleDestPointTo<275&&angleDestPointTo>265) {
      //System.out.println("LocalizaToPoint mode: 270 degree");
      turnTo(0);
      moveStraightFor(0.25);
      turnTo(180);
      localizeToNearest(90);
    }else if(angleDestPointTo>0&&angleDestPointTo<90) {
      //System.out.println("LocalizaToPoint mode: 0-90 degree");
      turnTo(90);
      localizeToNearest(-90);
    }else if(angleDestPointTo>90&&angleDestPointTo<180) {
      //System.out.println("LocalizaToPoint mode: 90-180 degree");
      turnTo(90);
      localizeToNearest(90);
    }else if(angleDestPointTo>180&&angleDestPointTo<270) {
      //System.out.println("LocalizaToPoint mode: 180-270 degree");
      turnTo(270);
      localizeToNearest(-90);
    }else if(angleDestPointTo>270&&angleDestPointTo<360) {
      //System.out.println("LocalizaToPoint mode: 270-360 degree");
      turnTo(270);
      localizeToNearest(90);
    }
  }
  
  /**
   * if there is an obstacle on the path, design an available path
   * @param ultrasonicDistance obstacle distance
   * @param destination end point
   */
  public static void avoidTravel(double ultrasonicDistance, Point destination) {
    if (ultrasonicDistance > BASE_WIDTH) {
      System.out.println(1);
      moveStraightFor((ultrasonicDistance - BASE_WIDTH)/ TILE_SIZE);
      avoid();
    } else {
      avoid();
    }
    travelTo(destination);
  }
  
  /**
   * aovid the obstacle in front of the robot
   */
  public static void avoid() {
    turnBy(90);
    double ultrasonicRead1 = (UltrasonicLocalizer.readUsDistance1() + UltrasonicLocalizer.readUsDistance1() + UltrasonicLocalizer.readUsDistance1()) / (3 * 100);     //ultrasonic distance in meter
    double MaximumObstacle = Math.sqrt(2) * OBSTACLE_SIZE;
    //assumed first point on the avoidpath
    Point p = calculatePoint((OBSTACLE_SIZE + 0.75 * BASE_WIDTH));
    boolean isOnIsland = p.x>(Resources.island.ll.x+0.75*BASE_WIDTH) && p.x<(Resources.island.ur.x-0.75*BASE_WIDTH) && p.y>(Resources.island.ll.y+0.75*BASE_WIDTH) && p.y<(Resources.island.ur.y-0.75*BASE_WIDTH);
    if ((ultrasonicRead1 > OBSTACLE_SIZE + 1.25 * BASE_WIDTH)&& isOnIsland) {
      //the first point on the avoid path
      Point p1 = p;
      directTravelTo2(p1);
      
      turnBy(-90);
      //the second point on the avoid path
      Point p2 = calculatePoint((OBSTACLE_SIZE + 2 * BASE_WIDTH));
      directTravelTo2(p2);
    } else {
      turnBy(180);
      double ultrasonicRead2 = (UltrasonicLocalizer.readUsDistance1() + UltrasonicLocalizer.readUsDistance1() + UltrasonicLocalizer.readUsDistance1()) / (3 * 100);     //ultrasonic distance in meter
      //assumed first point on the avoidpath
      p = calculatePoint((OBSTACLE_SIZE + 0.75 * BASE_WIDTH));
      isOnIsland = p.x>(Resources.island.ll.x+0.75*BASE_WIDTH) && p.x<(Resources.island.ur.x-0.75*BASE_WIDTH) && p.y>(Resources.island.ll.y+0.75*BASE_WIDTH) && p.y<(Resources.island.ur.y-0.75*BASE_WIDTH);
      if ((ultrasonicRead2 > OBSTACLE_SIZE + 1.25 * BASE_WIDTH)&& isOnIsland) {
        //the first point on the avoid path
        Point p1 = p;
        directTravelTo2(p1);
      } else {
        System.out.println("Fail to avoid, redesign");
      }
      turnBy(90);
      //the second point on the avoid path
      Point p2 = calculatePoint((OBSTACLE_SIZE + 2 * BASE_WIDTH));
      directTravelTo2(p2);
    }
  }
  
  /**
   * avoid the obstacle near to the path in the left of the robot
   * @param leftDistance distance to get close to the robot
   */
  private static void avoidLeft(double leftDistance) {
    System.out.println("avoidLeft!!");
    moveStraightFor((leftDistance-BASE_WIDTH));
    turnBy(90);
    //the first point on the avoid path
    Point p1 = calculatePoint(0.7*BASE_WIDTH);
    directTravelTo2(p1);
    turnBy(-90);
    //the second point on the avoid path
    Point p2 = calculatePoint((OBSTACLE_SIZE+2*BASE_WIDTH));
    directTravelTo2(p2);
  }
  
  /**
   * avoid the obstacle near to the path in the right of the robot
   * @param rightDistance distance to get close to the robot
   */
  private static void avoidRight(double rightDistance) {
    System.out.println("avoidRIght!!");
    moveStraightFor((rightDistance-BASE_WIDTH));
    turnBy(-90);
    //the first point on the avoid path
    Point p1 = calculatePoint(0.7*BASE_WIDTH);
    directTravelTo2(p1);
    turnBy(90);
    //the first point on the avoid path
    Point p2 = calculatePoint((OBSTACLE_SIZE+2*BASE_WIDTH));
    directTravelTo2(p2);
  }
  
  /**
   * calculate the point at which the robot will reach when move for the input distance with current angle
   * @param distance moving distance in meter
   * @return the calculated point
   */
  private static Point calculatePoint(double distance) {
    double x1 = odometer.getXyt()[0];
    double y1 = odometer.getXyt()[1];
    double currentAngle = odometer.getXyt()[2];
    if(0<=currentAngle && currentAngle<90) {
      x1 += (distance*Math.sin(currentAngle*Math.PI/180));
      y1 += (distance*Math.cos(currentAngle*Math.PI/180));
    }else if(90<=currentAngle && currentAngle<180) {
      x1 += (distance*Math.sin((180-currentAngle)*Math.PI/180));
      y1 -= (distance*Math.cos((180-currentAngle)*Math.PI/180));
    }else if(180<=currentAngle && currentAngle<270) {
      x1 -= (distance*Math.sin((currentAngle-180)*Math.PI/180));
      y1 -= (distance*Math.cos((currentAngle-180)*Math.PI/180));
    }else if(270<=currentAngle && currentAngle<360) {
      x1 -= (distance*Math.sin((360-currentAngle)*Math.PI/180));
      y1 += (distance*Math.cos((360-currentAngle)*Math.PI/180));
    }
    return new Point(x1/TILE_SIZE,y1/TILE_SIZE);
  }

  public static void decideTravelTo(Point destination) {
    Point currPoint = new Point(odometer.getXyt()[0] / TILE_SIZE, odometer.getXyt()[1] / TILE_SIZE);
    double relativeDistance = distanceBetween(currPoint, destination);
    if(relativeDistance>2.5) {
      directTravelTo(destination);
    }else {
      directTravelTo2(destination);
    }
    
  }
  
  /**
   * direct travel to a point without localizing at the end
   * @param destination destination point
   */
  public static void directTravelTo2(Point destination) {
    //System.out.println("destination: "+destination.x+"  "+destination.y);
    Point currPoint = new Point(odometer.getXyt()[0] / TILE_SIZE, odometer.getXyt()[1] / TILE_SIZE);
    double relativeDistance = distanceBetween(currPoint, destination);
    //System.out.println(currPoint.x+" "+currPoint.y+" "+relativeDistance);
    double relativeAngle = getDestinationAngle(currPoint, destination);
    //System.out.println("angle: "+relativeAngle);
    turnTo(relativeAngle);
    moveStraightFor(relativeDistance);
    odometer.setXyt(destination.x*TILE_SIZE, destination.y*TILE_SIZE, relativeAngle);
  }
  
}