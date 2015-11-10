package ftc3543.opmodes;

import ftclib.FtcDcMotor;
import ftclib.FtcServo;
import ftclib.FtcTouch;
import trclib.TrcMotorController;
import trclib.TrcEvent;
import trclib.TrcMotorLimitSwitches;
import trclib.TrcMotorPositionSensor;
import trclib.TrcPidController;
import trclib.TrcPidMotor;

public class Elevator implements TrcPidController.PidInput, TrcMotorLimitSwitches
{
    //
    // This component consists of an elevator motor, a lower
    // limit switch, an upper limit switch and an encoder to
    // keep track of the position of the elevator.
    //
    private FtcDcMotor elevatorMotor;
    private TrcPidController pidController;
    private TrcPidMotor pidMotor;
    private FtcTouch lowerLimitSwitch;
    private FtcTouch upperLimitSwitch;
    private FtcServo chainLock;
    private boolean elevatorOverride = false;
    private double encoderPolarity = 1.0;

    public Elevator()
    {
        elevatorMotor = new FtcDcMotor("elevator", this);
        elevatorMotor.setPositionSensorInverted(true);
        pidController = new TrcPidController(
                "elevator",
                RobotInfo.ELEVATOR_KP, RobotInfo.ELEVATOR_KI,
                RobotInfo.ELEVATOR_KD, RobotInfo.ELEVATOR_KF,
                RobotInfo.ELEVATOR_TOLERANCE,RobotInfo.ELEVATOR_SETTLING,
                this);
        pidController.setAbsoluteSetPoint(true);
        pidMotor = new TrcPidMotor("elevator", elevatorMotor, pidController);
        pidMotor.setPositionScale(RobotInfo.ELEVATOR_INCHES_PER_CLICK);
        lowerLimitSwitch = new FtcTouch("lowerLimitSwitch");
        upperLimitSwitch = new FtcTouch("upperLimitSwitch");
        chainLock = new FtcServo("chainLock");
    }

    public void zeroCalibrate(double calPower)
    {
//        pidMotor.zeroCalibrate(calPower);
    }

    public void setElevatorOverride(boolean enabled)
    {
        elevatorOverride = enabled;
    }

    public void setChainLock(boolean locked)
    {
        chainLock.setPosition(
                locked? RobotInfo.CHAINLOCK_LOCK_POSITION: RobotInfo.CHAINLOCK_UNLOCK_POSITION);
    }

    public void setPower(double power)
    {
        if (elevatorOverride)
        {
            pidMotor.setPower(power);
        }
        else
        {
            pidMotor.setPidPower(power,
                                 RobotInfo.ELEVATOR_MIN_HEIGHT,
                                 RobotInfo.ELEVATOR_MAX_HEIGHT,
                                 true);
        }
    }

    public void setHeight(double height)
    {
        pidMotor.setTarget(height, true);
    }

    public void setHeight(double height, TrcEvent event, double timeout)
    {
        pidMotor.setTarget(height, event, timeout);
    }

    public double getHeight()
    {
        return elevatorMotor.getPosition()*RobotInfo.ELEVATOR_INCHES_PER_CLICK;
    }

    public boolean isLowerLimitSwitchPressed()
    {
        return lowerLimitSwitch.isActive();
    }

    public boolean isUpperLimitSwitchPressed()
    {
        return upperLimitSwitch.isActive();
    }

    public void displayDebugInfo(int lineNum)
    {
        pidController.displayPidInfo(lineNum);
    }

    //
    // Implements TrcPidController.PidInput.
    //
    public double getInput(TrcPidController pidCtrl)
    {
        double value = 0.0;

        if (pidCtrl == pidController)
        {
            value = getHeight();
        }

        return value;
    }   //getInput

    //
    // Implements TrcMotorLimitSwitches.
    //

    public boolean isForwardLimitSwitchActive(TrcMotorController speedController)
    {
        return upperLimitSwitch.isActive();
    }   //isForwardLimitSwitchActive

    public boolean isReverseLimitSwitchActive(TrcMotorController speedController)
    {
        return lowerLimitSwitch.isActive();
    }   //isReverseLimitSwitchActive

}   //class Elevator
