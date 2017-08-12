package sample;

import javafx.scene.shape.Path;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by Ruben on 8/9/2017.
 */


public class RotateMovement implements Movement {



    int detail;
    Point2D endPoint, startPoint;

    double initialAngle, endAngle;

    private ArrayList<Snapshot> robotSnapshots;

    public RotateMovement(Point2D startPoint, double initialAngle, double endAngle, int detail){
        this.startPoint=startPoint;
        this.initialAngle=initialAngle;
        this.endAngle = endAngle;
        this.detail = detail;
        update();
    }


    @Override
    public int getDetail() {
        return detail;
    }

    @Override
    public void setDetail(int detail) {
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

    @Override
    public void update() {

        endPoint = startPoint;

        robotSnapshots = new ArrayList<>();

        int snapShotCount = refreshRate * detail;
        double delTheta = getChangeTheta(endAngle,initialAngle) / (snapShotCount);
        for(int i =0;i<snapShotCount;i++){
            double currentAngle = Resources.mod((initialAngle + (delTheta * (i+1))), 360);
            robotSnapshots.add(i,new Snapshot(startPoint.getX(), startPoint.getY(), currentAngle));
        }
    }

    @Override
    public ArrayList<Snapshot> getSnapshots() {
        return robotSnapshots;
    }

    private static double getChangeTheta(double endingAngle, double initialAngle){

        double delTheta = Resources.mod(endingAngle - initialAngle, 360);
        if(delTheta>180){
            delTheta = 180 - Resources.mod(delTheta, 180);
            delTheta /= -1;
        }else{
            delTheta = Resources.mod(endingAngle, 180);
        }
        return delTheta;
    }




}
