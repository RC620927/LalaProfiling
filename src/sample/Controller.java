package sample;

import RealBot.*;
import RealBot.Trajectory;
import RealBot.Path;
import de.thomasbolz.javafx.NumberSpinner;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import sun.plugin.javascript.navig.Anchor;

import javax.sound.sampled.Line;
import java.awt.geom.Point2D;
import java.math.BigDecimal;

public class Controller {



    //initialize hboxes on right for variables
    @FXML
    public HBox initialPositionHBox;

    @FXML
    public HBox endingPositionHBox;

    @FXML
    public HBox initialAngleHBox;

    @FXML
    public HBox endingAngleHBox;

    @FXML
    public HBox initialSpeedHBox;

    @FXML
    public HBox endingSpeedHBox;

    @FXML
    public HBox topSpeedHBox;

    @FXML
    public HBox distanceHBox;

    @FXML
    public HBox fudgeHBox;

    @FXML
    public RadioButton forwardsRadioButton;

    @FXML
    public RadioButton reverseRadioButton;


    @FXML
    public VBox movementsVBox;

    @FXML
    public AnchorPane layoutAnchorPane;

    @FXML
    public StackPane stackPaneViewer;

    @FXML
    public Button newButton;

    @FXML
    public Button deleteButton;

    @FXML
    public Button runButton;

    @FXML
    public Group viewGroup;

    @FXML
    public ScrollBar scrollBarX;

    @FXML
    public ScrollBar scrollBarY;

    @FXML
    public Slider zoomSlider;

    @FXML
    public RadioMenuItem zoom25,zoom50,zoom75,zoom100,zoom125,zoom150;


    public NumberSpinner initialX,initialY, endingX,endingY, distance, initialAngle, endingAngle, initialSpeed,endingSpeed,topSpeed,fudge1,fudge2;

    @FXML
    public VBox BIGLAYOUT;

    /*@FXML
    public Canvas c;*/

    MockBotView mockBotView;
    MockBotBuild mockBotBuild;

    MockBot.MovementType movementType;

    boolean key = false;
    boolean togglerKey =false;

    double refreshRate = 200; //refreshes per second
    double scrollXRefreshed; // time in millis when last scrolled.. to prevent wasting alot of resources.
    double scrollYRefreshed; // time in millis when last scrolled.. to prevent wasting alot of resources.

    @FXML
    public void initialize(){

        /*LineMovement lm = new LineMovement(new Point2D.Double(0,0), new Point2D.Double(0,400),false,5);
        BezierCurveMovement bcm = new BezierCurveMovement(new Point2D.Double(0, 0), new Point2D.Double(500,150), 0,130,300,150,false,5);
        RotateMovement rm = new RotateMovement(new Point2D.Double(0, 0),0,90,5);
        Path p = new Path(bcm.getSnapshots(), 50,new Snapshot(0,0,0));
        Trajectory t = new Trajectory(p,0,0,150,0,100,160);
        RealBot rb = new RealBot(t, 50);
        */

        RealBotBuilder rbb = new RealBotBuilder(new Point2D.Double(164,15),0.0,30,30,230,180);

//        rbb.getMovements().addQuarticBezierCurve(new Point2D.Double(400,603),180,206,206, 0,300, false,150,0);
        rbb.getMovements().addLine(79, false,150,0);//end scoring at 164, 94
     /*   rbb.getMovements().addBezierCurve(new Point2D.Double(200,43),270,36,36,true,150,-70);
        rbb.getMovements().addBezierCurve(new Point2D.Double(230,43), 270,1,1,true,150,0);
        rbb.getMovements().addTurn(180,150,0);
        rbb.getMovements().addLine(25,false,150,0);//end at 230,18*/


        rbb.getMovements().addBezierCurve(new Point2D.Double(230,63),180,36,66,true,150,0);
        rbb.getMovements().addLine(45,false,150,0);//end at 230,18

        /*rbb.getMovements().addBezierCurve(new Point2D.Double(194,54),90,36,36,true,150,-70);
        rbb.getMovements().addBezierCurve(new Point2D.Double(164,54),90,10,10,true,150,0);
        */
        rbb.getMovements().addBezierCurve(new Point2D.Double(164,29),0,60,40,true,150,0);


       // rbb.getMovements().addTurn(0,150,0); // end at 164,54
        rbb.getMovements().addLine(65, false,150,0);// end scoring at 164, 94


/*
        rbb.getMovements().addBezierCurve(new Point2D.Double(128,43),90,36,36,true,150,-70);
        rbb.getMovements().addBezierCurve(new Point2D.Double(100,43), 90,10,10,true,150,0);
        rbb.getMovements().addTurn(180,150,0);
        rbb.getMovements().addLine(25,false,150,0);//end at 100,18
*/

        rbb.getMovements().addBezierCurve(new Point2D.Double(100,63),180,36,66,true,150,0);
        rbb.getMovements().addLine(45,false,150,0);//end at 230,18



     /*   rbb.getMovements().addBezierCurve(new Point2D.Double(136,54),270,36,36,true,0,0,150,-70);
        rbb.getMovements().addBezierCurve(new Point2D.Double(164,54),270,10,10,true,-70,-70,150,0);
        rbb.getMovements().addTurn(0,0,0,150,0);
        rbb.getMovements().addLine(25, false, 0,0,150,0);
*/

        rbb.getMovements().addBezierCurve(new Point2D.Double(70,80),60,10,40,true,150,0);

        rbb.getMovements().addLine(50,false,150,0);


     //   rbb.getMovements().addBezierCurve(new Point2D.Double(164,44),0,60,60,true,150,0);
     //   rbb.getMovements().addLine(50, false,150,0);// end scoring at 164, 94


      /*  rbb.getMovements().addLine(90,false,0,0,150,0);
        rbb.getMovements().addBezierCurve(new Point2D.Double(200,50),180,50,60,true,0,0,150,0);
      //  rbb.getMovements().addTurn(270,0,0,150,0);
        rbb.getMovements().addLine(30,false,0,0,150,0);
        rbb.getMovements().addBezierCurve(new Point2D.Double(164,60),0,30,70,true,0,0,150,0);
        //rbb.getMovements().addTurn(0,0,0,150,0);
        rbb.getMovements().addLine(30,false,0,0,150,0);
        rbb.getMovements().addBezierCurve(new Point2D.Double(120,60),180,30,70,true,0,0,150,0);
       // rbb.getMovements().addTurn(180,0,0,150,0);
        rbb.getMovements().addLine(30,false,0,0,150,0);
       // rbb.getMovements().addLine(100,true,0,0,150,0);
        rbb.getMovements().addBezierCurve(new Point2D.Double(120,100),180,30,30,true,0,0,150,0);

        rbb.getMovements().addTurn(60,0,0,150,0);
        rbb.getMovements().addLine(30,false,0,0,150,0);*/


        RealBotView rbw = new RealBotView(rbb);
        rbw.setStyle("-fx-background-color: blue");

        Pane g = new Pane();
        g.setStyle("-fx-background-color: blue");
        Canvas c = new Canvas(300,300);
        rbw.widthProperty().bind(layoutAnchorPane.widthProperty());
        rbw.heightProperty().bind(layoutAnchorPane.heightProperty());
        rbw.scrollX.bindBidirectional(scrollBarX.valueProperty());
        rbw.scrollY.bindBidirectional(scrollBarY.valueProperty());
        rbw.zoom.bindBidirectional(zoomSlider.valueProperty());
        rbw.zoom.setValue(1);

        GraphicsContext gc = rbw.getGraphicsContext2D();
        c.setStyle("-fx-background-color: red");
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        //gc.fillOval(-100, -100, 800, 800);
        gc.fillRect(0,500,400,900);
        //stackPaneViewer.getChildren().add(c);
        //BIGLAYOUT.setStyle("-fx-background-color: blue");
        g.getChildren().add(rbw);
        layoutAnchorPane.getChildren().addAll(g);


        /*Rectangle clipRectangle = new Rectangle();
        BIGLAYOUT.setClip(clipRectangle);
        BIGLAYOUT.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            clipRectangle.setWidth(newValue.getWidth());
            clipRectangle.setHeight(newValue.getHeight());
        });*/

        ToggleGroup directionTG = new ToggleGroup();
        directionTG.getToggles().add(reverseRadioButton);
        directionTG.getToggles().add(forwardsRadioButton);

        initialX = new NumberSpinner(0.0,0.5);
        initialY = new NumberSpinner(0.0,0.5);
        initialPositionHBox.getChildren().addAll(initialX,initialY);

        endingX = new NumberSpinner(0.0,0.5);
        endingY = new NumberSpinner(0.0,0.5);
        endingPositionHBox.getChildren().addAll(endingX,endingY);

        distance = new NumberSpinner(0.0,0.5,0.0, Double.POSITIVE_INFINITY);
        distanceHBox.getChildren().add(distance);

        initialAngle = new NumberSpinner(0.0,0.5, 0.0, 360.0);
        initialAngleHBox.getChildren().add(initialAngle);

        endingAngle = new NumberSpinner(0.0,0.5,  0.0, 360.0);
        endingAngleHBox.getChildren().add(endingAngle);

        initialSpeed = new NumberSpinner(0.0,0.5);
        initialSpeedHBox.getChildren().add(initialSpeed);

        endingSpeed = new NumberSpinner(0.0,0.5);
        endingSpeedHBox.getChildren().add(endingSpeed);

        topSpeed = new NumberSpinner(0.0,0.5);
        topSpeedHBox.getChildren().add(topSpeed);

        fudge1 = new NumberSpinner(0.0,0.5);
        fudge2 = new NumberSpinner(0.0,0.5);
        fudgeHBox.getChildren().addAll(fudge1,fudge2);


        //setup scrollbars
        scrollBarX.toFront();
        scrollBarY.toFront();
        scrollXRefreshed = System.currentTimeMillis();
        scrollYRefreshed = System.currentTimeMillis();
        /*scrollBarX.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(System.currentTimeMillis()-scrollXRefreshed >1000/refreshRate){
                    mockBotView.setScrollX(newValue.doubleValue());
                    scrollXRefreshed=System.currentTimeMillis();
                }

            }
        });
        scrollBarY.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(System.currentTimeMillis()-scrollYRefreshed >1000/refreshRate){
                    mockBotView.setScrollY(newValue.doubleValue());
                    scrollYRefreshed=System.currentTimeMillis();
                }
            }
        });*/

        //blackout boxes not necessary
        fudge1.setDisable(true);
        fudge2.setDisable(true);
        initialX.setDisable(true);
        initialY.setDisable(true);
        endingX.setDisable(true);
        endingY.setDisable(true);
        distance.setDisable(false);
        endingAngle.setDisable(true);
        initialAngle.setDisable(true);


        //setup Mock bots
        mockBotBuild= new MockBotBuild(new Point2D.Double(200,0), 0);
       mockBotBuild.addBezierCurve(new Point2D.Double(500,200),60,100,200,false);
        // mockBotBuild.addLine(200,false);
/*
        mockBotBuild.addBezierCurve(new Point2D.Double(400,120), 270, 150,100,true);

        mockBotBuild.addTurn(180);
        mockBotBuild.addLine(120, false);

        mockBotBuild.addBezierCurve(new Point2D.Double(200,120), 90, 150,100,true);
        mockBotBuild.addTurn(0);
        mockBotBuild.addLine(80,false);
        */
        mockBotView = new MockBotView(mockBotBuild);
        mockBotView.setScaleX(1);
        mockBotView.setScaleY(1);
        mockBotView.setStyle("-fx-background-color: #2f4f4f");
        mockBotView.setZoom(1);

        //setup listeners
        endingX.numberProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                if (oldValue != newValue && !key) {
                    key=true;
                    double delX = newValue-initialX.getNumber();
                    double delY = endingY.getNumber()-initialY.getNumber();
                    distance.setNumber(Math.sqrt(delX * delX + Math.pow(delY, 2)));
                    modifySelectedMockBot();
                    key=false;
                }


            }
        });
        endingY.numberProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {

                if (newValue != oldValue && !key) {
                    key=true;
                    double delX = endingX.getNumber()-initialX.getNumber();
                    double delY = newValue-initialY.getNumber();
                    distance.setNumber(Math.sqrt(delX * delX + Math.pow(delY, 2)));
                    modifySelectedMockBot();
                    key=false;
                }
            }
        });
        distance.numberProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {

                if (oldValue != newValue&&!key) {
                    key=true;
                    double theta = endingAngle.getNumber();
                    endingX.setNumber(Math.sin(Math.toRadians(theta)) * newValue + initialX.getNumber());
                    endingY.setNumber(Math.cos(Math.toRadians(theta)) * newValue + initialY.getNumber());
                    modifySelectedMockBot();
                    key=false;
                }

            }
        });

        endingAngle.numberProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                if(oldValue!=newValue && !key){
                    key=true;
                    modifySelectedMockBot();
                    key=false;
                }
            }
        });
        fudge1.numberProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                if(oldValue!=newValue && !key){
                    key=true;
                    modifySelectedMockBot();
                    key=false;
                }
            }
        });
        fudge2.numberProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                if(oldValue!=newValue && !key){
                    key=true;
                    modifySelectedMockBot();
                    key=false;
                }
            }
        });
        forwardsRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue!=newValue && !key){
                    key=true;
                    modifySelectedMockBot();
                    key=false;
                    System.out.println("FORWARDSSs");
                }
            }
        });


        //setup mock bots list view
        movementsVBox.getChildren().add(mockBotView.getMockBotListView());
        mockBotView.getMockBotListView().setOnMouseClicked(e->{
            mockBotView.getMockBotListView().onMouseClicked();
            key=true;
            setValues(mockBotView.getMockBotListView().getSelectionModel().getSelectedItem());
            key=false;
            System.out.println("AFTER");
        });

        //layoutAnchorPane.getChildren().add(mockBotView);
        //layoutAnchorPane.getChildren().add(mockBotView.getMockBotDotsViewer());


        //setup table

        runButton.setOnAction(e->{
            //mockBotView.play();
            rbw.play();
        });

        deleteButton.setOnAction(e->{
          //  deleteSelectedMockBot();
            rbb.testWriteExcel();
        });


        NewMovementWindow nmw = new NewMovementWindow(mockBotBuild);
        newButton.setOnAction(e->{
            /*
            GUIInteractions.addToMockBuild(mockBotBuild,movementTypeChooser.getValue(),
                    new Point2D.Double(endingX.getNumber(),endingY.getNumber()),distance.getNumber(),
                    endingAngle.getNumber(),fudge1.getNumber(),fudge2.getNumber());*/
            //mockBotBuild.addLine(10,false);
           // nmw.use();

            rbw.centerOnRobot();
        });

        ToggleGroup zoomToggle = new ToggleGroup();
        zoom25.setOnAction(e->{mockBotView.setZoom(0.25);});
        zoom25.setToggleGroup(zoomToggle);
        zoom50.setOnAction(e->{mockBotView.setZoom(0.5);});
        zoom50.setToggleGroup(zoomToggle);
        zoom75.setOnAction(e->{mockBotView.setZoom(0.75);});
        zoom75.setToggleGroup(zoomToggle);
        zoom100.setOnAction(e->{mockBotView.setZoom(1);});
        zoom100.setToggleGroup(zoomToggle);
        zoom125.setOnAction(e->{mockBotView.setZoom(1.25);});
        zoom125.setToggleGroup(zoomToggle);
        zoom150.setOnAction(e->{mockBotView.setZoom(1.5);});
        zoom150.setToggleGroup(zoomToggle);


        rbw.reset();
    }

    public void setValues(MockBot mb){
        if(mb!=null){
            togglerKey = true;
            this.initialX.setNumber(mb.getMovement().getStartPoint().getX());
            this.initialY.setNumber(mb.getMovement().getStartPoint().getY());
            this.endingX.setNumber(mb.getMovement().getEndPoint().getX());
            this.endingY.setNumber(mb.getMovement().getEndPoint().getY());
            double delX = endingX.getNumber() -initialX.getNumber();
            double delY = endingY.getNumber()-initialY.getNumber();
            this.distance.setNumber(Math.sqrt(delX * delX + Math.pow(delY, 2)));
            this.initialAngle.setNumber(mb.getMovement().getInitialAngle());
            this.endingAngle.setNumber(mb.getMovement().getEndAngle());
            if(mb.getMovement().getReverse()){
                this.reverseRadioButton.selectedProperty().set(true);
            }else{
                this.forwardsRadioButton.selectedProperty().set(true);
            }
            if(mb.getMt()== MockBot.MovementType.BEZIERCURVE){
                this.fudge1.setNumber(((BezierCurveMovement) mb.getMovement()).getFudge1());
                this.fudge2.setNumber(((BezierCurveMovement) mb.getMovement()).getFudge2());
                toggleBCBoxes();

                System.out.println("BC");
            }else{
                if(mb.getMt() == MockBot.MovementType.LINE){

                    toggleLineBoxes();
                    System.out.println("Line");
                }else{

                    toggleTurnBoxes();
                    System.out.println("Turn");
                }
                fudge1.setNumber(0.0);
                fudge2.setNumber(0.0);
                togglerKey=false;
            }
        }
    }

    private void toggleLineBoxes(){
        fudge1.setDisable(true);
        fudge2.setDisable(true);
        initialX.setDisable(true);
        initialY.setDisable(true);
        endingX.setDisable(true);
        endingY.setDisable(true);
        distance.setDisable(false);
        endingAngle.setDisable(true);
        initialAngle.setDisable(true);
        System.out.println("LINEFR");
    }
    private void toggleBCBoxes(){
        fudge1.setDisable(false);
        fudge2.setDisable(false);
        initialX.setDisable(true);
        initialY.setDisable(true);
        endingX.setDisable(false);
        endingY.setDisable(false);
        distance.setDisable(true);
        endingAngle.setDisable(false);
        initialAngle.setDisable(true);
        System.out.println("BCFR");
    }
    private void toggleTurnBoxes(){
        fudge1.setDisable(true);
        fudge2.setDisable(true);
        initialX.setDisable(true);
        initialY.setDisable(true);
        endingX.setDisable(true);
        endingY.setDisable(true);
        distance.setDisable(true);
        endingAngle.setDisable(false);
        initialAngle.setDisable(true);
        System.out.println("TURNFR");
    }

    private void modifySelectedMockBot(){
        if(mockBotView!=null){
            MockBot selected = mockBotView.getMockBotListView().getSelectionModel().getSelectedItem();
            if(selected!=null) {
                if(selected.getMt() == MockBot.MovementType.BEZIERCURVE){
                    ((BezierCurveMovement) selected.getMovement()).setEndingAngle(endingAngle.getNumber());
                    ((BezierCurveMovement) selected.getMovement()).setEndPoint(new Point2D.Double(endingX.getNumber(),endingY.getNumber()));
                    ((BezierCurveMovement) selected.getMovement()).setFudge1(fudge1.getNumber());
                    ((BezierCurveMovement) selected.getMovement()).setFudge2(fudge2.getNumber());

                }else if(selected.getMt() == MockBot.MovementType.LINE){
                    ((LineMovement) selected.getMovement()).setEndPoint(new Point2D.Double(endingX.getNumber(),endingY.getNumber()));
                }else if(selected.getMt() == MockBot.MovementType.ROTATE){
                    ((RotateMovement) selected.getMovement()).setEndAngle(endingAngle.getNumber());
                }
                selected.getMovement().setReverse(reverseRadioButton.selectedProperty().getValue());
                selected.update();
                mockBotBuild.updateStarts();
            }
        }
    }

    private void deleteSelectedMockBot(){
        if(mockBotView!=null){
            MockBot selected = mockBotView.getMockBotListView().getSelectionModel().getSelectedItem();
            mockBotBuild.remove(selected);
        }
    }

}
