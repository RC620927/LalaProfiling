package sample;

import java.awt.geom.Point2D;

/**
 * Created by Ruben on 8/9/2017.
 */
public interface Movement {

    double getDuration();

    void setDuration(double duration);

    Point2D getEndPoint();

    Point2D getStartPoint();

    void setStartPoint(Point2D startPoint);

    void update();

}
