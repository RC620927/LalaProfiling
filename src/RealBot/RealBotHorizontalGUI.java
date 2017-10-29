package RealBot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import Lalaprofiling.Application.Movement;

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
    public Slider randomnessSlider;
    @FXML
    public Label topSpeedLabel;
    @FXML
    public Label maxAccelerationLabel;
    @FXML
    public Label difficultyLabel;
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
        realBotBuilder.addChangeListener((old,current)->{
            updateGUIValues();
        });
        randomnessSlider.valueProperty().bindBidirectional(realBotBuilder.randomnessProperty());

        runButton.setOnAction(e->{
            realBotGUI.playRobot();
        });

    }

    private double difficulty=0;
    private double difficultyC = 0;
    void updateGUIValues(){
        topSpeedLabel.setText("" +realBotBuilder.getTopSpeed());
        maxAccelerationLabel.setText("" + realBotBuilder.getAcceleration());
        totalTimeLabel.setText("" + realBotBuilder.getTotalTime());
        if(randomnessSlider.valueProperty().getValue()!=difficultyC){
            difficultyC=difficulty;
           // difficultyLabel.setText("" + randomnessSlider.valueProperty().getValue().toString());
        }
     //   difficulty =20;

        difficulty = Double.valueOf(realBotGUI.getRealBotView().getDifficulty());
        System.out.println(realBotGUI.toString() + this.toString() + realBotBuilder.toString());
        System.out.println(realBotGUI.getRealBotView().getDifficulty());
        randomnessSlider.valueProperty().set(difficulty*30);

        /*        if(realBotBuilder.getDifficulty()!=difficulty){
            this.difficulty=realBotBuilder.getDifficulty();
            AL.setText("" + realBotBuilder.getDifficulty());
        }*/

    }

}
