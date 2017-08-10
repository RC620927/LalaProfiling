package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import sun.plugin.javascript.navig.Anchor;

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
    public ScrollPane movementsScrollPane;

    @FXML
    public AnchorPane movementsAnchorPane;

    @FXML
    public AnchorPane layoutAnchorPane;

    @FXML
    public TableView<String> movementsTable;

    @FXML
    public Button newButton;

    @FXML
    public Button deleteButton;

    @FXML
    public ChoiceBox<String> movementTypeChooser;





    @FXML
    public void initialize(){

        //setup ChoiceBox
        movementTypeChooser.getItems().addAll("Line", "Bezier Curve", "Turn");
        movementTypeChooser.setValue("Line");



    }



}
