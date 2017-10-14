package RealBot;

import de.thomasbolz.javafx.NumberSpinner;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import sample.BezierCurveMovement;
import sample.Movement;
import sample.StillMovement;

import java.util.function.BiConsumer;

/**
 * Created by raque on 10/10/2017.
 */
public class RealBotHorizontalGUI extends HBox{
    private boolean updatingGUIvalues = false;

    private BiConsumer<Movement, Movement> ROMChangeListener;
    private RealBotGUI realBotGUI;
    private RealBotBuilder realBotBuilder;

    @FXML
    public Label difficultyLabel;
    @FXML
    public Slider randomnessSlider;
    @FXML
    public Label topSpeedLabel;
    @FXML
    public Label maxAccelerationLabel;
    @FXML
    public Label totalTimeLabel;

    @FXML
    public Button runButton;


    public RealBotHorizontalGUI(RealBotBuilder realBotBuilder, RealBotGUI realBotGUI) {
        super();
        this.realBotBuilder=realBotBuilder;
        this.realBotGUI = realBotGUI;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RealBotGUIHorizontalFXML.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try{
            fxmlLoader.load();
        }catch (Exception e){
            System.out.println("AADFDSFDFDFD");
            throw new RuntimeException(e);
        }
        randomnessSlider.valueProperty().addListener((obs,old,current)->{
            realBotBuilder.setRandomness(current.doubleValue());
            updateGUIValues();
        });
        runButton.setOnAction(e->{
            realBotGUI.playRobot();
        });

    }


    synchronized void updateGUIValues(){
        topSpeedLabel.setText("" +realBotBuilder.getTopSpeed());
        maxAccelerationLabel.setText("" + realBotBuilder.getAcceleration());
        totalTimeLabel.setText("" + realBotBuilder.getTotalTime());
        randomnessSlider.setValue(realBotBuilder.getRandomness());
        difficultyLabel.setText("" + realBotBuilder.getDifficulty());
    }

}
