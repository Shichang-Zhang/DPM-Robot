package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.LightLocalizer.DISTANCE_WHEEL_ROBOTCENTER;

import static ca.mcgill.ecse211.project.Resources.*;


import java.lang.Thread;
import ca.mcgill.ecse211.playingfield.Point;
import simlejos.ExecutionController;
import simlejos.hardware.ev3.LocalEV3;

/**
 * Main class of the program.
 *
 * TODO Describe your project overview in detail here (in this Javadoc comment).
 * Firstly arrangle the waypoints order according to passed parameters.  
 * Then use ultrasonic localizer to localize the robot to a given angle.
 * Then use light localzier to localize the robot to the start point.
 * Then move the robot to the front of the bridge
 * Then move straight for a certain distance to accross the bridge.
 * Then navigation starts. The robot will be navigated to each waypoints. 
 * After reaching the last waypoint, it will go back to the front of bridge.
 * The robot moves straight for a certain distance to accross the bridge.
 * The robot moves to original start point. 
 */
public class Main {

  /** Main entry point. 
   * @throws Exception */
  public static void main(String[] args){
    /**----------------------Initialization-----------------------------------------**/
    ExecutionController.performPhysicsSteps(INITIAL_NUMBER_OF_PHYSICS_STEPS);
    ExecutionController.performPhysicsStepsInBackground(PHYSICS_STEP_PERIOD);
    
    // Start the odometer thread and update the number of threads
    new Thread(odometer).start();
    ExecutionController.setNumberOfParties(NUMBER_OF_THREADS);
    
    if(Resources.redTeam==1) {
      colorIndex=1;
      println("Team 1 is red Team!");
    }else if(Resources.greenTeam==1) {
      colorIndex=2;
      println("Team 1 is green Team!");
    }else {
      println("No Team Number matches your team!!");
    }
    /**----------------------Initialization-----------------------------------------**/
    
    /**----------------------Test-----------------------------------------**/
    //odometer.setXyt(10*TILE_SIZE, 3*TILE_SIZE, 180);
    //Navigation.travelTo(new Point(10,1));
    
    /**----------------------Test-----------------------------------------**/
    
    /**----------------------Final Demo-----------------------------------------**/
    PathManager.arrangePoints();
    //1.Localize to RED Team Starting Point 
    println("Start!");
    UltrasonicLocalizer.localize();
    LightLocalizer.localizeToNearest(90);
    Point initialPoint=new Point(0,0);
    if(colorIndex==1) {
      if(Resources.redCorner==0) {
        initialPoint = new Point(red.ll.x+1,red.ll.y+1);
        odometer.setXyt((initialPoint.x)*TILE_SIZE, (initialPoint.y)*TILE_SIZE, 90);
      }else if(Resources.redCorner==3) {
        initialPoint=new Point(red.ll.x+1,red.ur.y-1);
        odometer.setXyt((initialPoint.x)*TILE_SIZE, (initialPoint.y)*TILE_SIZE, 180);
      }  
    }else {
      if(Resources.greenCorner==2) {
        UltrasonicLocalizer.turnBy(-90);
        initialPoint=new Point(green.ur.x-1,green.ur.y-1);
        odometer.setXyt((initialPoint.x)*TILE_SIZE, (initialPoint.y)*TILE_SIZE, 180);
      }else if(Resources.greenCorner==1) {
        initialPoint=new Point(green.ur.x-1,green.ll.y+1);
        odometer.setXyt((initialPoint.x)*TILE_SIZE, (initialPoint.y)*TILE_SIZE, 0);
      }
     }
    println("Initial Point: "+initialPoint);
    //at the start point, beep 3 times
    for(int i=0;i<3;i++) {
      LocalEV3.getAudio().beep();
      LightLocalizer.waitFor(1100);
    }
    //2.Pass the Bridge to the main island
    BridgePasser passer = new BridgePasser();
    passer.acrossBridge();
    Navigation.moveStraightFor(DISTANCE_WHEEL_ROBOTCENTER/TILE_SIZE);
    LightLocalizer.smallDistanceLocalization(tnLocalizationPoint);
    //3.Navigate to the waypoint
    for(int i=0;i<Resources.waypoints.size();i++) {
      odometer.printPosition();
        if(Math.abs(odometer.getXyt()[0]/TILE_SIZE-waypoints.get(i).x)>0.05||Math.abs(odometer.getXyt()[1]/TILE_SIZE-waypoints.get(i).y)>0.05) {
           println("Travel to " + waypoints.get(i) + ".");
           Navigation.travelTo(waypoints.get(i));
        }else {
          println("Already At" + waypoints.get(i) + ".");
        }
        //at the waypoint, beep 3 times
        for(int j=0;j<3;j++) {
          LocalEV3.getAudio().beep();
          LightLocalizer.waitFor(1100);
        }
    }
    
   //4.Return home
    passer.returnHome();
    Navigation.directTravelTo2(initialPoint);
    if(Math.abs(odometer.getXyt()[0]/TILE_SIZE-initialPoint.x)>0.05||Math.abs(odometer.getXyt()[1]/TILE_SIZE-initialPoint.y)>0.05) {
      println("Travel to " + initialPoint + ".");
      Navigation.directTravelTo2(initialPoint);
      Navigation.moveStraightFor(DISTANCE_WHEEL_ROBOTCENTER/TILE_SIZE);
   }else {
     println("Already At" + initialPoint + ".");
   }
    
    println("Back to the starting position, all process are done!");
    for(int j=0;j<5;j++) {
      LocalEV3.getAudio().beep();
      LightLocalizer.waitFor(1100);
    }
    
    
    /**----------------------Final Demo-----------------------------------------**/
   
    /**----------------------beta demo-----------------------------------------**/
   /*
    
     //1.Localize to RED Team Starting Point 
    println("Start!");
    UltrasonicLocalizer.localize();
    LightLocalizer.localizeToNearest(90);
    odometer.setXyt(1*TILE_SIZE, 8*TILE_SIZE, 180);
    //at the start point, beep 3 times
    for(int i=0;i<3;i++) {
      LocalEV3.getAudio().beep();
      LightLocalizer.waitFor(1100);
    }
    //2.Pass the Bridge to the main island
    BridgePasser passer = new BridgePasser();
    passer.acrossBridge();
    UltrasonicLocalizer.turnBy(-90);
    LightLocalizer.localizeToNearest(90);
    Point localizePoint=new Point(passer.end.x+0.5,passer.end.y-1);
    odometer.setXyt(localizePoint.x*TILE_SIZE, localizePoint.y*TILE_SIZE, 180);
    //3.Navigate to the waypoint
    for(int i=0;i<Resources.waypoints.size();i++) {
      odometer.printPosition();
        if(Math.abs(odometer.getXyt()[0]/TILE_SIZE-waypoints.get(i).x)>0.05||Math.abs(odometer.getXyt()[1]/TILE_SIZE-waypoints.get(i).y)>0.05) {
           println("Travel to " + waypoints.get(i) + ".");
           Navigation.directTravelTo(waypoints.get(i));
        }else {
          println("Already At" + waypoints.get(i) + ".");
        }
        //at the waypoint, beep 3 times
        for(int j=0;j<3;j++) {
          LocalEV3.getAudio().beep();
          LightLocalizer.waitFor(1100);
        }
    }
    
    if(Resources.waypoints.size()>1) {
      Navigation.travelTo(waypoints.get(0));
      Navigation.moveStraightFor(-0.8);
      Navigation.directTravelTo(waypoints.get(0));
    }
    
    //4.Return to the starting position
    if(Math.abs(odometer.getXyt()[0]/TILE_SIZE-localizePoint.x)>0.05||Math.abs(odometer.getXyt()[1]/TILE_SIZE-localizePoint.y)>0.05) {
      Navigation.directTravelTo(localizePoint);
    }  
    passer.returnHome();
    Navigation.directTravelTo(new Point(1,8));
    Navigation.moveStraightFor(DISTANCE_WHEEL_ROBOTCENTER/TILE_SIZE);
    
    */
    /**----------------------beta demo-----------------------------------------**/
  }
  
  /**
   * Example using WifiConnection to communicate with a server and receive data concerning the
   * competition such as the starting corner the robot is placed in.<br>
   * 
   * <p>Keep in mind that this class is an <b>example</b> of how to use the Wi-Fi code; you must use
   * the WifiConnection class yourself in your own code as appropriate. In this example, we simply
   * show how to get and process different types of data.<br>
   * 
   * <p>There are two variables you MUST set manually (in Resources.java) before using this code:
   * 
   * <ol>
   * <li>SERVER_IP: The IP address of the computer running the server application. This will be your
   * own laptop, until the beta beta demo or competition where this is the TA or professor's laptop.
   * In that case, set the IP to the default (indicated in Resources).</li>
   * <li>TEAM_NUMBER: your project team number.</li>
   * </ol>
   * 
   * <p>Note: You can disable printing from the Wi-Fi code via ENABLE_DEBUG_WIFI_PRINT.
   * 
   * @author Michael Smith, Tharsan Ponnampalam, Younes Boubekeur, Olivier St-Martin Cormier
   */
  public static void wifiExample() {
    // Note that we are using the Resources.println() method, not System.out.println(), to ensure
    // the team number is always printed
    println("Running...");

    // Example 1: Print out all received data
    println("Map:\n" + wifiParameters);

    // Example 2: Print out specific values
    println("Red Team: " + redTeam);
    println("Green Zone: " + green);
    println("Island Zone, upper right: " + island.ur);
    println("Red tunnel footprint, lower left y value: " + tnr.ll.y);
    println("All waypoints: " + waypoints);

    // Example 3: Compare value (simplified example)
    if (overpass.endpointA.x >= island.ll.x && overpass.endpointA.y >= island.ll.y) {
      println("Overpass endpoint A is on the island.");
    } else {
      errPrintln("Overpass endpoint A is in the water!"); // prints to stderr (shown in red)
    }
    
    // Example 4: Calculate the distance between two waypoints
    println("Distance between waypoints 3 and 5:",
        Navigation.distanceBetween(waypoint(3), waypoint(5)));

    // Example 5: Calculate the area of a region
    println("The island area is " + island.getWidth() * island.getHeight() + ".");
  }

}
