package RealBot.IO;

import RealBot.RObservableMovement;
import RealBot.RealBotBuilder;
import RealBot.RobotConfig;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rc.CastrooOrnelas.io.*;
import sample.BezierCurveMovement;
import sample.StillMovement;

import java.awt.geom.Point2D;
import java.io.*;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by raque on 10/11/2017.
 * Class intended to store the configuration of each robot - acceleration, top speed, starting configuration, etc
 * Also intended to receive all trajectories in this workspace - including starting position, angle, and movemenrs
 *
 * config file contains - url's for this robot's movements, robot config, name
 *
 */



public class RealBotIO {

    private String workspaceURL;
    private RobotConfig robotConfig;
    private RSimpleTXTIO CONFIGIO;
    private ArrayList<String> autonURLs;

    private final RExcelReader<String[]>  movementRExcelReader = new RExcelReader<>((row)->{
        short last=row.getLastCellNum();
        String[] vars = new String[last+1];
        for(int i=0;i<=last;i++){
            XSSFCell cell = row.getCell(i);
            if(cell!=null ){
                cell.setCellType(CellType.STRING);
                vars[i] = row.getCell(i).getStringCellValue();
            }
        }
        return vars;
    });

    public RealBotIO(String workspaceURL) throws IOException {
        this.workspaceURL=workspaceURL;
        CONFIGIO = new RSimpleTXTIO(workspaceURL + "\\CONFIG.txt", true);
    }

    //load pre-made robot configuration
    private void loadConfig(){
        HashMap<String, String[]> configVals = CONFIGIO.getValues();
        autonURLs= new ArrayList<>();
        double maxAccel = Double.valueOf(configVals.get("MaxAccel")[0]);
        double maxSpeed = Double.valueOf(configVals.get("MaxSpeed")[0]);
        double botWidth = Double.valueOf(configVals.get("BotWidth")[0]);
        double botLength = Double.valueOf(configVals.get("BotLength")[0]);

        robotConfig = new RobotConfig(maxAccel,maxSpeed,botWidth,botLength);
        for(String url: configVals.get("URL")){
            this.autonURLs.add(url);
        }
    }

    //change robot ocnfiguration
    private void setConfig(RobotConfig robotConfig) throws IOException {
        if(this.robotConfig.maxAccel!=robotConfig.maxAccel){
            CONFIGIO.overwrite("MaxAccel", Double.toString(robotConfig.maxAccel));
        }
        if(this.robotConfig.maxSpeed!=robotConfig.maxSpeed){
            CONFIGIO.overwrite("MaxSpeed", Double.toString(robotConfig.maxSpeed));
        }
        if(this.robotConfig.botWidth!=robotConfig.botWidth){
            CONFIGIO.overwrite("BotWidth", Double.toString(robotConfig.botWidth));
        }
    }

    public String[] getAutoNames(){
        return CONFIGIO.getValues().get("URL");
    }

    /*
        EXCEL values go: 0Name	1Type	2EndingX	3EndingY	4EndingAngle	5Distance	6EndingSpeed	7TopSpeed
        	8Fudge1	9Fudge2	10Reverse	11Time
        TXT Values: STARTX,STARTY,STARTTHETA
     */
    public RealBotBuilder getAutonomous(String name) throws IOException {
        loadConfig();

        //create robot builder
        RSimpleTXTReader movementTXTReader = new RSimpleTXTReader(workspaceURL +"\\" +name+".txt");
        HashMap<String, String[]> initVars = movementTXTReader.getValues();
        Point2D.Double startPoint = new Point2D.Double(Double.valueOf(initVars.get("STARTX")[0]),
                Double.valueOf(initVars.get("STARTY")[0]));
        double startAngle = Double.valueOf(initVars.get("STARTTHETA")[0]);

        System.out.println("LOADING RBBUILDER   STARTX:" + startPoint.getX() +
                "   STARTY:" + startPoint.getY() +"   STARTTHETA:"+startAngle);
        RealBotBuilder realBotBuilder = new RealBotBuilder(startPoint,startAngle,robotConfig.botWidth,
                robotConfig.botLength, robotConfig.maxAccel, robotConfig.maxAccel, robotConfig.maxSpeed);

        //load movements into builder
        ArrayList<String[]> movementStringVars = movementRExcelReader.read(
                new XSSFWorkbook(workspaceURL +"\\" +name+".xlsx").getSheetAt(0), 1);
        boolean skipFirst=true;
        for(String[] vars: movementStringVars){
            String Mname = vars[0];
            String MmovementTypeName= vars[1];

            double endSpeed=0, topSpeed=robotConfig.maxSpeed;

            boolean nfe=false;
            try{
                endSpeed = Double.valueOf(vars[6]);
                topSpeed = Double.valueOf(vars[7]);
            }catch(Exception e ){
                if(!MmovementTypeName.equals("STILL")){
                    System.out.println();
                    System.err.println("SPEEDSNOTFOUND:" + e.getMessage());
                }
                nfe=true;
            }
            switch(MmovementTypeName){
                case "STILL":
                    realBotBuilder.getMovements().addStill(Mname, Double.valueOf(vars[11])*1000);
                    break;
                case "BEZIERCURVE":
                    if(!nfe){
                        Point2D.Double endPoint = new Point2D.Double(Double.valueOf(vars[2]),Double.valueOf(vars[3]));
                        double endingAngle = Double.valueOf(vars[4]);
                        double fudge1 = Double.valueOf(vars[8]);
                        double fudge2 = Double.valueOf(vars[9]);
                        boolean reverse = Boolean.valueOf(vars[10]);
                        realBotBuilder.getMovements().addBezierCurve(Mname, endPoint,endingAngle,fudge1,fudge2,reverse,topSpeed,endSpeed);
                    }
                    break;
                case "LINE":
                    if(!nfe){
                        double distance = Double.valueOf(vars[5]);
                        boolean reverse = Boolean.valueOf(vars[10]);
                        realBotBuilder.getMovements().addLine(Mname, distance,reverse,topSpeed,endSpeed);
                    }
                    break;
                case "ROTATE":
                    if(!nfe){
                        double endingAngle = Double.valueOf(vars[4]);
                        realBotBuilder.getMovements().addTurn(Mname, endingAngle,topSpeed,endSpeed);
                    }
                    break;
            }

        }


        return realBotBuilder;
    }
    /*
        EXCEL values go: 0Name	1Type	2EndingX	3EndingY	4EndingAngle	5Distance	6EndingSpeed	7TopSpeed
        	8Fudge1	9Fudge2	10Reverse	11Time
        TXT Values: STARTX,STARTY,STARTTHETA
     */
    public void writeAutonomous(String name, RealBotBuilder realBotBuilder) throws IOException {
        //write starting values in txt file
        RSimpleTXTWriter writer = new RSimpleTXTWriter(workspaceURL +"\\"+name+".txt", false);
        writer.writeValue("STARTX", Double.toString(realBotBuilder.getStartingPoint().getX()));
        writer.writeValue("STARTY", Double.toString(realBotBuilder.getStartingPoint().getY()));
        writer.writeValue("STARTTHETA", Double.toString(realBotBuilder.getStartingAngle()));


        //write values in excel
        File workBookFile = new File(workspaceURL +"\\" +name+".xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet();

        RExcelWriter<RObservableMovement> excelWriter = new RExcelWriter<>( (row, rom)->{
            row.createCell(0).setCellValue(rom.getName());
            row.createCell(1).setCellValue(rom.getMovementType().getName());

            switch(rom.getMovementType().getName()){
                case "BEZIERCURVE":
                    row.createCell(2).setCellValue(rom.getEndPoint().getX());
                    row.createCell(3).setCellValue(rom.getEndPoint().getY());
                    row.createCell(4).setCellValue(rom.getEndAngle());
                    row.createCell(6).setCellValue(rom.getEndingSpeed());
                    row.createCell(7).setCellValue(rom.getTopSpeed());
                    row.createCell(8).setCellValue(((BezierCurveMovement) rom.getMovement()).getFudge1());
                    row.createCell(9).setCellValue(((BezierCurveMovement) rom.getMovement()).getFudge2());
                    row.createCell(10).setCellValue(Boolean.toString(rom.getReverse()));
                    break;
                case "ROTATE":
                    row.createCell(4).setCellValue(rom.getEndAngle());
                    row.createCell(6).setCellValue(rom.getEndingSpeed());
                    row.createCell(7).setCellValue(rom.getTopSpeed());
                    break;
                case "STILL":
                    row.createCell(11).setCellValue(((StillMovement) rom.getMovement()).getTime());
                    break;
                case "LINE":
                    double distance = rom.getEndPoint().distance(rom.getStartPoint());
                    row.createCell(5).setCellValue(distance);
                    row.createCell(6).setCellValue(rom.getEndingSpeed());
                    row.createCell(7).setCellValue(rom.getTopSpeed());
                    row.createCell(10).setCellValue(Boolean.toString(rom.getReverse()));
                    break;
            }
        });
        //Name	1Type	2EndingX	3EndingY	4EndingAngle	5Distance	6EndingSpeed	7TopSpeed
        //8Fudge1	9Fudge2	10Reverse	11Time
        String[] titleArray = new String[]{"Name","Type","EndingX","EndingY","EndingAngle","Distance",
                "EndingSpeed","TopSpeed","Fudge1","Fudge2","Reverse","Time"};
        XSSFRow cRow = workbook.getSheetAt(0).createRow(0);
        for(int i=0;i<titleArray.length;i++){
            cRow.createCell(i).setCellValue(titleArray[i]);
        }

        excelWriter.write(workbook.getSheetAt(0), realBotBuilder.getMovements().getArrayList(),1);
        //actually write to excel file
        FileOutputStream fOS = new FileOutputStream(workBookFile);
        workbook.write(fOS);
        fOS.close();
    }


    public RobotConfig getRobotConfig(){
        if(robotConfig!=null){
            return robotConfig;
        }else{
            loadConfig();
            return robotConfig;
        }
    }


}
