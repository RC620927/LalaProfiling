package Lalaprofiling.Application;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by raque on 9/10/2017.
 */
public class StillMovement implements Movement {

    double detail;
    Point2D endPoint, startPoint;
    boolean reverse;

    double initialAngle, endAngle;
    double time;

    private ArrayList<Snapshot> robotSnapshots;

    public StillMovement(Point2D startPoint, double initialAngle, double time, double detail){
        this.startPoint=startPoint;
        this.initialAngle=initialAngle;
        this.endAngle = initialAngle;
        this.detail = detail;
        this.reverse=false;
        this.time=time;
        update();
    }


    @Override
    public double getDetail() {
        return detail;
    }

    @Override
    public void setDetail(double detail) {
        this.detail = detail;
        update();
    }

    @Override
    public Point2D getEndPoint() {
        return endPoint;
    }

    @Override
    public Point2D getStartPoint() {
        return startPoint;
    }

    @Override
    public void setStartPoint(Point2D startPoint) {
        this.startPoint=startPoint;
        update();
    }

    //creates all the snapshots for the given constants
    //used everytime a variable is changed or the first time initiated
    @Override
    public void update() {

        endPoint = startPoint;

        robotSnapshots = new ArrayList<>();

        robotSnapshots.add(new Snapshot(startPoint.getX(),startPoint.getY(),initialAngle));
    }

    @Override
    public ArrayList<Snapshot> getSnapshots() {
        return robotSnapshots;
    }


    @Override
    public double getEndAngle() {
        return endAngle;
    }

    @Override
    public double getInitialAngle() {
        return initialAngle;
    }

    public void setInitialAngle(double initialAngle) {
        this.initialAngle = initialAngle;
        endAngle=initialAngle;
        autoDetail();
        update();
    }

    public void setEndAngle(double endAngle) {
        this.endAngle = endAngle;
        this.initialAngle=endAngle;
        autoDetail();
        update();
    }


    public boolean getReverse() {
        return reverse;
    }

    @Override
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    private void autoDetail(){
        setDetail(Math.abs(RotateMovement.getChangeTheta(getEndAngle(),getInitialAngle()))/200);
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public StillMovement clone() {
        return new StillMovement(new Point2D.Double(this.startPoint.getX(),
                this.startPoint.getY()), initialAngle, time, detail);
    }

}
