package RealBot;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by raque on 9/10/2017.
 */
public class StillTrajectory implements Trajectory {
    //acceleration inches/sec speed also in inches /sec
    private Path path;
    private double time;

    private ArrayList<Moment> moments;

    public ArrayList<Moment> getMoments(){
        return moments;
    }

    public StillTrajectory(Path path, double time) {
        this.path=path;
        this.time=time;
        update();
    }

    //for rotate, velocity is turning velocity, so current velocity  = leftVelocity = -rightVelocity
    private void update(){
        ArrayList<SuperSnapShot> superSnapShots = path.superSnapshots;
        moments = new ArrayList<>();
        SuperSnapShot currentSnapshot = superSnapShots.get(0);

        double delT = 1/refreshRate;

        boolean stop = false;
        boolean deAccelerate = false;
        int i=0;
        while(!stop){
            i++;
            currentSnapshot = path.superSnapshots.get(0);
            if(i*delT<=time){
                moments.add(new Moment("",i * delT,currentSnapshot.getS().x, currentSnapshot.getS().y,
                        currentSnapshot.getS().angle,0,0));
            }else{
                stop=true;
            }
        }

    }

    @Override
    public double getTotalTime() {
        return time;
    }
}

