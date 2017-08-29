package RealBot;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * Created by raque on 8/26/2017.
 */
public class RealBot extends Rectangle{


    int refreshRate = 10;
    RealBotBuilder rbb;
    double botWidth;
    RealBot instance;

    public RealBot(RealBotBuilder rbb){
        this.rbb=rbb;
    }

    Trajectory t;
    public RealBot(Trajectory t, double botWidth)
    {
        this.setWidth(botWidth);
        this.setHeight(botWidth);
        this.t=t;
        this.botWidth=botWidth;
        instance=this;
    }

    boolean playing =false;
    public void play(){

        if(!playing){
            Runnable go = new Runnable() {
                @Override
                public void run() {
                    playing=true;
                    double currentX=0;
                    double currentY=0;
                    double currentTheta=0;
                    double currentTime=0.0;
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

                        double leftChange = ((double) timeWait/1000)*leftVel;
                        double rightChange = ((double) timeWait/1000)*rightVel;
                        double delTheta =  Math.toDegrees(Math.atan((leftChange-rightChange)/botWidth));
                        double delD = ((double) timeWait/1000) * (leftVel+rightVel)/2;
                        currentTheta+= delTheta;
                       // currentTheta=m.angle;
                        currentX+= delD * Math.sin(Math.toRadians(currentTheta));
                        currentY+= delD * Math.cos(Math.toRadians(currentTheta));
                        instance.setTranslateX(250 + currentX- botWidth/2);
                        instance.setTranslateY(-100-currentY + botWidth/2);
                        //instance.setTranslateY(20);
                        //instance.setTranslateX(20);

                        instance.setRotate(currentTheta);
                        System.out.println(m.timeStamp +"::"+ instance.getTranslateY());
                    }
                    playing=false;
                }
            };
           new Thread(go).start();
        }

    }
}
