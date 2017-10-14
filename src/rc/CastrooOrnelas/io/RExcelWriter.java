package rc.CastrooOrnelas.io;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;

/**
 * Created by raque on 9/8/2017.
 */
public class RExcelWriter <type>{
    public interface RExcelRowWriter<type>{
        public void write(XSSFRow row, type obj);
    }

    RExcelRowWriter<type> rowWriter;

    public RExcelWriter(RExcelRowWriter<type> rowWriter){
        this.rowWriter=rowWriter;
    }

    public void write(XSSFSheet excelSheet, ArrayList<type> values, int rowOffset){
        int i=rowOffset;
        for(type obj: values){
            XSSFRow row = excelSheet.createRow(i);
            rowWriter.write(row,obj);
            i++;
        }
    }
}
