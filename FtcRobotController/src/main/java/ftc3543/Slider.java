package ftc3543;

import ftclib.FtcDcMotor;
import ftclib.FtcTouchSensor;
import trclib.TrcDigitalTrigger;
import trclib.TrcEvent;
import trclib.TrcPidController;
import trclib.TrcPidMotor;

public class Slider implements TrcPidController.PidInput,
                               TrcDigitalTrigger.TriggerHandler
{
    private FtcTouchSensor lowerLimitSwitch;
    private FtcTouchSensor upperLimitSwitch;
    private TrcDigitalTrigger lowerLimitTrigger;
    private FtcDcMotor motor;
    private TrcPidController pidCtrl;
    private TrcPidMotor pidMotor;

    public Slider()
    {
        lowerLimitSwitch = new FtcTouchSensor("slideLowerLimit");
        upperLimitSwitch = new FtcTouchSensor("slideUpperLimit");
        lowerLimitTrigger = new TrcDigitalTrigger("slideLowerTrigger", lowerLimitSwitch, this);
        lowerLimitTrigger.setEnabled(true);
        motor = new FtcDcMotor("slider", lowerLimitSwitch, upperLimitSwitch);
        motor.setInverted(true);
        pidCtrl = new TrcPidController(
                "slider",
                RobotInfo.SLIDER_KP, RobotInfo.SLIDER_KI,
                RobotInfo.SLIDER_KD, RobotInfo.SLIDER_KF,
                RobotInfo.SLIDER_TOLERANCE,RobotInfo.SLIDER_SETTLING,
                this);
        pidCtrl.setAbsoluteSetPoint(true);
        pidMotor = new TrcPidMotor("slider", motor, pidCtrl);
        pidMotor.setPositionScale(RobotInfo.SLIDER_INCHES_PER_CLICK);
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
        pidCtrl.displayPidInfo(lineNum);
    }

    //
    // Implements TrcPidController.PidInput.
    //

    @Override
    public double getInput(TrcPidController pidCtrl)
    {
        double value = 0.0;

        if (pidCtrl == this.pidCtrl)
        {
            value = getLength();
        }

        return value;
    }   //getInput

    //
    // Implements TrcDigitalTrigger.TriggerHandler
    //

    @Override
    public void DigitalTriggerEvent(TrcDigitalTrigger digitalTrigger, boolean active)
    {
        if (digitalTrigger == lowerLimitTrigger)
        {
            motor.resetPosition();
        }
    }   //DigitalTriggerEvent

}   //class Slider
