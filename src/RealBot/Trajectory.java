package RealBot;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Movement;
import sample.Resources;
import sample.Snapshot;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by raque on 8/24/2017.
 */
public class Trajectory {

    public static final double refreshRate = 50;

    //acceleration inches/sec speed also in inches /sec
    private Path path;
    private double initialLeftSpeed, initialRightSpeed, topSpeed, endingSpeed;
    private double maxAcceleration, maxStopAcceleration;

    private ArrayList<Moment> moments;

    public ArrayList<Moment> getMoments(){
        return moments;
    }

    public Trajectory(Path path, double initialLeftSpeed, double initialRightSpeed, double topSpeed,
                      double endingSpeed, double maxAcceleration, double maxStopAcceleration) {
        this.path=path;
        this.initialLeftSpeed = initialLeftSpeed;
        this.initialRightSpeed = initialRightSpeed;
        this.topSpeed = topSpeed;
        this.endingSpeed = endingSpeed;
        this.maxAcceleration = maxAcceleration;
        this.maxStopAcceleration = maxStopAcceleration;
        update();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet("1");

            XSSFRow currentRow;

            int index = 1;
            for (Moment m : moments) {
                currentRow = spreadsheet.createRow(index++);
                currentRow.createCell(0).setCellValue(index * (1 / refreshRate));
                currentRow.createCell(1).setCellValue(m.lVel);
                currentRow.createCell(2).setCellValue(m.rVel);
                currentRow.createCell(3).setCellValue(m.sss.getS().angle);
                currentRow.createCell(4).setCellValue(m.sss.getRightWheelSnapshot().x);
                currentRow.createCell(5).setCellValue(m.sss.getRightWheelSnapshot().y);

                currentRow.createCell(7).setCellValue(m.sss.getLeftWheelSnapshot().x);
                currentRow.createCell(8).setCellValue(m.sss.getLeftWheelSnapshot().y);
            }
            FileOutputStream out = new FileOutputStream(
                    new File("Graph.xlsx"));
            workbook.write(out);

            out.close();
        }catch(Exception e){

        }

    }


    private void update(){
        ArrayList<SuperSnapShot> superSnapShots = path.superSnapshots;
        moments = new ArrayList<>();

        int lastIndex =0;

        double delT = 1/refreshRate;
        double currentLeftVelocity =  initialLeftSpeed;
        double currentRightVelocity = initialRightSpeed;
        double maxAvgNextLeftVelocity;
        double maxAvgNextRightVelocity;

        double currentLeftArcLength =0;
        double currentRightArcLength =0;

        double currentLeftAcceleration = maxAcceleration;
        double currentRightAcceleration = maxAcceleration;

        boolean stop = false;
        double leftD=0,rightD=0;
        int i=0;

        boolean leftReverse,rightReverse;
        while(!stop){
            i++;

            //check if u need to deaccelerate
            double deAccelerationLeftTimeNeeded = Math.abs((currentLeftVelocity - endingSpeed) / maxStopAcceleration);
            double deAccelerationRightTimeNeeded = Math.abs((currentRightVelocity - endingSpeed) / maxStopAcceleration);
            boolean leftPriority;
            boolean deAccelerate = false;
            double deAcceleratonTimeNeeded = Math.max(deAccelerationLeftTimeNeeded,deAccelerationRightTimeNeeded);
            double spaceToDeaccelerateNeeded;

            if(deAccelerationLeftTimeNeeded>=deAccelerationRightTimeNeeded){
                leftPriority=true;
                spaceToDeaccelerateNeeded = deAcceleratonTimeNeeded * (currentLeftVelocity + (deAcceleratonTimeNeeded/2)  * -maxStopAcceleration);
                if((path.getTotalLeftArcLength() - currentLeftArcLength) <= spaceToDeaccelerateNeeded){
                    deAccelerate=true;
                }
            }else{
                leftPriority=false;
                spaceToDeaccelerateNeeded = deAcceleratonTimeNeeded * (currentRightVelocity + (deAcceleratonTimeNeeded/2)  * -maxStopAcceleration);
                if((path.getTotalRightArcLength() - currentRightArcLength) <= spaceToDeaccelerateNeeded){
                    deAccelerate=true;
                }
            }

            //check which way you need to accelerate
            SuperSnapShot lastSnapshot = superSnapShots.get(lastIndex);
            SuperSnapShot nextIndexedSnapshot = lastIndex<superSnapShots.size()-1? superSnapShots.get(lastIndex+1):null;
            leftReverse= false;
            rightReverse=false;
            /*if(nextIndexedSnapshot!=null){
                double movementAngleLeft = Resources.angleOf(
                        new Point2D.Double(lastSnapshot.getLeftWheelSnapshot().x, lastSnapshot.getLeftWheelSnapshot().y),
                        new Point2D.Double(nextIndexedSnapshot.getLeftWheelSnapshot().x, nextIndexedSnapshot.getLeftWheelSnapshot().y));
                double movementAngleRight = Resources.angleOf(
                        new Point2D.Double(lastSnapshot.getRightWheelSnapshot().x, lastSnapshot.getRightWheelSnapshot().y),
                        new Point2D.Double(nextIndexedSnapshot.getRightWheelSnapshot().x, nextIndexedSnapshot.getRightWheelSnapshot().y));


                //if leftside is moving back
                if(!Resources.forwardsMovement( nextIndexedSnapshot.getS().angle, movementAngleLeft)){
                  //  leftReverse=true;
                }else{
                    leftReverse=false;
                }
                //if rightside is moving back
                if(!Resources.forwardsMovement(nextIndexedSnapshot.getS().angle, movementAngleRight)){
                    //rightReverse=true;
                }else{
                    rightReverse=false;
                }
            }*/
            leftReverse = nextIndexedSnapshot.isLeftReverse();
            rightReverse=nextIndexedSnapshot.isRightReverse();

            //if you hace to deccelerate, make sure you are using stop acceleration
            if(deAccelerate){
                currentLeftAcceleration=-maxStopAcceleration;
                currentRightAcceleration=-maxStopAcceleration;
            }else {
                currentLeftAcceleration=maxAcceleration;
                currentRightAcceleration=maxAcceleration;
            }

            //if reverse then go backwards
            if(leftReverse){
                currentLeftAcceleration= -currentLeftAcceleration;
            }
            if(rightReverse){
                currentRightAcceleration= -currentRightAcceleration;
            }






            //average speed over this interval
            maxAvgNextLeftVelocity = Resources.limit(currentLeftVelocity + ((delT/2) * currentLeftAcceleration), -topSpeed,topSpeed);
            maxAvgNextRightVelocity = Resources.limit(currentRightVelocity + ((delT/2) * currentRightAcceleration), -topSpeed,topSpeed);


            SuperSnapShot nextLeftSSS = path.seekLeft(currentLeftArcLength + Math.abs(maxAvgNextLeftVelocity) * delT);
            SuperSnapShot nextRightSSS = path.seekRight(currentRightArcLength + Math.abs(maxAvgNextRightVelocity) * delT);

            //limit to the outside wheels
            if(nextLeftSSS!=null && nextRightSSS!=null){
                int maxNextLeftIndex = nextLeftSSS.getIndex();
                int maxNextRightIndex = nextRightSSS.getIndex();

                int nextIndex = Math.min(maxNextRightIndex,maxNextLeftIndex);


                SuperSnapShot nextSnapshot = superSnapShots.get(nextIndex);
                lastIndex=nextIndex;

                currentLeftArcLength = nextSnapshot.getLeftArcLength();
                currentRightArcLength = nextSnapshot.getRightArcLength();


                leftD = nextSnapshot.getLeftArcLength()- lastSnapshot.getLeftArcLength();
                rightD = nextSnapshot.getRightArcLength()- lastSnapshot.getRightArcLength();

                leftD = leftReverse? -leftD: leftD;
                rightD = rightReverse? -rightD:rightD;

                //leftD = Math.hypot(nextSnapshot.getLeftWheelSnapshot().y - lastSnapshot.getLeftWheelSnapshot().y,
                 //       nextSnapshot.getLeftWheelSnapshot().x - lastSnapshot.getLeftWheelSnapshot().x);
                //rightD = Math.hypot(nextSnapshot.getRightWheelSnapshot().y - lastSnapshot.getRightWheelSnapshot().y,
                    //    nextSnapshot.getRightWheelSnapshot().x - lastSnapshot.getRightWheelSnapshot().x);
                //leftD = leftReverse? -leftD: leftD;
                //rightD = rightReverse? -rightD:rightD;

                double pastIterationLeftVelocity = leftD/delT;
                double pastIterationVelocity = rightD/delT;


                //        currentLeftVelocity=nextLeftVelocity;
                //          currentRightVelocity = nextRightVelocity;



                //calculate actual current velocity after limiting the inside wheel
                if(nextIndex==maxNextLeftIndex){
                    currentLeftVelocity = currentLeftVelocity + currentLeftAcceleration * delT;
                    currentLeftVelocity = Resources.limit(currentLeftVelocity, -topSpeed,topSpeed);
                    currentRightVelocity = currentLeftVelocity * (rightD/leftD);
                }else{
                    currentRightVelocity = currentRightVelocity + currentRightAcceleration * delT;
                    currentRightVelocity = Resources.limit(currentRightVelocity, -topSpeed,topSpeed);
                    currentLeftVelocity = currentRightVelocity * (leftD/rightD);
                }


                moments.add(new Moment("",i * delT,nextSnapshot.getS().x,nextSnapshot.getS().y,
                        nextSnapshot.getS().angle,currentLeftVelocity,
                        currentRightVelocity, nextSnapshot));

            }else{
                if(leftPriority){
                    moments.get(moments.size()-1).lVel=endingSpeed;
                    moments.get(moments.size()-1).rVel=(rightD/leftD) * endingSpeed;
                }else{
                    moments.get(moments.size()-1).rVel=endingSpeed;
                    moments.get(moments.size()-1).lVel=(leftD/rightD) * endingSpeed;
                }
                stop=true;
            }

        }



        int x=1;



    }


}
