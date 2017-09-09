package RealBot;

import rc.CastrooOrnelas.datatypes.RObservable;
import sample.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.BiConsumer;

/**
 * Created by raque on 8/24/2017.
 */
public class RObservableMovement  implements RObservable<Movement>{
    private Movement movement;
    private ArrayList<BiConsumer<Movement,Movement>> changeListeners;
    private double initialSpeed, topSpeed, endingSpeed;
    private MovementType movementType;

    public enum MovementType{
        ROTATE("ROTATE"),LINE("LINE"),BEZIERCURVE("BEZIERCURVE");
        String name;
        MovementType(String name){
            this.name=name;
        }
        public String getName(){
            return name;
        }
    }

    public RObservableMovement(Movement movement, MovementType movementType, double initialSpeed, double topSpeed, double endingSpeed) {
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
            Movement cached = movement;
            movement.setDetail(detail);
            notifyListeners(cached);
        }
    }

    public void setInitialAngle(double initialAngle){
        if(initialAngle!=movement.getInitialAngle()){
            Movement cached = movement;
            movement.setInitialAngle(initialAngle);
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
            Movement cached = movement;
            movement.setStartPoint(startPoint);
            notifyListeners(cached);
        }
    };

    public double getEndAngle(){
        return movement.getEndAngle();
    }

    public void setReverse(boolean reverse){
        if(reverse!=movement.getReverse()){
            Movement cached = movement;
            movement.setReverse(reverse);
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
    public void setValue(String valueName, double value){
        Movement cached = movement;
        if(this.getMovementType() == MovementType.ROTATE){
            switch (valueName){
                case "end_angle":
                    ((RotateMovement) movement).setEndAngle(value);
                    break;
            }
        }else if(this.getMovementType() == MovementType.LINE){
            switch (valueName){
                case "distance":
                    double pastAngle, startX, startY;
                    pastAngle = getInitialAngle();
                    startX = getStartPoint().getX();
                    startY = getStartPoint().getY();

                    double endX = startX +
                            Math.sin(Math.toRadians(pastAngle)) * value;
                    double endY = startY +
                            Math.cos(Math.toRadians(pastAngle)) * value;
                    ((LineMovement) movement).setEndPoint(new Point2D.Double(endX,endY));
                    break;
            }
        }else if(this.getMovementType() == MovementType.BEZIERCURVE){
            switch (valueName){
                case "fudge1":
                    ((BezierCurveMovement) movement).setFudge1(value);
                    break;
                case "fudge2":
                    ((BezierCurveMovement) movement).setFudge2(value);
                    break;
                case "end_angle":
                    ((BezierCurveMovement) movement).setEndingAngle(value);
                    break;
                case "end_x":
                    ((BezierCurveMovement) movement).setEndPoint(new Point2D.Double(value, getEndPoint().getY()));
                    break;
                case "end_y":
                    ((BezierCurveMovement) movement).setEndPoint(new Point2D.Double(getEndPoint().getX(), value));
                    break;
            }
        }
        notifyListeners(cached);
    }

}
