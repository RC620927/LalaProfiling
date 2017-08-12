package sample;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by Ruben on 8/9/2017.
 */
public class LineMovement implements Movement {


    Point2D endPoint, startPoint;
    double initialAngle;
    int detail;


    private ArrayList<Snapshot> robotSnapshots;

    public LineMovement(Point2D endPoint, Point2D startPoint, int detail) {
        this.endPoint = endPoint;
        this.startPoint = startPoint;
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

        initialAngle = Math.atan((endPoint.getX() - startPoint.getX()) / (endPoint.getY() - startPoint.getY()));
        if(endPoint.getY()<startPoint.getY()){
            initialAngle = initialAngle>0?  Math.toRadians(180) - initialAngle : Math.toRadians(-180) - initialAngle;
        }

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



}
