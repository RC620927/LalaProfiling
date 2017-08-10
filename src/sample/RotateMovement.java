package sample;

import javafx.scene.shape.Path;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by Ruben on 8/9/2017.
 */


public class RotateMovement implements Movement {

    double duration;
    Point2D endPoint, startPoint;
    Path p;

    double initialAngle, endAngle;

    ArrayList<Snapshot> robotSnapshots;

    public RotateMovement(Point2D startPoint, double initialAngle, double endAngle){
        this.startPoint=startPoint;
        this.initialAngle=initialAngle;
        this.endAngle = endAngle;
        update();
    }


    @Override
    public double getDuration() {
        return duration;
    }

    @Override
    public void setDuration(double duration) {
        this.duration=duration;
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
    }

    @Override
    public void update() {

    }

}
