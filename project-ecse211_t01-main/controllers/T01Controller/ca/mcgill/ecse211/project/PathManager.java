package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import java.util.ArrayList;
import java.util.List;
import ca.mcgill.ecse211.playingfield.Point;

/**
 * This class arrange path for the robot
 */
public class PathManager {

    /**
     * This method does three functions:
     * 1. find the closest point as startpiont, rearrange the waypoints list
     * 2. find the point to across the overpass.
     * 3. find the point to move under the overpass.
     * @throws Exception 
     */
    public static void arrangePoints() {
        // update new waypoint list
        Resources.waypoints = findStartPoint();
        // find the point to prepare for acrossing overpass
//        Resources.overpassMileStone = findOverpassPoint();
        // find the point pair that intersect over pass
//        Resources.intersectMileStone = findIntersectPair();
        
        ArrayList<Point[]> overAndUnder = overpassRelated();
        overpassMileStone=overAndUnder.get(0)[0];
        intersectMileStone=overAndUnder.get(1)[0];
        if(overpassMileStone==null) overpassMileStone=new Point(0,0);
        if(intersectMileStone==null) intersectMileStone=new Point(0,0);
        
        Point[] startAndEnd = findOverpassStartAndEnd();
        Resources.overpassStartLocalizationPoint=startAndEnd[0];
        Resources.overpassEndLocalizationPoint=startAndEnd[1];
        
        startAndEnd = findUnderpassStartAndEnd(intersectMileStone);
        Resources.underpassStartLocalizationPoint=startAndEnd[0];
        Resources.underpassEndLocalizationPoint=startAndEnd[1];
        
        println(
            " New waypoints: " + waypoints + "\n" +
            " Points to across overpass: " + overpassMileStone.toString() + "\n" +
            " Point that start to intersect: " + intersectMileStone.toString() +"\n"+
            " OverpassStartLocalizationPoint: " + overpassStartLocalizationPoint.toString() +"\n"+
            " OverpassEndLocalizationPoint: " + overpassEndLocalizationPoint.toString() + "\n"+
            " Overpass Start: " + Resources.overpassStartPoint.toString() + "\n"+
            " Overpass End: " + Resources.overpassEndPoint.toString()+ "\n" +
            " Overpass Angle (Relative to x-axis): "+Resources.overpassAngle+ "\n"+
            " Underpass Start: " + Resources.underpassStartLocalizationPoint.toString() + "\n"+
            " Underpass End: " + Resources.underpassEndLocalizationPoint.toString()+ "\n" 
            
        );
    }

    private static List<Point> findStartPoint(){
      //eliminate invalid points
      List<Point> temp = new ArrayList<Point>();
      for(Point p:waypoints) {
        if(0<=p.x && p.x<=15 && 0<=p.y && p.y<=9) {
          temp.add(p);
        }
      }
      waypoints = temp;
      
      Point tnPoint;
      if(colorIndex==1) {
        tnPoint=new Point((tnr.ll.x+tnr.ur.x)/2,(tnr.ll.y+tnr.ur.y)/2);
      }else {
        tnPoint=new Point((tng.ll.x+tng.ur.x)/2,(tng.ll.y+tng.ur.y)/2);
      }
      double minDistance = Integer.MAX_VALUE;
      int minIndex = 0;
      for(Point p: waypoints){
          double curDistance = Math.sqrt(Math.pow(p.x - tnPoint.x, 2) + Math.pow(p.y-tnPoint.y, 2));
          // looking for small distance
          if(curDistance < minDistance){
              minDistance = curDistance; 
              minIndex = waypoints.indexOf(p);
          }
      }
      List<Point> newWayPoints=new ArrayList<Point>();
      if(minIndex==0) {
        newWayPoints=waypoints;
      }else if(minIndex==waypoints.size()-1) {
        newWayPoints.add(waypoints.get(waypoints.size()-1));
        for(Point p:waypoints) {
          if(p.x!=waypoints.get(waypoints.size()-1).x||p.y!=waypoints.get(waypoints.size()-1).y) {
            newWayPoints.add(p);
          }         
        }
      }else {
        for(int i=minIndex;i<waypoints.size();i++) {
          newWayPoints.add(waypoints.get(i));
        }
        for(int i=0;i<minIndex;i++) {
          newWayPoints.add(waypoints.get(i));
        }
      }

      return newWayPoints;
  }

    
    private static ArrayList<Point[]> overpassRelated() {
      ArrayList<Point[]> overpassRelated = new ArrayList<Point[]>();
      Point[] over = new Point[2] ; 
      Point[] under = new Point[2];
      overpassRelated.add(over);
      overpassRelated.add(under);
      
      //k=(y1-y2)/(x1-x2)
      double overpassSlope;
      if(overpass.endpointA.x==overpass.endpointB.x) {
        overpassSlope=Double.MAX_VALUE;
      }else {
        overpassSlope= (overpass.endpointB.y-overpass.endpointA.y)/(overpass.endpointB.x-overpass.endpointA.x);
      }
      //b=y-kx
      double overpass_y_intersection = overpass.endpointA.y-overpassSlope*(overpass.endpointA.x);
      //y=kx+b
      Line overpassLine = new Line(overpassSlope,overpass_y_intersection);
      
      double overpassRelativeAngle = Math.atan(Math.abs(overpassSlope))*180/Math.PI;
      if(overpassSlope > 0) {
        overpassRelativeAngle = 180 - overpassRelativeAngle;
      }
      for(int i =0;i<waypoints.size()-1;i++){
        Point current = waypoints.get(i);
        Point next = waypoints.get(i+1);
        double waypointSlope;
        if(current.x==next.x) {
          waypointSlope=Double.MAX_VALUE;
        }else{
          waypointSlope= (next.y-current.y)/(next.x-current.x);}
        double waypoint_y_intersection = current.y-waypointSlope*(current.x);
        Line waypointLine = new Line(waypointSlope,waypoint_y_intersection);
        boolean isIntersect;
        if(overpassLine.slope==waypointSlope) {
          isIntersect = false;
          //continue;
        }else {
          double intersection_x;
          double intersection_y;
          if(overpass.endpointA.x==overpass.endpointB.x) {
            intersection_x = overpass.endpointA.x;
            intersection_y = waypointLine.slope*intersection_x+waypointLine.y_intersection;
          }else if (current.x==next.x){
            intersection_x = current.x;
            intersection_y = overpassLine.slope*intersection_x+overpassLine.y_intersection;
          }else {
            intersection_x= (waypointLine.y_intersection-overpassLine.y_intersection)/(overpassLine.slope-waypointLine.slope);
            intersection_y = waypointLine.slope*intersection_x+waypointLine.y_intersection;
          }
          boolean xInOverpassLineRange = (intersection_x>=Math.min(overpass.endpointA.x, overpass.endpointB.x)) && (intersection_x<=Math.max(overpass.endpointA.x, overpass.endpointB.x));
          boolean xInWaypointLineRange = (intersection_x>=Math.min(current.x, next.x)) && (intersection_x<=Math.max(current.x, next.x));
          boolean yInOverpassLineRange = (intersection_y>=Math.min(overpass.endpointA.y, overpass.endpointB.y)) && (intersection_y<=Math.max(overpass.endpointA.y, overpass.endpointB.y));
          boolean yInWaypointLineRange = (intersection_y>=Math.min(current.y, next.y)) && (intersection_y<=Math.max(current.y, next.y));;
          System.out.println(xInOverpassLineRange+" " +xInWaypointLineRange+"  "+intersection_x);
          if(xInOverpassLineRange && xInWaypointLineRange && yInOverpassLineRange && yInWaypointLineRange) {
            isIntersect = true;
          }else {
            isIntersect = false;
            //continue;
          }
        }
        System.out.println(isIntersect);
        if(isIntersect) {
          double waypointRelativeAngle = Math.atan(Math.abs(waypointSlope))*180/Math.PI;
          if(waypointSlope > 0) {
            waypointRelativeAngle = 180 - waypointRelativeAngle;
          }
          System.out.println(waypointRelativeAngle);
          double angleDifference = Math.abs(overpassRelativeAngle-waypointRelativeAngle);
          if(angleDifference>=45 && angleDifference<=135) {
            //go under overpass
            under[0]=current;
            under[1]=next;
          }else {
            //go over overpass
            over[0]=current;
            over[1]=next;
          }
        }
      }
      return overpassRelated;
    }

    
    private static Point findOverpassPoint(){
        double overpassAngle = Math.toDegrees(Math.atan((overpass.endpointA.y - overpass.endpointB.y)/(overpass.endpointA.x - overpass.endpointB.x)));
        double overpasslength = Math.sqrt(
            Math.pow(overpass.endpointA.x - overpass.endpointB.x, 2) +
            Math.pow(overpass.endpointA.y - overpass.endpointB.y, 2)
        );

        int index = 0;
        // point to start to accross overpass
        Point start = new Point(0,0);
        // point after acrossing overpass
        Point end = new Point(0,0);
        Point ans = new Point(0,0);
        
        while(index < 10){

            start = waypoints.get(index);
            end = index+1==10? waypoints.get(0): waypoints.get(index+1);

            double twoPointAngle = Math.toDegrees(Math.atan((end.y -start.y)/(end.x -start.x)));
            double twoPointlength = Math.sqrt(
                Math.pow(end.x -start.x, 2) +
                Math.pow(end.y -start.y, 2)
            );

            // System.out.println("twoPointAngle: " + twoPointAngle + " twoPointlength:" + twoPointlength + " overpassAngel:" + overpassAngle + " length:" + overpasslength);
            // two angles have similar direction is their ration is bigger than 0
            // if the distance is bigger than overpass length, it implicitly indicates overpass is in the middle of two points
            if(twoPointAngle/overpassAngle > 0 && twoPointlength >= overpasslength){
                ans = start;
            }
            index++;
        }

        return ans;
    }

    
    private static Point findIntersectPair(){

        int index = 0;
        // point to start to accross overpass
        Point start = new Point(0,0);
        // point after acrossing overpass
        Point end = new Point(0,0);
        Point ans = new Point(0,0);
        
        while(index < 10){

            start = waypoints.get(index);
            end = index+1==10? waypoints.get(0): waypoints.get(index+1);

            // System.out.println("twoPointAngle: " + twoPointAngle + " twoPointlength:" + twoPointlength + " overpassAngel:" + overpassAngle + " length:" + overpasslength);
 
            if(isIntersect(start, end, overpass.endpointA, overpass.endpointB)){
                ans = start;
            }
            index++;
        }

        return ans;
    }

    private static boolean isIntersect(Point a, Point b, Point c, Point d){

        if(Math.max(a.x, b.x) < Math.min(c.x, d.x)) return false;
        if(Math.max(c.x, d.x)< Math.min(a.x, b.x)) return false;
        if(Math.max(a.y, b.y) < Math.min(c.y, d.y)) return false;
        if(Math.max(c.y, d.y) < Math.min(a.y, b.y)) return false;


        return true;
    }
    
    private static Point[] findOverpassStartAndEnd() {
      Point[] startAndEnd = new Point[2];//0:start point    1:end point
      Point A; //localization point near overpass.endpoint.A
      Point B; //localization point near overpass.endpoint.B
      double angle = Math.atan2(Math.abs(overpass.endpointB.y-overpass.endpointA.y),Math.abs(overpass.endpointB.x-overpass.endpointA.x))*180/Math.PI;
      double adjustDistance=0;
      if(angle>45) {
        adjustDistance=1*Math.tan(Math.toRadians(90-angle));
      }else if(angle<45){
        adjustDistance=1*Math.tan(Math.toRadians(angle));
      }else {
        adjustDistance=0;
      }
      
      if(overpass.endpointA.y>overpass.endpointB.y) {
        if(angle>45) {
          A=new Point(overpass.endpointA.x-adjustDistance,overpass.endpointA.y+1);
          B=new Point(overpass.endpointB.x+adjustDistance,overpass.endpointB.y-1);
        }else if(angle<45) {
          A=new Point(overpass.endpointA.x-1,overpass.endpointA.y+adjustDistance);
          B=new Point(overpass.endpointB.x+1,overpass.endpointB.y-adjustDistance);
        }else {
          A=new Point(overpass.endpointA.x-1,overpass.endpointA.y+1);
          B=new Point(overpass.endpointB.x+1,overpass.endpointB.y-1);
        }     
      }else if(overpass.endpointA.y<overpass.endpointB.y) {
        if(angle>45) {
          A=new Point(overpass.endpointA.x-adjustDistance,overpass.endpointA.y-1);
          B=new Point(overpass.endpointB.x+adjustDistance,overpass.endpointB.y+1);
        }else if(angle<45) {
          A=new Point(overpass.endpointA.x-1,overpass.endpointA.y-adjustDistance);
          B=new Point(overpass.endpointB.x+1,overpass.endpointB.y+adjustDistance);
        }else {
          A=new Point(overpass.endpointA.x-1,overpass.endpointA.y-1);
          B=new Point(overpass.endpointB.x+1,overpass.endpointB.y+1);
        }
        //if overpass is horizontal
      }else {
        A=new Point(overpass.endpointA.x-1,overpass.endpointA.y);
        B=new Point(overpass.endpointB.x+1,overpass.endpointB.y);
      }
      //if overpass is vertical
      if(overpass.endpointA.x==overpass.endpointB.x) {
        if(overpass.endpointA.y>overpass.endpointB.y) {
          A=new Point(overpass.endpointA.x,overpass.endpointA.y+1);
          B=new Point(overpass.endpointB.x,overpass.endpointB.y-1);
        }else {
          A=new Point(overpass.endpointA.x,overpass.endpointA.y-1);
          B=new Point(overpass.endpointB.x,overpass.endpointB.y+1);
        }
        
      }
      
      if(Resources.redTeam==1||Resources.greenTeam==1) {
        double distanceA= Math.sqrt(Math.pow(A.x - Resources.overpassMileStone.x, 2) + Math.pow(A.y - Resources.overpassMileStone.y, 2));
        double distanceB= Math.sqrt(Math.pow(B.x - Resources.overpassMileStone.x, 2) + Math.pow(B.y - Resources.overpassMileStone.y, 2));
        if(distanceA<distanceB) {
          //A is near the overpass enter point 
          startAndEnd[0]=A;
          startAndEnd[1]=B;
          Resources.overpassStartPoint=overpass.endpointA;
          Resources.overpassEndPoint=overpass.endpointB;
        }else {
          startAndEnd[0]=B;
          startAndEnd[1]=A;
          Resources.overpassStartPoint=overpass.endpointB;
          Resources.overpassEndPoint=overpass.endpointA;
        }
        
      }else {
        println("No Team Number matches your team!!");
      }
       Resources.overpassAngle=Navigation.getDestinationAngle(overpassStartPoint, overpassEndPoint);
      return startAndEnd;
    }
    
    
    private static Point[] findUnderpassStartAndEnd(Point current) {
      Point[] startAndEnd = new Point[2];//0:start point    1:end point
      Point a=new Point(0,0); //localization point at right of overpass
      Point b=new Point(0,0); //localization point ar left of overpass
      double adjustDistance=0.7;
      
      double x1 = (overpass.endpointA.x+overpass.endpointB.x)/2 ;
      double y1 = (overpass.endpointA.y+overpass.endpointB.y)/2;
      double slope;
      if(overpass.endpointA.x==overpass.endpointB.x) {
        slope = Double.MAX_VALUE;
      }else {
        slope =(overpass.endpointB.y-overpass.endpointA.y)/(overpass.endpointB.x-overpass.endpointA.x);
      }
      double overpass_y_intersection = overpass.endpointA.y-slope*(overpass.endpointA.x);
      
      if(slope > 0) {
        a =  new Point(x1+adjustDistance,y1-adjustDistance/Math.abs(slope));
        b = new Point(x1-adjustDistance,y1+adjustDistance/Math.abs(slope));
      }else if(slope<0){
        a =  new Point(x1+adjustDistance,y1+adjustDistance/Math.abs(slope));
        b = new Point(x1-adjustDistance,y1-adjustDistance/Math.abs(slope));
      }else {
        //slope = 0
        a =  new Point(x1,y1-adjustDistance);
        b = new Point(x1,y1+adjustDistance);
      }
      
      if(overpass.endpointA.x==overpass.endpointB.x) {
        //overpass is verticle
        if(current.x>overpass.endpointA.x) {
        //on the right side, travel to left side
          startAndEnd[0]=a;
          startAndEnd[1]=b;
        }else {
        //on the left side, travel to right side
          startAndEnd[0]=b;
          startAndEnd[1]=a;
        } 
      }else if(slope*current.x+ overpass_y_intersection> current.y) {
        //on the right side, travel to left side
        startAndEnd[0]=a;
        startAndEnd[1]=b;
      }else {
        //on the left side, travel to right side
        startAndEnd[0]=b;
        startAndEnd[1]=a;
      }
      
      return startAndEnd;
    }
    
    
    
    
}

/**
 * y=kx+b
 */
class Line {
  public Line(double slope, double y_intersection) {
    this.slope = slope;
    this.y_intersection=y_intersection;
  }
  double slope;
  double y_intersection;
}
