package RealBot;

import javafx.scene.canvas.Canvas;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rc.CastroOrnelas.FRC.TrajectoryIO;
import rc.CastrooOrnelas.datatypes.RList;
import rc.CastrooOrnelas.datatypes.RObservable;
import sample.*;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static RealBot.Trajectory.refreshRate;

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
    private RealBot realBot;

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
                trajectories.add(t);
            }else if(rom.getMovementType() == RObservableMovement.MovementType.LINE ||
                    rom.getMovementType() == RObservableMovement.MovementType.BEZIERCURVE){
                Trajectory t = new StandardTrajectory(p, rom.getInitialSpeed(),
                        rom.getTopSpeed(),rom.getEndingSpeed(),acceleration,stopAcceleration);
                trajectories.add(t);
            }

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

    public RealBot getRealBot() {
        return realBot;
    }
}
