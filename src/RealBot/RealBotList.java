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

    public RealBotList(RealBotBuilder realBotBuilder) {
        super();
        this.realBotBuilder=realBotBuilder;
    }

    public void addLine(double distance, boolean reverse,
                        double topSpeed, double endingSpeed){
        double pastAngle, startX, startY, initialSpeed;
        if(this.getItems().isEmpty()){
            pastAngle = realBotBuilder.getStartingAngle();
            startX = realBotBuilder.getStartingPoint().getX();
            startY = realBotBuilder.getStartingPoint().getY();
            initialSpeed = 0;

        }else{
            startX = this.getItems().get(this.getItems().size()-1).getEndPoint().getX();
            startY = this.getItems().get(this.getItems().size()-1).getEndPoint().getY();
            pastAngle = this.getItems().get(this.getItems().size()-1).getEndAngle();
            initialSpeed = this.getItems().get(this.getItems().size()-1).getEndingSpeed();
        }

        double endX = startX +
                Math.sin(Math.toRadians(pastAngle)) * distance;
        double endY = startY +
                Math.cos(Math.toRadians(pastAngle)) * distance;

        LineMovement lm = new LineMovement(new Point2D.Double(startX,startY),
                new Point2D.Double(endX,endY),reverse, distance/160);
        addMovement(lm,RObservableMovement.MovementType.LINE, initialSpeed, topSpeed, endingSpeed);
    }

    //add a line, but if endpoint with angle does not quite align, then rotate first. should depracate at some point
    public void addLine(Point2D endPoint, boolean reverse, double topSpeed, double endingSpeed){
        double pastAngle, startX, startY, initialSpeed;
        if(this.getItems().isEmpty()){
            pastAngle = realBotBuilder.getStartingAngle();
            startX = realBotBuilder.getStartingPoint().getX();
            startY = realBotBuilder.getStartingPoint().getY();
            initialSpeed = 0;

        }else{
            startX = this.getItems().get(this.getItems().size()-1).getEndPoint().getX();
            startY = this.getItems().get(this.getItems().size()-1).getEndPoint().getY();
            pastAngle = this.getItems().get(this.getItems().size()-1).getEndAngle();
            initialSpeed = this.getItems().get(this.getItems().size()-1).getEndingSpeed();
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
            addMovement(rm,RObservableMovement.MovementType.ROTATE, initialSpeed, topSpeed, endingSpeed);
        }

        //initialspeeds for movements messed up
        LineMovement lm = new LineMovement(new Point2D.Double(startX,startY), new Point2D.Double(endX,endY), reverse, distance/160);
        addMovement(lm,RObservableMovement.MovementType.LINE, initialSpeed, topSpeed, endingSpeed);
    }

    public void addTurn(double endingAngle,
                         double topSpeed, double endingSpeed){
        double pastAngle, startX, startY, initialSpeed;
        if(this.getItems().isEmpty()){
            pastAngle = realBotBuilder.getStartingAngle();
            startX = realBotBuilder.getStartingPoint().getX();
            startY = realBotBuilder.getStartingPoint().getY();
            initialSpeed = 0;
        }else{
            startX = this.getItems().get(this.getItems().size()-1).getEndPoint().getX();
            startY = this.getItems().get(this.getItems().size()-1).getEndPoint().getY();
            pastAngle = this.getItems().get(this.getItems().size()-1).getEndAngle();
            initialSpeed = this.getItems().get(this.getItems().size()-1).getEndingSpeed();
        }

        RotateMovement rm = new RotateMovement(new Point2D.Double(startX,startY),
                pastAngle,endingAngle,  Math.abs(RotateMovement.getChangeTheta(endingAngle,pastAngle))/200);
        addMovement(rm, RObservableMovement.MovementType.ROTATE, initialSpeed, topSpeed, endingSpeed);
    }


    public void addBezierCurve(Point2D endPoint, double endingAngle, double fudge1, double fudge2, boolean reverse,
                               double topSpeed, double endingSpeed){
        double pastAngle, startX, startY, initialSpeed;
        if(this.getItems().isEmpty()){
            pastAngle = realBotBuilder.getStartingAngle();
            startX = realBotBuilder.getStartingPoint().getX();
            startY = realBotBuilder.getStartingPoint().getY();
            initialSpeed = 0;

        }else{
            startX = this.getItems().get(this.getItems().size()-1).getEndPoint().getX();
            startY = this.getItems().get(this.getItems().size()-1).getEndPoint().getY();
            pastAngle = this.getItems().get(this.getItems().size()-1).getEndAngle();
            initialSpeed = this.getItems().get(this.getItems().size()-1).getEndingSpeed();
        }

        double endX = endPoint.getX();
        double endY = endPoint.getY();
        double distance = Math.hypot(endX-startX, endY-startY);

        BezierCurveMovement bcm = new BezierCurveMovement(new Point2D.Double(startX,startY),
                endPoint, pastAngle, endingAngle, fudge1, fudge2, reverse, distance/120 );

        addMovement(bcm, RObservableMovement.MovementType.BEZIERCURVE,
                initialSpeed, topSpeed, endingSpeed);

    }

    public void addQuarticBezierCurve(Point2D endPoint, double endingAngle, double fudge1, double fudge2,double cpx, double cpy, boolean reverse,
                               double topSpeed, double endingSpeed){
        double pastAngle, startX, startY, initialSpeed;
        if(this.getItems().isEmpty()){
            pastAngle = realBotBuilder.getStartingAngle();
            startX = realBotBuilder.getStartingPoint().getX();
            startY = realBotBuilder.getStartingPoint().getY();
            initialSpeed = 0;

        }else{
            startX = this.getItems().get(this.getItems().size()-1).getEndPoint().getX();
            startY = this.getItems().get(this.getItems().size()-1).getEndPoint().getY();
            pastAngle = this.getItems().get(this.getItems().size()-1).getEndAngle();
            initialSpeed = this.getItems().get(this.getItems().size()-1).getEndingSpeed();
        }

        double endX = endPoint.getX();
        double endY = endPoint.getY();
        double distance = Math.hypot(endX-startX, endY-startY);

        QuarticBezierCurveMovement bcm = new QuarticBezierCurveMovement(new Point2D.Double(startX,startY),
                endPoint, pastAngle, endingAngle, fudge1, fudge2,cpx,cpy, reverse, distance/120 );

        addMovement(bcm, RObservableMovement.MovementType.BEZIERCURVE,
                initialSpeed, topSpeed, endingSpeed);

    }


    private void addMovement(Movement movement,
                             RObservableMovement.MovementType movementType, double initialSpeed, double topSpeed, double endingSpeed){
        RObservableMovement rom = new RObservableMovement(movement, movementType,
                initialSpeed, topSpeed, endingSpeed);
        realBotBuilder.addChangeListenerToMovement(rom);
        this.add(rom);
    }
}
