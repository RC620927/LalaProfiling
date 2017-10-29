package RealBot;

import Lalaprofiling.Application.*;
import rc.CastrooOrnelas.datatypes.RObservable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.BiConsumer;

/**
 * Created by raque on 8/24/2017.
 * Wrapper for a Movement
 */
public class RObservableMovement  implements RObservable<Movement>, Cloneable{
    private Movement movement;
    private ArrayList<BiConsumer<Movement,Movement>> changeListeners;
    private double initialSpeed, topSpeed, endingSpeed;
    private String name;
    private MovementType movementType;

    public enum MovementType{
        ROTATE("ROTATE"),LINE("LINE"),BEZIERCURVE("BEZIERCURVE"),QUARTICBEZIERCURVE("QUARTICBEZIERCURVE"), STILL("STILL");
        String name;
        MovementType(String name){
            this.name=name;
        }
        public String getName(){
            return name;
        }
    }

    public RObservableMovement(String name,Movement movement, MovementType movementType, double initialSpeed, double topSpeed, double endingSpeed) {
        this.name = name;
        this.movement = movement;
        this.initialSpeed = initialSpeed;
        this.topSpeed = topSpeed;
        this.endingSpeed = endingSpeed;
        this.movementType = movementType;
        changeListeners = new ArrayList<>();
    }

    public Movement getValue(){
        return movement;
    }

    public MovementType getMovementType(){return  movementType;}

    public double getDetail(){
        return movement.getDetail();
    }

    public void setDetail(double detail){
        if(detail!=movement.getDetail()){
            Movement cached = (Movement) movement.clone();
            movement.setDetail(detail);
            movement.update();
            notifyListeners(cached);
        }
    }

    public void setInitialAngle(double initialAngle){
        if(initialAngle!=movement.getInitialAngle()){
            Movement cached = (Movement) movement.clone();
            movement.setInitialAngle(initialAngle);
            movement.update();
            notifyListeners(cached);
        }
    }

    public Point2D getEndPoint(){
        return movement.getEndPoint();
    }

    public Point2D getStartPoint(){
        return movement.getStartPoint();
    }

    public double getInitialAngle(){
        return movement.getInitialAngle();
    }

    public void setStartPoint(Point2D startPoint){
        if(!startPoint.equals(movement.getStartPoint())){
            Movement cached = (Movement) movement.clone();
            movement.setStartPoint(startPoint);
            movement.update();
            notifyListeners(cached);
        }
    };

    public double getEndAngle(){
        return movement.getEndAngle();
    }

    public void setReverse(boolean reverse){
        if(reverse!=movement.getReverse()){
            Movement cached = (Movement) movement.clone();
            movement.setReverse(reverse);
            movement.update();
            notifyListeners(cached);
        }
    }

    public boolean getReverse(){
        return movement.getReverse();
    }

    public ArrayList<Snapshot> getSnapshots(){
        return movement.getSnapshots();
    }

    private void notifyListeners(Movement pastMovement){
        for(BiConsumer<Movement,Movement> bc : changeListeners){
            bc.accept(pastMovement,this.movement);
        }
    }

    public void addChangeListener(BiConsumer<Movement,Movement> changeListener){
        if(changeListeners!=null){
            changeListeners.add(changeListener);
        }
    }

    public void removeChangeListener(BiConsumer<Movement,Movement> changeListener){
        if(changeListeners!=null){
            changeListeners.remove(changeListener);
        }
    }

    public double getInitialSpeed() {
        return initialSpeed;
    }


    public double getTopSpeed() {
        return topSpeed;
    }

    public double getEndingSpeed() {
        return endingSpeed;
    }

    //to set value of fudge1, fudge 2
    public void setValue(String valueName, Object value){
        Movement cached = (Movement) movement.clone();
        //check if valueName is a Trajectory variable (speeds) or if it is reverse - these are shared by all movements
        switch(valueName){
            case "top_speed":
                this.topSpeed=(double) value;
                break;
            case "initial_speed":
                this.initialSpeed=(double) value;
                break;
            case "ending_speeed":
                this.endingSpeed=(double) value;
                break;
            case "reverse":
                this.setReverse((boolean) value);

        }

        //check if valueName is a movement variable
        if(this.getMovementType() == MovementType.ROTATE){
            switch (valueName){
                case "end_angle":
                    ((RotateMovement) movement).setEndAngle((double) value);
                    break;
            }
        }else if(this.getMovementType() == MovementType.LINE){
            switch (valueName){
                case "distance":
                    double pastAngle, startX, startY;
                    pastAngle = getInitialAngle();
                    startX = getStartPoint().getX();
                    startY = getStartPoint().getY();
                    boolean reverse = cached.getReverse();
                    double effectiveTravelAngle = reverse?pastAngle+180:pastAngle;
                    double endX = startX +
                            Math.sin(Math.toRadians(effectiveTravelAngle)) * (double) value;
                    double endY = startY +
                            Math.cos(Math.toRadians(effectiveTravelAngle)) * (double) value;
                    ((LineMovement) movement).setEndPoint(new Point2D.Double(endX,endY));
                    break;
            }
        }else if(this.getMovementType() == MovementType.BEZIERCURVE){
            switch (valueName){
                case "fudge_1":
                    ((BezierCurveMovement) movement).setFudge1((double) value);
                    break;
                case "fudge_2":
                    ((BezierCurveMovement) movement).setFudge2((double) value);
                    break;
                case "end_angle":
                    ((BezierCurveMovement) movement).setEndingAngle((double) value);
                    break;
                case "end_x":
                    ((BezierCurveMovement) movement).setEndPoint(new Point2D.Double((double) value, getEndPoint().getY()));
                    break;
                case "end_y":
                    ((BezierCurveMovement) movement).setEndPoint(new Point2D.Double(getEndPoint().getX(), (double) value));
                    break;
            }
        }else if(this.getMovementType() == MovementType.STILL){
            switch (valueName){
                case "time":
                    ((StillMovement) movement).setTime((double) value);
                    break;
            }
        }
        movement.update();
        notifyListeners(cached);
    }

    public Movement getMovement(){
        return movement;
    }

    //clone this ROM, for the purposes of it being observable, one needs an old copy and a new copy
    public RObservableMovement clone(){
        return new RObservableMovement(name.substring(0,name.length()),
                (Movement)movement.clone(),this.movementType,this.initialSpeed,this.topSpeed,this.endingSpeed);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
