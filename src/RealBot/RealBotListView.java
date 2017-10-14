package RealBot;

import javafx.beans.property.DoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.image.Image;
import javafx.util.Callback;
import sample.StillMovement;

import java.awt.geom.Point2D;

/**
 * Created by raque on 9/19/2017.
 * created to have a listview to display current realbots in a realbotbuilder
 */
public class RealBotListView extends ListView<RObservableMovement>{

    RealBotBuilder realBotBuilder;

    public RealBotListView(RealBotBuilder realBotBuilder){
        super();
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.realBotBuilder=realBotBuilder;
        this.setCellFactory((realBot) ->{
            return new ListCell<RObservableMovement>(){

                @Override
                public void updateItem(RObservableMovement rObservableMovement, boolean empty){
                    super.updateItem(rObservableMovement, empty);
                    if(rObservableMovement!=null){
                        this.setText(rObservableMovement.getMovementType().getName());

                    }
                }
            };
        });
        updateList();
        realBotBuilder.addChangeListener((old,current)->{
            updateList();
        });
    }

    public void updateList(){
        RObservableMovement selectedROM = this.getSelectionModel().getSelectedItem();
        this.getItems().removeAll(this.getItems());
        this.getItems().addAll(realBotBuilder.getMovements().getItems());
        if(selectedROM!=null){
            this.getSelectionModel().select(selectedROM);
        }else{
            this.getSelectionModel().selectFirst();
        }
    }


}
