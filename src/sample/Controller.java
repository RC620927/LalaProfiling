package sample;

import RealBot.*;
import RealBot.IO.RealBotIO;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import rc.CastrooOrnelas.user.URLWindow;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Controller {
/*
    color palette 2280
    dark   #343d46 - foreground
    reg    #4f5b66 - headings
    med    #65737e - side
    light  #a7adba - background
    exlight#c0c5ce - letters
    */

    //initialize hboxes on right for variables

    @FXML
    public AnchorPane layoutAnchorPane;

    @FXML
    public StackPane stackPaneViewer;

    @FXML
    public Button centeringButton;

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


    @FXML
    public VBox BIGLAYOUT;

    @FXML
    public VBox verticalGUI;

    @FXML
    public HBox horizontalGUI;

    @FXML
    public MenuItem openMenuItem;

    @FXML
    public MenuItem saveMenuItem;

    @FXML
    public Menu autonomousMenu;

    /*@FXML
    public Canvas c;*/

    MockBotView mockBotView;
    MockBotBuild mockBotBuild;

    MockBot.MovementType movementType;

    boolean key = false;
    boolean togglerKey =false;

    double refreshRate = 200; //refreshes per second


    Pane viewHolder = new Pane();

    String currentWorkspace="";
    String currentAutoName="";
    RealBotGUI currentGUI;
    RealBotBuilder currentAuto;
    RealBotIO currentIO;


    @FXML
    public void initialize(){

        /*LineMovement lm = new LineMovement(new Point2D.Double(0,0), new Point2D.Double(0,400),false,5);
        BezierCurveMovement bcm = new BezierCurveMovement(new Point2D.Double(0, 0), new Point2D.Double(500,150), 0,130,300,150,false,5);
        RotateMovement rm = new RotateMovement(new Point2D.Double(0, 0),0,90,5);
        Path p = new Path(bcm.getSnapshots(), 50,new Snapshot(0,0,0));
        Trajectory t = new Trajectory(p,0,0,150,0,100,160);
        RealBot rb = new RealBot(t, 50);
        */

        RealBotBuilder rbb = new RealBotBuilder(new Point2D.Double(164,15),0.0,30,30,230,180, 150);
       // rbb.getMovements().addLine(79,false,130,0);//end at 94
      //  rbb.getMovements().addStill(150);
     //   rbb.getMovements().addBezierCurve(new Point2D.Double(134,64),90,15,15,true,150,0);
/*        rbb.getMovements().addTurn(180,150,0);
        rbb.getMovements().addLine(40,false,150,0); //end picking up at 134,24
        rbb.getMovements().addStill(100);
        rbb.getMovements().addBezierCurve(new Point2D.Double(164,64),225,25,15,true,150,0);
        rbb.getMovements().addTurn(0,150,0);
        rbb.getMovements().addStill(1000);
        rbb.getMovements().addLine(30,false,150,0);


        rbb.getMovements().addBezierCurve(new Point2D.Double(194,64),270,15,15,true,150,0);
        rbb.getMovements().addTurn(180,150,0);
        rbb.getMovements().addLine(40,false,150,0); //end picking up at 134,24
        rbb.getMovements().addStill(100);
        rbb.getMovements().addBezierCurve(new Point2D.Double(164,64),135,25,15,true,150,0);
        rbb.getMovements().addTurn(0,150,0);
        rbb.getMovements().addStill(1000);
        rbb.getMovements().addLine(30,false,150,0);*/
/*        try {
            RealBotIO rbio = new RealBotIO(Loader.getWorkspaceURL());
            rbb = rbio.getAutonomous(Loader.getLastAuto());
            int i =4;
            rbb.getBotLength();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            loadWorkspace(Loader.getWorkspaceURL());
            loadAutonomous(Loader.getLastAuto());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        RealBotGUI rbgui = new RealBotGUI(rbb);
        RealBotView rbw = rbgui.getRealBotView();

        openMenuItem.setOnAction(e->{
            loadWorkspace(new URLWindow().use());
        });

        saveMenuItem.setOnAction(e->{
            save();
        });

        viewHolder.setStyle("-fx-background-color: #a7adba");
/*        rbw.widthProperty().bind(layoutAnchorPane.widthProperty());
        rbw.heightProperty().bind(layoutAnchorPane.heightProperty());
        rbw.scrollX.bindBidirectional(scrollBarX.valueProperty());
        rbw.scrollY.bindBidirectional(scrollBarY.valueProperty());
        rbw.zoom.bindBidirectional(zoomSlider.valueProperty());
        rbw.zoom.setValue(1);*/


        GraphicsContext gc = rbw.getGraphicsContext2D();
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        //gc.fillOval(-100, -100, 800, 800);
        gc.fillRect(0,500,400,900);
        //stackPaneViewer.getChildren().add(c);
        //BIGLAYOUT.setStyle("-fx-background-color: blue");
      //  viewHolder.getChildren().add(rbw);
        layoutAnchorPane.getChildren().addAll(viewHolder);

        //setup scrollbars
        scrollBarX.toFront();
        scrollBarY.toFront();

        centeringButton.setOnAction(e->{
            rbw.centerOnRobot();
        });
/*
        verticalGUI.getChildren().add(rbgui.getMainVLayer());
        horizontalGUI.getChildren().add(rbgui.getMainHLayer());*/
        HBox.setHgrow(rbgui.getMainHLayer(),Priority.ALWAYS);
        VBox.setVgrow(rbgui.getMainVLayer(), Priority.ALWAYS);
        rbw.reset();
        rbw.centerOnRobot();
    }

    private void loadWorkspace(String url){
        if(currentWorkspace!=url){
            try {
                currentIO = new RealBotIO(url);
                String[] autoNames = currentIO.getAutoNames();
                autonomousMenu.getItems().removeAll(autonomousMenu.getItems());
                for(String auto:autoNames){
                    MenuItem mi = new MenuItem();
                    mi.setText(auto);
                    mi.setOnAction(e->{
                        loadAutonomous(auto);
                    });
                    autonomousMenu.getItems().add(mi);
                }
                currentAuto=null;
                currentGUI=null;
                reload();
            } catch (IOException e) {
                currentAuto=null;
                currentGUI=null;
                currentIO=null;
                e.printStackTrace();
            }
        }

    }

    private void loadAutonomous(String name){
        if(currentAutoName!=name){
            currentAutoName=name;
            try {
                if(currentGUI!=null){
                    currentGUI.disable();
                    currentGUI.getRealBotView().widthProperty().unbind();
                    currentGUI.getRealBotView().heightProperty().unbind();
                    currentGUI.getRealBotView().scrollX.unbindBidirectional(scrollBarX.valueProperty());
                    currentGUI.getRealBotView().scrollY.unbindBidirectional(scrollBarY.valueProperty());
                    currentGUI.getRealBotView().zoom.unbindBidirectional(zoomSlider.valueProperty());
                }

                currentAuto = currentIO.getAutonomous(name);
                currentGUI = new RealBotGUI(currentAuto);
                currentGUI.enable();

            } catch (IOException e) {
                currentGUI=null;
                currentAuto=null;
                e.printStackTrace();
            }
            reload();
        }
    }

    private void reload(){
        verticalGUI.getChildren().removeAll(verticalGUI.getChildren());
        horizontalGUI.getChildren().removeAll(horizontalGUI.getChildren());

        viewHolder.getChildren().removeAll(viewHolder.getChildren());
        if(currentGUI!=null){
            viewHolder.getChildren().add(currentGUI.getRealBotView());
            horizontalGUI.getChildren().add(currentGUI.getMainHLayer());
            verticalGUI.getChildren().add(currentGUI.getMainVLayer());
            HBox.setHgrow(currentGUI.getMainHLayer(), Priority.ALWAYS);
            VBox.setVgrow(currentGUI.getMainVLayer(), Priority.ALWAYS);

            currentGUI.getRealBotView().widthProperty().bind(layoutAnchorPane.widthProperty());
            currentGUI.getRealBotView().heightProperty().bind(layoutAnchorPane.heightProperty());
            currentGUI.getRealBotView().scrollX.bindBidirectional(scrollBarX.valueProperty());
            currentGUI.getRealBotView().scrollY.bindBidirectional(scrollBarY.valueProperty());
            currentGUI.getRealBotView().zoom.bindBidirectional(zoomSlider.valueProperty());
            currentGUI.getRealBotView().zoom.setValue(1);
        }
    }

    private void save(){
        try {
            System.out.println("SAVING");
            currentIO.writeAutonomous(currentAutoName,currentAuto);
        } catch (IOException e) {
            System.err.println("ERROR SAVING" + e.getMessage());
            e.printStackTrace();
        }
    }


}
