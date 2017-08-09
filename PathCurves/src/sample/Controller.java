package sample;

import de.thomasbolz.javafx.NumberSpinner;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class Controller {


    @FXML
    public Canvas canvas;


    public HBox xBox;

    public HBox yBox;

    public HBox iBox;

    public HBox fBox;

    @FXML
    public VBox buttons;

    @FXML
    public Label aLabel;

    @FXML
    public Label slopeLabel;

    @FXML
    public Label bLabel;

    @FXML
    public Button enter;


    public void initialize(){


        xBox = new NumberSpinner(BigDecimal.valueOf(20), BigDecimal.valueOf(1));
        yBox = new NumberSpinner(BigDecimal.valueOf(20), BigDecimal.valueOf(1));
        iBox = new NumberSpinner(BigDecimal.valueOf(0), BigDecimal.valueOf(1));
        fBox = new NumberSpinner(BigDecimal.valueOf(20), BigDecimal.valueOf(1));
        buttons.getChildren().addAll(xBox,yBox,iBox,fBox);



        enter.setOnAction(e->{
            updateCanvas(new Curve(  ((NumberSpinner) xBox).getNumber().doubleValue(), ((NumberSpinner) yBox).getNumber().doubleValue(),
                    ((NumberSpinner) iBox).getNumber().doubleValue(), ((NumberSpinner) fBox).getNumber().doubleValue(), true ));
        });
    }


    public void updateCanvas(Curve c){
        canvas.getGraphicsContext2D().clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        for(int x = 0; x < 200; x++){
            Curve.CurvePoint cp = c.seekNext(0.2 * x);
            aLabel.setText("a:" + c.getA());
            bLabel.setText("b:" +c.getB());
            slopeLabel.setText("S:" + c.slopeEndingCheck);

            if(cp!=null){

                canvas.getGraphicsContext2D().fillRect(cp.point.getX() *5 + 200, 300-cp.point.getY() *5,2,2);
            }

        }




    }


}
