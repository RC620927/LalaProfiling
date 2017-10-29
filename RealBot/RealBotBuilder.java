package RealBot;

import Lalaprofiling.Application.Snapshot;
import Lalaprofiling.Application.StillMovement;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import rc.CastrooOrnelas.FRC.TrajectoryIO;
import rc.CastrooOrnelas.datatypes.RList;
import rc.CastrooOrnelas.datatypes.RObservable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import static RealBot.Trajectory.refreshRate;

/**
 * Created by raque on 8/24/2017.
 */
public class RealBotBuilder implements RObservable<ArrayList<Trajectory>>{

    private ArrayList<BiConsumer<ArrayList<Trajectory>,ArrayList<Trajectory>>> changeListeners;

    private RList<Moment> momentList;

    private Point2D startingPoint;
    private Double startingAngle;
    private double botWidth, botLength;
    private double acceleration, stopAcceleration;
    private double topSpeed;

    private int idgen;

    private RealBotList realBotList;
    private RealBot realBot;

    private double totalTime=0;
    private DoubleProperty randomness = new SimpleDoubleProperty(0);

    private ArrayList<Trajectory> trajectories;

    public RealBotBuilder(Point2D startingPoint, Double startingAngle, double botWidth, double botLength,
                          double acceleration, double stopAcceleration, double topSpeed){
        changeListeners= new ArrayList<>();
        this.startingPoint=startingPoint;
        this.startingAngle=startingAngle;
        this.botLength=botLength;
        this.botWidth=botWidth;
        this.acceleration=acceleration;
        this.stopAcceleration= stopAcceleration;
        this.topSpeed=topSpeed;
        realBotList = new RealBotList(this);
        realBotList.addChangeListener((o,n)->{
            updateTrajectories();
        });
        trajectories = new ArrayList<>();
        idgen=0;
        realBot = new RealBot(botWidth);
    }

    //delete after use
    public void testWriteExcel(){
        try {
            double totalM = 0;
            for(Trajectory t:trajectories){
                totalM+=t.getMoments().size();
            }
            System.out.println("Total time: " + totalM * (1 / refreshRate));

            TrajectoryIO tio = new TrajectoryIO("Graphs.xlsx");
            tio.writeTrajectories(0,trajectories);


            /*XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet("1");

            XSSFRow currentRow;

            int index = 1;
            for(Trajectory t : trajectories){
                for (Moment m : t.getMoments()) {
                    currentRow = spreadsheet.createRow(index++);
                    currentRow.createCell(0).setCellValue(index * (1 / refreshRate));
                    currentRow.createCell(1).setCellValue(m.lVel);
                    currentRow.createCell(2).setCellValue(m.rVel);*//*
                    currentRow.createCell(3).setCellValue(m.sss.getS().angle);
                    currentRow.createCell(4).setCellValue(m.sss.getRightWheelSnapshot().x);
                    currentRow.createCell(5).setCellValue(m.sss.getRightWheelSnapshot().y);

                    currentRow.createCell(7).setCellValue(m.sss.getLeftWheelSnapshot().x);
                    currentRow.createCell(8).setCellValue(m.sss.getLeftWheelSnapshot().y);*//*
                }
            }

            System.out.println("Total time: " + index * (1 / refreshRate));
            FileOutputStream out = new FileOutputStream(
                    new File("Graphs.xlsx"));
            workbook.write(out);

            out.close();*/
        }catch(Exception e){
            System.out.println("MESSUP:"+e.getMessage());
        }
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
            int dfi=2;
            if(current!=old){
                updateTrajectories();
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
        ArrayList<Trajectory> cached = trajectories;
        totalTime=0;
        trajectories = new ArrayList<>();
        int i=0;
        for(RObservableMovement rom: getMovements().getItems()){
            i++;
            Path p;
            if(i==1){
                 p = new Path(rom.getSnapshots(), botWidth,
                        new Snapshot(getStartingPoint().getX(),getStartingPoint().getY(),getStartingAngle()));
            }else{
                double x = getMovements().getItems().get(i-2).getEndPoint().getX();
                double y = getMovements().getItems().get(i-2).getEndPoint().getY();
                double t = getMovements().getItems().get(i-2).getEndAngle();

                p = new Path(rom.getSnapshots(), botWidth, new Snapshot(x,y,t));
            }
            if(rom.getMovementType() == RObservableMovement.MovementType.ROTATE){
                Trajectory t = new RotateTrajectory(p,rom.getTopSpeed(),acceleration, stopAcceleration);
                totalTime+=t.getTotalTime();
                trajectories.add(t);
            }else if(rom.getMovementType() == RObservableMovement.MovementType.LINE ||
                    rom.getMovementType() == RObservableMovement.MovementType.BEZIERCURVE ||
                    rom.getMovementType() == RObservableMovement.MovementType.QUARTICBEZIERCURVE){
                Trajectory t = new StandardTrajectory(p, rom.getInitialSpeed(),
                        rom.getTopSpeed(),rom.getEndingSpeed(),acceleration,stopAcceleration);
                totalTime+=t.getTotalTime();
                trajectories.add(t);
            }else if(rom.getMovementType() == RObservableMovement.MovementType.STILL){
                Trajectory t = new StillTrajectory(p,((StillMovement) rom.getMovement()).getTime());
                totalTime+=t.getTotalTime();
                trajectories.add(t);
            }

        }
        notifyListeners(cached);
    }

    public synchronized void updateAll(){
        for(int i=0;i<this.getMovements().getItems().size()-1;i++){
            updateMovementStarts(i);
        }
        updateTrajectories();
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

    public double getBotLength() {
        return botLength;
    }

    public ArrayList<Trajectory> getTrajectories() {
        return trajectories;
    }

    public RealBot getRealBot() {
        return realBot;
    }

    @Override
    public void addChangeListener(BiConsumer<ArrayList<Trajectory>, ArrayList<Trajectory>> changeListener){
        if(changeListener!=null){
            this.changeListeners.add(changeListener);
        }
    }

    @Override
    public void removeChangeListener(BiConsumer<ArrayList<Trajectory>,ArrayList<Trajectory>> changeListener) {
        if(changeListener!=null){
            this.changeListeners.remove(changeListener);
        }
    }

    private void notifyListeners(ArrayList<Trajectory> pastTrajectories){
        for(BiConsumer bc: changeListeners){
            bc.accept(pastTrajectories,trajectories);
        }
    }

    public double getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(double topSpeed) {
        this.topSpeed = topSpeed;
    }

    public double getRandomness() {
        return randomness.getValue();
    }

    public void setRandomness(double randomness) {
        this.randomness.set(randomness);
    }

    public DoubleProperty randomnessProperty(){
        return randomness;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getTotalTime() {
        return totalTime;
    }
}
