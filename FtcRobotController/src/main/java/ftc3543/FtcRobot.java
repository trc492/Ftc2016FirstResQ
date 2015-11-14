package ftc3543;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import ftclib.FtcDcMotor;
import ftclib.FtcMRGyro;
import ftclib.FtcHiTechnicGyro;
import ftclib.FtcOpMode;
import ftclib.FtcOpticalDistanceSensor;
import ftclib.FtcTouch;
import trclib.TrcAnalogTrigger;
import trclib.TrcDigitalTrigger;
import trclib.TrcDriveBase;
import trclib.TrcGyro;
import trclib.TrcPidController;
import trclib.TrcPidDrive;
import trclib.TrcRobot;

public class FtcRobot implements TrcPidController.PidInput,
                                 TrcAnalogTrigger.AnalogTriggerHandler,
                                 TrcDigitalTrigger.DigitalTriggerHandler
{
    //
    // Sensors.
    //
    public FtcMRGyro mrGyro;
    public FtcHiTechnicGyro hitechnicGyro;
    public TrcGyro gyro;
    public FtcOpticalDistanceSensor lightSensor;
    public FtcTouch touchSensor;
    public ColorSensor colorSensor;
    public UltrasonicSensor sonarSensor;
    //
    // DriveBase subsystem.
    //
    public FtcDcMotor leftFrontWheel;
    public FtcDcMotor rightFrontWheel;
    public FtcDcMotor leftRearWheel;
    public FtcDcMotor rightRearWheel;
    public TrcDriveBase driveBase;
    public TrcPidController pidCtrlDrive;
    public TrcPidController pidCtrlTurn;
    public TrcPidDrive pidDrive;
    public TrcDigitalTrigger touchTrigger;
    public TrcAnalogTrigger lineTrigger;
    public TrcPidController pidCtrlLineFollow;
    public TrcPidDrive pidLineFollow;
    //
    // TreadDrive subsystem.
    //
    public TreadDrive treadDrive;
    //
    // Elevator subsystem.
    //
    public Elevator elevator;
    //
    // HangingHook subsystem.
    //
    public HangingHook hangingHook;
    //
    // ClimberRelease subsystem.
    //
    public ClimberRelease leftWing;
    public ClimberRelease rightWing;
    //
    // CattleGuard subsystem.
    //
    public CattleGuard cattleGuard;
    //
    // ButtonPush subsystem.
    //
    public ButtonPusher buttonPusher;

    public FtcRobot(TrcRobot.RunMode runMode)
    {
        HardwareMap hardwareMap = FtcOpMode.getInstance().hardwareMap;
        hardwareMap.logDevices();
        //
        // Initialize sensors.
        //
        mrGyro = new FtcMRGyro("gyroSensor");
        hitechnicGyro = new FtcHiTechnicGyro("hitechnicGyro");
        gyro = hitechnicGyro;
        lightSensor = new FtcOpticalDistanceSensor("lightSensor");
        touchSensor = new FtcTouch("touchSensor");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        sonarSensor = hardwareMap.ultrasonicSensor.get("sonarSensor");
        //
        // DriveBase subsystem.
        //
        leftFrontWheel = new FtcDcMotor("leftFrontWheel");
        rightFrontWheel = new FtcDcMotor("rightFrontWheel");
        leftRearWheel = new FtcDcMotor("leftRearWheel");
        rightRearWheel = new FtcDcMotor("rightRearWheel");
        leftFrontWheel.setInverted(true);
        leftRearWheel.setInverted(true);
        driveBase = new TrcDriveBase(
                leftFrontWheel,
                leftRearWheel,
                rightFrontWheel,
                rightRearWheel,
                gyro);
        driveBase.setYPositionScale(RobotInfo.DRIVE_INCHES_PER_CLICK);
        driveBase.resetPosition();
        pidCtrlDrive = new TrcPidController(
                "drivePid",
                RobotInfo.DRIVE_KP, RobotInfo.DRIVE_KI,
                RobotInfo.DRIVE_KD, RobotInfo.DRIVE_KF,
                RobotInfo.DRIVE_TOLERANCE, RobotInfo.DRIVE_SETTLING,
                this);
        pidCtrlTurn = new TrcPidController(
                "turnPid",
                RobotInfo.TURN_KP, RobotInfo.TURN_KI,
                RobotInfo.TURN_KD, RobotInfo.TURN_KF,
                RobotInfo.TURN_TOLERANCE, RobotInfo.TURN_SETTLING,
                this);
        pidDrive = new TrcPidDrive("pidDrive", driveBase, null, pidCtrlDrive, pidCtrlTurn);
        //
        // PID Line following.
        //
        touchTrigger = new TrcDigitalTrigger("touchTrigger", touchSensor, this);
        lineTrigger = new TrcAnalogTrigger(
                "lineTrigger", lightSensor, RobotInfo.LINE_THRESHOLD, this, false);
        pidCtrlLineFollow = new TrcPidController(
                "lineFollowPid",
                RobotInfo.LINEFOLLOW_KP, RobotInfo.LINEFOLLOW_KI,
                RobotInfo.LINEFOLLOW_KD, RobotInfo.LINEFOLLOW_KF,
                RobotInfo.LINEFOLLOW_TOLERANCE, RobotInfo.LINEFOLLOW_SETTLING,
                this);
        pidCtrlLineFollow.setAbsoluteSetPoint(true);
        pidLineFollow = new TrcPidDrive(
                "lineFollowDrive", driveBase, null, pidCtrlDrive, pidCtrlLineFollow);
        //
        // TreadDrive subsystem.
        //
        treadDrive = new TreadDrive();
        //
        // Elevator subsystem.
        //
        elevator = new Elevator();
        elevator.zeroCalibrate(RobotInfo.ELEVATOR_CAL_POWER);
        //
        // HangingHook subsystem.
        //
        hangingHook = new HangingHook();
        hangingHook.retract();
        //
        // ClimberRelease subsystem.
        //
        leftWing = new ClimberRelease("leftWing", false);
        rightWing = new ClimberRelease("rightWing", true);
        leftWing.retract();
        rightWing.retract();
        //
        // CattleGuard subsystem.
        //
        cattleGuard = new CattleGuard();
        cattleGuard.retract();
        //
        // ButtonPusher subsystem.
        //
        buttonPusher = new ButtonPusher();
        buttonPusher.retract();
    }   //FtcRobot

    //
    // Implements TrcPidController.PidInput
    //

    @Override
    public double getInput(TrcPidController pidCtrl)
    {
        double input = 0.0;

        if (pidCtrl == pidCtrlDrive)
        {
            input = driveBase.getYPosition();
        }
        else if (pidCtrl == pidCtrlTurn)
        {
            input = driveBase.getHeading();
        }
        else if (pidCtrl == pidCtrlLineFollow)
        {
            input = lightSensor.getValue();
        }

        return input;
    }   //getInput

    //
    // Implements TrcAnalogTrigger.AnalogTriggerHandler
    //
    public void AnalogTriggerEvent(
            TrcAnalogTrigger analogTrigger,
            TrcAnalogTrigger.Zone zone,
            double value)
    {
        if (analogTrigger == lineTrigger &&
            zone == TrcAnalogTrigger.Zone.HIGH_ZONE &&
            pidDrive.isEnabled())
        {
            pidDrive.cancel();
        }
    }   //AnalogTriggerEvent

    //
    // Implements TrcDigitalTrigger.DigitalTriggerHandler
    //
    public void DigitalTriggerEvent(TrcDigitalTrigger digitalTrigger, boolean active)
    {
        if (digitalTrigger == touchTrigger && active && pidLineFollow.isEnabled())
        {
            pidLineFollow.cancel();
        }
    }   //DigitalTriggerEvent

}   //class FtcRobot
