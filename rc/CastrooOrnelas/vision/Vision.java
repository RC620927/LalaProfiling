package rc.CastrooOrnelas.vision;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Created by raque on 10/2/2017.
 * created as a base for vision detection based on hue, potentially brightness later on
 * meant to use as little resources as possible to be run at a very fast processing rate
 * i,j -> i = row#, j = column#
 */
public class Vision {

    private ModifiedPixel targetPixel;
    private BooleanPixel booleanPixelArray[][];
    private ArrayList<BooleanPixel> acceptedPixels;
    private int targetRows[];
    private int height, width;
    private double tolerance=0.7;

    //cos function used to estimate score of each hue. Taken to the 15th to exaggerate extremes.
    private final Function<Integer, Double> indivScoreAlgorithm = (val) ->{
        Double score = Math.pow((Math.cos( ((Math.PI/2)/256) * val )),15);
        return score;
    };

    //sin function to be used after adding up individual scores assuming max 3
    private final Function<Double, Double> sumScoreAlgorithm = (sum) ->{
        Double score = Math.pow((Math.sin( ((Math.PI/2)/3) * sum )),6);
        return score;
    };

    public Vision(ModifiedPixel targetPixel, int width, int height){
        this.targetPixel=targetPixel;
        this.height=height;
        this.width=width;
        targetRows = new int[height+1];
        booleanPixelArray = new BooleanPixel[height+1][width+1];
        for(int i=1;i<=height;i++){
            targetRows[i]=i;
            for(int j=1;j<=width;j++){
                booleanPixelArray[i][j] = new BooleanPixel(i,j,false);
            }
        }
        acceptedPixels = new ArrayList<>();
    }

    public void setTolerance(double tolerance){
        this.tolerance=tolerance;
    }

    //only scan certain rows instead of whole picture
    public void setTargetRows(int targetRows[]){
        this.targetRows=targetRows;
    }


    public void crunch(ModifiedPixel image[][]){
        int count=0;
        double sum=0;

        acceptedPixels.removeAll(acceptedPixels);
        for(int i:targetRows){
            for(int j=1;j<=width;j++){
                BooleanPixel bP = new BooleanPixel(i,j, scorePixel(image[i][j]) >tolerance);
                if(scorePixel(image[i][j]) >tolerance){
                    count++;
                    sum+=scorePixel(image[i][j]);
                }
                booleanPixelArray[i][j] = bP;
                acceptedPixels.add(bP);
            }
        }
        System.out.print(sum/count);
    }

    private double scorePixel(ModifiedPixel pixel){
        if(pixel==null){
            return 0;
        }
        double rScore = indivScoreAlgorithm.apply(Math.abs(targetPixel.red  - pixel.red));
        double bScore = indivScoreAlgorithm.apply(Math.abs(targetPixel.blue  - pixel.blue));
        double gScore = indivScoreAlgorithm.apply(Math.abs(targetPixel.green  - pixel.green));

        double sumScore = sumScoreAlgorithm.apply(rScore+gScore+bScore);
        return sumScore;
    }

    public BooleanPixel[][] getBooleanPixelArray() {
        return booleanPixelArray;
    }

    public ArrayList<BooleanPixel> getAcceptedPixels() {
        return acceptedPixels;
    }
}
