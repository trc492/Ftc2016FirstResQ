package ftc3543;

import ftclib.FtcDcMotor;
import ftclib.FtcServo;
import ftclib.FtcTouchSensor;
import hallib.HalDashboard;
import trclib.TrcDigitalTrigger;
import trclib.TrcEvent;
import trclib.TrcPidController;
import trclib.TrcPidMotor;

public class Winch implements TrcPidController.PidInput,
                              TrcDigitalTrigger.TriggerHandler
{
    //
    // This component consists of a winch motor, a lower limit switch,
    // an encoder to keep track of the position of tape measure and a
    // servo to engage/disengage the brake.
    //
    private HalDashboard dashboard;
    private FtcTouchSensor lowerLimitSwitch;
    private TrcDigitalTrigger lowerLimitTrigger;
    private FtcDcMotor motor;
    private TrcPidController pidCtrl;
    private TrcPidMotor pidMotor;
    private FtcServo brake;
    private boolean brakeOn = false;

    public Winch()
    {
        dashboard = HalDashboard.getInstance();
        lowerLimitSwitch = new FtcTouchSensor("winchLowerLimit");
        lowerLimitTrigger = new TrcDigitalTrigger("winchLowerLimit", lowerLimitSwitch, this);
        lowerLimitTrigger.setEnabled(true);
        motor = new FtcDcMotor("winch", lowerLimitSwitch);
        pidCtrl = new TrcPidController(
                "winch",
                RobotInfo.WINCH_KP, RobotInfo.WINCH_KI,
                RobotInfo.WINCH_KD, RobotInfo.WINCH_KF,
                RobotInfo.WINCH_TOLERANCE,RobotInfo.WINCH_SETTLING,
                this);
        pidCtrl.setAbsoluteSetPoint(true);
        pidMotor = new TrcPidMotor("winch", motor, pidCtrl);
        pidMotor.setPositionScale(RobotInfo.WINCH_INCHES_PER_CLICK);
        brake = new FtcServo("brake");
        setBrakeOn(false);
    }

    public void zeroCalibrate()
    {
        pidMotor.zeroCalibrate(RobotInfo.WINCH_CAL_POWER);
    }

    public void setBrakeOn(boolean brakeOn)
    {
        brake.setPosition(
                brakeOn? RobotInfo.WINCH_BRAKE_ON_POSITION: RobotInfo.WINCH_BRAKE_OFF_POSITION);
        this.brakeOn = brakeOn;
    }

    public void setPower(double power)
    {
        if (power != 0.0 && brakeOn)
        {
            //
            // Winch is moving and brake is ON, disengage brake.
            //
            setBrakeOn(false);
        }

        if (power > 0.0 && pidMotor.getPosition() >= RobotInfo.WINCH_MAX_LENGTH)
        {
            //
            // We reached maximum length, stop.
            //
            power = 0.0;
        }

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

    public double getEncoderPosition()
    {
        return motor.getPosition();
    }

    public boolean isLowerLimitSwitchPressed()
    {
        return lowerLimitSwitch.isActive();
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
            value = pidMotor.getPosition();
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

}   //class Winch
