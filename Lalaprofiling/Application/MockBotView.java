package Lalaprofiling.Application;

import javafx.scene.Group;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.geom.Point2D;
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
    private Rectangle outline;

    private double zoom;
    private double scrollX, scrollY;



    //750 pixels height for 54" when at 25% zoom

    public MockBotView(MockBotBuild mockBotBuild){

        super();

        zoom =1;
        this.mockBotBuild = mockBotBuild;
        mockBotBuild.setMockBotView(this);
        int i =0;
    //    outline = new Rectangle(0,0,324,648);
     //   outline.setStroke(Color.BLACK);
      //  outline.setFill(Color.TRANSPARENT);


        for(MockBot mb : mockBotBuild.getMockBots()){
            if(i++ ==0){
                mb.setVisibility(true);
                mb.getBotNode().setTranslateX(mockBotBuild.getStartingPoint().getX() - mb.getBotNode().getBoundsInLocal().getWidth()/2);
                mb.getBotNode().setTranslateY(500 - mockBotBuild.getStartingPoint().getY()  -mb.getBotNode().getBoundsInLocal().getHeight()/2);
                mb.getBotNode().setRotate(mockBotBuild.getStartingAngle());
            }
            this.getChildren().add(mb.getBotNode());
            this.getChildren().add(mb.guideNode);
        }

        this.setOnZoom(z->{
            zoom = zoom * z.getTotalZoomFactor();
            System.out.println("Z:" +zoom);
        });

        this.setOnMouseClicked(e->{
            resetNodes();
        });
        g = this;
        mockBotListView = new MockBotListView(this.mockBotBuild, this);
        mockBotDotsViewer = new MockBotDotsViewer(this.mockBotBuild, this);
    }

    public MockBotBuild getMockBotBuild(){
        return  mockBotBuild;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        this.getMockBotDotsViewer().setZoom(zoom);
     //   outline.setWidth(324 * zoom * 4);
     //   outline.setHeight(648 * 4 * zoom);
        for(MockBot mb: mockBotBuild.getMockBots()){
            mb.setZoom(this.zoom);
        }
        if(!playing){
            resetNodes();
        }
    }

    public void setScrollX(double scrollX) {
        this.scrollX = scrollX;
    //    outline.setX(scrollX);
        this.getMockBotDotsViewer().setScrollX(scrollX);
        for(MockBot mb: mockBotBuild.getMockBots()){
            mb.setScrollX(this.scrollX);
        }
        if(!playing){
            resetNodes();
        }
    }

    public void setScrollY(double scrollY) {
        this.scrollY = scrollY;
    //    outline.setY(-scrollY);
        this.getMockBotDotsViewer().setScrollY(scrollY);
        for(MockBot mb: mockBotBuild.getMockBots()){
            mb.setScrollY(this.scrollY);
        }
        if(!playing){
            resetNodes();
        }
    }

    private boolean playing = false;
    public void play(){
        new Thread(){
            public void run(){
                playing=true;
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
                playing=false;
            }
        }.start();


    }

    public void resetNodes(){
        System.out.println("111");
        if(playing==false){

            System.out.println("222");
            this.getChildren().forEach(e->{
                e.setVisible(false);
            });
            MockBot first =  mockBotBuild.getMockBots().get(0);
            if(first!=null){
                first.resetPosition();
                first.getBotNode().setVisible(true);
            }
        }
    }

    public class MockBotListView extends ListView<MockBot>{
        MockBotBuild mockBotBuild;
        ArrayList<MockBot> mockBots;
        MockBotView mockBotView;

        public MockBotListView(MockBotBuild mockBotBuild, MockBotView mockBotView){
            super();
            this.mockBotBuild=mockBotBuild;
            this.mockBotView = mockBotView;
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

            this.setOnMouseClicked(e->{
                onMouseClicked();
            });

            update();


        }

        public void update(){
            MockBot selected = this.getSelectionModel().getSelectedItem();
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
            try{
                this.getSelectionModel().select(selected);
            }catch(Exception e){

            }
        }

        public void onMouseClicked(){
            mockBotView.getMockBotDotsViewer().highlight(this.getSelectionModel().getSelectedItem());
            mockBotView.resetNodes();
            System.out.println("PRESSED");
        }

    }

    public class MockBotDotsViewer extends Group{

        MockBotBuild mockBotBuild;
        HashMap<MockBot, ArrayList<Rectangle>> points;
        MockBotView mockBotView;
        private double zoom, scrollX, scrollY;

        public MockBotDotsViewer(MockBotBuild mockBotBuild, MockBotView mockBotView){
            super();
            this.mockBotBuild=mockBotBuild;
            this.mockBotView=mockBotView;
            zoom=1;
            update();
        }

        public void setZoom(double zoom){
            this.zoom=zoom;
            update();
        }

        synchronized public void update(){
            points=new HashMap<>();
            this.getChildren().removeAll(this.getChildren());
            for(MockBot mb: mockBotBuild.getMockBots()){
                ArrayList<Rectangle> rectangles = new ArrayList<>();
                for(Snapshot s: mb.getMovement().getSnapshots()){
                    Rectangle r = new Rectangle((zoom*s.x)-1 + scrollX,500 - scrollY -(zoom*s.y) -1,2,2);
                    rectangles.add(r);
                    this.getChildren().add(r);
                }
                points.put(mb, rectangles);
            }
        }

        private MockBot pastMockBot=null;
        public void highlight(MockBot mb){
            ArrayList<Rectangle> rectangles = points.get(mb);
            if(pastMockBot!=null && points.get(pastMockBot)!=null){
                for(Rectangle r: points.get(pastMockBot)){
                    r.setFill(Color.BLACK);
                    r.setHeight(2);
                    r.setWidth(2);

                    r.setX(r.getX()+3);
                    r.setY(r.getY()+3);
                }
            }
            if(rectangles !=null){
                for(Rectangle r: rectangles){
                    r.setFill(Color.FORESTGREEN);
                    r.setHeight(7);
                    r.setWidth(7);
                    r.setX(r.getX()-3);
                    r.setY(r.getY()-3);
                }
                pastMockBot = mb;

            }
        }
        public void setScrollX(double scrollX) {
            this.scrollX = scrollX;
            update();
        }

        public void setScrollY(double scrollY) {
            this.scrollY = scrollY;
            update();
        }
    }


    public MockBotListView getMockBotListView() {
        return mockBotListView;
    }

    public MockBotDotsViewer getMockBotDotsViewer() {
        return mockBotDotsViewer;
    }

    public double getZoom() {
        return zoom;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }


}
