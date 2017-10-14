package rc.CastrooOrnelas.util;

/**
 * Created by raque on 10/11/2017.
 */
public class RArrayUtil {

    public static Object[] concat(Object[] a, Object[] b){
        int aLen = a.length;
        int bLen = b.length;
        Object[] result = new Object[aLen+bLen];
        System.arraycopy(a,0,result,0,aLen);
        System.arraycopy(b,0,result,aLen,bLen);
        return result;
    }

}
