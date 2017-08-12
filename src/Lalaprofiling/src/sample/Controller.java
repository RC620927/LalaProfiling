package sample;

import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import sun.plugin.javascript.navig.Anchor;

import java.awt.geom.Point2D;

public class Controller {



    //initialize hboxes on right for variables
    @FXML
    public HBox initialPositionHBox;

    @FXML
    public HBox endingPositionHBox;

    @FXML
    public HBox initialAngleHBox;

    @FXML
    public HBox endingAngleHBox;

    @FXML
    public HBox initialSpeedHBox;

    @FXML
    public HBox endingSpeedHBox;

    @FXML
    public HBox topSpeedHBox;


    @FXML
    public ScrollPane movementsScrollPane;

    @FXML
    public AnchorPane movementsAnchorPane;

    @FXML
    public AnchorPane layoutAnchorPane;

    @FXML
    public TableView<String> movementsTable;

    @FXML
    public Button newButton;

    @FXML
    public Button deleteButton;

    @FXML
    public Button runButton;


    @FXML
    public ChoiceBox<String> movementTypeChooser;





    @FXML
    public void initialize(){

        LineMovement lm = new LineMovement(new Point2D.Double(0,0), new Point2D.Double(200,200), 2 );
        RotateMovement rm = new RotateMovement(new Point2D.Double(300,300), 10,300, 2);
        BezierCurveMovement bcm = new BezierCurveMovement(new Point2D.Double(200,100), new Point2D.Double(400,300),
                new Point2D.Double(200,200),new Point2D.Double(400,200), 2);
        MockBot mb = new MockBot(bcm.getSnapshots(), MockBot.MovementType.BEZIERCURVE);



        //setup ChoiceBox
        movementTypeChooser.getItems().addAll("Line", "Bezier Curve", "Turn");
        movementTypeChooser.setValue("Line");
        layoutAnchorPane.getChildren().add(mb.getBotNode());
        mb.getBotNode().setTranslateX(200);
        mb.getBotNode().setTranslateY(200);
        mb.getBotNode().setRotate(40);
        Rectangle r = new Rectangle(100,100);
        layoutAnchorPane.getChildren().add(r);
        r.setTranslateX(100);
        r.setTranslateX(100);


        Path p = new Path();
        p.getElements().add(new MoveTo(100,100));
        p.getElements().add(new LineTo(300,300));

        PathTransition pt = new PathTransition();
        pt.setDuration(Duration.seconds(2));
        pt.setNode(r);
        pt.setPath(p);
        pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);




        runButton.setOnAction(e->{
            mb.execute();
//            pt.play();

         /*   RotateTransition rt = new RotateTransition();
            rt.setDuration(Duration.seconds(2));
            rt.setNode(mb.getBotNode());
            rt.setFromAngle(10);
            rt.setToAngle(110);
            rt.play();*/
        });

    }



}
