package rc.CastrooOrnelas.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by raque on 10/11/2017.
 */
public class RSimpleTXTWriter {

    String url;
    File file;
    ArrayList<String> lines;
    BufferedWriter writer;

    public RSimpleTXTWriter(String url, boolean concat) throws IOException {
        this.url=url;
        file = new File(url);
        if(!file.exists()){
            file.createNewFile();
        }
        if(!file.exists() || !file.canWrite()){
            throw new IOException();
        }
        writer = new BufferedWriter(new FileWriter(file, concat));
    }

    public void writeValues(HashMap<String, String[]> values) throws IOException {

        for(String key :values.keySet()){
            String valuesLine = key+":";
            String stringValues[] = values.get(key);
            for(String s: stringValues){
                valuesLine = valuesLine.concat(s+";");
            }
            writer.newLine();
            writer.append(valuesLine);
        }

        writer.flush();
    }

    public void writeValue(String key, String s) throws IOException {
        HashMap<String, String[]> hash= new HashMap<>();
        String arr[] = {s};
        hash.put(key,arr);
        writeValues(hash);
    }

    public void close() throws IOException {
        writer.close();
    }
}
