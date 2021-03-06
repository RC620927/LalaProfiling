package sample;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.awt.geom.Point2D;

/**
 * Created by raque on 8/10/2017.
 * random math methods constantly used.
 * Should be put into a util package at some point
 */
public class Resources {
    private static Resources ourInstance = new Resources();

    public static Resources getInstance() {
        return ourInstance;
    }

    private Resources() {
    }

    public static double mod(double value, int mod){
        value = value%mod;
        value = (value+mod)%mod;
        return value;
    }

    public static double angleOf(Point2D startPoint, Point2D endPoint){
        double angle = Math.atan( (endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX()));
        angle = Math.toRadians(90) - angle;
        angle = Math.toDegrees(angle);
        angle = (endPoint.getX() - startPoint.getX()) <0 ? angle + 180 : angle;
        return angle;
    }

    public static boolean forwardsMovement(double currentAngle, double movementAngle){
        double delTheta = movementAngle-currentAngle;
        delTheta%=360;
        delTheta = (delTheta+360) %360;
        if(delTheta>90 && delTheta<270){
            return false;
        }else{
            return true;
        }
    }

    public static double limit(double value, double min, double max){
        return Math.min(Math.max(value,min),max);
    }

    public static Image rotateImage(Image image, double rotation){
        ImageView iv = new ImageView(image);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        params.setTransform(new Rotate(rotation,image.getHeight()/2, image.getWidth()/2));
        params.setViewport(new Rectangle2D(0,0,image.getHeight(), image.getWidth()));
        return iv.snapshot(params,null);
    }
}


