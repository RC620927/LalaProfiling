package RealBot;

import sample.Snapshot;

/**
 * Created by raque on 8/24/2017.
 */
public class SuperSnapShot {

    private Snapshot s;
    private Snapshot leftWheelSnapshot, rightWheelSnapshot;
    private boolean leftReverse, rightReverse;
    private double botWidth;
    private int index;
    private double leftArcLength, rightArcLength;

    public SuperSnapShot(Snapshot s, double botWidth, double leftArcLength, double rightArcLength, boolean leftReverse, boolean rightReverse, int index){
        this.s = s;
        leftWheelSnapshot = new Snapshot(s.x-(botWidth * Math.cos(Math.toRadians(s.angle))),
                s.y +(botWidth * Math.sin(Math.toRadians(s.angle))), s.angle);
        rightWheelSnapshot = new Snapshot(s.x+(botWidth * Math.cos(Math.toRadians(s.angle))),
                s.y -(botWidth * Math.sin(Math.toRadians(s.angle))), s.angle);

        this.index=index;
        this.leftArcLength=leftArcLength;
        this.rightArcLength=rightArcLength;
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

    public boolean isLeftReverse() {
        return leftReverse;
    }

    public boolean isRightReverse() {
        return rightReverse;
    }
}

