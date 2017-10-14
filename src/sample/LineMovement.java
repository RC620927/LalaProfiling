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
    boolean reverse;


    private ArrayList<Snapshot> robotSnapshots;

    public LineMovement(Point2D startPoint, Point2D endPoint, boolean reverse, double detail) {
        this.endPoint = endPoint;
        this.startPoint = startPoint;
        this.detail = detail;
        this.reverse=reverse;
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
        autoDetail();
        update();
    }

    @Override
    public Point2D getStartPoint() {
        return startPoint;
    }

    @Override
    public void setStartPoint(Point2D startPoint) {
        this.startPoint=startPoint;
        autoDetail();
        update();
    }

    //creates all the snapshots for the given constants
    //used everytime a variable is changed or the first time initiated
    @Override
    public void update() {

        initialAngle = Math.atan( (endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX()));
        initialAngle = Math.toRadians(90) - initialAngle;
        initialAngle = Math.toDegrees(initialAngle);
        initialAngle = (endPoint.getX() - startPoint.getX()) <0 ? initialAngle + 180 : initialAngle;
        if(reverse){
            initialAngle+=180;
            initialAngle%=360;
        }


        robotSnapshots = new ArrayList<>();
        double snapshotCount = detail * refreshRate;
        double delX = (endPoint.getX() - startPoint.getX()) / snapshotCount;
        double delY = (endPoint.getY() - startPoint.getY()) / snapshotCount;
        for(int i = 0; i<snapshotCount;i++){
            double x = startPoint.getX() + ((i) * delX);
            double y = startPoint.getY() + ((i) * delY);
            robotSnapshots.add(i, new Snapshot(x,y,initialAngle));
        }
        int a =0;
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


    public void setInitialAngle(double initialAngle){

    }

    public boolean getReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        if(reverse!=this.reverse){
            double delX = (endPoint.getX() - startPoint.getX());
            double delY = (endPoint.getY() - startPoint.getY());
            endPoint = new Point2D.Double(startPoint.getX()-delX,startPoint.getY()-delY);
            this.reverse = reverse;
            update();
        }


    }

    private void autoDetail(){
        setDetail(Math.hypot(endPoint.getX()-getStartPoint().getX(), endPoint.getY()-getStartPoint().getY())/160);
    }

    public LineMovement clone(){
        return new LineMovement(new Point2D.Double(this.startPoint.getX(),this.startPoint.getY()),
                new Point2D.Double(this.endPoint.getX(),this.endPoint.getY()),reverse,detail);
    }
}
