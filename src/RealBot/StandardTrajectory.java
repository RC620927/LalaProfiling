package RealBot;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by raque on 9/3/2017.
 */
public class StandardTrajectory implements Trajectory{



    //acceleration inches/sec speed also in inches /sec
    private Path path;
    private double initialSpeed, topSpeed, endingSpeed;
    private double maxAcceleration, maxStopAcceleration;



    private ArrayList<Moment> moments;

    public ArrayList<Moment> getMoments(){
        return moments;
    }

    public StandardTrajectory(Path path, double initialSpeed, double topSpeed,
                      double endingSpeed, double maxAcceleration, double maxStopAcceleration) {
        this.path=path;
        this.initialSpeed = initialSpeed;
        this.topSpeed = topSpeed;
        this.endingSpeed = endingSpeed;
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
                currentRow.createCell(2).setCellValue(m.rVel);/*
                currentRow.createCell(3).setCellValue(m.sss.getS().angle);
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


    private void update(){
        ArrayList<SuperSnapShot> superSnapShots = path.superSnapshots;
        moments = new ArrayList<>();
        double currentArclength = 0;
        double currentVelocity = initialSpeed;
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
        double leftD, rightD, delD;
        int i=0;
        while(!stop){
            i++;


            //calculate if we need to slow for end
            stoppingDeltaV = currentVelocity - endingSpeed;
            stoppingTime  = (Math.abs(stoppingDeltaV)/maxAcceleration);
            spaceToStop =  Math.abs(stoppingTime * (currentVelocity + endingSpeed)/2);

            if((path.getTotalArcLength() - currentArclength) <spaceToStop){
                deAccelerate = true;
            }

            //calculate this iteration's velocity
            currentAcceleration = lastSnapshot.isReverse()? -maxAcceleration:maxAcceleration;
            if(deAccelerate){
                currentAcceleration=-currentAcceleration;
            }

            currentVelocity = currentVelocity + currentAcceleration * delT;
            //limit to outside wheel
            SuperSnapShot nextIndexed = superSnapShots.get(lastIndex +1);
            double tempLeftWheelD = nextIndexed.getLeftArcLength() - lastSnapshot.getLeftArcLength();
            double tempRightWheelD = nextIndexed.getRightArcLength() - lastSnapshot.getRightArcLength();
            double tempDelD = nextIndexed.getArcLength() - lastSnapshot.getArcLength();
            double limitingSpeed = topSpeed;
            if(tempLeftWheelD > tempDelD){
                limitingSpeed = tempDelD/tempLeftWheelD * topSpeed;
            }else if(tempRightWheelD >tempDelD) {
                limitingSpeed = tempDelD / tempRightWheelD * topSpeed;
            }

            currentVelocity = Resources.limit(currentVelocity, -limitingSpeed, limitingSpeed);


            //calculate index of snapshot upcoming by getting arclength of average velocity over this interval *delT + currentArclength
            currentArclength = currentArclength + (delT * Math.abs((pastVelocity + currentVelocity)/2) );
            currentSnapshot = path.seek(currentArclength);

            /*leftD = Math.hypot(currentSnapshot.getLeftWheelSnapshot().y - lastSnapshot.getLeftWheelSnapshot().y,
                    currentSnapshot.getLeftWheelSnapshot().x - lastSnapshot.getLeftWheelSnapshot().x);
            rightD = Math.hypot(currentSnapshot.getRightWheelSnapshot().y - lastSnapshot.getRightWheelSnapshot().y,
                    currentSnapshot.getRightWheelSnapshot().x - lastSnapshot.getRightWheelSnapshot().x);
            delD = Math.hypot(currentSnapshot.getS().y - lastSnapshot.getS().y,
                    currentSnapshot.getS().x - lastSnapshot.getS().x);
            */

            leftD = currentSnapshot.getLeftArcLength() - lastSnapshot.getLeftArcLength();
            rightD = currentSnapshot.getRightArcLength() - lastSnapshot.getRightArcLength();
            delD = currentSnapshot.getArcLength() - lastSnapshot.getArcLength();

            leftD = currentSnapshot.isLeftReverse()? -leftD: leftD;
            rightD = currentSnapshot.isRightReverse()? -rightD :rightD;
            delD = currentSnapshot.isReverse()? -delD:delD;

            double leftVelocity =  (leftD/delD) * currentVelocity;
            double rightVelocity = (rightD/delD) * currentVelocity;

            boolean stahp = false;
            if(currentSnapshot!=null && (currentArclength< path.getTotalArcLength()) &&
                    currentSnapshot.getLeftArcLength() < path.getTotalLeftArcLength() &&
                    currentSnapshot.getRightArcLength() < path.getTotalRightArcLength()){
                if( !(deAccelerate==true &&
                        ((pastVelocity>=endingSpeed && currentVelocity<= endingSpeed && !currentSnapshot.isReverse())
                                || (pastVelocity<=endingSpeed && currentVelocity>= endingSpeed && currentSnapshot.isReverse()) ))){

                    moments.add(new Moment("",i * delT,currentSnapshot.getS().x, currentSnapshot.getS().y,
                            currentSnapshot.getS().angle,leftVelocity,rightVelocity));


                    pastVelocity = currentVelocity;
                    lastSnapshot = currentSnapshot;
                    lastIndex = currentSnapshot.getIndex();

                }else{
                    stop =true;
                    moments.get(moments.size()-1).lVel = currentVelocity * (leftD/delD);
                    moments.get(moments.size()-1).rVel = currentVelocity * (rightD/leftD);
                }
            }else{
                moments.get(moments.size()-1).lVel = currentVelocity * (leftD/delD);
                moments.get(moments.size()-1).rVel = currentVelocity * (rightD/leftD);

                stop=true;
            }
        }

    }

    /*private void update(){
        ArrayList<SuperSnapShot> superSnapShots = path.superSnapshots;
        moments = new ArrayList<>();

        int lastIndex =0;

        double delT = 1/refreshRate;
        double currentLeftVelocity =  initialLeftSpeed;
        double currentRightVelocity = initialRightSpeed;
        double maxAvgNextLeftVelocity;
        double maxAvgNextRightVelocity;

        double pastLVelocity  = initialLeftSpeed;
        double pastRVelocity = initialRightSpeed;

        double currentLeftArcLength =0;
        double currentRightArcLength =0;

        double currentLeftAcceleration = maxAcceleration;
        double currentRightAcceleration = maxAcceleration;

        boolean deAccelerate = false;

        double pastLD = 0, pastRD = 0;

        boolean stop = false;
        double leftD=0,rightD=0;
        int i=0;

        boolean leftReverse,rightReverse;
        while(!stop){
            i++;

            //check if u need to deaccelerate
            updateDeaccelerationValues(currentLeftVelocity, currentRightVelocity);
            if(stoppingLeftPriority){
                if((path.getTotalLeftArcLength() - currentLeftArcLength) <= spaceToDeaccelerate){
                    deAccelerate=true;
                }
            }else{
                if((path.getTotalRightArcLength() - currentRightArcLength) <= spaceToDeaccelerate){
                    deAccelerate=true;
                }
            }


            //check which way you need to accelerate
            SuperSnapShot lastSnapshot = superSnapShots.get(lastIndex);
            SuperSnapShot nextIndexedSnapshot = lastIndex<superSnapShots.size()-1? superSnapShots.get(lastIndex+1):null;
            leftReverse= false;
            rightReverse=false;
            *//*if(nextIndexedSnapshot!=null){
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
            }*//*
            leftReverse = nextIndexedSnapshot.isLeftReverse();

            rightReverse = nextIndexedSnapshot.isRightReverse();

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

            //get snapshots for maximum
            SuperSnapShot nextLeftSSS = path.seekLeft(currentLeftArcLength + Math.abs(maxAvgNextLeftVelocity) * delT);
            SuperSnapShot nextRightSSS = path.seekRight(currentRightArcLength + Math.abs(maxAvgNextRightVelocity) * delT);

            //limit to the outside wheels
            if(nextLeftSSS!=null && nextRightSSS!=null){
                int maxNextLeftIndex = nextLeftSSS.getIndex();
                int maxNextRightIndex = nextRightSSS.getIndex();

                //use the maximum index to limit to outside wheel properly
               // int nextIndex = Math.min(maxNextRightIndex,maxNextLeftIndex);

                int nextIndex = maxNextLeftIndex + maxNextRightIndex;


                nextIndex/=2;

                SuperSnapShot nextSnapshot = superSnapShots.get(nextIndex);
                lastIndex=nextIndex;

                currentLeftArcLength = nextSnapshot.getLeftArcLength();
                currentRightArcLength = nextSnapshot.getRightArcLength();


                *//*leftD = nextSnapshot.getLeftArcLength()- lastSnapshot.getLeftArcLength();
                rightD = nextSnapshot.getRightArcLength()- lastSnapshot.getRightArcLength();

                double leftTest,rightTest;

                leftD = leftReverse? -leftD: leftD;
                rightD = rightReverse? -rightD:rightD;*//*

                leftD = Math.hypot(nextSnapshot.getLeftWheelSnapshot().y - lastSnapshot.getLeftWheelSnapshot().y,
                        nextSnapshot.getLeftWheelSnapshot().x - lastSnapshot.getLeftWheelSnapshot().x);
                rightD = Math.hypot(nextSnapshot.getRightWheelSnapshot().y - lastSnapshot.getRightWheelSnapshot().y,
                        nextSnapshot.getRightWheelSnapshot().x - lastSnapshot.getRightWheelSnapshot().x);
                leftD = leftReverse? -leftD: leftD;
                rightD = rightReverse? -rightD :rightD;

                //        currentLeftVelocity=nextLeftVelocity;
                //          currentRightVelocity = nextRightVelocity;



                //calculate actual current velocity after limiting the inside wheel

                *//*if(nextIndex==maxNextLeftIndex){
                    currentLeftVelocity = currentLeftVelocity + currentLeftAcceleration * delT;
                    currentLeftVelocity = Resources.limit(currentLeftVelocity, -topSpeed,topSpeed);
                    currentRightVelocity = currentLeftVelocity * (rightD/leftD);
                    currentRightVelocity *= leftReverse && currentLeftVelocity >0? -1:1;
                }else{
                    currentRightVelocity = currentRightVelocity + currentRightAcceleration * delT;
                    currentRightVelocity = Resources.limit(currentRightVelocity, -topSpeed,topSpeed);
                    currentLeftVelocity = currentRightVelocity * (leftD/rightD);
                    currentLeftVelocity *= rightReverse && currentRightVelocity >0? -1:1;
                }*//*
                currentLeftVelocity = Resources.limit(leftD/delT, -topSpeed, topSpeed);
                currentRightVelocity = Resources.limit(rightD/delT, -topSpeed, topSpeed) ;

                if(Math.abs(currentLeftVelocity - pastLVelocity)/delT > Math.abs(currentLeftAcceleration) + 10){
                    currentLeftVelocity = pastLVelocity + currentLeftAcceleration * delT;
                }
                if(Math.abs(currentRightVelocity - pastRVelocity)/delT > Math.abs(currentRightAcceleration) + 10){
                    currentRightVelocity = pastRVelocity + currentRightAcceleration * delT;
                }
                pastLVelocity = currentLeftVelocity;
                pastRVelocity = currentRightVelocity;


                moments.add(new Moment("",i * delT,nextSnapshot.getS().x,nextSnapshot.getS().y,
                        nextSnapshot.getS().angle,currentLeftVelocity,
                        currentRightVelocity, nextSnapshot));

            }else{
                if(stoppingLeftPriority){
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


    public void updateDeaccelerationValues(double leftVelocity, double rightVelocity){

        boolean leftPriority;
        boolean deAccelerate = false;
        double maxDeltaSpeed =  Math.max(Math.abs(leftVelocity-endingSpeed), Math.abs(rightVelocity-endingSpeed));
        timeToDeaccelerate = maxDeltaSpeed / maxStopAcceleration;
        double maxSpeed = (maxDeltaSpeed == Math.abs(leftVelocity-endingSpeed)) ? leftVelocity:rightVelocity;

        //space to deaccelerate wont work if going from positive to negative when stopping which is a very impossible scenario?
        spaceToDeaccelerate = timeToDeaccelerate * (maxSpeed + (timeToDeaccelerate/2) * -maxStopAcceleration);
        stoppingLeftPriority = (maxSpeed == leftVelocity)? true:false;

        *//*if(deAccelerationLeftTimeNeeded>=deAccelerationRightTimeNeeded){
            leftPriority=true;
            spaceToDeaccelerateNeeded = deAcceleratonTimeNeeded * (Math.abs(currentLeftVelocity) + (deAcceleratonTimeNeeded/2)  * -maxStopAcceleration);
            if((path.getTotalLeftArcLength() - currentLeftArcLength) <= spaceToDeaccelerateNeeded){
                deAccelerate=true;
            }
        }else{
            leftPriority=false;
            spaceToDeaccelerateNeeded = deAcceleratonTimeNeeded * (Math.abs(currentRightVelocity) + (deAcceleratonTimeNeeded/2)  * -maxStopAcceleration);
            if((path.getTotalRightArcLength() - currentRightArcLength) <= spaceToDeaccelerateNeeded){
                deAccelerate=true;
            }
        }*//*

    }
*/

}
