package sample;

import de.thomasbolz.javafx.NumberSpinner;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import sun.plugin.javascript.navig.Anchor;

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
    public Button newButton;

    @FXML
    public Button deleteButton;

    @FXML
    public Button runButton;

    @FXML
    public Group viewGroup;

    @FXML
    public ChoiceBox<String> movementTypeChooser;

    @FXML
    public ScrollBar scrollBarX;

    @FXML
    public ScrollBar scrollBarY;

    @FXML
    public RadioMenuItem zoom25,zoom50,zoom75,zoom100,zoom125,zoom150;


    public NumberSpinner initialX,initialY, endingX,endingY, distance, initialAngle, endingAngle, initialSpeed,endingSpeed,topSpeed,fudge1,fudge2;




    boolean key = false;
    @FXML
    public void initialize(){



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


        //setup ChoiceBox
        movementTypeChooser.getItems().addAll("Line", "Bezier Curve", "Turn");
        movementTypeChooser.setValue("Line");

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
        movementTypeChooser.setOnAction(e->{
            switch (movementTypeChooser.getSelectionModel().getSelectedItem()){
                case "Line":
                    fudge1.setDisable(true);
                    fudge2.setDisable(true);
                    initialX.setDisable(true);
                    initialY.setDisable(true);
                    endingX.setDisable(true);
                    endingY.setDisable(true);
                    distance.setDisable(false);
                    endingAngle.setDisable(true);
                    initialAngle.setDisable(true);

                    break;
                case "Turn":
                    fudge1.setDisable(true);
                    fudge2.setDisable(true);
                    initialX.setDisable(true);
                    initialY.setDisable(true);
                    endingX.setDisable(true);
                    endingY.setDisable(true);
                    distance.setDisable(true);
                    endingAngle.setDisable(false);
                    initialAngle.setDisable(true);
                    break;
                case "Bezier Curve":
                    fudge1.setDisable(false);
                    fudge2.setDisable(false);
                    initialX.setDisable(true);
                    initialY.setDisable(true);
                    endingX.setDisable(false);
                    endingY.setDisable(false);
                    distance.setDisable(true);
                    endingAngle.setDisable(false);
                    initialAngle.setDisable(true);
                    break;
            }
        });

        endingX.numberProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {

                if (oldValue != newValue && !key) {
                    key=true;
                    double delX = newValue-initialX.getNumber();
                    double delY = endingY.getNumber()-initialY.getNumber();
                    distance.setNumber(Math.sqrt(delX * delX + Math.pow(delY, 2)));
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
                    endingX.setNumber(Math.sin(theta) * newValue);
                    endingY.setNumber(Math.cos(theta) * newValue);
                    key=false;
                }

            }
        });


        //setup Mock bots
        MockBotBuild mockBotBuild= new MockBotBuild(new Point2D.Double(200,0), 0);
        mockBotBuild.addLine(200);
        mockBotBuild.addTurn(180);
        mockBotBuild.addBezierCurve(new Point2D.Double(400,120), 90, 150,100);

        mockBotBuild.addTurn(180);
        mockBotBuild.addLine(120);
        mockBotBuild.addTurn(0);
        mockBotBuild.addBezierCurve(new Point2D.Double(200,120), 270, 150,100);
        mockBotBuild.addTurn(0);
        mockBotBuild.addLine(80);
        MockBotView mockBotView = new MockBotView(mockBotBuild);
        mockBotView.setScaleX(1);
        mockBotView.setScaleY(1);
        mockBotView.setStyle("-fx-background-color: #2f4f4f");
        mockBotView.setZoom(1);

        //setup mock bots list view
        movementsVBox.getChildren().add(mockBotView.getMockBotListView());
        mockBotView.getMockBotListView().setOnMouseClicked(e->{
            mockBotView.getMockBotListView().onMouseClicked();
            setValues(mockBotView.getMockBotListView().getSelectionModel().getSelectedItem());
            System.out.println("AFTER");
        });

        layoutAnchorPane.getChildren().add(mockBotView);
        layoutAnchorPane.getChildren().add(mockBotView.getMockBotDotsViewer());


        //setup table

        runButton.setOnAction(e->{
            mockBotView.play();
        });

        newButton.setOnAction(e->{
            /*
            GUIInteractions.addToMockBuild(mockBotBuild,movementTypeChooser.getValue(),
                    new Point2D.Double(endingX.getNumber(),endingY.getNumber()),distance.getNumber(),
                    endingAngle.getNumber(),fudge1.getNumber(),fudge2.getNumber());*/
            mockBotBuild.addLine(10);
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
    }

    public void setValues(MockBot mb){
        this.initialX.setNumber(mb.getMovement().getStartPoint().getX());
        this.initialY.setNumber(mb.getMovement().getStartPoint().getY());
        this.endingX.setNumber(mb.getMovement().getEndPoint().getX());
        this.endingY.setNumber(mb.getMovement().getEndPoint().getY());
        this.initialAngle.setNumber(mb.getMovement().getInitialAngle());
        this.endingAngle.setNumber(mb.getMovement().getEndAngle());
        if(mb.getMt()== MockBot.MovementType.BEZIERCURVE){
            this.fudge1.setNumber(((BezierCurveMovement) mb.getMovement()).getFudge1());
            this.fudge2.setNumber(((BezierCurveMovement) mb.getMovement()).getFudge2());
            movementTypeChooser.setValue("Bezier Curve");
        }else{
            if(mb.getMt() == MockBot.MovementType.LINE){
                movementTypeChooser.setValue("Line");
            }else{
                movementTypeChooser.setValue("Turn");
            }
            fudge1.setNumber(0.0);
            fudge2.setNumber(0.0);
        }


    }



}
