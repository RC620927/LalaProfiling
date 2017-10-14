package rc.CastrooOrnelas.FRC;

import RealBot.Moment;
import RealBot.Trajectory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rc.CastrooOrnelas.io.RExcelReader;
import rc.CastrooOrnelas.io.RExcelWriter;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by raque on 9/8/2017.
 */
public class TrajectoryIO {

    private XSSFWorkbook workbook;
    private RExcelReader<Moment> excelReader;
    private RExcelWriter<Moment> excelWriter;
    private FileOutputStream fOP;
    private FileInputStream fIP;
    private String url;

    public TrajectoryIO(String url) throws IOException, InvalidFormatException {
        excelReader = new RExcelReader<>((row)->{
            double timeStamp = row.getCell(0).getNumericCellValue();
            double lvel = row.getCell(1).getNumericCellValue();
            double rvel = row.getCell(2).getNumericCellValue();
            double angle = row.getCell(3).getNumericCellValue();
            double x = row.getCell(4).getNumericCellValue();
            double y = row.getCell(5).getNumericCellValue();
            String marker = row.getCell(6).getStringCellValue();
            return new Moment(marker,timeStamp,x,y,angle,lvel,rvel);
        });
        excelWriter = new RExcelWriter<>((row, mom) -> {
            row.createCell(0).setCellValue(mom.timeStamp);
            row.createCell(1).setCellValue(mom.lVel);
            row.createCell(2).setCellValue(mom.rVel);
            row.createCell(3).setCellValue(mom.angle);
            row.createCell(4).setCellValue(mom.x);
            row.createCell(5).setCellValue(mom.y);
            row.createCell(6).setCellValue(mom.marker);
        });
        this.url = url;


        fIP = new FileInputStream(new File(url));
        workbook = new XSSFWorkbook(fIP);
        fIP.close();
    }

    public Trajectory readTrajectory(int sheet){
        ArrayList<Moment> moments = excelReader.read(workbook.getSheetAt(sheet));

        Trajectory t = ()->{
            return moments;
        };

        return t;
    }


    public void writeTrajectories(int sheet, ArrayList<Trajectory> trajectories){
        ArrayList<Moment> moments = new ArrayList<>();
        for(Trajectory t:trajectories){
            moments.addAll(t.getMoments());
        }

        excelWriter.write(workbook.getSheetAt(sheet),moments,0);
        try {
            fOP = new FileOutputStream(new File(url));
            workbook.write(fOP);
            fOP.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
