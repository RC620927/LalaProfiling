package RealBot;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.Movement;

/**
 * Created by raque on 8/28/2017.
 */
public class RealBotView extends Canvas {

    RealBotBuilder realBotBuilder;
    private GraphicsContext gc;
    Image robotImage;

    public RealBotView(RealBotBuilder realBotBuilder){
        this(realBotBuilder, null);
    }

    public RealBotView(RealBotBuilder realBotBuilder, Image robotImage){
        this.realBotBuilder = realBotBuilder;
        if(robotImage!=null){
            this.robotImage=robotImage;
        }else{
            this.robotImage = new Image("RobotPicture.png");
        }
        gc=this.getGraphicsContext2D();
    }

    public void play(){
        this.
    }

    private void draw(){
        drawWaypoints();
        drawRobot();
    }

    private Trajectory outlinedTrajectory = null;
    private final int skipPoints = (int) (0.5 * Trajectory.refreshRate);
    private void drawWaypoints(){

        for(Trajectory t: realBotBuilder.getTrajectories()){
            Color c = Color.BLACK;
            if(t==outlinedTrajectory){c = Color.DARKGREEN;}

            //draw each waypoint, skipping every certain number of moments so as to not have a ridiculous amount of points
            for(int i =0; i<t.getMoments().size();i+=skipPoints){
                Moment m = t.getMoments().get(i);
                gc.setFill(c);
                gc.fillRoundRect(m.x, getY(m.y),3,3,1,1);
            }

        }
    }

    private void outlineWaypoints(Trajectory t){
        outlinedTrajectory=t;
        draw();
    }

    private double getY(double y){

    }

    private void drawRobot(){

    }



}
