package rc.CastrooOrnelas.datatypes;

import java.util.function.BiConsumer;

/**
 * Created by raque on 8/24/2017.
 */
public interface RObservable<type>{


    public void addChangeListener(BiConsumer<type,type> changeListener);

}
