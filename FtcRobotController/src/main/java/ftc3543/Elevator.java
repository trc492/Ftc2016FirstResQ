package ftc3543;

import ftclib.FtcDcMotor;
import ftclib.FtcServo;
import ftclib.FtcTouchSensor;
import trclib.TrcDigitalTrigger;
import trclib.TrcEvent;
import trclib.TrcPidController;
import trclib.TrcPidMotor;

public class Elevator implements TrcPidController.PidInput,
                                 TrcDigitalTrigger.TriggerHandler
{
    //
    // This component consists of an elevator motor, a lower limit switch,
    // an upper limit switch, an encoder to keep track of the position of
    // the elevator and a servo to engage/disengage the brake.
    //
    private FtcTouchSensor lowerLimitSwitch;
//    private FtcTouchSensor upperLimitSwitch;
    private TrcDigitalTrigger lowerLimitTrigger;
    private FtcDcMotor motor;
    private TrcPidController pidCtrl;
    private TrcPidMotor pidMotor;
    private FtcServo brake;
    private boolean brakeOn = false;

    public Elevator()
    {
        lowerLimitSwitch = new FtcTouchSensor("lowerLimitSwitch");
//        upperLimitSwitch = new FtcTouchSensor("upperLimitSwitch");
        lowerLimitTrigger = new TrcDigitalTrigger("elevatorLowerLimit", lowerLimitSwitch, this);
        lowerLimitTrigger.setEnabled(true);
        motor = new FtcDcMotor("elevator", lowerLimitSwitch);//, upperLimitSwitch);
        pidCtrl = new TrcPidController(
                "elevator",
                RobotInfo.ELEVATOR_KP, RobotInfo.ELEVATOR_KI,
                RobotInfo.ELEVATOR_KD, RobotInfo.ELEVATOR_KF,
                RobotInfo.ELEVATOR_TOLERANCE,RobotInfo.ELEVATOR_SETTLING,
                this);
        pidCtrl.setAbsoluteSetPoint(true);
        pidMotor = new TrcPidMotor("elevator", motor, pidCtrl);
        pidMotor.setPositionScale(RobotInfo.ELEVATOR_INCHES_PER_CLICK);
        brake = new FtcServo("brake");
        setBrakeOn(false);
    }

    public void zeroCalibrate(double calPower)
    {
        pidMotor.zeroCalibrate(calPower);
    }

    public void setBrakeOn(boolean brakeOn)
    {
        brake.setPosition(
                brakeOn? RobotInfo.BRAKE_ON_POSITION: RobotInfo.BRAKE_OFF_POSITION);
        this.brakeOn = brakeOn;
    }

    public void setPower(double power)
    {
        if (power != 0.0 && brakeOn)
        {
            //
            // Elevator is moving and brake is ON, disengage brake.
            //
            setBrakeOn(false);
        }
        pidMotor.setPower(power);
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
        return pidMotor.getPosition();
    }

    public boolean isLowerLimitSwitchPressed()
    {
        return lowerLimitSwitch.isActive();
    }

    public boolean isUpperLimitSwitchPressed()
    {
        return false;//upperLimitSwitch.isActive();
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
            value = getHeight();
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

}   //class Elevator
