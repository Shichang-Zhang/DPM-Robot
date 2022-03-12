package ca.mcgill.ecse211.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.mcgill.ecse211.playingfield.Point;
import ca.mcgill.ecse211.playingfield.Region;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the playing field logic. These tests should NOT depend on the robot.
 * 
 * @author Younes Boubekeur
 */
public class TestPlayingFieldLogic {

  /** Tests Point.makePointsFromString(). */
  @Test void testMakePointsFromString() {
    // Test usual case
    var expected = List.of(p(1, 2));
    var actual = Point.makePointsFromString("(1, 2)");
    assertEquals(expected, actual);
    
    expected = List.of(p(1, 2), p(2.5, 7.5), p(-7, -6.0));
    actual = Point.makePointsFromString("(1, 2), (2.5, 7.5), (-7, -6.0)");
    assertEquals(expected, actual);

    // Test edge cases
    expected = Collections.emptyList();
    actual = Point.makePointsFromString("");
    assertEquals(expected, actual);
    
    actual = Point.makePointsFromString(null);
    assertEquals(expected, actual);
  }

  @Test void testPointEquality() {
    assertEquals(p(2, 3), p(2, 3.001));
    assertNotEquals(p(2, 3), p(3, 2));
  }

  @Test void testRegionEquality() {
    assertEquals(new Region(p(0, -0.001), p(2, 3)), new Region(p(0, 0), p(2, 3.001)));
    assertNotEquals(new Region(p(0, 0), p(2, 3)), new Region(p(0, 0), p(3, 3)));
  }

  @Test void testRegionInputValidation() {
    assertThrows(IllegalArgumentException.class, () -> new Region(p(2, 3), p(0, 0)));
  }

  @Test void testRegionGetDimensions() {
    var region = new Region(p(0, 0), p(2, 3));
    assertEquals(2, region.getWidth());
    assertEquals(3, region.getHeight());
  }

  /** Helper method to create a Point. */
  private static Point p(double x, double y) {
    return new Point(x, y);
  }

}
