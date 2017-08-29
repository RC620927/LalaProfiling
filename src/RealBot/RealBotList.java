package RealBot;

import javafx.scene.image.Image;
import rc.CastrooOrnelas.datatypes.RList;
import sample.*;

import java.awt.geom.Point2D;

/**
 * Created by raque on 8/24/2017.
 */
public class RealBotList extends RList<RObservableMovement>{
    RealBotBuilder realBotBuilder;

    public RealBotList(RealBotBuilder realBotBuilder){
        this.realBotBuilder=realBotBuilder;
    }

    public void addLine(double distance, boolean reverse,
                        double initialLeftSpeed, double initialRightSpeed, double topSpeed, double endingSpeed){
        double pastAngle, startX, startY;
        if(this.getItems().isEmpty()){
            pastAngle = realBotBuilder.getStartingAngle();
            startX = realBotBuilder.getStartingPoint().getX();
            startY = realBotBuilder.getStartingPoint().getY();

        }else{
            startX = this.getItems().get(this.getItems().size()-1).getEndPoint().getX();
            startY = this.getItems().get(this.getItems().size()-1).getEndPoint().getY();
            pastAngle = this.getItems().get(this.getItems().size()-1).getEndAngle();
        }

        double endX = startX +
                Math.sin(Math.toRadians(pastAngle)) * distance;
        double endY = startY +
                Math.cos(Math.toRadians(pastAngle)) * distance;

        LineMovement lm = new LineMovement(new Point2D.Double(startX,startY), new Point2D.Double(endX,endY),reverse, distance/160);
        addMovement(lm, initialLeftSpeed, initialRightSpeed, topSpeed, endingSpeed);
    }

    //add a line, but if endpoint with angle does not quite align, then rotate first.
    public void addLine(Point2D endPoint, boolean reverse,
                        double initialLeftSpeed, double initialRightSpeed, double topSpeed, double endingSpeed){
        double pastAngle, startX, startY;
        if(this.getItems().isEmpty()){
            pastAngle = realBotBuilder.getStartingAngle();
            startX = realBotBuilder.getStartingPoint().getX();
            startY = realBotBuilder.getStartingPoint().getY();

        }else{
            startX = this.getItems().get(this.getItems().size()-1).getEndPoint().getX();
            startY = this.getItems().get(this.getItems().size()-1).getEndPoint().getY();
            pastAngle = this.getItems().get(this.getItems().size()-1).getEndAngle();
        }

        double endX = endPoint.getX();
        double endY = endPoint.getY();
        double distance = Math.hypot(endX-startX, endY-startY);

        double travelAngle = Math.atan( (endY-startY) / (endX-startX));
        travelAngle = Math.toRadians(90) - travelAngle;
        travelAngle = Math.toDegrees(travelAngle);
        travelAngle = (endX-startX) <0 ? travelAngle + 180 : travelAngle;

        if(Math.abs(RotateMovement.getChangeTheta(travelAngle,pastAngle)) >1 ){
            RotateMovement rm = new RotateMovement(new Point2D.Double(startX,startY),
                    pastAngle,travelAngle, RotateMovement.getChangeTheta(travelAngle,pastAngle)/200);
            addMovement(rm, initialLeftSpeed, initialRightSpeed, topSpeed, endingSpeed);
        }

        LineMovement lm = new LineMovement(new Point2D.Double(startX,startY), new Point2D.Double(endX,endY), reverse, distance/160);
        addMovement(lm, initialLeftSpeed, initialRightSpeed, topSpeed, endingSpeed);
    }

    public void addTurn(double endingAngle,
                        double initialLeftSpeed, double initialRightSpeed, double topSpeed, double endingSpeed){
        double pastAngle, startX, startY;
        if(this.getItems().isEmpty()){
            pastAngle = realBotBuilder.getStartingAngle();
            startX = realBotBuilder.getStartingPoint().getX();
            startY = realBotBuilder.getStartingPoint().getY();

        }else{
            startX = this.getItems().get(this.getItems().size()-1).getEndPoint().getX();
            startY = this.getItems().get(this.getItems().size()-1).getEndPoint().getY();
            pastAngle = this.getItems().get(this.getItems().size()-1).getEndAngle();
        }

        RotateMovement rm = new RotateMovement(new Point2D.Double(startX,startY),
                pastAngle,endingAngle,  Math.abs(RotateMovement.getChangeTheta(endingAngle,pastAngle))/200);
        addMovement(rm, initialLeftSpeed, initialRightSpeed, topSpeed, endingSpeed);
    }


    public void addBezierCurve(Point2D endPoint, double endingAngle, double fudge1, double fudge2, boolean reverse,
                               double initialLeftSpeed, double initialRightSpeed, double topSpeed, double endingSpeed){
        double pastAngle, startX, startY;
        if(this.getItems().isEmpty()){
            pastAngle = realBotBuilder.getStartingAngle();
            startX = realBotBuilder.getStartingPoint().getX();
            startY = realBotBuilder.getStartingPoint().getY();

        }else{
            startX = this.getItems().get(this.getItems().size()-1).getEndPoint().getX();
            startY = this.getItems().get(this.getItems().size()-1).getEndPoint().getY();
            pastAngle = this.getItems().get(this.getItems().size()-1).getEndAngle();
        }

        double endX = endPoint.getX();
        double endY = endPoint.getY();
        double distance = Math.hypot(endX-startX, endY-startY);

        BezierCurveMovement bcm = new BezierCurveMovement(this.getItems().get(this.getItems().size()-1).getEndPoint(),
                endPoint, pastAngle, endingAngle, fudge1, fudge2, reverse, distance/120 );

        addMovement(bcm, initialLeftSpeed, initialRightSpeed, topSpeed, endingSpeed);

    }

    private void addMovement(Movement movement,
                             double initialLeftSpeed, double initialRightSpeed, double topSpeed, double endingSpeed){
        RObservableMovement rom = new RObservableMovement(movement,
                initialLeftSpeed, initialRightSpeed, topSpeed, endingSpeed);
        realBotBuilder.addChangeListenerToMovement(rom);
        this.add(rom);
    }
}
