package sample;

import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        ROTATE,LINE,BEZIERCURVE;
    }

    private Image image;
    private ArrayList<Snapshot> snapshots;
    private Node botNode;
    private MovementType mt;

    public MockBot(Image image, ArrayList<Snapshot> snapshots, MovementType mt) {
        this.image = image;
        this.snapshots = snapshots;
        botNode = new ImageView(image);
        this.mt = mt;
    }


    public MockBot(ArrayList<Snapshot> snapshots, MovementType mt) {
        this.snapshots = snapshots;
        this.botNode = new Rectangle(50,50);
        this.mt = mt;
    }

    public void setVisibility(boolean v){
        botNode.setVisible(v);
    }

    public void execute(){


        botNode.setVisible(true);
        if(mt == MovementType.BEZIERCURVE || mt == MovementType.LINE) {

            new Thread(){
                @Override
                public void run(){

                    for (Snapshot s: snapshots){
                        botNode.setRotate(s.angle);
                        botNode.setTranslateX(s.x);
                        botNode.setTranslateY(500- s.y);
                        try {
                            Thread.sleep(1000/Movement.refreshRate);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

        }else if(mt == MovementType.ROTATE) {
            new Thread(){
                @Override
                public void run(){
                    for (Snapshot s: snapshots){
                        botNode.setRotate(s.angle);
                        try {
                            Thread.sleep(1000/Movement.refreshRate);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

    }

    public Node getBotNode(){
        return botNode;
    }

}
