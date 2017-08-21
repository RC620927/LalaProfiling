package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Ruben on 8/19/2017.
 */
public class NewMovementWindow extends VBox {

    Stage st ;


    @FXML
    public ChoiceBox<String> movementChooser;

    @FXML
    public Button addButton;

    @FXML
    public Button cancelButton;

    boolean nope;

    private MockBotBuild mbb;

    public NewMovementWindow(MockBotBuild mbb){
        nope=false;
        this.mbb = mbb;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GetMovementFXML.fxml"));
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

        movementChooser.getItems().addAll("Line", "Bezier Curve", "Turn");

        st = new Stage();
        st.initModality(Modality.APPLICATION_MODAL);
        st.setTitle("NEW MOVEMENT");
        st.setScene(new Scene(this,200,100));

    }



    synchronized public void use(){
        nope=false;
        movementChooser.setValue("Line");
        st.showAndWait();
        if(!nope){
            MockBot lastMB = (mbb.getMockBots().get(mbb.getMockBots().size()-1));
            Point2D lastPoint = lastMB.getMovement().getEndPoint();
            switch(movementChooser.getSelectionModel().getSelectedItem()){
                case "Line":
                    mbb.addLine(50,false);
                    break;
                case "Bezier Curve":
                    if(lastMB!=null){
                        mbb.addBezierCurve(new Point2D.Double(lastPoint.getX()+50,lastPoint.getY()+50),
                                0,50,50,false);
                    }else{
                        mbb.addBezierCurve(new Point2D.Double(mbb.getStartingPoint().getX()+50,
                                mbb.getStartingPoint().getY() + 50),0,50,50,false);
                    }
                    break;
                case "Turn":
                    mbb.addTurn(0);
                    break;
            }
        }


    }


}
