package RealBot;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import sample.Movement;
import sample.Resources;

/**
 * Created by raque on 8/28/2017.
 */
public class RealBotView extends Canvas {

    RealBotBuilder realBotBuilder;
    RealBot rb;
    private GraphicsContext gc;
    public DoubleProperty scrollX, scrollY, zoom;


    Image robotImage;
    Image adjustedRobotImage;

    public RealBotView(RealBotBuilder realBotBuilder){
        this(realBotBuilder, null);
    }


    /*
        realBotBuilder - the builder being shown in this view. It holds all the trajectories, and variables needed to display content.
        robotImageUrl - string url to picture of robot to be resized according to builder's robot dimensions , if null, display standard image
     */
    public RealBotView(RealBotBuilder realBotBuilder, String robotImageURL){
        super(300,300);
        this.realBotBuilder = realBotBuilder;
        this.rb = realBotBuilder.getRealBot();
        if(robotImage!=null){
            this.robotImage= new Image(robotImageURL);
            this.adjustedRobotImage = new Image(robotImageURL,inToPx * realBotBuilder.getBotWidth(),
                    inToPx * realBotBuilder.getBotLength(), false, false);
        }else{
            this.robotImage = new Image("RobotPicture.png");
            this.adjustedRobotImage = new Image("RobotPicture.png", inToPx * realBotBuilder.getBotWidth(),
                    inToPx * realBotBuilder.getBotLength(), false, false);
        }
        realBotBuilder.addChangeListener((old, current)->{
            reset();
        });

        gc=this.getGraphicsContext2D();
        scrollX = new SimpleDoubleProperty(0);
        scrollY = new SimpleDoubleProperty(0);
        zoom = new SimpleDoubleProperty(1);

        scrollX.addListener((obs,o,n) ->{
            if(shouldUpdate()){draw();}
        });
        scrollY.addListener((obs,o,n) ->{
            if(shouldUpdate()){draw();}
        });
        zoom.addListener((obs,o,n) ->{
            if (shouldUpdate()) {draw();}
        });

        //reset realbot so that it is at starting point
        rb.reset(realBotBuilder.getStartingPoint().getX(),
                realBotBuilder.getStartingPoint().getY(),realBotBuilder.getStartingAngle());
    }


    private int updateTicket =0;
    private double lastUpdate = System.currentTimeMillis();
    //returns true after 50 millis if it is the last update requested in those 50 millis or if there has not been an update in 50 millis
    private boolean shouldUpdate(){
        if(playing){
            return false;
        }

        if(updateTicket>=Integer.MAX_VALUE-20){
            updateTicket=0;
        }
        int thisTicket = ++updateTicket;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(thisTicket<updateTicket){
            if(System.currentTimeMillis()- lastUpdate > 50){
                lastUpdate = System.currentTimeMillis();
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }

    boolean drawing = false;
    boolean centered = false;
    //draw on the canvas the field, waypoints, and the robot, applying a transform to zoom in the process.
    private void draw(){
        if(!drawing){
            drawing = true;
            gc.clearRect(0,0,this.getWidth(),this.getHeight());

            double zoom = this.zoom.getValue();
            Scale s = new Scale(zoom,zoom, this.getLayoutBounds().getWidth()/2, this.getLayoutBounds().getHeight()/2);
            gc.transform(s.getMxx(), s.getMyx(), s.getMxy(), s.getMyy(), s.getTx(), s.getTy());

            drawField();
            drawWaypoints();
            drawRobot();

            s = new Scale(1/zoom,1/zoom, this.getLayoutBounds().getWidth()/2, this.getLayoutBounds().getHeight()/2);
            gc.transform(s.getMxx(), s.getMyx(), s.getMxy(), s.getMyy(), s.getTx(), s.getTy());
            drawing=false;
        }
    }

    //draws, but public. To be used externally scarcely to draw the first iteration
    public void reset(){
        if(shouldUpdate()) draw();
    }

    private Trajectory outlinedTrajectory = null;
    private final int skipPoints = (int) (0.15 * Trajectory.refreshRate);
    //draws the waypoints that the robot goes through, spaced based on velocity, the higher the velocity, the higher the spacing.
    private void drawWaypoints(){

        for(Trajectory t: realBotBuilder.getTrajectories()){
            Color c = Color.BLACK;
            if(t==outlinedTrajectory){c = Color.DARKGREEN;}

            //draw each waypoint, skipping every certain number of moments so as to not have a ridiculous amount of points
            for(int i =0; i<t.getMoments().size();i+=skipPoints){
                Moment m = t.getMoments().get(i);
                gc.setFill(c);
                gc.fillRoundRect(getRealX(m.x)-1.5, getRealY(m.y)-1.5,3,3,1,1);
            }

        }
    }

    public static final int refreshRate = 50;
    public boolean playing = false;
    //begins the RealBot moving process, then giving it trajectories from the builder.
    public void play(){
        if(!playing){
            playing=true;
            Runnable r = ()->{
                rb.reset(realBotBuilder.getStartingPoint().getX(),
                        realBotBuilder.getStartingPoint().getY(),realBotBuilder.getStartingAngle());
                for(Trajectory t: realBotBuilder.getTrajectories()){
                    rb.play(t);
                    while(rb.playing){
                        if(centered){
                            centerOnRobot();
                        }
                        draw();
                        try {
                            Thread.sleep((long) (1000/refreshRate));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                playing=false;
            };
            new Thread(r).start();
        }
    }

    //made to highlight the waypoints of the current trajectory being edited
    private void outlineWaypoints(Trajectory t){
        outlinedTrajectory=t;
        draw();
    }

    private static final int inToPx = 2;
    //transform 1 in to 4 px
    //used to transform inch coordinates on the field, to coordinates from the canvas before zooming in.
    private double getRealX(double x){
        return inToPx * (x - scrollX.get());
    }

    //used to transform inch coordinates on the field, to coordinates from the canvas before zooming in.
    private double getRealY(double y){
        return inToPx * ((this.getLayoutBounds().getHeight()) - y - scrollY.get()) ;
    }

    //deprecate ast some point
    private static void scaleImage(Image image, double requestedX, double requestedY){
        double scaleX = requestedX/image.getWidth();
        double scaleY = requestedY/image.getHeight();
        Image returnee;
    }

    //center the screen on the robot
    public void centerOnRobot(){
        double viewerWidth = this.getWidth() ==0? 1030:this.getWidth();
        double viewerHeight =this.getHeight() ==0?678:this.getHeight();
        double currentScreenCenterX = viewerWidth/2;
        double currentScreenCentery = viewerHeight/2;
        scrollX.setValue(-(currentScreenCenterX/inToPx) + rb.currentX);
        scrollY.setValue((-(currentScreenCentery/inToPx)  - rb.currentY + viewerHeight ));
    }

    //draws the robot based on the realbot values provided
    private void drawRobot(){
        double centerX = getRealX(rb.currentX);
        double centerY = getRealY(rb.currentY);
        double adjustedX = getRealX(rb.currentX -rb.botWidth/2);
        double adjustedY = getRealY(rb.currentY +realBotBuilder.getBotLength()/2);

        gc.save();
        Rotate r = new Rotate(rb.currentTheta, adjustedX + rb.botWidth , adjustedY + realBotBuilder.getBotLength());
        gc.transform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(adjustedRobotImage, adjustedX, adjustedY);

        gc.restore();
        gc.setFill(Color.TEAL);
        gc.fillRect(centerX,centerY,2,2);
    }

    //draws the outline of the field, with later functionality of drawing an image of the field components.
    private void drawField(){
        gc.setStroke(Color.BLACK);
        gc.strokeRect(getRealX(0),getRealY(628), getRealX(324)-getRealX(0),getRealY(0)-getRealY(628));
    }


}
