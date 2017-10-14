package RealBot;

import rc.CastrooOrnelas.datatypes.RObservable;

import java.util.function.BiConsumer;

/**
 * Created by raque on 8/24/2017.
 */
public class RObservableTrajectory implements RObservable<Trajectory>{


    @Override
    public void addChangeListener(BiConsumer<Trajectory, Trajectory> changeListener) {

    }

    @Override
    public void removeChangeListener(BiConsumer<Trajectory,Trajectory> changeListener) {

    }
}
