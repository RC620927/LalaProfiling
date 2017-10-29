package RealBot;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

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
                        this.setText(rObservableMovement.getName());
                    }else{
                        this.setText("");
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
