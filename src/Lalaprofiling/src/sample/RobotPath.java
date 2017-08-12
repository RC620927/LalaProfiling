package sample;

import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

/**
 * Created by Ruben on 8/9/2017.
 */
public class RobotPath {

    private Path robotPath;

    private double currentX, currentY;
    private double currentAngle;


 //   private ArrayList<Shape>

    public RobotPath(){

    }

    public Path getRobotPath() {
        return robotPath;
    }

    public void newLineMovement(double endingX, double endingY){

    }

    public void newBezierCurveMovement(double endingX, double endingY, double fudge, double endingAngle, boolean cubic){
        double c1X, c2X, c1Y,c2Y;


    }

    public void newTurnMovement(){

    }

}
