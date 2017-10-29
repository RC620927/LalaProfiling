package Lalaprofiling.Application;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.Math.pow;

/**
 * Created by raque on 9/6/2017.
 */
public class QuarticBezierCurveMovement implements Movement{
    private Point2D initialPoint, endPoint, controlPoint1,controlPoint2;
    private Point2D controlPointMiddle;
    private double detail;
    private ArrayList<Snapshot> robotSnapshots;
    private boolean reverse;


    private double initialAngle, endingAngle, fudge1,fudge2;
    private ArrayList<Snapshot> snapshots;

    public QuarticBezierCurveMovement(Point2D initialPoint, Point2D endPoint,  double initialAngle,
                               double endingAngle, double fudge1, double fudge2,double cpx, double cpy, boolean reverse, double detail) {
        this.initialPoint = initialPoint;
        this.endPoint = endPoint;
        this.initialAngle = initialAngle;
        this.endingAngle = endingAngle;
        this.detail = detail;
        this.fudge1=fudge1;
        this.fudge2=fudge2;
        this.reverse=reverse;
        if(!reverse){
            controlPoint1 = new Point2D.Double(initialPoint.getX() + Math.sin(Math.toRadians(initialAngle)) * fudge1,
                    initialPoint.getY() + Math.cos(Math.toRadians(initialAngle)) * fudge1);
            controlPoint2 = new Point2D.Double(endPoint.getX() - Math.sin(Math.toRadians(endingAngle)) * fudge2,
                    endPoint.getY() - Math.cos(Math.toRadians(endingAngle)) * fudge2);
        }else{
            controlPoint1 = new Point2D.Double(initialPoint.getX() - Math.sin(Math.toRadians(initialAngle)) * fudge1,
                    initialPoint.getY() - Math.cos(Math.toRadians(initialAngle)) * fudge1);
            controlPoint2 = new Point2D.Double(endPoint.getX() + Math.sin(Math.toRadians(endingAngle)) * fudge2,
                    endPoint.getY() + Math.cos(Math.toRadians(endingAngle)) * fudge2);
        }
        controlPointMiddle = new Point2D.Double(cpx,cpy);
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
        autoDetail();
        controlPoint1 = new Point2D.Double(initialPoint.getX() + Math.sin(Math.toRadians(initialAngle)) * fudge1,
                initialPoint.getY() + Math.cos(Math.toRadians(initialAngle)) * fudge1);
        update();
    }

    //based on [x,y]=(1–t)4P0+4(1–t)3tP1+6(1–t)2t2P2+4(1-t)t3P3+t4P4
    //creates all the snapshots for the given constants
    //used everytime a variable is changed or the first time initiated
    @Override
    public void update() {
        snapshots = new ArrayList<>();
        for(double i =0; i< refreshRate*detail ;i++){
            double t = (1+i)/(refreshRate * detail);
            double currentX = (pow(1-t, 4)) * initialPoint.getX() +
                    (4 * pow(1-t, 3) * t) * controlPoint1.getX() +
                    (6 * pow(1-t, 2) * t * t) * controlPointMiddle.getX() +
                    (4 * (1-t) * pow(t,3)) * controlPoint2.getX() +
                    (pow(t,4)) * endPoint.getX();
            double currentY = (pow(1-t, 4)) * initialPoint.getY() +
                    (4 * pow(1-t, 3) * t) * controlPoint1.getY() +
                    (6 * pow(1-t, 2) * t * t) * controlPointMiddle.getY() +
                    (4 * (1-t) * pow(t,3)) * controlPoint2.getY() +
                    (pow(t,4)) * endPoint.getY();
            double dy = 4* pow(1-t,3)*(-1) * initialPoint.getY() +
                    ((4 * (pow(1-t,3)+  t*(-3*pow(1-t,2))) ) * controlPoint1.getY()) +
                    (6 * (2*t*(pow(1-t,2))+ 2*(-1*(1-t) * pow(t,2))) * controlPointMiddle.getY()) +
                    ((4 * (3*pow(t,2) -4*pow(t,3)) ) * controlPoint2.getY()) +
                    (4 * pow(t,3) * endPoint.getY());
            double dx = 4* pow(1-t,3)*(-1) * initialPoint.getX() +
                    ((4 * (pow(1-t,3)+  t*(-3*pow(1-t,2))) ) * controlPoint1.getX()) +
                    (6 * (2*t*(pow(1-t,2))+ 2*(-1*(1-t) * pow(t,2))) * controlPointMiddle.getX()) +
                    ((4 * (3*pow(t,2) -4*pow(t,3)) ) * controlPoint2.getX()) +
                    (4 * pow(t,3) * endPoint.getY());
            double currentAngle = Math.atan(dy/dx);
            currentAngle = Math.toRadians(90) - currentAngle;
            currentAngle = Math.toDegrees(currentAngle);
            currentAngle = dx<0? currentAngle + 180 : currentAngle;
            //if reverse add 180 to current angle
            if(reverse){
                currentAngle+=180;
                currentAngle%=360;
            }

            snapshots.add(new Snapshot(currentX,currentY,currentAngle));

        }
        endingAngle = Math.atan( (endPoint.getY() - controlPoint2.getY()) / (endPoint.getX() - controlPoint2.getX()));
        endingAngle = Math.toRadians(90) - endingAngle;
        endingAngle = Math.toDegrees(endingAngle);
        endingAngle = (endPoint.getX() - controlPoint2.getX()) <0 ? endingAngle + 180 : endingAngle;
        if(reverse){
            endingAngle+=180;
            endingAngle%=360;
        }
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

    public void setInitialPoint(Point2D initialPoint) {
        setStartPoint(initialPoint);
    }

    public void setEndPoint(Point2D endPoint) {
        this.endPoint = endPoint;
        autoDetail();
        updateControlPoint2();
        update();
    }

    public void setInitialAngle(double initialAngle) {
        this.initialAngle = initialAngle;
        updateControlPoint1();
        update();
    }

    public void setEndingAngle(double endingAngle) {
        this.endingAngle = endingAngle;
        updateControlPoint2();
        update();
    }

    public void setFudge1(double fudge1) {
        this.fudge1 = fudge1;
        updateControlPoint1();
        update();
    }

    public void setFudge2(double fudge2) {
        this.fudge2 = fudge2;
        updateControlPoint2();
        update();
    }

    private void updateControlPoint1(){
        if(!reverse){
            controlPoint1 = new Point2D.Double(initialPoint.getX() + Math.sin(Math.toRadians(initialAngle)) * fudge1,
                    initialPoint.getY() + Math.cos(Math.toRadians(initialAngle)) * fudge1);
        }else{
            controlPoint1 = new Point2D.Double(initialPoint.getX() - Math.sin(Math.toRadians(initialAngle)) * fudge1,
                    initialPoint.getY() - Math.cos(Math.toRadians(initialAngle)) * fudge1);
        }
    }

    private void updateControlPoint2(){
        if(!reverse){
            controlPoint2 = new Point2D.Double(endPoint.getX() - Math.sin(Math.toRadians(endingAngle)) * fudge2,
                    endPoint.getY() - Math.cos(Math.toRadians(endingAngle)) * fudge2);
        }else{
            controlPoint2 = new Point2D.Double(endPoint.getX() + Math.sin(Math.toRadians(endingAngle)) * fudge2,
                    endPoint.getY() + Math.cos(Math.toRadians(endingAngle)) * fudge2);
        }

    }

    @Override
    public boolean getReverse() {
        return reverse;
    }

    @Override
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
        updateControlPoint2();
        updateControlPoint1();
    }

    private void autoDetail(){
        setDetail(Math.hypot(endPoint.getX()-getStartPoint().getX(), endPoint.getY()-getStartPoint().getY())/120);
    }

    public QuarticBezierCurveMovement clone(){
        return new QuarticBezierCurveMovement(new Point2D.Double(this.initialPoint.getX(),this.initialPoint.getY()),
                new Point2D.Double(endPoint.getX(),endPoint.getY()), initialAngle,endingAngle,fudge1,fudge2,controlPointMiddle.getX(),controlPointMiddle.getY(),
                reverse,detail);
    }
}
