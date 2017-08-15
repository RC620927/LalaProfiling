package sample;

import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import javax.swing.text.StyleContext;
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

    public MockBot(Image image, Movement movement, MovementType mt) {
        this.image = image;
        this.snapshots = movement.getSnapshots();
        this.botNode = new ImageView(image);
        this.movement = movement;
        this.mt = mt;
        setVisibility(false);
        running=false;
        position=0;
    }

    public MockBot(Movement movement, MovementType mt) {
        this.snapshots = movement.getSnapshots();
        this.movement = movement;
        this.botNode = new Rectangle(50,50);
        this.mt = mt;
        setVisibility(false);
        running=false;
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
                        botNode.setTranslateX(  s.x - botNode.getBoundsInLocal().getWidth()/2);
                        botNode.setTranslateY(500- s.y - botNode.getBoundsInLocal().getHeight()/2);
                        guideNode.setTranslateX(  s.x );
                        guideNode.setTranslateY(500- s.y );

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
                        botNode.setTranslateX(s.x - botNode.getBoundsInLocal().getWidth()/2);
                        botNode.setTranslateY(500-s.y - botNode.getBoundsInLocal().getWidth()/2);
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
}
