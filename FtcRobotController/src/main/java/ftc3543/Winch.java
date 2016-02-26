package ftc3543;

import ftclib.FtcDcMotor;
import ftclib.FtcServo;
import ftclib.FtcTouchSensor;
import hallib.HalDashboard;
import trclib.TrcDigitalTrigger;
import trclib.TrcEnhancedServo;
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
    private FtcDcMotor winchMotor;
    private FtcDcMotor feederMotor;
    private TrcPidController pidCtrl;
    private TrcPidMotor pidMotor;
    private FtcServo brake;
    private boolean brakeOn = false;
    private FtcServo tilterServo;
    private TrcEnhancedServo tilter;

    public Winch()
    {
        dashboard = HalDashboard.getInstance();
        lowerLimitSwitch = new FtcTouchSensor("winchLowerLimit");
        lowerLimitTrigger = new TrcDigitalTrigger("winchLowerLimit", lowerLimitSwitch, this);
        lowerLimitTrigger.setEnabled(true);
        winchMotor = new FtcDcMotor("winch");// lowerLimitSwitch);
        feederMotor = new FtcDcMotor("feeder");
        winchMotor.setInverted(true);
        pidCtrl = new TrcPidController(
                "winch",
                RobotInfo.WINCH_KP, RobotInfo.WINCH_KI,
                RobotInfo.WINCH_KD, RobotInfo.WINCH_KF,
                RobotInfo.WINCH_TOLERANCE,RobotInfo.WINCH_SETTLING,
                this);
        pidCtrl.setAbsoluteSetPoint(true);
        pidMotor = new TrcPidMotor("winch", feederMotor, winchMotor, pidCtrl);
        pidMotor.setPositionScale(RobotInfo.WINCH_INCHES_PER_COUNT);
        brake = new FtcServo("brake");
        setBrakeOn(false);
        tilterServo = new FtcServo("tilterServo");
        tilter = new TrcEnhancedServo("tilter", tilterServo);
        tilterServo.setInverted(true);
        tilter.setPosition(RobotInfo.WINCH_TILTER_START_POSITION);
        tilter.setStepMode(RobotInfo.WINCH_TILTER_MAX_STEPRATE,
                           RobotInfo.WINCH_TILTER_MIN_POSITION,
                           RobotInfo.WINCH_TILTER_MAX_POSITION);
    }   //Winch

    public void zeroCalibrate()
    {
        pidMotor.zeroCalibrate(RobotInfo.WINCH_CAL_POWER);
    }   //zeroCalibrate

    public void setPower(double power)
    {
        if (power != 0.0 && brakeOn)
        {
            //
            // Winch is moving and brake is ON, disengage brake.
            //
            setBrakeOn(false);
        }

        /*
        if (power > 0.0 && pidMotor.getPosition() >= RobotInfo.WINCH_MAX_LENGTH)
        {
            //
            // We reached maximum length, stop.
            //
            power = 0.0;
        }
        */

        pidMotor.setPower(power);
    }   //setPower

    public void setLength(double length)
    {
        pidMotor.setTarget(length, true);
    }   //setLength

    public void setLength(double length, TrcEvent event, double timeout)
    {
        pidMotor.setTarget(length, event, timeout);
    }   //setLength

    public double getLength()
    {
        return pidMotor.getPosition();
    }   //getLength

    public double getEncoderPosition()
    {
        return feederMotor.getPosition();
    }   //getEncoderPosition

    public boolean isLowerLimitSwitchPressed()
    {
        return lowerLimitSwitch.isActive();
    }   //isLowerLimitSwitchPressed

    public void setBrakeOn(boolean brakeOn)
    {
        brake.setPosition(
                brakeOn? RobotInfo.WINCH_BRAKE_ON_POSITION: RobotInfo.WINCH_BRAKE_OFF_POSITION);
        this.brakeOn = brakeOn;
    }   //setBrakeOn

    public void setTilterPosition(double position)
    {
        tilter.setPosition(position);
    }   //setTilterPosition

    public double getTilterPosition()
    {
        return tilterServo.getPosition();
    }   //getTilterPosition

    public void setTilterPower(double power)
    {
        tilter.setPower(power);
    }   //setTilterPower

    public void displayDebugInfo(int lineNum)
    {
        pidCtrl.displayPidInfo(lineNum);
    }   //displayDebugInfo

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
            feederMotor.resetPosition();
        }
    }   //DigitalTriggerEvent

}   //class Winch
