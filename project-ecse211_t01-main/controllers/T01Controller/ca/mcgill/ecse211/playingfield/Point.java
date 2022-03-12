package ca.mcgill.ecse211.playingfield;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a coordinate point on the playing field grid.
 * 
 * @author Younes Boubekeur
 */
public class Point {

  /** The x coordinate in tile lengths. */
  public double x;

  /** The y coordinate in tile lengths. */
  public double y;

  /** The threshold for coordinates to be considered equal. This value is around 1mm. */
  private static final double EPSILON = 0.003; // ft

  /** Constructs a Point. The arguments are in tile lengths. */
  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  /**
   * Makes points from a string, eg "(1,1),(2.5,3)".
   * 
   * @return a list of points
   */
  public static List<Point> makePointsFromString(String s) {
    List<Point> result = new ArrayList<Point>();
    
    if (s == null || !s.contains(")")) {
      return result;
    }
    
    s = s.replaceAll("\\s+", "").replaceAll("\\(", "").replaceAll("\\),", "\\)");
    
    for (var fragment: s.split("\\)")) {
      var xy = fragment.split(",");
      result.add(new Point(Double.parseDouble(xy[0]), Double.parseDouble(xy[1])));
    }
    
    return result;
  }

  @Override public boolean equals(Object o) {
    if (!(o instanceof Point)) {
      return false;
    }
    var other = (Point) o;
    return Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON;
  }

  @Override public final int hashCode() {
    return Objects.hash(x, y);
  }

  @Override public String toString() {
    var fmt = new DecimalFormat("#.##");
    return "(" + fmt.format(x) + ", " + fmt.format(y) + ")";
  }

}
