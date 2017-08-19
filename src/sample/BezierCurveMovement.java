package sample;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by Ruben on 8/9/2017.
 */
public class BezierCurveMovement implements Movement {



    private Point2D initialPoint, endPoint, controlPoint1,controlPoint2;
    private double detail;
    private ArrayList<Snapshot> robotSnapshots;


    private double initialAngle, endingAngle, fudge1,fudge2;
    private ArrayList<Snapshot> snapshots;

    public BezierCurveMovement(Point2D initialPoint, Point2D endPoint,  double initialAngle,
                               double endingAngle, double fudge1, double fudge2, double detail) {
        this.initialPoint = initialPoint;
        this.endPoint = endPoint;
        this.initialAngle = initialAngle;
        this.endingAngle = endingAngle;
        this.detail = detail;
        controlPoint1 = new Point2D.Double(initialPoint.getX() + Math.sin(Math.toRadians(initialAngle)) * fudge1,
                initialPoint.getY() + Math.cos(Math.toRadians(initialAngle)) * fudge1);
        controlPoint2 = new Point2D.Double(endPoint.getX() - Math.sin(Math.toRadians(endingAngle)) * fudge2,
                endPoint.getY() - Math.cos(Math.toRadians(endingAngle)) * fudge2);
        update();

    }

    public BezierCurveMovement(Point2D initialPoint, Point2D endPoint, Point2D controlPoint1, Point2D controlPoint2, int detail) {
        this.initialPoint = initialPoint;
        this.endPoint = endPoint;
        this.controlPoint1 = controlPoint1;
        this.controlPoint2 = controlPoint2;
        this.detail = detail;
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
        return initialPoint;
    }

    @Override
    public void setStartPoint(Point2D startPoint) {
        this.initialPoint=startPoint;
        update();
    }

    //based on [x,y]=(1–t)3P0+3(1–t)2tP1+3(1–t)t2P2+t3P3

    @Override
    public void update() {
        snapshots = new ArrayList<>();
        for(double i =0; i< refreshRate*detail ;i++){
            double t = (1+i)/(refreshRate * detail);
            double currentX = (Math.pow(1-t, 3)) * initialPoint.getX() +
                    (3 * Math.pow(1-t, 2) * t) * controlPoint1.getX() +
                    (3 * (1-t) * Math.pow(t,2)) * controlPoint2.getX() +
                    (Math.pow(t,3)) * endPoint.getX();
            double currentY = (Math.pow(1-t, 3)) * initialPoint.getY() +
                    (3 * Math.pow(1-t, 2) * t) * controlPoint1.getY() +
                    (3 * (1-t) * Math.pow(t,2)) * controlPoint2.getY() +
                    (Math.pow(t,3)) * endPoint.getY();
            double dy = 3*(1-t)*(1-t)*(-1) * initialPoint.getY() +
                    ((3- 12 * t  + 9 *t*t ) * controlPoint1.getY()) +
                    ((6 * t - 9 * t*t) * controlPoint2.getY()) +
                    (3 * t*t * endPoint.getY());
            double dx= 3*(1-t)*(1-t)*(-1) * initialPoint.getX() +
                    ((3- 12 * t  + 9 *t*t ) * controlPoint1.getX()) +
                    ((6 * t - 9 * t*t) * controlPoint2.getX()) +
                    (3 * t*t * endPoint.getX());
            double currentAngle = Math.atan(dy/dx);
            currentAngle = Math.toRadians(90) - currentAngle;
            currentAngle = Math.toDegrees(currentAngle);
            currentAngle = dx<0? currentAngle + 180 : currentAngle;
            snapshots.add(new Snapshot(currentX,currentY,currentAngle));

        }
        endingAngle = Math.atan( (endPoint.getY() - controlPoint2.getY()) / (endPoint.getX() - controlPoint2.getX()));
        endingAngle = Math.toRadians(90) - endingAngle;
        endingAngle = Math.toDegrees(endingAngle);
        endingAngle = (endPoint.getX() - controlPoint2.getX()) <0 ? endingAngle + 180 : endingAngle;
    }

    @Override
    public ArrayList<Snapshot> getSnapshots() {
        return snapshots;
    }

    @Override
    public double getEndAngle() {
        return endingAngle;
    }

    public double getInitialAngle() {
        return initialAngle;
    }

    public double getFudge1() {
        return fudge1;
    }

    public double getFudge2() {
        return fudge2;
    }
}
