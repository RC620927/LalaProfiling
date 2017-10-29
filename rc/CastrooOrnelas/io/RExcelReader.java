package rc.CastrooOrnelas.io;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by raque on 9/8/2017.
 * The purpose of this class is to facilitate the storage of Objects in an Excel Sheet
 */
public class RExcelReader<type>{

    public interface RExcelRowReader<returntype>{
        public returntype read(XSSFRow row);
    }

    RExcelRowReader<type> rowReader;

    //Row Reader takes in the Excel Row, and returns an Object based on the values in each Cell stored
    public RExcelReader(RExcelRowReader<type> rowReader){
        this.rowReader=rowReader;
    }

    //read an excel sheet and return a list of Objects read from RExcelReader, starting at a certain Row
    public ArrayList<type> read(XSSFSheet excelSheet, int startingRow){
        ArrayList<type> values = new ArrayList<>();
        XSSFRow currentRow;
        Iterator<Row> rowIterator = excelSheet.rowIterator();
        for(int i=0;i<startingRow;i++){
            rowIterator.next();
        }
        try{
            while((currentRow = (XSSFRow) rowIterator.next()) !=null){
                values.add(rowReader.read(currentRow));
            }
        }catch (NoSuchElementException nsee){
            System.err.print("NOSUCHELEMENTEXCEPTION");
        }

        return values;
    }

    public ArrayList<type> read(XSSFSheet excelSheet){
        return read(excelSheet,0);
    }
}
