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
        itemsArrayList = new ArrayList<type>();
        changeListeners = new ArrayList<>();
    }


    public synchronized ArrayList<type> getItems(){
        return itemsArrayList;
    }

    public synchronized type getItem(int i){
        type item;
        try{
            item = itemsArrayList.get(i);
        }catch(IndexOutOfBoundsException e){
            item=null;
        }
        return item;
    }

    public int size(){
        return itemsArrayList.size();
    }

    public synchronized void add(type item){
        RList<type> cached = this;
        itemsArrayList.add(item);
        for(BiConsumer dc: changeListeners){
            dc.accept(cached,this);
        }
    }

    public synchronized void add(int index, type item){
        RList<type> cached = this;
        itemsArrayList.add(index, item);
        for(BiConsumer dc: changeListeners){
            dc.accept(cached,this);
        }
    }

    public synchronized void add(RList<type> otherList){
        RList<type> cached = otherList;
        for(type item : otherList.getItems()){
            itemsArrayList.add(item);
        }
        for(BiConsumer dc: changeListeners){
            dc.accept(cached,this);
        }
    }

    public synchronized void remove(type item){
        RList<type> cached = this;
        itemsArrayList.remove(item);
        for(BiConsumer dc: changeListeners){
            dc.accept(cached,this);
        }
    }

    public void remove(int i){
        RList<type> cached = this;
        itemsArrayList.remove(i);
        for(BiConsumer dc: changeListeners){
            dc.accept(cached,this);
        }
    }

    public ArrayList<type> getArrayList() {
        return itemsArrayList;
    }

    public void addChangeListener(BiConsumer<RList<type>, RList<type>> changeListener){
        if(changeListener!=null){
            this.changeListeners.add(changeListener);
        }
    }

    @Override
    public void removeChangeListener(BiConsumer<RList<type>, RList<type>> changeListener) {
        if(changeListener!=null){
            this.changeListeners.remove(changeListener);
        }
    }
}
