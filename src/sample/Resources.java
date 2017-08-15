package sample;

/**
 * Created by raque on 8/10/2017.
 */
public class Resources {
    private static Resources ourInstance = new Resources();

    public static Resources getInstance() {
        return ourInstance;
    }

    private Resources() {
    }

    public static double mod(double value, int mod){
        value = value%mod;
        value = (value+mod)%mod;
        return value;
    }

}


