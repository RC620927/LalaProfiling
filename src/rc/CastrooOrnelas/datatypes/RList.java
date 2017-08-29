package rc.CastrooOrnelas.datatypes;

import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.function.BiConsumer;

/**
 * Created by raque on 8/24/2017.
 */
public class RList<type> implements  RObservable<RList<type>>{
    ArrayList<BiConsumer<RList<type>, RList<type>>> changeListeners;

    private ArrayList<type> itemsArrayList;

    public RList(){
        this(new ArrayList<type>());
    }

    public RList(ArrayList<type> items){
        changeListeners = new ArrayList<>();
    }


    public synchronized ArrayList<type> getItems(){
        return itemsArrayList;
    }

    public synchronized type getItem(int i){
        return itemsArrayList.get(i);
    }

    public synchronized void add(type item){
        ArrayList<type> cached = itemsArrayList;
        itemsArrayList.add(item);
        for(BiConsumer dc: changeListeners){
            dc.accept(cached,itemsArrayList);
        }
    }

    public synchronized void add(RList<type> otherList){
        RList<type> cached = otherList;
        for(type item : otherList.getItems()){
            itemsArrayList.add(item);
        }
        for(BiConsumer dc: changeListeners){
            dc.accept(cached,itemsArrayList);
        }
    }

    public synchronized void remove(type item){
        RList<type> cached = this;
        itemsArrayList.remove(item);
        for(BiConsumer dc: changeListeners){
            dc.accept(cached,itemsArrayList);
        }
    }

    public void remove(int i){
        RList<type> cached = this;
        itemsArrayList.remove(i);
        for(BiConsumer dc: changeListeners){
            dc.accept(cached,this);
        }
    }

    public void addChangeListener(BiConsumer<RList<type>, RList<type>> changeListener){
        if(changeListener!=null){
            this.changeListeners.add(changeListener);
        }
    }
}
