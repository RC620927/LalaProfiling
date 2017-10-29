package RealBot;

import de.thomasbolz.javafx.NumberSpinner;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Lalaprofiling.Application.BezierCurveMovement;
import Lalaprofiling.Application.Movement;
import Lalaprofiling.Application.StillMovement;

import java.util.function.BiConsumer;

/**
 * Created by raque on 9/20/2017.
 */
public class RealBotVerticalGUI  extends VBox{
    private RealBotListView realBotListView;
    private RealBotGUI realBotGUI;

    @FXML
    private HBox initialPosHBox;
    @FXML
    private HBox  endingPosHBox;
    @FXML
    private HBox  timeHBox;
    @FXML
    private HBox   distanceHBox;
    @FXML
    private HBox   angleHBox;
    @FXML
    private HBox   speedHBox;
    @FXML
    private HBox   topHBox;
    @FXML
    private HBox   fudgeHBox;


    @FXML
    private VBox movementsVBox;

    @FXML
    private RadioButton reverseRadioButton;

    @FXML
    private Button newButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;

    private boolean updatingGUIvalues = false;

    private BiConsumer<Movement, Movement> ROMChangeListener;

    public NumberSpinner initialX,initialY,
            endingX,endingY,
            time,
            distance,
            initialAngle, endingAngle,
            initialSpeed, endingSpeed,
            topSpeed,
            fudge1,fudge2;


    private RObservableMovement selectedROM;

    public RealBotVerticalGUI(RealBotBuilder realBotBuilder, RealBotGUI realBotGUI) {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RealBotGUIVerticalFXML.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        this.realBotListView= new RealBotListView(realBotBuilder);
        this.realBotGUI=realBotGUI;

        ROMChangeListener = (old,current) -> {
            if (updatingGUIvalues == false) {
                updateGUIValues();
            }
        };

        try{
            fxmlLoader.load();
        }catch (Exception e){
            System.out.println("AADFDSFDFDFD");
            throw new RuntimeException(e);
        }

/*        initialPosHBox = new HBox();
        endingPosHBox = new HBox();
        distanceHBox = new HBox();
        angleHBox = new HBox();
        speedHBox = new HBox();
        topHBox = new HBox();
        fudgeHBox = new HBox();*/


        addListener(reverseRadioButton.selectedProperty(), "reverse");

        initialX = new NumberSpinner(0.0,0.5);
        initialY = new NumberSpinner(0.0,0.5);
        initialPosHBox.getChildren().addAll(initialX,initialY);

        endingX = new NumberSpinner(0.0,0.5);
        addListener(endingX.numberProperty(),"end_x");
        endingY = new NumberSpinner(0.0,0.5);
        addListener(endingY.numberProperty(),"end_y");
        endingPosHBox.getChildren().addAll(endingX,endingY);

        time = new NumberSpinner(0.0,0.1,0.0,Double.POSITIVE_INFINITY);
        addListener(time.numberProperty(), "time");
        timeHBox.getChildren().add(time);

        distance = new NumberSpinner(0.0,0.5,0.0, Double.POSITIVE_INFINITY);
        addListener(distance.numberProperty(),"distance");
        distanceHBox.getChildren().add(distance);

        initialAngle = new NumberSpinner(0.0,0.5, 0.0, 360.0);
        angleHBox.getChildren().add(initialAngle);

        endingAngle = new NumberSpinner(0.0,0.5,  0.0, 360.0);
        addListener(endingAngle.numberProperty(),"end_angle");
        angleHBox.getChildren().add(endingAngle);

        initialSpeed = new NumberSpinner(0.0,0.5);
        speedHBox.getChildren().add(initialSpeed);

        endingSpeed = new NumberSpinner(0.0,0.5);
        addListener(endingSpeed.numberProperty(),"end_speed");
        speedHBox.getChildren().add(endingSpeed);

        topSpeed = new NumberSpinner(0.0,0.5);
        addListener(topSpeed.numberProperty(),"top_speed");
        topHBox.getChildren().add(topSpeed);

        fudge1 = new NumberSpinner(0.0,0.5);
        addListener(fudge1.numberProperty(),"fudge_1");
        fudge2 = new NumberSpinner(0.0,0.5);
        addListener(fudge2.numberProperty(),"fudge_2");
        fudgeHBox.getChildren().addAll(fudge1,fudge2);

        realBotListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        realBotListView.setOnMouseReleased( e->{
            listViewClicked();
        });

        newButton.setOnAction(e->{
            realBotGUI.useNewMovementWindow();
        });

        deleteButton.setOnAction(e->{
            realBotBuilder.getMovements().removeMovement(realBotListView.getSelectionModel().getSelectedItem());
        });

/*        upButton.setOnAction(e->{
            RObservableMovement selected = realBotListView.getSelectionModel().getSelectedItem();
            if(selected!=null){
                int indexTo = realBotBuilder.getMovements().getArrayList().indexOf(selected)-1;
                indexTo = (int) Resources.limit(indexTo,0,realBotBuilder.getMovements().size()-1);
                realBotBuilder.getMovements().moveMovement(selected, indexTo);
            }
        });

        downButton.setOnAction(e->{
            RObservableMovement selected = realBotListView.getSelectionModel().getSelectedItem();
            if(selected!=null){
                int indexTo = realBotBuilder.getMovements().getArrayList().indexOf(selected)+1;
                indexTo = (int) Resources.limit(indexTo,0,realBotBuilder.getMovements().size()-1);
                realBotBuilder.getMovements().moveMovement(selected, indexTo);
            }
        });*/

        movementsVBox.getChildren().add(realBotListView);
        //this.getChildren().add(realBotListView);

    }

    //when the list view is clicked, see if something is selected in order to pull up that rom's daya
    private void listViewClicked(){
        RObservableMovement rom;
        if((rom = realBotListView.getSelectionModel().getSelectedItem())!=null){
            if(selectedROM!=null){
                if(rom!= selectedROM){
                    selectedROM.removeChangeListener(ROMChangeListener);
                    selectedROM = rom;
                    updateGUIValues();
                    toggleBoxes();
                    addROMListener(selectedROM);
                }
            }else{
                selectedROM=rom;
                updateGUIValues();
                toggleBoxes();
                addROMListener(selectedROM);
            }
        }
    }

    //add listener to each Number Spinner
    private void addListener(Property p, String var){
        p.addListener((object,o,n) ->{
            if (!updatingGUIvalues){
                editSelected(var, object.getValue());

            }
        });
    }

    //tell gui to update values based on ROM when the selected ROM changes
    private void addROMListener(RObservableMovement rom){
        rom.addChangeListener(ROMChangeListener);
    }

    private synchronized void updateGUIValues(){

        if(selectedROM!=null && updatingGUIvalues==false){
            updatingGUIvalues=true;
            this.initialSpeed.setNumber(selectedROM.getInitialSpeed());
            this.endingSpeed.setNumber(selectedROM.getEndingSpeed());
            this.topSpeed.setNumber(selectedROM.getTopSpeed());
            this.initialX.setNumber(selectedROM.getMovement().getStartPoint().getX());
            this.initialY.setNumber(selectedROM.getMovement().getStartPoint().getY());
            this.endingX.setNumber(selectedROM.getMovement().getEndPoint().getX());
            this.endingY.setNumber(selectedROM.getMovement().getEndPoint().getY());
            double delX = endingX.getNumber() -initialX.getNumber();
            double delY = endingY.getNumber()-initialY.getNumber();
            this.distance.setNumber(Math.sqrt(delX * delX + Math.pow(delY, 2)));
            this.initialAngle.setNumber(selectedROM.getMovement().getInitialAngle());
            this.endingAngle.setNumber(selectedROM.getMovement().getEndAngle());
            this.reverseRadioButton.selectedProperty().set(selectedROM.getMovement().getReverse());

            //time is meant for still only
            this.time.setNumber(0.0);

            if(selectedROM.getMovementType()== RObservableMovement.MovementType.BEZIERCURVE){
                this.fudge1.setNumber(((BezierCurveMovement) selectedROM.getMovement()).getFudge1());
                this.fudge2.setNumber(((BezierCurveMovement) selectedROM.getMovement()).getFudge2());
                toggleBCBoxes();

                System.out.println("BC");
            }else{
                if(selectedROM.getMovementType() == RObservableMovement.MovementType.LINE){

                    toggleLineBoxes();
                    System.out.println("Line");
                }else if(selectedROM.getMovementType() == RObservableMovement.MovementType.ROTATE){

                    toggleTurnBoxes();
                    System.out.println("Turn");
                }else if(selectedROM.getMovementType() == RObservableMovement.MovementType.STILL){
                    this.time.setNumber(((StillMovement) selectedROM.getMovement()).getTime());
                    toggleBoxes();
                    System.out.println("Still");

                }else{
                    toggleBoxes();
                }
                fudge1.setNumber(0.0);
                fudge2.setNumber(0.0);
            }
            initialX.setNumber(selectedROM.getStartPoint().getX());
            updatingGUIvalues=false;
        }
    }

    private void editSelected(String var,Object obj){
        if(selectedROM!=null){
            selectedROM.setValue(var, obj);
          //  updateGUIValues(); the rom change listener automatically updates gui values, so no need to re-reupdate
        }
    }

    private void toggleBoxes(){
        if(selectedROM.getMovementType()== RObservableMovement.MovementType.ROTATE){
            toggleTurnBoxes();
        }else if(selectedROM.getMovementType()== RObservableMovement.MovementType.BEZIERCURVE){
            toggleBCBoxes();
        }else if(selectedROM.getMovementType()== RObservableMovement.MovementType.LINE) {
            toggleLineBoxes();
        }else if(selectedROM.getMovementType()== RObservableMovement.MovementType.STILL){
            toggleStillBoxes();
        }else{
            toggleBoxes(true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        }
    }

    private void toggleBoxes(boolean f1, boolean f2, boolean ix, boolean iy, boolean ex,
                             boolean ey, boolean d, boolean ea, boolean ia, boolean is,
                             boolean es, boolean ts, boolean re, boolean ti) {
        fudge1.setDisable(f1);
        fudge2.setDisable(f2);
        initialX.setDisable(ix);
        initialY.setDisable(iy);
        endingX.setDisable(ex);
        endingY.setDisable(ey);
        distance.setDisable(d);
        endingAngle.setDisable(ea);
        initialAngle.setDisable(ia);
        initialSpeed.setDisable(is);
        endingSpeed.setDisable(es);
        topSpeed.setDisable(ts);
        reverseRadioButton.setDisable(re);
        time.setDisable(ti);
    }

    private void toggleLineBoxes() {
        toggleBoxes(true, true, true, true, true, true, false, true, true, true, false, false, false, true);
    }

    private void toggleBCBoxes() {
        toggleBoxes(false, false, true, true, false ,false, true, false, true, true, false, false, false, true);
    }

    private void toggleTurnBoxes() {
        toggleBoxes(true, true, true, true, true, true, true, false, true, true, false, false, true, true);
    }

    private void toggleStillBoxes(){
        toggleBoxes(true, true, true, true ,true, true, true, true, true, true, true, true, true, false);
    }
}
