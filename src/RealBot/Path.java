package RealBot;

import rc.CastrooOrnelas.datatypes.RList;
import sample.Resources;
import sample.Snapshot;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by raque on 8/25/2017.
 */
public class Path {

    ArrayList<SuperSnapShot> superSnapshots;
    private double totalLeftArcLength, totalRightArcLength, totalArcLength;
    private Point2D startPoint;

    public Path(ArrayList<Snapshot> snapshots, double botWidth, Snapshot startPoint){
        superSnapshots = new ArrayList<>();
        int index =0;
        //left to right.
        double currentLeftArcLength =0, currentRightArcLength =0, currentArcLength = 0;
        boolean leftReverse = false;
        boolean rightReverse= false;
        boolean reverse = false;
        Snapshot pLeft = new Snapshot(startPoint.x-(0.5 * botWidth * Math.cos(Math.toRadians(startPoint.angle))),
                startPoint.y +(0.5 * botWidth * Math.sin(Math.toRadians(startPoint.angle))), startPoint.angle);
        Snapshot pRight = new Snapshot(startPoint.x+(0.5 * botWidth * Math.cos(Math.toRadians(startPoint.angle))),
                startPoint.y -(0.5 * botWidth * Math.sin(Math.toRadians(startPoint.angle))), startPoint.angle);
        Snapshot ps = startPoint;

        for(Snapshot s: snapshots){
            Snapshot leftWheelSnapshot = new Snapshot(s.x-(0.5 * botWidth * Math.cos(Math.toRadians(s.angle))),
                    s.y +(0.5 * botWidth * Math.sin(Math.toRadians(s.angle))), s.angle);
            Snapshot rightWheelSnapshot = new Snapshot(s.x+(0.5 * botWidth * Math.cos(Math.toRadians(s.angle))),
                    s.y -(0.5 * botWidth * Math.sin(Math.toRadians(s.angle))), s.angle);
            double leftDelD, rightDelD, delD;

            leftDelD = Math.hypot(leftWheelSnapshot.x - pLeft.x, leftWheelSnapshot.y - pLeft.y);
            rightDelD = Math.hypot(rightWheelSnapshot.x - pRight.x, rightWheelSnapshot.y - pRight.y);
            delD = Math.hypot(s.x - ps.x, s.y - ps.y);

            currentLeftArcLength+= leftDelD;
            currentRightArcLength+= rightDelD;
            currentArcLength+= delD;

            double movementAngleLeft = Resources.angleOf(new Point2D.Double(pLeft.x, pLeft.y),
                    new Point2D.Double(leftWheelSnapshot.x, leftWheelSnapshot.y));
            double movementAngleRight = Resources.angleOf(new Point2D.Double(pRight.x, pRight.y),
                    new Point2D.Double(rightWheelSnapshot.x, rightWheelSnapshot.y));
            double movementAngle = Resources.angleOf(new Point2D.Double(ps.x, ps.y),
                    new Point2D.Double(s.x, s.y));
            reverse= !Resources.forwardsMovement(s.angle, movementAngle);
            leftReverse= !Resources.forwardsMovement(s.angle, movementAngleLeft);
            rightReverse= !Resources.forwardsMovement(s.angle, movementAngleRight);

            pLeft = leftWheelSnapshot;
            pRight = rightWheelSnapshot;
            ps = s;

            superSnapshots.add(new SuperSnapShot(s, botWidth,currentArcLength, currentLeftArcLength,currentRightArcLength,reverse, leftReverse,rightReverse, index));
            index++;
        }
        totalLeftArcLength=currentLeftArcLength;
        totalRightArcLength=currentRightArcLength;
        totalArcLength =currentArcLength;

        //back to front make sure you decelerate.

    }



    public SuperSnapShot seekLeft(double arcLength){
        boolean stop=false;

        SuperSnapShot closest = superSnapshots.get(superSnapshots.size()-1);
        double prevD = 100000000000.0;

        for(int i=0; !stop;i++){
            if(i < superSnapshots.size()){
                double d = arcLength - superSnapshots.get(i).getLeftArcLength();
                if(Math.abs(d) < Math.abs(prevD)){
                    closest= superSnapshots.get(i);
                    prevD = d;
                }
                if(d<0){
                    stop = true;
                }
            }else{
                closest = null;
                stop=true;
            }
        }
        return closest;
    }

    public SuperSnapShot seekRight(double arcLength){
        boolean stop=false;

        SuperSnapShot closest = superSnapshots.get(superSnapshots.size()-1);
        double prevD = 100000000000.0;

        for(int i=0; !stop && (i<superSnapshots.size()-1);i++){
            double d = arcLength - superSnapshots.get(i).getRightArcLength();
            if(Math.abs(d) < Math.abs(prevD)){
                closest= superSnapshots.get(i);
                prevD = d;
            }
            if(d<0){
                stop = true;
            }
        }
        return closest;
    }

    public SuperSnapShot seek(double arcLength){
        boolean stop=false;

        SuperSnapShot closest = superSnapshots.get(superSnapshots.size()-1);
        double prevD = 100000000000.0;

        for(int i=0; !stop && (i<superSnapshots.size()-1);i++){
            double d = arcLength - superSnapshots.get(i).getArcLength();
            if(Math.abs(d) < Math.abs(prevD)){
                closest= superSnapshots.get(i);
                prevD = d;
            }
            if(d<0){
                stop = true;
            }
        }
        return closest;
    }

    public double getTotalLeftArcLength() {
        return totalLeftArcLength;
    }

    public double getTotalRightArcLength() {
        return totalRightArcLength;
    }

    public double getTotalArcLength(){return totalArcLength;}

    public Point2D getStartPoint() {
        return startPoint;
    }
}
