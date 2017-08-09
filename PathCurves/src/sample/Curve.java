package sample;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Created by Ruben on 8/6/2017.
 */
public class Curve {
    public double startPosition, endPosition;
    public double xOffset, yOffset;
    private double normalizedXOffset, normalizedYOffset;
    public double initialAngle, endingangle;
    public boolean forwards;

    private double arcLength;


    //a being the positive x-intercept for the ellipse and b the positive y intercept for ellipse
    private double a,b;

    private Function<Double, Point2D.Double> getPointWithArcLength;
    private Function<Double, Double> getX, getY;

    public double slopeEndingCheck;



    int divisor = 100;


    //point on curve to facilitate creating a database of all points on curve.
    public class CurvePoint{
        double currentArcLength, delX,delY;
        Point2D.Double point;

        public CurvePoint(double currentArcLength,  Point2D.Double point, double delX, double delY) {
            this.currentArcLength = currentArcLength;
            this.delX = delX;
            this.delY = delY;
            this.point = point;
        }

    }
    public HashMap<Integer, CurvePoint> points;


    public Curve(double xOffset, double yOffset, double initialAngle, double endingangle, boolean forwards) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.initialAngle = initialAngle;
        this.endingangle = endingangle;
        this.forwards=forwards;
        makeEquation();
    }


    private Point2D.Double finalPoint;
    private double finalArcLength;

    private void makeEquation(){
        normalizeOffsets();


        // find the axis of the ellipse

        double normalizedEndingAngle = (endingangle-initialAngle - 360) % 360;

        normalizedEndingAngle = normalizedEndingAngle % 360 < 0 ?  (normalizedEndingAngle % 360) + 360 : (normalizedEndingAngle % 360);
        double endSlope = 1 / Math.tan(Math.toRadians(normalizedEndingAngle));

        if(-2 * normalizedXOffset * endSlope  + normalizedYOffset <=0){ // 2 curves
       // if (false){
            double customAngle;
            if(normalizedXOffset>0){
                customAngle = (endingangle+10) % 360;
            }else{
                customAngle = (endingangle-10+ 360) % 360;
            }



            Curve c1 = new Curve(xOffset/2, yOffset/2, initialAngle, 90, true);
            Curve c2 = new Curve(xOffset/2, yOffset/2, 90, endingangle, true);



            HashMap<Integer, CurvePoint> c2Points = c2.rotatePoints(customAngle);
            this.points = c1.points;
            for(Integer i: c2Points.keySet()){
                Point2D.Double newPoint = new Point2D.Double(c2Points.get(i).point.getX() + c1.finalPoint.getX(),
                        c2Points.get(i).point.getY() + c1.finalPoint.getY());
                this.points.put(i + c1.points.size(),
                        new CurvePoint(c2.points.get(i).currentArcLength + c1.finalArcLength,
                        newPoint, c2.points.get(i).delX, c2.points.get(i).delY ));
            }


        }else{ //normal
            if(Math.abs(normalizedEndingAngle-90)<0.01){ //equation for normalized ending angle of 90
                a = normalizedXOffset;
                b = normalizedYOffset;
            }else if(normalizedEndingAngle>0 && ((normalizedEndingAngle<180 && normalizedXOffset>0)  ||
                    (normalizedEndingAngle>180 && normalizedXOffset<0) ) ){ //standard equation applicable to any normalized ending angles between 0 and 180 not including 90

                slopeEndingCheck = endSlope;
                a =  normalizedXOffset * normalizedYOffset   -  endSlope * normalizedXOffset * normalizedXOffset ;
                a /= -2 * normalizedXOffset * endSlope  + normalizedYOffset;

                b = Math.sqrt(Math.abs(
                        (endSlope * a*a * normalizedYOffset) / (a - normalizedXOffset)
                ));

                System.out.println("slope:" + endSlope);
            }else if(normalizedEndingAngle == 0){ // do a straight line
                b=normalizedYOffset;
                a=0;
            }else { //error
                throw new IndexOutOfBoundsException(
                        "Ending Angle must not be more than or equal to 180 degrees away from initial angle, and positive");
            }

            System.out.println("b:" + b);
            System.out.println("a:" + a);

            //create hashmap of 500 approximate points on curve spread
            points = new HashMap<>();

            double currentArcLength=0;
            CurvePoint previousCurvePoint = new CurvePoint(0, new Point2D.Double(0,0), 0, 0);

            int index = divisor-1;

            // double degreeRange = Math.toDegrees( Math.atan(  normalizedYOffset   /  Math.abs(a-normalizedXOffset)));

            int check = index;
            for(int i=0; i<index;i++){

                double x, y;
                if(normalizedXOffset>0){
                    double theta = Math.toRadians(180 - (i * (180/ (double) divisor)));

                    x = a * Math.cos(theta) + a;
                    y = b * Math.sin(theta);
                }else{
                    double theta = Math.toRadians((i * (180/ (double) divisor)));
                    x = a -  a * Math.cos(theta) ;
                    y = b * Math.sin(theta);
                }




                Point2D.Double currentPoint = new Point2D.Double(x,y);
                double delX = currentPoint.getX() - previousCurvePoint.point.getX();
                double delY = currentPoint.getY() - previousCurvePoint.point.getY();
                CurvePoint cp = new CurvePoint(currentArcLength, currentPoint, delX, delY);
                previousCurvePoint = cp;
                points.put(i,cp);

                //add arc length of this point for next point
                currentArcLength+=Math.sqrt(delX*delX + delY*delY);

                //stop the loop once you pass the point necessary
                if(Math.abs(x)>=Math.abs(normalizedXOffset)){
                    index = i;
                }
                check = i;

            }
            System.out.println(index);

            arcLength = points.get(check).currentArcLength;
            System.out.println("total arc length:" + arcLength);
            System.out.println("final x,y:" + previousCurvePoint.point.getX() +"," + previousCurvePoint.point.getY() );
            finalPoint = previousCurvePoint.point;
            finalArcLength = previousCurvePoint.currentArcLength;
            //create 3 parametric equations for x and y based on how far up arc length, for each x, and for each y
        }




    }

    private int currentIndex = 0;
    public synchronized CurvePoint seekNext(double arcLength){

        if(currentIndex>= points.size()){
            currentIndex=1;
        }
        if(arcLength<0){
            throw new IndexOutOfBoundsException();
        }

        if(points.get(currentIndex).currentArcLength>arcLength){
            currentIndex=0;
        }

        boolean done = false;

        while(currentIndex<(points.values().size()) && !done){
            if(points.get(currentIndex).currentArcLength>= arcLength){
                done = true;
            }
            currentIndex++;
            if(currentIndex==divisor  && !done){
                currentIndex=-1;
                done = true;
            }
        }
        if(currentIndex!= -1){
            return points.get(currentIndex);
        }else{
            return null;
        }
    }

    public synchronized void resetSeek(){
        currentIndex=0;
    }


    //convert offsets as if cartesian plane was rotated to initialAngle
    private void normalizeOffsets(){
        double z = Math.sqrt((xOffset * xOffset) + (yOffset*yOffset));
        double theta = 360-initialAngle;
       //adjusted angle in radians, be careful
        double phi = Math.atan(yOffset/xOffset) - Math.toRadians(theta);

        double normalizedEndingAngle = (endingangle+initialAngle - 360) % 360;
        normalizedEndingAngle = normalizedEndingAngle % 360 < 0 ?  (normalizedEndingAngle % 360) + 360 : (normalizedEndingAngle % 360);

        //set normalized values

        if(normalizedEndingAngle<180){
            normalizedXOffset = Math.cos(phi) * z;
            normalizedYOffset = Math.sin(phi) * z;
        }else{
            normalizedXOffset = -Math.cos(phi) * z;
            normalizedYOffset = -Math.sin(phi) * z;
        }
        System.out.println("x:" + normalizedXOffset);
        System.out.println("y:" + normalizedYOffset);
    }

    public double getArcLength() {
        return arcLength;
    }

    //degrees
    public  HashMap<Integer, CurvePoint> rotatePoints(double angle){
        HashMap<Integer, CurvePoint> newPoints = new HashMap<>();
        for(Integer i : newPoints.keySet()){
            CurvePoint oldCP = points.get(i);
            Point2D p = oldCP.point;
            double currentAngle = Math.atan(p.getY()/p.getX());
            currentAngle = normalizedXOffset>=0? -currentAngle : currentAngle;
            currentAngle = currentAngle + Math.toRadians(angle);
            double d = Math.sqrt(p.getX()*p.getX() + p.getY()*p.getY());
            Point2D.Double newPoint2D= new Point2D.Double(d * Math.cos(currentAngle),  d*Math.sin(currentAngle));
            CurvePoint newCP = new CurvePoint(oldCP.currentArcLength, newPoint2D,oldCP.delX, oldCP.delY);
            newPoints.put(i, newCP);
        }

        return newPoints;
    }


    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }
}
