package RealBot;

import Lalaprofiling.Application.Snapshot;

/**
 * Created by raque on 8/24/2017.
 * holds a snapshot for the center of the robot and the left & right wheels
 * contains several useful methods and extra constants to be used by trajectory generator
 */
public class SuperSnapShot {

    private Snapshot s;
    private Snapshot leftWheelSnapshot, rightWheelSnapshot;
    private boolean leftReverse, rightReverse, reverse;
    private double botWidth;
    private int index;
    private double leftArcLength, rightArcLength, arcLength;


    public SuperSnapShot(Snapshot s, double botWidth,double arcLength, double leftArcLength, double rightArcLength, boolean reverse, boolean leftReverse, boolean rightReverse, int index){
        this.s = s;
        leftWheelSnapshot = new Snapshot(s.x-(botWidth * Math.cos(Math.toRadians(s.angle))),
                s.y +(botWidth * Math.sin(Math.toRadians(s.angle))), s.angle);
        rightWheelSnapshot = new Snapshot(s.x+(botWidth * Math.cos(Math.toRadians(s.angle))),
                s.y -(botWidth * Math.sin(Math.toRadians(s.angle))), s.angle);

        this.index=index;
        this.arcLength = arcLength;
        this.leftArcLength=leftArcLength;
        this.rightArcLength=rightArcLength;
        this.reverse=reverse;
        this.leftReverse=leftReverse;
        this.rightReverse=rightReverse;
    }

    public Snapshot getS() {
        return s;
    }

    public Snapshot getLeftWheelSnapshot() {
        return leftWheelSnapshot;
    }

    public Snapshot getRightWheelSnapshot() {
        return rightWheelSnapshot;
    }

    public double getBotWidth() {
        return botWidth;
    }

    public int getIndex() {
        return index;
    }

    public double getLeftArcLength() {
        return leftArcLength;
    }

    public double getRightArcLength() {
        return rightArcLength;
    }

    public double getArcLength() {
        return arcLength;
    }

    public boolean isLeftReverse() {
        return leftReverse;
    }

    public boolean isRightReverse() {
        return rightReverse;
    }

    public boolean isReverse() {
        return reverse;
    }
}

