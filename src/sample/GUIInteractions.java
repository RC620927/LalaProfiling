package sample;

import java.awt.geom.Point2D;

/**
 * Created by raque on 8/13/2017.
 */
public class GUIInteractions {

    public static void addToMockBuild(MockBotBuild mbb,String movementType,Point2D.Double endPoint,
                                 Double distance, Double endingAngle, Double fudge1, Double fudge2){
        switch(movementType){
            case "Line":
                mbb.addLine(distance);
                break;
            case "Bezier Curve":
                mbb.addBezierCurve(endPoint,endingAngle,fudge1,fudge2);
                break;
            case "Turn":
                mbb.addTurn(endingAngle);
                break;
        }
    }



}
