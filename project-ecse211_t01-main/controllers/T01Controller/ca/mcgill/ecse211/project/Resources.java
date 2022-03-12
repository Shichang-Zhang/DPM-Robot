package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.playingfield.FileLoader;
import ca.mcgill.ecse211.playingfield.Overpass;
import ca.mcgill.ecse211.playingfield.Point;
import ca.mcgill.ecse211.playingfield.Region;
import ca.mcgill.ecse211.wificlient.WifiConnection;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import simlejos.hardware.motor.Motor;
import simlejos.hardware.port.SensorPort;
import simlejos.hardware.sensor.EV3ColorSensor;
import simlejos.hardware.sensor.EV3UltrasonicSensor;
import simlejos.robotics.RegulatedMotor;

/* (non-Javadoc comment)
 * TODO Integrate this carefully with your existing Resources class (See below for where to add
 * your code from your current Resources file). The order in which things are declared matters!
 */

/**
 * Class for static resources (things that stay the same throughout the entire program execution),
 * like constants and hardware.
 * <br><br>
 * Use these resources in other files by adding this line at the top (see examples):<br><br>
 * 
 * {@code import static ca.mcgill.ecse211.project.Resources.*;}
 */
public class Resources {
  
  // Wi-Fi client parameters
  /** The default server IP used by the profs and TA's. */
  public static final String DEFAULT_SERVER_IP = "127.0.0.1";

  /**
   * The IP address of the server that sends data to the robot. For the beta demo and competition,
   * replace this line with
   * 
   * <p>{@code public static final String SERVER_IP = DEFAULT_SERVER_IP;}
   */
  public static final String SERVER_IP = "127.0.0.1"; // = DEFAULT_SERVER_IP;

  /** The team number. */
  public static final int TEAM_NUMBER = 01; // TODO

  /** Enables printing of debug info from the WifiConnection class. */
  public static final boolean ENABLE_DEBUG_WIFI_PRINT = true;

  /**
   * Enable this to attempt to receive Wi-Fi parameters at the start of the program.
   * Otherwise, the parameters will be loaded from the PARAMS_FILE defined below.
   */
  public static final boolean RECEIVE_WIFI_PARAMS =true;

  // Simulation-related constants
  
  /** The time between physics steps in milliseconds. */
  public static final int PHYSICS_STEP_PERIOD = 500; // ms
  
  /** 
   * The number of threads used in your program (main, odometer).
   * Change this if you use more threads.
   */
  public static final int NUMBER_OF_THREADS = 2;
  
  /** The initial number of physics steps performed at the start of the program. */
  public static final int INITIAL_NUMBER_OF_PHYSICS_STEPS = 50;
  
  /** The relative path of the parameter file. */
  public static final String PARAMS_FILE = "../../server/example_data_fill.xml";
  
  /** The tile size in meters. Note that 0.3048 m = 1 ft. */
  public static final double TILE_SIZE = 0.3048;

  //----------------------------- DECLARE YOUR CURRENT RESOURCES HERE -----------------------------
  //----------------------------- eg, constants, motors, sensors, etc -----------------------------

  // Robot constants
  
  /** The maximum distance detected by the ultrasonic sensor, in cm. */
  public static final int MAX_SENSOR_DIST = 255;
  
  /** The limit of invalid samples that we read from the US sensor before assuming no obstacle. */
  public static final int INVALID_SAMPLE_LIMIT = 20;
  
  /** The wheel radius in meters. */
  public static final double WHEEL_RAD = 0.021;
  
  /** The robot width in meters. */
  public static final double BASE_WIDTH = 0.1543;
  
  /** The speed at which the robot moves forward in degrees per second. */
  public static final int FORWARD_SPEED = 500;
  
  /** The speed at which the robot rotates in degrees per second. */
  public static final int ROTATE_SPEED = 200;
  
  /** The motor acceleration in degrees per second squared. */
  public static final int ACCELERATION = 3000;
  
  /** Timeout period in milliseconds. */
  public static final int TIMEOUT_PERIOD = 3000;
  
  /** Obstacle size. */
 public static final double OBSTACLE_SIZE = 0.259;
  
 /** Waiting for a period of time. */
  public static final int WAIT_PERIOD = 1000;
  
  /** The speed at which the robot localizes to a point. */
  public static final int LOCALIZE_SPEED = 170;

  // Hardware resources

  /** The left motor. */
  public static final RegulatedMotor leftMotor = Motor.A;
  
  /** The right motor. */
  public static final RegulatedMotor rightMotor = Motor.D;
  
  /** The ultrasonic sensor. */
  public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
  
  /** The left color sensor. */
  public static final EV3ColorSensor leftColorSensor = new EV3ColorSensor(SensorPort.S2);
  
  /** The right color sensor. */
  public static final EV3ColorSensor rightColorSensor = new EV3ColorSensor(SensorPort.S3);

  // Software singletons
  
  /** The odometer. */
  public static Odometer odometer = Odometer.getOdometer();


  // Wi-Fi parameters

  /** Container for the Wi-Fi parameters. */
  public static Map<String, Object> wifiParameters;
  
  // This static initializer MUST be declared before any Wi-Fi parameters.
  static {
    if (RECEIVE_WIFI_PARAMS) {
      receiveWifiParameters();
    } else {
      wifiParameters = FileLoader.loadFrom(PARAMS_FILE);
    }
  }
  
  /** Red team number. */
  public static int redTeam = getIntWP("RedTeam");
  //public static int redTeam = 1;
  /** Red team's starting corner. */
  public static int redCorner = getIntWP("RedCorner");

  /** Green team number. */
  public static int greenTeam = getIntWP("GreenTeam");

  /** Green team's starting corner. */
  public static int greenCorner = getIntWP("GreenCorner");

  /** The Red Zone. */
  public static Region red = makeRegion("Red");

  /** The Green Zone. */
  public static Region green = makeRegion("Green");

  /** The Island. */
  public static Region island = makeRegion("Island");

  /** The red tunnel footprint. */
  public static Region tnr = makeRegion("TNR");

  /** The green tunnel footprint. */
  public static Region tng = makeRegion("TNG");

  /** The overpass. */
  public static Overpass overpass = makeOverpass();
  //public static Overpass overpass = new Overpass(new Point(5,4),new Point(9,2));
  
  /** The racetrack waypoints, zero-indexed. */
  public static List<Point> waypoints = makeWaypoints();

  /** The point to prepare for accross the overpass */
  public static Point overpassMileStone=new Point(0,0);

  /** The point to prepare for accross the overpass */
  public static Point intersectMileStone=new Point(0,0);
  
  public static Point overpassStartLocalizationPoint;
  
  public static Point overpassEndLocalizationPoint;
  
  public static Point overpassStartPoint;
  
  public static Point overpassEndPoint;
  
  public static double overpassAngle;
  
  public static Point underpassStartLocalizationPoint;
  
  public static Point underpassEndLocalizationPoint;
  
  public static Point tnLocalizationPoint;
  
  public static int colorIndex;// color index to indentify the color of the team, if 1: redTeam if 2:greenTeam
  
  /**
   * Receives Wi-Fi parameters from the server program.
   */
  public static void receiveWifiParameters() {
    // Only initialize the parameters if needed
    if (!RECEIVE_WIFI_PARAMS || wifiParameters != null) {
      return;
    }
   
    println("Waiting to receive Wi-Fi parameters.");
    
    // Connect to server and get the data, catching any errors that might occur
    try (var conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT)) {
      /* Connect to the server and wait until the user presses the "Start" button
       * in the GUI on their computer with the data filled in. */
      wifiParameters = conn.getData();
    } catch (Exception e) {
      errPrintln("Error: " + e.getMessage());
    }
  }
  
  /**
   * Returns the Wi-Fi parameter value associated with the given key.
   * 
   * @param key the Wi-Fi parameter key
   * @return the Wi-Fi parameter int value associated with the given key
   */
  public static double getWP(String key) {
    return ((BigDecimal) wifiParameters.getOrDefault(key, 0)).doubleValue();
  }
  
  /**
   * Returns the Wi-Fi parameter value associated with the given key as an int.
   * 
   * @param key the Wi-Fi parameter key
   * @return the Wi-Fi parameter int value associated with the given key
   */
  public static int getIntWP(String key) {
    return ((BigDecimal) wifiParameters.getOrDefault(key, 0)).intValue();
  }
  
  /** Convenience method to return a waypoint at the given index. */
  public static Point waypoint(int index) {
    if (waypoints.size() == 0) {
      throw new RuntimeException("The waypoint list is empty.");
    }
    if (index < 0 || index >= waypoints.size()) {
      throw new IllegalArgumentException("Waypoint " + index
          + " does not exist in the waypoint list.");
    }
    return waypoints.get(index);
  }
  
  /** Makes a point given a Wi-Fi parameter prefix. */
  public static Point makePoint(String paramPrefix) {
    return new Point(getWP(paramPrefix + "_x"), getWP(paramPrefix + "_y"));
  }
  
  /** Makes a region given a Wi-Fi parameter prefix. */
  public static Region makeRegion(String paramPrefix) {
    return new Region(makePoint(paramPrefix + "_LL"), makePoint(paramPrefix + "_UR"));
  }
  
  /** Makes the overpass defined by endpoints A and B. */
  public static Overpass makeOverpass() {
    return new Overpass(makePoint("OP_A"), makePoint("OP_B"));
  }
  
  /** Makes an ordered list of waypoints from the given Wi-Fi parameters. */
  public static List<Point> makeWaypoints() {
    return LongStream.range(0, wifiParameters.entrySet().stream()
        .filter(e -> e.getKey().matches("WP_[0-9]+_x")
            && ((BigDecimal) e.getValue()).compareTo(BigDecimal.ZERO) != 0).count())
        .mapToObj(i -> makePoint("WP_" + i))
        .collect(Collectors.toUnmodifiableList());
  }
  
  /**
   * Print method that prepends the team number before the object(s) to be printed, separated by
   * spaces, followed by a newline at the end. This method must be used instead of
   * System.out.println(), to ensure console output can be traced to the team.
   * Note that printing constantly (eg, in a loop) can be inefficient and impact robot performance.
   * Example usage/output:
   * 
   * <br>{@code println("Moving to " + waypoint(i));}
   * <br><pre>Team 01: Moving to (1, 3).</pre>
   */
  public static void println(Object... objects) {
    customPrint(true, System.out, objects);
  }
  
  /**
   * Print method for errors that prepends the team number before the object(s) to be printed,
   * separated by spaces, followed by a newline at the end. This method must be used instead of
   * System.err.println(), to ensure console output can be traced to the team. Example usage/output:
   * 
   * <br>{@code println("Moving to " + waypoint(i));}
   * <br><pre>Team 01: Moving to (1, 3).</pre>
   */
  public static void errPrintln(Object... objects) {
    customPrint(true, System.err, objects);
  }
  
  /**
   * Print method that prepends the team number before the object(s) to be printed, separated by
   * spaces, without a newline at the end. This method must be used instead of
   * System.out.print(), to ensure console output can be traced to the team.
   */
  public static void print(Object... objects) {
    customPrint(false, System.out, objects);
  }
  
  /**
   * Custom print method to allow customization while always prepending the team number before
   * items to be printed.
   * 
   * @param printNewlines if this is true, the printed output will end with a newline
   * @param ps the PrintStream, usually System.out or System.err
   * @param objects the item(s) to be printed
   */
  public static void customPrint(boolean printNewlines, PrintStream ps, Object... objects) {
    @SuppressWarnings("unused")
    var teamN = "Team " + (TEAM_NUMBER <= 9 ? "0" : "") + TEAM_NUMBER;
    if (objects == null) {
      if (printNewlines) {
        ps.println(teamN + ": null");
      } else {
        ps.print(teamN + ": null");
      }
    } else {
      ps.print(teamN + ": ");
      for (var o: objects) {
        ps.print(o + " ");
      }
      if (printNewlines) {
        ps.println();
      }
    }
  }

}
