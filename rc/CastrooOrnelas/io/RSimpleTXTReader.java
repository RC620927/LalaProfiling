package rc.CastrooOrnelas.io;

import rc.CastrooOrnelas.util.RArrayUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Created by raque on 10/11/2017.
 */
public class RSimpleTXTReader {
    private HashMap<String, String[]> values;
    private String url;
    private File file;
    BufferedReader reader;

    public RSimpleTXTReader(String url) throws FileNotFoundException {
        values = new HashMap<>();
        file = new File(url);
        if(!file.exists() || !file.canRead()){
            throw new FileNotFoundException("CANTFIND:" + url);
        }
        reader = new BufferedReader(new FileReader(file));

    }

    public void read(){
        reader.lines().forEach((line) -> {
            int index = line.indexOf(":");

            //index is -1 if not found, so ignore line otherwise
            if (index >= 0) {
                String key = line.substring(0, index);
                line = line.replaceFirst(".*:", "");

                String valuesInKey[] = line.split(";");
                if (values.containsKey(key)) {
                    Object combinedValues[] = RArrayUtil.concat(values.get(key), valuesInKey);
                    String combinedStringValues[] = new String[combinedValues.length];
                    for (int i = 0; i < combinedValues.length; i++) {
                        combinedStringValues[i] = (String) combinedValues[i];
                    }
                    values.put(key, combinedStringValues);
                } else {
                    values.put(key, valuesInKey);
                }
            }
        });

    }

    public HashMap<String, String[]> getValues() {
        read();
        return values;
    }

}
