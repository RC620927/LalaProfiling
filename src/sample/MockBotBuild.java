package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by raque on 8/11/2017.
 */
public class MockBotBuild {

    private ArrayList<MockBot> mockBots;
    private Point2D startingPoint;
    private double startingAngle;
    private MockBotView mockBotView;



    public MockBotBuild(Point2D startingPoint, double startingAngle){
        mockBots = new ArrayList<>();

        this.startingPoint = startingPoint;
        this.startingAngle = startingAngle;
    }

    public void addLine(double distance, boolean reverse){
        double pastAngle, startX, startY;
        if(mockBots.isEmpty()){
            pastAngle = startingAngle;
            startX = startingPoint.getX();
            startY = startingPoint.getY();

        }else{
            startX = mockBots.get(mockBots.size()-1).getMovement().getEndPoint().getX();
            startY = mockBots.get(mockBots.size()-1).getMovement().getEndPoint().getY();
            pastAngle = mockBots.get(mockBots.size()-1).getMovement().getEndAngle();
        }

        double endX = startX +
                Math.sin(Math.toRadians(pastAngle)) * distance;
        double endY = startY +
                Math.cos(Math.toRadians(pastAngle)) * distance;

        LineMovement lm = new LineMovement(new Point2D.Double(startX,startY), new Point2D.Double(endX,endY),reverse, distance/160);
        MockBot mb = new MockBot(new Image("RobotPicture.png"), lm, MockBot.MovementType.LINE);
        mockBots.add(mb);
        if(mockBotView!=null){mockBotView.getChildren().add(mb.getBotNode());}
        update();
    }

    //add a line, but if endpoint with angle does not quite align, then rotate first.
    public void addLine(Point2D endPoint, boolean reverse){
        double pastAngle, startX, startY;
        if(mockBots.isEmpty()){
            pastAngle = startingAngle;
            startX = startingPoint.getX();
            startY = startingPoint.getY();

        }else{
            startX = mockBots.get(mockBots.size()-1).getMovement().getEndPoint().getX();
            startY = mockBots.get(mockBots.size()-1).getMovement().getEndPoint().getY();
            pastAngle = mockBots.get(mockBots.size()-1).getMovement().getEndAngle();
        }

        double endX = endPoint.getX();
        double endY = endPoint.getY();
        double distance = Math.hypot(endX-startX, endY-startY);

        double travelAngle = Math.atan( (endY-startY) / (endX-startX));
        travelAngle = Math.toRadians(90) - travelAngle;
        travelAngle = Math.toDegrees(travelAngle);
        travelAngle = (endX-startX) <0 ? travelAngle + 180 : travelAngle;

        if(Math.abs(RotateMovement.getChangeTheta(travelAngle,pastAngle)) >1 ){
            RotateMovement rm = new RotateMovement(new Point2D.Double(startX,startY),
                    pastAngle,travelAngle, RotateMovement.getChangeTheta(travelAngle,pastAngle)/200);
            MockBot mb1 = new MockBot(new Image("RobotPicture.png"),rm, MockBot.MovementType.ROTATE);
            mockBots.add(mb1);
            if(mockBotView!=null){mockBotView.getChildren().add(mb1.getBotNode());}
        }

        LineMovement lm = new LineMovement(new Point2D.Double(startX,startY), new Point2D.Double(endX,endY), reverse, distance/160);
        MockBot mb2 = new MockBot(new Image("RobotPicture.png"), lm, MockBot.MovementType.LINE);
        mockBots.add(mb2);
        if(mockBotView!=null){mockBotView.getChildren().add(mb2.getBotNode());}
        update();
    }


    public void setMockBotView(MockBotView mbw){
        this.mockBotView = mbw;
    }


    public void addTurn(double endingAngle){
        double pastAngle, startX, startY;
        if(mockBots.isEmpty()){
            pastAngle = startingAngle;
            startX = startingPoint.getX();
            startY = startingPoint.getY();

        }else{
            startX = mockBots.get(mockBots.size()-1).getMovement().getEndPoint().getX();
            startY = mockBots.get(mockBots.size()-1).getMovement().getEndPoint().getY();
            pastAngle = mockBots.get(mockBots.size()-1).getMovement().getEndAngle();
        }

        RotateMovement rm = new RotateMovement(new Point2D.Double(startX,startY),
                pastAngle,endingAngle,  Math.abs(RotateMovement.getChangeTheta(endingAngle,pastAngle))/200);
        MockBot mb = new MockBot(new Image("RobotPicture.png"), rm, MockBot.MovementType.ROTATE);
        mockBots.add(mb);
        if(mockBotView!=null){mockBotView.getChildren().add(mb.getBotNode());}
        update();
    }


    public void addBezierCurve(Point2D endPoint, double endingAngle, double fudge1, double fudge2, boolean reverse){
        double pastAngle, startX, startY;
        if(mockBots.isEmpty()){
            pastAngle = startingAngle;
            startX = startingPoint.getX();
            startY = startingPoint.getY();

        }else{
            startX = mockBots.get(mockBots.size()-1).getMovement().getEndPoint().getX();
            startY = mockBots.get(mockBots.size()-1).getMovement().getEndPoint().getY();
            pastAngle = mockBots.get(mockBots.size()-1).getMovement().getEndAngle();
        }

        double endX = endPoint.getX();
        double endY = endPoint.getY();
        double distance = Math.hypot(endX-startX, endY-startY);

        BezierCurveMovement bcm = new BezierCurveMovement(new Point2D.Double(startX,startY),
                endPoint, pastAngle, endingAngle, fudge1, fudge2, reverse, distance/120 );
        MockBot mb = new MockBot(new Image("RobotPicture.png"), bcm, MockBot.MovementType.BEZIERCURVE);
        mockBots.add(mb);
        if(mockBotView!=null){mockBotView.getChildren().add(mb.getBotNode());}
        update();
    }

    public void remove(int index){

    }
    public void remove(MockBot mb){
        this.mockBots.remove(mb);
        updateStarts();
    }

    public ArrayList<MockBot> getMockBots(){
        return  mockBots;
    }

    public Point2D getStartingPoint() {
        return startingPoint;
    }

    public double getStartingAngle() {
        return startingAngle;
    }

    synchronized public void update(){
        int index =1;
        for(MockBot mb : mockBots){
            mb.setPosition(index);
            if(mockBotView!=null){
                mb.setZoom(mockBotView.getZoom());
                mb.setScrollY(mockBotView.getScrollY());
                mb.setScrollX(mockBotView.getScrollX());
            }


            index++;
        }

        if(mockBotView!=null){
            mockBotView.getMockBotListView().update();
            mockBotView.getMockBotDotsViewer().update();
        }
    }

    public void updateStarts(){
        int index =1;
        for(MockBot mb : mockBots){
            mb.setPosition(index);
            if(mockBotView!=null){

                if(index==1){
                    mb.getMovement().setStartPoint(startingPoint);
                    mb.getMovement().setInitialAngle(startingAngle);
                    mb.update();
                }else{
                    mb.getMovement().setStartPoint(mockBots.get(index-2).getMovement().getEndPoint());
                    mb.getMovement().setInitialAngle(mockBots.get(index-2).getMovement().getEndAngle());
                    mb.update();
                }
            }
            index++;
        }
        if(mockBotView!=null){
            mockBotView.getMockBotListView().update();
            mockBotView.getMockBotDotsViewer().update();
        }


    }

}
