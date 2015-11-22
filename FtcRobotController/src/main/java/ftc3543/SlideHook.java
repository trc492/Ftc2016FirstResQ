package ftc3543;

import ftclib.FtcDcMotor;
import ftclib.FtcTouch;
import trclib.TrcDigitalTrigger;
import trclib.TrcEvent;
import trclib.TrcMotorController;
import trclib.TrcMotorLimitSwitches;
import trclib.TrcPidController;
import trclib.TrcPidMotor;

public class SlideHook implements TrcPidController.PidInput,
                                  TrcMotorLimitSwitches,
                                  TrcDigitalTrigger.TriggerHandler
{
    private FtcDcMotor motor;
    private TrcPidController pidController;
    private TrcPidMotor pidMotor;
    private FtcTouch lowerLimitSwitch;
    private FtcTouch upperLimitSwitch;
    private TrcDigitalTrigger lowerLimitTrigger;

    public SlideHook()
    {
        motor = new FtcDcMotor("slideHook", this);
        motor.setInverted(true);
        pidController = new TrcPidController(
                "slideHook",
                RobotInfo.SLIDEHOOK_KP, RobotInfo.SLIDEHOOK_KI,
                RobotInfo.SLIDEHOOK_KD, RobotInfo.SLIDEHOOK_KF,
                RobotInfo.SLIDEHOOK_TOLERANCE,RobotInfo.SLIDEHOOK_SETTLING,
                this);
        pidController.setAbsoluteSetPoint(true);
        pidMotor = new TrcPidMotor("slideHook", motor, pidController);
        pidMotor.setPositionScale(RobotInfo.SLIDEHOOK_INCHES_PER_CLICK);
        lowerLimitSwitch = new FtcTouch("slideLowerLimit");
        upperLimitSwitch = new FtcTouch("slideUpperLimit");
        lowerLimitTrigger = new TrcDigitalTrigger("slideLowerTrigger", lowerLimitSwitch, this);
        lowerLimitTrigger.setEnabled(true);
    }

    public void zeroCalibrate(double calPower)
    {
        pidMotor.zeroCalibrate(calPower);
    }

    public void setPower(double power)
    {
        pidMotor.setPower(power);
    }

    public void setLength(double length)
    {
        pidMotor.setTarget(length, true);
    }

    public void setLength(double length, TrcEvent event, double timeout)
    {
        pidMotor.setTarget(length, event, timeout);
    }

    public double getLength()
    {
        return pidMotor.getPosition();
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
            value = getLength();
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

    //
    // Implements TrcDigitalTrigger.TriggerHandler
    //

    public void DigitalTriggerEvent(TrcDigitalTrigger digitalTrigger, boolean active)
    {
        if (digitalTrigger == lowerLimitTrigger)
        {
            motor.resetPosition();
        }
    }   //DigitalTriggerEvent

}   //class SlideHook
