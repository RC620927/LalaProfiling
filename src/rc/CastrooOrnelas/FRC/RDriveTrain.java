package rc.CastrooOrnelas.FRC;

import rc.CastrooOrnelas.controls.RPID;
import Lalaprofiling.Application.Resources;

import java.util.function.Supplier;

/**
 * Created by raque on 9/7/2017.
 */
public class RDriveTrain {

    private boolean driveTrainEnabled;

    private RPID autoAnglePID;
    private RPID leftPID, rightPID;
    private double kV, kA, maxSpeed, maxAccel;
    private double leftVelocity, rightVelocity;

    private double pastActualVelocityUpdate = 0;
    private double pastActualLeftPosition=0, pastActualRightPosition=0;
    private double actualLeftVelocity, actualRightVelocity;

    private double leftPIDVoltageAdjustment, rightPIDVoltageAdjustment;
    private double pastLeftVelocitySetpoint = 0, pastRightVelocitySetpoint = 0;
    private double pastLeftVelocitySetpointTime = 0, pastRightVelocitySetpointTime = 0;
    private Supplier<Double> angleSupplier, leftPositionSupplier, rightPositionSupplier;
    private boolean vPIDEnabled;

    private Runnable driveTrainLoop;

    //suppliers in inches
    public RDriveTrain(double vP, double vI, double vD, double aP, double aI, double aD, double maxSpeed,
                       double maxAccel, Supplier<Double> angleSupplier,
                       Supplier<Double> leftPositionSupplier, Supplier<Double> rightPositionSupplier){
        leftPID = new RPID(vP,vI,vD);
        rightPID = new RPID(vP,vI,vD);
        autoAnglePID = new RPID(aP, aI, aD);
        this.maxSpeed=maxSpeed;
        this.maxAccel=maxAccel;
        kV = 1/maxSpeed;
        kA = 0.15/maxAccel;
        leftVelocity=0;
        rightVelocity=0;
        this.angleSupplier=angleSupplier;
        this.leftPositionSupplier=leftPositionSupplier;
        this.rightPositionSupplier = rightPositionSupplier;
        vPIDEnabled=false;
        driveTrainEnabled=true;
    }

    public void setVelocity(double leftVelocity, double rightVelocity){
        leftVelocity = Resources.limit(leftVelocity, -maxSpeed, maxSpeed);
        rightVelocity = Resources.limit(rightVelocity, -maxSpeed, maxSpeed);

        leftPID.setSetPoint(leftVelocity);
        rightPID.setSetPoint(rightVelocity);
        double leftAccel = leftVelocity - pastLeftVelocitySetpoint;
        leftAccel *= 1000 * (1/(System.currentTimeMillis() - pastLeftVelocitySetpointTime));
        double rightAccel = rightVelocity - pastRightVelocitySetpoint;
        rightAccel *= 1000 * (1/(System.currentTimeMillis() - pastRightVelocitySetpointTime));

        double leftVoltage = leftPIDVelocityToVoltage(leftVelocity, leftAccel);
        double rightVoltage = rightPIDVelocityToVoltage(rightVelocity, rightAccel);

        //setVoltage to drivetrain
        setVoltage(leftVoltage,rightVoltage);

        //save values to use for next iteration;
        pastLeftVelocitySetpoint=leftVelocity;
        pastRightVelocitySetpoint=rightVelocity;
        pastLeftVelocitySetpointTime=System.currentTimeMillis();
        pastRightVelocitySetpointTime=System.currentTimeMillis();
    }

    //meant to be used only for AUTO in conjuction with Trajectories
    public void setVelocity(double leftVelocity, double rightVelocity, double angle){
        autoAnglePID.setSetPoint(angle);
        double anglePIDAdjustment = autoAnglePID.crunch(angleSupplier.get());
        setVelocity(leftVelocity+anglePIDAdjustment, rightVelocity-anglePIDAdjustment);
    }



    public double getLeftVelocity(){
        return actualLeftVelocity;
    }

    public double getRightVelocity(){
        return actualRightVelocity;
    }

    public double rawVelocityToVoltage(double velocity, double accel){
        return kV * velocity + kA* accel;
    }

    private double leftPIDVelocityToVoltage(double velocity, double accel){
        double voltage = rawVelocityToVoltage(velocity,accel);
        voltage +=(leftPIDVoltageAdjustment * (leftVelocity/maxSpeed));
        return voltage;
    }

    public double rightPIDVelocityToVoltage(double velocity, double accel){
        double voltage = rawVelocityToVoltage(velocity,accel);
        voltage +=(rightPIDVoltageAdjustment * (rightVelocity/maxSpeed));
        return voltage;
    }

    private void setVoltage(double leftVoltage, double rightVoltage){

        //set Speed controllers to this voltage

    }

    public void enablePID(){
        leftPIDVoltageAdjustment=0;
        rightPIDVoltageAdjustment=0;
        driveTrainEnabled=true;
        vPIDEnabled=true;
    }

    public void disablePID(){
        vPIDEnabled=false;
    }

    public synchronized void enable(){
        startLoop();
    }

    public synchronized void disable(){
        driveTrainEnabled=false;
    }

    private synchronized void startLoop(){
        if(driveTrainEnabled=false){
            new Thread(this::updateLoop).start();
        }
        driveTrainEnabled=true;
    }

    public void updateLoop(){
         driveTrainLoop = ()->{
            while(driveTrainEnabled){
                if(vPIDEnabled){
                    //calculate actual velocities
                    double currentLeftPos = leftPositionSupplier.get();
                    double currentRightPos = rightPositionSupplier.get();

                    //gonna need a better way to calculate velocity based on some sort of filter to get rid of outliers
                    actualLeftVelocity =1000 * (currentLeftPos - pastActualLeftPosition)/
                            (System.currentTimeMillis()-pastActualVelocityUpdate);
                    actualRightVelocity =1000 * (currentRightPos - pastActualRightPosition)/
                            (System.currentTimeMillis()-pastActualVelocityUpdate);
                    pastActualVelocityUpdate = System.currentTimeMillis();
                    leftPIDVoltageAdjustment+= leftPID.crunch(actualLeftVelocity);
                    rightPIDVoltageAdjustment+= rightPID.crunch(actualRightVelocity);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
