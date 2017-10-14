package rc.CastrooOrnelas.util;

import javafx.scene.image.Image;

import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Created by raque on 10/2/2017.
 * designed to retrieve a value from a list of known keys. If the key desired is not there, it makes a guess based on the closest keys.
 *
 *
 */


public class DoubleKeyInterpolator<type>{

    private TreeMap<Double, type> knownMap;
    private BiFunction<type, type, type> interpolatorAlgo;

    public DoubleKeyInterpolator(BiFunction<type, type, type> interpolatorAlgo){
        this(new TreeMap<Double, type>(), interpolatorAlgo);
    }

    public DoubleKeyInterpolator(TreeMap<Double, type> knownMap,BiFunction<type, type, type> interpolatorAlgo){
        this.knownMap=knownMap;
        this.interpolatorAlgo=interpolatorAlgo;
    }
    
    public TreeMap<Double, type> getKnownMap(){
        return knownMap;
    }
    
    public type crunch(Double key){
        type returnee = knownMap.get(key);
        if(returnee==null){
            Double lowerKey = knownMap.lowerKey(key);
            Double upperKey = knownMap.ceilingKey(key);
            if(lowerKey==null && upperKey ==null){
                return null;
            }else if(lowerKey == null){
                return knownMap.get(upperKey);
            }else if(upperKey == null){
                return knownMap.get(lowerKey);
            }else {
                return interpolatorAlgo.apply(knownMap.get(upperKey), knownMap.get(lowerKey));
            }
        }else{
            return returnee;
        }
    }
    
    
    
}
