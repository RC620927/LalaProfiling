package rc.CastroOrnelas.io;

import RealBot.Trajectory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.Iterator;

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

    public void write(XSSFSheet excelSheet, ArrayList<type> values){

        int i=0;
        for(type obj: values){
            XSSFRow row = excelSheet.createRow(i);
            rowWriter.write(row,obj);
            i++;
        }
    }
}
