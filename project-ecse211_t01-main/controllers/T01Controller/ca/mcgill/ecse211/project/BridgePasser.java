package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.playingfield.*;
import static ca.mcgill.ecse211.project.Resources.*;
import ca.mcgill.ecse211.project.*;

/**
 * This class handle bridge related problems:
 * 1. localize bridge
 * 2. travese bridge
 */
public class BridgePasser {

    Point start; // point to across bridge
    Point end; // point after acrossing the bridge
    Point startPoint=null;
    Point endPoint=null;    
    Region tn;
    Region startRegion;
    
    public void acrossBridge(){
      
      if(Resources.redTeam==1) {
        startRegion=Resources.red;
        println("Team 1 is red Team!");
      }else if(Resources.greenTeam==1) {
        startRegion=Resources.green;
        println("Team 1 is green Team!");
      }else {
        println("No Team Number matches your team!!");
      }
      
      if(colorIndex==1) {
        tn=Resources.tnr;;
        println("Use Red Team Tunnel:"+tn.ur+" to "+tn.ll);
      }else if(colorIndex==2) {
        tn=Resources.tng;
        println("Use Greem Team Tunnel:"+tn.ur+" to "+tn.ll);
      }else {
        println("No Team Number matches your team!!");
      }
      
        // step 1. calcalute point to accross Bridge
        findBoardingPoint();
        // step 2. travers to the other side of the bridge
        traverseBridge(start, end);
    }

    public void returnHome(){
      println("Return To the Starting Position.");
      // step 1. travers to the other side of the bridge
      Navigation.directTravelTo2(endPoint);
      Navigation.directTravelTo2(end);
      Navigation.directTravelTo2(start);
      Navigation.directTravelTo2(startPoint);
    }
    
    
    /** 
     * @param start
     * @param end
     */
    private void traverseBridge(Point start, Point end) {
        println("Travel from start region to the island.");
        println("Bridge: Start: " + start.toString() + ", End: " + end.toString());
        println("The Robot Should Navigate: Start: " + startPoint.toString() + ", Travel End: " + endPoint.toString()); 
        Navigation.directTravelTo2(startPoint);
        Navigation.directTravelTo2(start);
        Navigation.directTravelTo2(end);
        Navigation.directTravelTo2(endPoint);
        println("Finish Passing the Bridge");
    }

    private void findBoardingPoint() {
        //  ----------1--------ur
        //  4                   2
        //  ll--------3----------
        Point p1 = new Point ((tn.ur.x+tn.ll.x)/2, tn.ur.y);//vertical case
        Point p2 = new Point (tn.ur.x, (tn.ur.y+tn.ll.y)/2);//horizontalc ase
        Point p3 = new Point ((tn.ur.x+tn.ll.x)/2, tn.ll.y);//vertical case
        Point p4 = new Point (tn.ll.x , (tn.ur.y+tn.ll.y)/2);//horizontal case       
        if(isInStartRegion(p1)) {
          
          start=p1;
          end=p3;
          startPoint = new Point(start.x,start.y+0.5);
          endPoint = new Point(end.x,end.y-0.5);
          tnLocalizationPoint=new Point(endPoint.x+0.5,endPoint.y-0.5);
          if(isInIsland(tnLocalizationPoint)==false) {
            tnLocalizationPoint=new Point(endPoint.x-0.5,endPoint.y-0.5);
          }
          println("Up Mid Point"+ p1.toString()+" is Start Point of the tunnel, the tunnel is vertical");
          
        }else if(isInStartRegion(p2)) {
          println("Right Mid Point"+ p2.toString()+" is Start Point of the tunnel, the tunnel is horizontal");
          start=p2;
          end=p4;
          startPoint = new Point(start.x+0.5,start.y);
          endPoint = new Point(end.x-0.5,end.y);
          tnLocalizationPoint=new Point(endPoint.x-0.5,endPoint.y+0.5);
          if(isInIsland(tnLocalizationPoint)==false) {
            tnLocalizationPoint=new Point(endPoint.x-0.5,endPoint.y-0.5);
          }
        }else if(isInStartRegion(p3)){
          println("Down Mid Point"+ p3.toString()+" is Start Point of the tunnel, the tunnel is vertical");
          start=p3;
          end=p1;
          startPoint = new Point(start.x,start.y-0.5);
          endPoint = new Point(end.x,end.y+0.5);
          tnLocalizationPoint=new Point(endPoint.x+0.5,endPoint.y+0.5);
          if(isInIsland(tnLocalizationPoint)==false) {
            tnLocalizationPoint=new Point(endPoint.x-0.5,endPoint.y+0.5);
          }
        }else if(isInStartRegion(p4)){
          println("Left Mid Point"+ p4.toString()+" is Start Point of the tunnel, the tunnel is horizontal");
          start=p4;
          end=p2;
          startPoint = new Point(start.x-0.5,start.y);
          endPoint = new Point(end.x+0.5,end.y);
          tnLocalizationPoint=new Point(endPoint.x+0.5,endPoint.y+0.5);
          if(isInIsland(tnLocalizationPoint)==false) {
            tnLocalizationPoint=new Point(endPoint.x+0.5,endPoint.y-0.5);
          }
        }else {
          println("No point is okay to enter the tunnel!");
        }
        println("After-Tunnel Localization Point: "+tnLocalizationPoint);
    }

    

    
    private boolean isInRed(Point p) {
      if(p.x>=Resources.red.ll.x && p.x<=Resources.red.ur.x && p.y>=Resources.red.ll.y && p.y<=Resources.red.ur.y) {
        return true;
      }
      return false;
    }
    
    private boolean isInGreen(Point p) {
      if(p.x>=Resources.green.ll.x && p.x<=Resources.green.ur.x && p.y>=Resources.green.ll.y && p.y<=Resources.green.ur.y) {
        return true;
      }
      return false;
    }
    
    private boolean isInIsland(Point p) {
      if(p.x>Resources.island.ll.x && p.x<Resources.island.ur.x && p.y>Resources.island.ll.y && p.y<Resources.island.ur.y) {
        return true;
      }
      return false;
    }
    
    private boolean isInStartRegion(Point p) {
      if(colorIndex==1) {
        return isInRed(p);
      }else if(colorIndex==2) {
        return isInGreen(p);
      }else {
        return false;
      }
    }
    
    

    
}
