package rc.CastroOrnelas.io;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by raque on 9/8/2017.
 */
public class RExcelReader<type>{

    public interface RExcelRowReader<returntype>{
        public returntype read(XSSFRow row);
    }

    RExcelRowReader<type> rowReader;

    public RExcelReader(RExcelRowReader<type> rowReader){
        this.rowReader=rowReader;
    }

    public ArrayList<type> read(XSSFSheet excelSheet){
        ArrayList<type> values = new ArrayList<>();
        XSSFRow currentRow;
        Iterator<Row> rowIterator = excelSheet.rowIterator();

        while((currentRow = (XSSFRow) rowIterator.next()) !=null){
            values.add(rowReader.read(currentRow));
        }
        return values;
    }
}
