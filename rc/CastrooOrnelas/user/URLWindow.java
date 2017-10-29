package rc.CastrooOrnelas.user;

import RealBot.RObservableMovement;
import RealBot.RealBotBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * Created by raque on 10/13/2017.
 */
public class URLWindow extends VBox {
    Stage st ;


    @FXML
    public Label errorLabel;

    @FXML
    public Button enterButton;

    @FXML
    public Button cancelButton;

    @FXML
    public TextField urlTextField;

    boolean nope;
    public URLWindow(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("URLWindowFXML.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try{
            fxmlLoader.load();
        }catch (Exception e){
            System.out.println("AADFDSFDFDFD");
            throw new RuntimeException(e);
        }
        nope=false;

        enterButton.setOnAction(e->{
            st.close();
        });

        cancelButton.setOnAction(e->{
            nope=true;
            st.close();
        });

        errorLabel.setText("");

        st = new Stage();
        st.initModality(Modality.APPLICATION_MODAL);
        st.setTitle("NEW MOVEMENT");
        st.setScene(new Scene(this,700,100));

    }



    public String use(){
        nope=false;
        errorLabel.setText("");
        urlTextField.setText("");
        st.showAndWait();
        String returnee;
        if(!nope) {
            returnee = urlTextField.getText();
        }else{
            returnee=null;
        }
        return returnee;
    }


}
