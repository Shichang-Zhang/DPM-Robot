import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class extracts your robot into a separate wbo file to make it easier to manage the
 * competition. It is called from the Makefile.
 * This class must not be modified without permission from the course staff.
 * 
 * <p>The extraction algorithm is as follows:
 * 
 * <pre>
 * Determine the team number from the renamed TXXController.
 * Open the worlds/project.wbt file and search for "DPM-Robot {".
 * From the portion of the file after that string, keep appending characters until the robot is
 * completely appended. To do this, keep a counter of open curly braces and stop iteration when
 * it is zero.
 * </pre>
 *  
 * @author Younes Boubekeur
 */
public class ExtractRobot {
  
  /** The location of the Webots world file with respect to the Makefile. */
  public static final String WORLDS_DIR = "../../worlds";
  
  /** The location of the Webots world file with respect to the Makefile. */
  public static final String WBT_FILE = WORLDS_DIR + "/project.wbt";
  
  /** The main entry point. */
  public static void main(String[] args) {
    var teamNum = calculateTeamNumber();
    var teamNumStr = (teamNum <= 9 ? "0" : "") + teamNum;
    var robot = extractRobot(WBT_FILE);
    saveRobotToFile(robot, WORLDS_DIR + "/robot" + teamNumStr + ".wbo");
  }
  
  /** Determines the team number from the renamed TXXController. */
  private static int calculateTeamNumber() {
    var folderNames = System.getProperty("user.dir").split("/");
    var folderName = folderNames[folderNames.length - 1];
    try {
      return Integer.parseInt(folderName.replace("T", "").replace("Controller", ""));
    } catch (NumberFormatException e) {
      System.err.println("Team number not set!");
    }
    return 0;
  }
  
  /** Extracts the first declared DPM-Robot from the given file. */
  private static String extractRobot(String filename) {
    var result = "DPM-Robot {";
    var level = 1; // the nesting level created by {}
    try {
      var wbtFile = Files.readString(Path.of(filename));
      var wbtFileContentAfterDpmRobotDecl = wbtFile.split("DPM-Robot\\s*\\{")[1].toCharArray();
      var i = 0;
      while (level > 0) {
        var curr = wbtFileContentAfterDpmRobotDecl[i];
        result += curr;
        i++;
        if (curr == '{') {
          level++;
        } else if (curr == '}') {
          level--;
        }
      }
    } catch (IOException e) {
      System.err.println("Could not read " + filename + " due to error: " + e.getMessage());
    } catch (ArrayIndexOutOfBoundsException e) {
      System.err.println("No valid DPM-Robot declared in " + filename);
    }
    return result + "\n";
  }
  
  /** Saves the given robot to the given file. */
  public static void saveRobotToFile(String robot, String filename) {
    try {
      Files.writeString(Path.of(filename), robot);
    } catch (IOException e) {
      System.err.println("Failed to save extacted robot to " + filename + " due to error: "
          + e.getMessage());
    }
  }

}
