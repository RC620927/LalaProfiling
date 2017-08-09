package sample;

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

    private void makeEquation(){
        normalizeOffsets();

        // find the axis of the ellipse

        double normalizedEndingAngle = (endingangle+initialAngle - 360) % 360;
        normalizedEndingAngle = normalizedEndingAngle % 360 < 0 ?  (normalizedEndingAngle % 360) + 360 : (normalizedEndingAngle % 360);

        if(Math.abs(normalizedEndingAngle-90)<0.01){ //equation for normalized ending angle of 90
            a = normalizedXOffset;
            b = normalizedYOffset;
        }else if(normalizedEndingAngle>0 && ((normalizedEndingAngle<180 && normalizedXOffset>0)  ||
                (normalizedEndingAngle>180 && normalizedXOffset<0) ) ){ //standard equation applicable to any normalized ending angles between 0 and 180 not including 90
            double endSlope = 1 / Math.tan(Math.toRadians(normalizedEndingAngle));
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
        int divisor = 200;
        int index = divisor-1;

       // double degreeRange = Math.toDegrees( Math.atan(  normalizedYOffset   /  Math.abs(a-normalizedXOffset)));

        for(int i=0; i<index;i++){

            double x, y;
            if(normalizedXOffset>0){
                double theta = Math.toRadians(180 - (i * (180/ (double) divisor)));

                x = a * Math.cos(theta) + a;
                y = b * Math.sin(theta);
            }else{
                double theta = Math.toRadians((i * (180/ (double) divisor)));
                x = a * Math.cos(theta) - a;
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
            if(x>=normalizedXOffset){
                index = i;
            }

        }

        arcLength = points.get(index).currentArcLength;
        System.out.println("total arc length:" + arcLength);

        //create 3 parametric equations for x and y based on how far up arc length, for each x, and for each y

    }

    private int currentIndex = 0;
    public synchronized CurvePoint seekNext(double arcLength){
        if(arcLength<0){
            throw new IndexOutOfBoundsException();
        }

        if(points.get(currentIndex).currentArcLength>arcLength){
            currentIndex=0;
        }

        boolean done = false;

        while(currentIndex<500 && !done){
            if(points.get(currentIndex).currentArcLength>= arcLength){
                done = true;
            }
            currentIndex++;
            if(currentIndex==500  && !done){
                currentIndex=-1;
                done = true;
            }
        }
        if(currentIndex!= -1){
            return points.get(currentIndex);
        }else{
            return points.get(499);
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

        //set normalized values
        normalizedXOffset = Math.cos(phi) * z;
        normalizedYOffset = Math.sin(phi) * z;
        System.out.println("x:" + normalizedXOffset);
        System.out.println("y:" + normalizedYOffset);
    }

    public double getArcLength() {
        return arcLength;
    }
}
