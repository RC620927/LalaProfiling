package RealBot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.MockBot;
import sample.MockBotBuild;

import java.awt.geom.Point2D;

/**
 * Created by Ruben on 8/19/2017.
 */
public class NewMovementWindow extends VBox {

    Stage st ;


    @FXML
    public ChoiceBox<String> movementTypeChooser;

    @FXML
    public Button addButton;

    @FXML
    public Button cancelButton;

    @FXML
    public TextField nameTextField;
    @FXML
    public TextArea descriptionTextArea;


    boolean nope;

    private RealBotBuilder rbb;

    public NewMovementWindow(RealBotBuilder rbb){
        nope=false;
        this.rbb=rbb;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../RealBot/GetMovementFXML.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        }catch (Exception e){
            System.out.println("AADFDSFDFDFD");
            throw new RuntimeException(e);
        }

        addButton.setOnAction(e->{
            st.close();
        });

        cancelButton.setOnAction(e->{
            st.close();
            nope=true;
        });

        movementTypeChooser.getItems().addAll("Line", "Bezier Curve", "Turn", "Still");

        st = new Stage();
        st.initModality(Modality.APPLICATION_MODAL);
        st.setTitle("NEW MOVEMENT");
        st.setScene(new Scene(this,426,326));

    }



    synchronized public void use(){
        nope=false;
        movementTypeChooser.setValue("Line");
        st.showAndWait();
        if(!nope){
            RObservableMovement lastROM = rbb.getMovements().getItem(rbb.getMovements().size()-1);
            Point2D lastPoint = lastROM!=null?lastROM.getMovement().getEndPoint(): rbb.getStartingPoint();
            String name = nameTextField.getText();

            switch(movementTypeChooser.getSelectionModel().getSelectedItem()){
                case "Line":
                    rbb.getMovements().addLine(name, 50,false, rbb.getTopSpeed(), 0);
                    break;
                case "Bezier Curve":
                    rbb.getMovements().addBezierCurve(name, new Point2D.Double(lastPoint.getX()+50,lastPoint.getY()+50),
                                0,50,50,false, rbb.getTopSpeed(),0);
                    break;
                case "Turn":
                    rbb.getMovements().addTurn(name, 0,rbb.getTopSpeed(),0);
                    break;
                case "Still":
                    rbb.getMovements().addStill(name, 1000);
                    break;
            }
        }


    }


}
