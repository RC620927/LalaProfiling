package RealBot;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import rc.CastrooOrnelas.datatypes.RPID;

import java.util.Random;

/**
 * Created by raque on 8/26/2017.
 */
public class RealBot extends Rectangle{


    int refreshRate = 10;
    RealBotBuilder rbb;
    double botWidth;
    RealBot instance;

    RPID anglePID;

   // public RealBot(RealBotBuilder rbb){
     //   this.rbb=rbb;
   // }

    Trajectory t;

    double currentX=0;
    double currentY=0;
    double currentTheta=0;
    double currentTime=0.0;

    public RealBot(double botWidth)
    {
        this.setWidth(botWidth);
        this.setHeight(botWidth);
        this.botWidth=botWidth;
        instance=this;

        anglePID = new RPID(2.5,0.0,1.0);
        anglePID.setContinuous(true);
        anglePID.setInputRange(0,360);
        anglePID.setAcceptableRange(0.2);
        anglePID.setOutputRange(-20,20);

    }

    public void reset(double x, double y, double theta){
        currentX = x;
        currentY = y;
        currentTheta = theta;
    }

    double past1 = currentTheta;
    double past2 = currentTheta;
    double past3 = currentTheta;
    double past1Wanted = currentTheta;
    double past2Wanted =currentTheta;
    double past3Wanted = currentTheta;
    boolean playing =false;
    public void play(Trajectory t){
        this.t=t;



        if(!playing){
            playing=true;
            Runnable go = new Runnable() {
                @Override
                public void run() {

                    currentTime=0;
                    for(Moment m: t.getMoments()){
                        long timeWait = (long)(1000* (m.timeStamp-currentTime));
                        currentTime=m.timeStamp;
                        try {
                            Thread.sleep(timeWait);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        double leftVel = new Double(m.lVel).equals(Double.NaN)? 0:m.lVel;
                        double rightVel = new Double(m.rVel).equals(Double.NaN)? 0:m.rVel;

                      //  leftVel*= (0.95 + new Random().nextDouble()/10);
                       // rightVel*= (0.95 + new Random().nextDouble()/10);
                        anglePID.setSetPoint(m.angle);
                        double anglePIDOutput = anglePID.crunch(currentTheta);
                        //anglePIDOutput=0;
                        leftVel+=anglePIDOutput;
                        rightVel-=anglePIDOutput;

                        double leftChange = ((double) timeWait/1000)*leftVel;
                        double rightChange = ((double) timeWait/1000)*rightVel;
                        double delTheta =  Math.toDegrees(Math.atan((leftChange-rightChange)/botWidth));

                        double delD = ((double) timeWait/1000) * (leftVel+rightVel)/2;

                        delD*=1.008;
                        currentTheta+= delTheta;
                        // currentTheta=m.angle;
                        currentX+= delD * Math.sin(Math.toRadians(currentTheta));
                        currentY+= delD * Math.cos(Math.toRadians(currentTheta));
                        instance.setTranslateX(250 + currentX- botWidth/2);
                        instance.setTranslateY(-100-currentY + botWidth/2);
                        //instance.setTranslateY(20);
                        //instance.setTranslateX(20);

                        instance.setRotate(currentTheta);
                        past1=currentTheta;
                        past2=past1;
                        past3=past2;
                        past1Wanted=m.angle;
                        past2Wanted=past1Wanted;
                        past3Wanted=past2Wanted;
                        System.out.println(m.timeStamp +":   X:"+ currentX + "   Y:" + currentY  + "   TH:" + currentTheta);
                        System.out.println(m.timeStamp +":   DelD:"+ delD + "    leftV:" + leftVel + "    RightV:" + rightVel + "    PID:" + anglePIDOutput);
                    }
                    playing=false;
                }
            };
           new Thread(go).start();
        }

    }



}
