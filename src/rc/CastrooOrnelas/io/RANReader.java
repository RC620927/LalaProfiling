package rc.CastrooOrnelas.io;

import java.util.ArrayList;

/**
 * Created by raque on 10/1/2017.
 */
public class RANReader<type> {

    public interface RANRowReader<returntype>{
        public returntype read(Object row[]);
    }

    RANRowReader<type> rowReader;

    public RANReader(RANRowReader<type> rowReader){
        this.rowReader=rowReader;
    }

    public ArrayList<type> read(Object[][] object2DArray){
        ArrayList<type> values = new ArrayList<>();
        Object[] currentRow;

        for(Object[] objectRow: object2DArray){
            values.add(rowReader.read(objectRow));
        }
        return  values;
    }
}

