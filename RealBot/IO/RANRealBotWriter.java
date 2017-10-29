package RealBot.IO;

import RealBot.Moment;
import RealBot.RealBotBuilder;
import RealBot.Trajectory;
import rc.CastrooOrnelas.io.RANWriter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by raque on 10/28/2017.
 */
public class RANRealBotWriter {

    RANWriter<Moment> ranWriter;

    public RANRealBotWriter(String url){
        try {
            RANWriter.RANRowWriter ranRowWriter = (moment)->{
                String[] neccessaryArray = new String[4];
                Moment m = (Moment) moment;
                neccessaryArray[0] = "" +m.timeStamp;
                neccessaryArray[1] = "" +m.lVel;
                neccessaryArray[2] = "" +m.rVel;
                neccessaryArray[3] = "" +m.angle;

                return neccessaryArray;
            };
            ranWriter = new RANWriter<>(url, ranRowWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(RealBotBuilder realBotBuilder){
        ArrayList<Moment> moments = new ArrayList<>();
        double cT=0;
        for(Trajectory t: realBotBuilder.getTrajectories()){
            for(Moment m : t.getMoments()){
                m.timeStamp = m.timeStamp + cT;
                moments.add(m);
            }
            cT+=t.getTotalTime();
        }

        ranWriter.write(moments);
    }

}
