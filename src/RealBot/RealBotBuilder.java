package RealBot;

import javafx.scene.canvas.Canvas;
import rc.CastrooOrnelas.datatypes.RList;
import rc.CastrooOrnelas.datatypes.RObservable;
import sample.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by raque on 8/24/2017.
 */
public class RealBotBuilder{


    private RList<Moment> momentList;

    private Point2D startingPoint;
    private Double startingAngle;
    private double botWidth, botHeight;
    private double acceleration, stopAcceleration;

    private int idgen;

    private RealBotList realBotList;

    private ArrayList<Trajectory> trajectories;

    public RealBotBuilder(Point2D startingPoint, Double startingAngle, double botWidth, double botHeight,
                          double acceleration, double stopAcceleration){
        this.startingPoint=startingPoint;
        this.startingAngle=startingAngle;
        this.botHeight=botHeight;
        this.botWidth=botWidth;
        this.acceleration=acceleration;
        this.stopAcceleration= stopAcceleration;
        realBotList = new RealBotList(this);
        idgen=0;
    }

    public RealBotList getMovements(){
        return realBotList;
    }

    public void updateRealBot(){

    }

    void addChangeListenerToMovement(RObservableMovement rom){
        rom.addChangeListener((old,current)->{
            if(current.getEndPoint()!=old.getEndPoint() || current.getEndAngle()!=old.getEndAngle()){
                updateAfterMovement(rom);
            }
        });
    }

    public synchronized void updateAfterMovement(RObservableMovement rom){
        int index = this.getMovements().getItems().indexOf(rom);
        if(index != this.getMovements().getItems().size()-1 && this.getMovements().getItems().size()>1){
            updateMovementStarts(index+1);
        }
    }

    private synchronized void updateMovementStarts(int index){
        RObservableMovement rom = this.getMovements().getItem(index);
        if(index==0){
            rom.setStartPoint(startingPoint);
            rom.setInitialAngle(startingAngle);
        }else{
            rom.setStartPoint(this.getMovements().getItem(index-1).getEndPoint());
            rom.setInitialAngle(this.getMovements().getItem(index-1).getEndAngle());
        }
    }

    public synchronized void updateTrajectories(){
        trajectories = new ArrayList<>();
        for(RObservableMovement rom: getMovements().getItems()){
            Path p = new Path(rom.getSnapshots(), botWidth,
                    new Snapshot(getStartingPoint().getX(),getStartingPoint().getY(),getStartingAngle()));
            Trajectory t = new Trajectory(p,rom.getInitialLeftSpeed(),rom.getInitialRightSpeed(),
                    rom.getTopSpeed(),rom.getEndingSpeed(),acceleration,stopAcceleration);
            trajectories.add(t);
        }
    }

    public synchronized void updateAll(){
        for(int i=0;i<this.getMovements().getItems().size()-1;i++){
            updateMovementStarts(i);
        }
    }

    public Point2D getStartingPoint() {
        return startingPoint;
    }

    public Double getStartingAngle() {
        return startingAngle;
    }

    public double getBotWidth() {
        return botWidth;
    }

    public double getBotHeight() {
        return botHeight;
    }

    public ArrayList<Trajectory> getTrajectories() {
        return trajectories;
    }
}
