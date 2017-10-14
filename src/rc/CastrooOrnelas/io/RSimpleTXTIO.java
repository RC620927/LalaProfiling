package rc.CastrooOrnelas.io;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by raque on 10/11/2017.
 */
public class RSimpleTXTIO {

    RSimpleTXTWriter writer;
    RSimpleTXTReader reader;
    String url;

    HashMap<String, String[]> backup;

    public RSimpleTXTIO(String url, boolean concatWriting) throws IOException {
        this.url=url;
        writer = new RSimpleTXTWriter(url, concatWriting);
        reader = new RSimpleTXTReader(url);
        backup();
    }

    //write one value-key combo, not overriding existing
    public void write(String key, String value) throws IOException {
        writer.writeValue(key, value);
    }

    //write all keys in this hash with values, not overriding existing
    public void write(HashMap<String, String[]> values) throws IOException {
        writer.writeValues(values);
    }

    //write values for this key in file, not overriding existing
    public void write(String key , String[] values) throws IOException {
        HashMap<String, String[]> hash = new HashMap<>();
        hash.put(key, values);
        writer.writeValues(hash);
    }

    //get values stored in file
    public HashMap<String, String[]> getValues(){
        return reader.getValues();
    }

    //remove all keys
    public void removeAll() throws IOException {
        writer.close();
        writer = new RSimpleTXTWriter(url, false);
    }

    //remove this key
    public void remove(String key) throws IOException {
        backup();
        backup.remove(key);
        removeAll();
        writer.writeValues(backup);
    }

    //remove the specific value on this key
    public void remove(String key, String value) throws IOException {
        backup();
        String[] arr = backup.get(key);
        String[] newArr = new String[arr.length];
        int j=0;
        for(int i=0;i<arr.length;i++){
            if(!arr[i].equals(value)) {
                newArr[j] = arr[i];
                j++;
            }
        }
        String[] newnewArr = new String[j];
        for(int i=0;i<j;i++){
            newnewArr[i] = newArr[i];
        }

        removeAll();
        backup.remove(key);
        backup.put(key, newnewArr);
        writer.writeValues(backup);
    }

    //replace this key's values with these
    public void overwrite(String key, String[] values) throws IOException {
        remove(key);
        write(key, values);
    }

    //overwrite the keys in the hash being bassed in, replacing those
    public void overwrite(HashMap<String, String[]> values) throws IOException {
        for(String key: values.keySet()){
            remove(key);
        }
        write(values);
    }

    public void overwrite(String key, String value) throws IOException {
        remove(key);
        write(key,value);
    }


    //update the backup in memory
    private void backup(){
        backup = getValues();
    }

    //rearrange txt file so all keys are in one line instead of in multiple if you wrote multiple times
    public void rearrange() throws IOException {
        backup();
        removeAll();
        writer.writeValues(backup);
    }
}
