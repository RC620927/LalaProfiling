package sample;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by Ruben on 8/9/2017.
 */
public interface Movement {



    public static int refreshRate = 50;

    double getDetail();

    void setDetail(double detail);

    void setInitialAngle(double initialAngle);

    Point2D getEndPoint();

    Point2D getStartPoint();

    double getInitialAngle();

    void setStartPoint(Point2D startPoint);

    void update();

    double getEndAngle();

    void setReverse(boolean reverse);

    boolean getReverse();

    ArrayList<Snapshot> getSnapshots();

}
