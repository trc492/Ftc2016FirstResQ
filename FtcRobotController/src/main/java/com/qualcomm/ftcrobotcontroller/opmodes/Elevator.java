package com.qualcomm.ftcrobotcontroller.opmodes;

public class Elevator
{
    //
    // This component consists of an elevator motor, a lower
    // limit switch, an upper limit switch and an encoder to
    // keep track of the position of the elevator.
    //
}   //class Elevator

/*
public class Elevator implements TrcMotorPosition, TrcPidController.PidInput
{
    private CANTalon elevatorMotor;
    private TrcPidController pidCtrl;
    private TrcPidMotor pidMotor;
    private boolean elevatorOverride = false;
    private double lastHeight = 0.0;
    private boolean stopped = true;

    public Elevator()
    {
        elevatorMotor = new CANTalon(RobotInfo.CANID_ELEVATOR);
        elevatorMotor.reverseSensor(true);
        pidCtrl = new TrcPidController(
                moduleName,
                RobotInfo.ELEVATOR_KP,
                RobotInfo.ELEVATOR_KI,
                RobotInfo.ELEVATOR_KD,
                RobotInfo.ELEVATOR_KF,
                RobotInfo.ELEVATOR_TOLERANCE,
                RobotInfo.ELEVATOR_SETTLING,
                this,
                TrcPidController.PIDCTRLO_ABS_SETPT);
        elevatorMotor.ConfigFwdLimitSwitchNormallyOpen(false);
        elevatorMotor.ConfigRevLimitSwitchNormallyOpen(false);
        pidMotor = new TrcPidMotor(moduleName,elevatorMotor,pidCtrl, this);
        pidMotor.setTargetScale(RobotInfo.ELEVATOR_INCHES_PER_CLICK);
        lastHeight = getHeight();
    }

    public void displayDebugInfo(int lineNum)
    {
        pidCtrl.displayPidInfo(lineNum);
    }

    public void setElevatorOverride(boolean enabled)
    {
        elevatorOverride = enabled;
    }

    public void zeroCalibrate(double calPower)
    {
        pidMotor.zeroCalibrate(calPower);
        lastHeight = getHeight();
    }

    public void setPower(double power)
    {
        if (elevatorOverride)
        {
            pidMotor.setPower(power);
        }
        else
        {
            pidMotor.setPidPower(
                    power,
                    RobotInfo.ELEVATOR_MIN_HEIGHT,
                    RobotInfo.ELEVATOR_MAX_HEIGHT,
                    true);
        }

        if (power == 0.0)
        {
            if (!stopped)
            {
                lastHeight = getHeight();
            }
            stopped = true;
        }
        else
        {
            stopped = false;
        }
    }

    public void setDeltaHeight(double deltaHeight)
    {
        lastHeight += deltaHeight;
        if (lastHeight > RobotInfo.ELEVATOR_MAX_HEIGHT)
        {
            lastHeight = RobotInfo.ELEVATOR_MAX_HEIGHT;
        }
        else if (lastHeight < RobotInfo.ELEVATOR_MIN_HEIGHT)
        {
            lastHeight = RobotInfo.ELEVATOR_MIN_HEIGHT;
        }
        pidMotor.setTarget(lastHeight, true);
    }

    public void setHeight(double height)
    {
        pidMotor.setTarget(height, true);
        lastHeight = height;
    }

    public void setHeight(double height, TrcEvent event, double timeout)
    {
        pidMotor.setTarget(height, event, timeout);
    }

    public double getHeight()
    {
        return getMotorPosition(elevatorMotor)*
               RobotInfo.ELEVATOR_INCHES_PER_CLICK;
    }

    public boolean isUpperLimitSwitchActive()
    {
        return !elevatorMotor.isFwdLimitSwitchClosed();
    }

    public boolean isLowerLimitSwitchActive()
    {
        return !elevatorMotor.isRevLimitSwitchClosed();
    }
    //
    // Implements TrcDriveBase.MotorPosition.
    //
    public double getMotorPosition(SpeedController speedController)
    {
        return ((CANTalon)speedController).getPosition();
    }   //getMotorPosition

    public double getMotorSpeed(SpeedController speedController)
    {
        return ((CANTalon)speedController).getSpeed()*10.0;
    }   //getMotorSpeed

    public void resetMotorPosition(SpeedController speedController)
    {
        ((CANTalon)speedController).setPosition(0.0);
    }   //resetMotorPosition

    public void reversePositionSensor(
            SpeedController speedController,
            boolean flip)
    {
        ((CANTalon)speedController).reverseSensor(flip);
    }   //reversePositionSensor

    public boolean isForwardLimitSwitchActive(SpeedController speedController)
    {
        return !((CANTalon)speedController).isFwdLimitSwitchClosed();
    }   //isForwardLimitSwitchActive

    public boolean isReverseLimitSwitchActive(SpeedController speedController)
    {
        return !((CANTalon)speedController).isRevLimitSwitchClosed();
    }   //isReverseLimitSwitchActive

    //
    // Implements TrcPidController.PidInput.
    //
    public double getInput(TrcPidController pidCtrl)
    {
        double value = 0.0;

        if (pidCtrl == this.pidCtrl)
        {
            value = getHeight();
        }

        return value;
    }   //getInput

}
*/