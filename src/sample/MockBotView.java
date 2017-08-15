package sample;

import javafx.animation.PathTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.w3c.dom.css.Rect;

import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by raque on 8/11/2017.
 */
public class MockBotView extends Group{

    private MockBotBuild mockBotBuild;
    Group g;

    private MockBotListView mockBotListView;
    private MockBotDotsViewer mockBotDotsViewer;

    public MockBotView(MockBotBuild mockBotBuild){
        super();
        this.mockBotBuild = mockBotBuild;
        mockBotBuild.setMockBotView(this);
        int i =0;
        for(MockBot mb : mockBotBuild.getMockBots()){
            if(i++ ==0){
                mb.setVisibility(true);
                mb.getBotNode().setTranslateX(mockBotBuild.getStartingPoint().getX() - mb.getBotNode().getBoundsInLocal().getWidth()/2);
                mb.getBotNode().setTranslateY(500 - mockBotBuild.getStartingPoint().getY()  -mb.getBotNode().getBoundsInLocal().getHeight()/2);
                mb.getBotNode().setRotate(mockBotBuild.getStartingAngle());
            }
            this.getChildren().add(mb.getBotNode());
        }
        g = this;
        mockBotListView = new MockBotListView(this.mockBotBuild);
        mockBotDotsViewer = new MockBotDotsViewer(this.mockBotBuild);

    }

    public MockBotBuild getMockBotBuild(){
        return  mockBotBuild;
    }

    public void play(){
        new Thread(){
            public void run(){

                for(MockBot mb: mockBotBuild.getMockBots()){ mb.setVisibility(false);}

                    int i =0;
                for(MockBot mb: mockBotBuild.getMockBots()){

                    mb.setVisibility(true);
                    mb.execute();


                    while(!mb.isDone()){
                        try {
                            Thread.sleep(25);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    };
                    if(i++ != mockBotBuild.getMockBots().size()-1){
                        mb.setVisibility(false);
                    }
                }
            }
        }.start();


    }

    public class MockBotListView extends ListView<MockBot>{
        MockBotBuild mockBotBuild;
        ArrayList<MockBot> mockBots;

        public MockBotListView(MockBotBuild mockBotBuild){
            super();
            this.mockBotBuild=mockBotBuild;
            this.setCellFactory( mb ->{
                return new ListCell<MockBot>() {
                    @Override
                    protected void updateItem(MockBot item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText("hi");
                            setStyle("");
                        } else {
                            Point2D startPoint = item.getMovement().getStartPoint();
                            Point2D endPoint = item.getMovement().getEndPoint();
                            double distance = Math.hypot(endPoint.getX() - startPoint.getX(), endPoint.getY() - startPoint.getY());
                            switch (item.getMt().getName()) {
                                case "LINE":
                                    setText(item.getPosition() + ":Line D:" + distance);
                                    break;
                                case "ROTATE":
                                    setText(item.getPosition() + "Turn END:" + item.getMovement().getEndAngle() + "deg");
                                    if(item.getPosition()>10){
                                        System.out.println("YO:" + item.getPosition());
                                    }
                                    break;
                                case "BEZIERCURVE":
                                    setText(item.getPosition() + "Bezier D:" + distance);
                                    break;
                            }

                        }
                    }
                };
            });
            update();


        }

        public void update(){
            if(this.getItems().size()!=0){
                this.getItems().remove(0,this.getItems().size());
            }

            mockBots = mockBotBuild.getMockBots();
            System.out.println("Refreshing List View");
            int i = 0;
            for(MockBot mb :mockBots){
                this.getItems().add(mb);
            }
            System.out.println(this.getItems().size());
        }

    }

    public class MockBotDotsViewer extends Group{

        MockBotBuild mockBotBuild;
        HashMap<MockBot, ArrayList<Rectangle>> points;

        public MockBotDotsViewer(MockBotBuild mockBotBuild){
            super();
            this.mockBotBuild=mockBotBuild;
            update();
        }

        public void update(){
            points=new HashMap<>();
            for(MockBot mb: mockBotBuild.getMockBots()){
                ArrayList<Rectangle> rectangles = new ArrayList<>();
                for(Snapshot s: mb.getMovement().getSnapshots()){
                    Rectangle r = new Rectangle(s.x,500 - s.y,1,1);
                    rectangles.add(r);
                    this.getChildren().add(r);
                }
                points.put(mb, rectangles);
            }


        }

        private MockBot pastMockBot=null;
        public void highlight(MockBot mb){
            ArrayList<Rectangle> rectangles = points.get(mb);
            if(pastMockBot!=null){
                for(Rectangle r: rectangles){
                    r.setFill(Color.BLACK);
                }
            }
            if(rectangles !=null){
                for(Rectangle r: rectangles){
                    r.setFill(Color.AQUA);
                }
                pastMockBot = mb;
            }
        }
    }

    public MockBotListView getMockBotListView() {
        return mockBotListView;
    }

    public MockBotDotsViewer getMockBotDotsViewer() {
        return mockBotDotsViewer;
    }
}
