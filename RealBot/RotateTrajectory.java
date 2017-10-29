package RealBot;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by raque on 9/3/2017.
 */
public class RotateTrajectory implements Trajectory {


    //acceleration inches/sec speed also in inches /sec
    private Path path;
    private double initialLeftSpeed, initialRightSpeed, topSpeed, endingSpeed;
    private double maxAcceleration, maxStopAcceleration;

    private double totalTime=0;

    private ArrayList<Moment> moments;

    public ArrayList<Moment> getMoments(){
        return moments;
    }

    public RotateTrajectory(Path path,double topSpeed,
                             double maxAcceleration, double maxStopAcceleration) {
        this.path=path;
        this.initialLeftSpeed = 0;
        this.initialRightSpeed = 0;
        this.topSpeed = topSpeed;
        this.endingSpeed = 0;
        this.maxAcceleration = maxAcceleration;
        this.maxStopAcceleration = maxStopAcceleration;
        update();
        int x =1;
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
                /*currentRow.createCell(3).setCellValue(m.sss.getS().angle);
                currentRow.createCell(4).setCellValue(m.sss.getRightWheelSnapshot().x);
                currentRow.createCell(5).setCellValue(m.sss.getRightWheelSnapshot().y);

                currentRow.createCell(7).setCellValue(m.sss.getLeftWheelSnapshot().x);
                currentRow.createCell(8).setCellValue(m.sss.getLeftWheelSnapshot().y);*/
            }
            FileOutputStream out = new FileOutputStream(
                    new File("Graph.xlsx"));
            workbook.write(out);

            out.close();
        }catch(Exception e){

        }

    }

    //for rotate, velocity is turning velocity, so current velocity  = leftVelocity = -rightVelocity
    private void update(){
        ArrayList<SuperSnapShot> superSnapShots = path.superSnapshots;
        moments = new ArrayList<>();
        double currentArclength = 0;
        double currentVelocity = initialLeftSpeed/2 + initialRightSpeed/2;
        double pastVelocity = currentVelocity;
        double currentAcceleration = 0;

        SuperSnapShot lastSnapshot = superSnapShots.get(0);
        int lastIndex =0;
        int lastMomentIndex = 0;
        SuperSnapShot currentSnapshot = superSnapShots.get(0);

        double delT = 1/refreshRate;
        double stoppingDeltaV;
        double stoppingTime;
        double spaceToStop;
        boolean stop = false;
        boolean deAccelerate = false;
        int i=0;
        while(!stop){
            i++;


            //calculate if we need to slow for end
            stoppingDeltaV = currentVelocity - endingSpeed;
            stoppingTime  = (Math.abs(stoppingDeltaV)/maxAcceleration);
            spaceToStop =  Math.abs(stoppingTime * (currentVelocity + endingSpeed)/2);

            if((path.getTotalLeftArcLength() - currentArclength) <= spaceToStop){
                deAccelerate = true;
            }

            //calculate this iteration's velocity
            currentAcceleration = lastSnapshot.isLeftReverse()? -maxAcceleration:maxAcceleration;
            if(deAccelerate){
                currentAcceleration=-currentAcceleration;
            }

            currentVelocity = currentVelocity + currentAcceleration * delT;

            //calculate index of snapshot upcoming by getting arclength of average velocity over this interval *delT + currentArclength
            currentArclength = currentArclength + (delT * Math.abs((pastVelocity + currentVelocity)/2) );
            currentSnapshot = path.seekLeft(currentArclength);


            double leftVelocity = currentVelocity;
            double rightVelocity = -currentVelocity;

            if(currentSnapshot!=null &&
                    currentSnapshot.getLeftArcLength() < path.getTotalLeftArcLength()){
                if( !(deAccelerate==true &&
                        ((pastVelocity>=endingSpeed && currentVelocity<= endingSpeed && !currentSnapshot.isReverse())
                                || (pastVelocity<=endingSpeed && currentVelocity>= endingSpeed && currentSnapshot.isReverse()) ))){
                    totalTime=i*delT;
                    moments.add(new Moment("",i * delT,currentSnapshot.getS().x, currentSnapshot.getS().y,
                            currentSnapshot.getS().angle,leftVelocity,rightVelocity));


                    pastVelocity = currentVelocity;
                    lastSnapshot = currentSnapshot;
                    lastIndex = currentSnapshot.getIndex();

                }else{
                    stop =true;
                    moments.get(moments.size()-1).lVel = currentVelocity;
                    moments.get(moments.size()-1).rVel = -currentVelocity;
                }
            }else{
                moments.get(moments.size()-1).lVel = currentVelocity;
                moments.get(moments.size()-1).rVel = -currentVelocity;

                stop=true;
            }
        }

    }

    @Override
    public double getTotalTime() {
        return totalTime;
    }
}
