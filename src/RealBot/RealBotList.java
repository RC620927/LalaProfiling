package RealBot;

import Lalaprofiling.Application.*;
import rc.CastrooOrnelas.datatypes.RList;

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

    public void addLine(String name, double distance, boolean reverse,
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
        double effectiveMovingAngle = reverse? pastAngle+180:pastAngle;

        double endX = startX +
                Math.sin(Math.toRadians(effectiveMovingAngle)) * distance;
        double endY = startY +
                Math.cos(Math.toRadians(effectiveMovingAngle)) * distance;

        LineMovement lm = new LineMovement(new Point2D.Double(startX,startY),
                new Point2D.Double(endX,endY),reverse, distance/160);
        addMovement(name, lm,RObservableMovement.MovementType.LINE, initialSpeed, topSpeed, endingSpeed);
    }

    //add a line, but if endpoint with angle does not quite align, then rotate first. should depracate at some point
    public void addLine(String name, Point2D endPoint, boolean reverse, double topSpeed, double endingSpeed){
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
            addMovement(name +"ROTATE",rm,RObservableMovement.MovementType.ROTATE, initialSpeed, topSpeed, endingSpeed);
        }

        //initialspeeds for movements messed up
        LineMovement lm = new LineMovement(new Point2D.Double(startX,startY), new Point2D.Double(endX,endY), reverse, distance/160);
        addMovement(name, lm,RObservableMovement.MovementType.LINE, initialSpeed, topSpeed, endingSpeed);
    }

    public void addTurn(String name, double endingAngle,
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
        addMovement(name, rm, RObservableMovement.MovementType.ROTATE, initialSpeed, topSpeed, endingSpeed);
    }


    public void addBezierCurve(String name, Point2D endPoint, double endingAngle, double fudge1, double fudge2, boolean reverse,
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

        addMovement(name, bcm, RObservableMovement.MovementType.BEZIERCURVE,
                initialSpeed, topSpeed, endingSpeed);

    }

    public void addQuarticBezierCurve(String name, Point2D endPoint, double endingAngle, double fudge1, double fudge2,double cpx, double cpy, boolean reverse,
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

        QuarticBezierCurveMovement qbcm = new QuarticBezierCurveMovement(new Point2D.Double(startX,startY),
                endPoint, pastAngle, endingAngle, fudge1, fudge2,cpx,cpy, reverse, distance/120 );

        addMovement(name, qbcm, RObservableMovement.MovementType.BEZIERCURVE,
                initialSpeed, topSpeed, endingSpeed);

    }

    //time in millis
    public void addStill(String name, double millis){
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
        if(Math.abs(initialSpeed)<=25){
            StillMovement sm = new StillMovement(new Point2D.Double(startX,startY),
                     pastAngle,millis/1000, 1);

            addMovement(name, sm, RObservableMovement.MovementType.STILL,
                    initialSpeed, 0, 0);
        }
    }

    private void addMovement(String name, Movement movement,
                             RObservableMovement.MovementType movementType, double initialSpeed, double topSpeed, double endingSpeed){
        RObservableMovement rom = new RObservableMovement(name, movement, movementType,
                initialSpeed, topSpeed, endingSpeed);
        realBotBuilder.addChangeListenerToMovement(rom);
        this.add(rom);
    }

    public void removeMovement(RObservableMovement m){
        this.remove(m);
        realBotBuilder.updateAll();
    }

    public void moveMovement(RObservableMovement m, int index){
        if(this.getItems().contains(m)){
            this.remove(m);
            this.add(index, m);
            realBotBuilder.updateAll();
        }
    }
}






