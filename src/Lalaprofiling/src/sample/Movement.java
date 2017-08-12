package sample;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by Ruben on 8/9/2017.
 */
public interface Movement {



    public static int refreshRate = 50;

    int getDetail();

    void setDetail(int detail);

    Point2D getEndPoint();

    Point2D getStartPoint();

    void setStartPoint(Point2D startPoint);

    void update();

    ArrayList<Snapshot> getSnapshots();
}
