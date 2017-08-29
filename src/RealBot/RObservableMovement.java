package RealBot;

import rc.CastrooOrnelas.datatypes.RObservable;
import sample.Movement;
import sample.Snapshot;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.BiConsumer;

/**
 * Created by raque on 8/24/2017.
 */
public class RObservableMovement  implements RObservable<Movement>{
    private Movement movement;
    private ArrayList<BiConsumer<Movement,Movement>> changeListeners;
    private double initialLeftSpeed, initialRightSpeed, topSpeed, endingSpeed;

    public RObservableMovement(Movement movement, double initialLeftSpeed, double initialRightSpeed, double topSpeed, double endingSpeed) {
        this.movement = movement;
        this.initialLeftSpeed = initialLeftSpeed;
        this.initialRightSpeed = initialRightSpeed;
        this.topSpeed = topSpeed;
        this.endingSpeed = endingSpeed;
    }

    public Movement getValue(){
        return movement;
    }

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

    public double getInitialLeftSpeed() {
        return initialLeftSpeed;
    }

    public double getInitialRightSpeed() {
        return initialRightSpeed;
    }

    public double getTopSpeed() {
        return topSpeed;
    }

    public double getEndingSpeed() {
        return endingSpeed;
    }
}
