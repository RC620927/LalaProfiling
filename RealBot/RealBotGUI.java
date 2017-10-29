package RealBot;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rc.CastrooOrnelas.controls.RLoop;

/**
 * Created by raque on 9/19/2017.
 */
public class RealBotGUI{
    RealBotBuilder realBotBuilder;

    private RLoop loop;

    private RealBotVerticalGUI mainVLayer;
    private RealBotHorizontalGUI mainHLayer;
    private RealBotView realBotView;
    private NewMovementWindow newMovementWindow;
    private boolean disabled=false;

    public RealBotGUI(RealBotBuilder realBotBuilder){
        this.realBotBuilder=realBotBuilder;
        mainVLayer= new RealBotVerticalGUI(realBotBuilder, this);
        mainHLayer = new RealBotHorizontalGUI(realBotBuilder,this);
        realBotView = new RealBotView(realBotBuilder);
        realBotView.setStyle("-fx-background-color: blue");
        newMovementWindow = new NewMovementWindow(realBotBuilder);

        Runnable runnable = ()->{
            if(!disabled){
                mainHLayer.updateGUIValues();
                realBotView.reset();
            }
        };
        loop = new RLoop(runnable, 10);
        startLoop();
    }

    public VBox getMainVLayer(){
        return mainVLayer;
    }

    public HBox getMainHLayer() {
        return mainHLayer;
    }

    public RealBotView getRealBotView() {
        return realBotView;
    }

    private void startLoop(){
        Runnable loader = ()->{
            realBotView.reset();
            realBotView.centerOnRobot();
            loop.start();
        };
        new Thread(loader).start();
    }

    void useNewMovementWindow(){
        newMovementWindow.use();
    }

    void playRobot(){
        realBotView.play();
    }

    public void disable(){
        loop.kill();
        disabled=true;
    }

    public void enable(){
        disabled=false;
        loop.start();
    }
}