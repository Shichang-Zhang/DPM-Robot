package ca.mcgill.ecse211.playingfield;

/** Line segment representing the overpass. */
public class Overpass {

  /** Endpoint A of the overpass. */
  public Point endpointA;
  
  /** Endpoint B of the overpass. */
  public Point endpointB;
  
  /** Constructs an Overpass defined by the two endpoints. */
  public Overpass(Point endpointA, Point endpointB) {
    this.endpointA = endpointA;
    this.endpointB = endpointB;
  }

}
