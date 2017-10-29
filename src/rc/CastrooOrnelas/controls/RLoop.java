package rc.CastrooOrnelas.controls;

/**
 * Created by raque on 10/9/2017.
 */
public class RLoop {

    private long millisWaitTime;
    private int hz;
    private Runnable runnable, loopRunnable;
    private boolean running, shouldRun;

    private Thread thread;

    public RLoop(Runnable runnable, double hz){
        this.runnable=runnable;
        this.millisWaitTime = (long) (1000/hz);
        running=false;

        this.loopRunnable = ()->{
            running=true;

            long delay = 0;
            double currentStartTime = System.currentTimeMillis();
            while(shouldRun){
                running=true;
                currentStartTime= System.currentTimeMillis();
                runnable.run();
                delay = (long) Math.max(0,System.currentTimeMillis() - currentStartTime);
                try {
                    Thread.sleep(Math.max(1,millisWaitTime - delay));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            running=false;

        };
    }

    public void start(){
        if(!running){
            thread = new Thread(loopRunnable);
            shouldRun=true;
            thread.start();
        }
    }

    public void kill(){
        shouldRun=false;
    }

    public boolean isRunning() {
        return running;
    }
}
