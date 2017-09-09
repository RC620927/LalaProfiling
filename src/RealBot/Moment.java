package RealBot;

import sample.MockBot;
import sample.MockBotBuild;
import sample.Snapshot;

import java.util.ArrayList;

/**
 * Created by raque on 8/24/2017.
 */
public class Moment {
    public double x,y, angle;
    public double lVel, rVel;
    public double timeStamp;
    public String marker;


    public Moment(String marker, double timeStamp, double x, double y, double angle, double lVel, double rVel) {
        this.timeStamp=timeStamp;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.lVel=lVel;
        this.rVel=rVel;
        this.marker=marker;

    }


   /* public static MomentList SnapShotstoMoments(MockBotBuild mbb){
        int index =0;
        RList returnee = new List();
        for(MockBot mb: mbb.getMockBots()){
            returnee.add(SnapShotstoMoments(mb.getMovement().getSnapshots(), "ENDID:" + index));
        }
        return returnee;
    }



    public static MomentList SnapShotstoMoments(ArrayList<Snapshot> snapshots){
        return new MomentList();
    }

    public static MomentList SnapShotstoMoments(ArrayList<Snapshot> snapshots, String endMarker){
        int index =0;
        MomentList returnee = new MomentList();
        for(Snapshot s: snapshots){

            if(index==snapshots.size()-1){

            }
        }
        return returnee;
    }*/

}