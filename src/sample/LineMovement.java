package sample;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by Ruben on 8/9/2017.
 */
public class LineMovement implements Movement {


    Point2D endPoint, startPoint;
    double initialAngle;
    double detail;


    private ArrayList<Snapshot> robotSnapshots;

    public LineMovement(Point2D startPoint, Point2D endPoint, double detail) {
        this.endPoint = endPoint;
        this.startPoint = startPoint;
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

    public void setEndPoint(Point2D endPoint){
        this.endPoint = endPoint;
        update();
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

        initialAngle = Math.atan( (endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX()));
        initialAngle = Math.toRadians(90) - initialAngle;
        initialAngle = Math.toDegrees(initialAngle);
        initialAngle = (endPoint.getX() - startPoint.getX()) <0 ? initialAngle + 180 : initialAngle;


        robotSnapshots = new ArrayList<>();
        double snapshotCount = detail * refreshRate;
        double delX = (endPoint.getX() - startPoint.getX()) / snapshotCount;
        double delY = (endPoint.getY() - startPoint.getY()) / snapshotCount;
        for(int i = 0; i<snapshotCount;i++){
            double x = startPoint.getX() + ((i+1) * delX);
            double y = startPoint.getY() + ((i+1) * delY);
            robotSnapshots.add(i, new Snapshot(x,y,initialAngle));
        }
    }

    @Override
    public ArrayList<Snapshot> getSnapshots() {
        return robotSnapshots;
    }

    @Override
    public double getEndAngle() {
        return initialAngle;
    }

    @Override
    public double getInitialAngle() {
        return initialAngle;
    }
}
