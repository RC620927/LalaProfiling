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


    public NumberSpinner initialX,initialY, endingX,endingY, distance, initialAngle, endingAngle, initialSpeed,endingSpeed,topSpeed,fudge1,fudge2;




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


        endingX.numberProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                distance.setNumber(Math.sqrt(newValue*newValue + Math.pow(endingY.getNumber(),2)));
            }
        });
        endingY.numberProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                distance.setNumber(Math.sqrt(newValue*newValue + Math.pow(endingX.getNumber(),2)));
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
        mockBotView.setStyle("-fx-background-color: #2f4f4f");

        //setup mock bots list view
        movementsVBox.getChildren().add(mockBotView.getMockBotListView());

        //setup ChoiceBox
        movementTypeChooser.getItems().addAll("Line", "Bezier Curve", "Turn");
        movementTypeChooser.setValue("Line");
        layoutAnchorPane.getChildren().add(mockBotView);

        //setup table

        runButton.setOnAction(e->{
            mockBotView.play();
        });

        newButton.setOnAction(e->{
            GUIInteractions.addToMockBuild(mockBotBuild,movementTypeChooser.getValue(),
                    new Point2D.Double(endingX.getNumber(),endingY.getNumber()),distance.getNumber(),
                    endingAngle.getNumber(),fudge1.getNumber(),fudge2.getNumber());
        });


    }



}
