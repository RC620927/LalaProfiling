package Lalaprofiling.Application;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 * Created by raque on 8/10/2017.
 */
public class MockBot {

    public enum MovementType{
        ROTATE("ROTATE"),LINE("LINE"),BEZIERCURVE("BEZIERCURVE");
        String name;
        MovementType(String name){
            this.name=name;
        }
        public String getName(){
            return name;
        }
    }

    private Image image;
    private ArrayList<Snapshot> snapshots;
    private Node botNode;
    private MovementType mt;
    private Movement movement;
    private int position;

    boolean running;

    private double scrollX,scrollY,zoom;

    public MockBot(Image image, Movement movement, MovementType mt) {
        this.image = image;
        this.snapshots = movement.getSnapshots();
        this.botNode = new ImageView(image);
        this.movement = movement;
        this.mt = mt;
        setVisibility(false);
        running=false;
        position=0;
        scrollX=0;
        scrollY=0;
    }

    public MockBot(Movement movement, MovementType mt) {
        this.snapshots = movement.getSnapshots();
        this.movement = movement;
        this.botNode = new Rectangle(50,50);
        this.mt = mt;
        setVisibility(false);
        running=false;
        scrollX = 0;
        scrollY = 0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setVisibility(boolean v){
        botNode.setVisible(v);
    }

    public Rectangle guideNode = new Rectangle(2,2);

    public void execute(){
        running=true;
        guideNode.setFill(Color.AQUA);
        botNode.setVisible(true);

        if(mt == MovementType.BEZIERCURVE || mt == MovementType.LINE) {

            new Thread(){
                @Override
                public void run(){

                    for (Snapshot s: snapshots){
                        botNode.setRotate(s.angle);
                        botNode.setTranslateX(scrollX + (zoom * s.x - botNode.getBoundsInLocal().getWidth()/2));
                        botNode.setTranslateY(500- scrollY - (zoom *s.y + botNode.getBoundsInLocal().getHeight()/2));
                        guideNode.setTranslateX( zoom * s.x );
                        guideNode.setTranslateY(500- (zoom*s.y) );

                        try {
                            Thread.sleep(1000/Movement.refreshRate);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    running=false;
                }
            }.start();

        }else if(mt == MovementType.ROTATE) {
            new Thread(){
                @Override
                public void run(){
                    for (Snapshot s: snapshots){
                        botNode.setRotate(s.angle);
                        botNode.setTranslateX(scrollX + (zoom*s.x - botNode.getBoundsInLocal().getWidth()/2));
                        botNode.setTranslateY(500- scrollY - (zoom*s.y +  botNode.getBoundsInLocal().getHeight()/2));
                        try {
                            Thread.sleep(1000/Movement.refreshRate);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    running=false;
                }
            }.start();
        }

    }

    public boolean isDone(){
        return !running;
    }

    public Node getBotNode(){
        return botNode;
    }

    public Movement getMovement() {
        return movement;
    }

    public MovementType getMt() {
        return mt;
    }

    public void resetPosition(){
        botNode.setRotate(getMovement().getInitialAngle());
        botNode.setTranslateX(scrollX + (zoom * getMovement().getStartPoint().getX() - botNode.getBoundsInLocal().getWidth()/2));
        botNode.setTranslateY(500- scrollY - (zoom * getMovement().getStartPoint().getY() + botNode.getBoundsInLocal().getHeight()/2));
        guideNode.setTranslateX( zoom * getMovement().getStartPoint().getX() );
        guideNode.setTranslateY(500- (zoom*getMovement().getStartPoint().getY() ) );
    }

    public void setScrollX(double scrollX) {
        this.scrollX = scrollX;
    }

    public void setScrollY(double scrollY) {
        this.scrollY = scrollY;
    }

    public void setZoom(double zoom) {
        this.getBotNode().setScaleY(zoom);
        this.getBotNode().setScaleX(zoom);
        this.zoom = zoom;
    }

    synchronized public void update(){
        this.snapshots = movement.getSnapshots();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MockBot mockBot = (MockBot) o;

        if (position != mockBot.position) return false;
        return snapshots.equals(mockBot.snapshots);
    }

    @Override
    public int hashCode() {
        int result = snapshots.hashCode();
        result = 31 * result + position;
        return result;
    }
}
