package ftc3543;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import ftclib.FtcDcMotor;
import ftclib.FtcMRI2cColorSensor;
import ftclib.FtcMRGyro;
import ftclib.FtcOpMode;
import ftclib.FtcOpticalDistanceSensor;
import ftclib.FtcServo;
import ftclib.FtcUltrasonicSensor;
import hallib.HalUtil;
import trclib.TrcAnalogTrigger;
import trclib.TrcDriveBase;
import trclib.TrcEnhancedServo;
import trclib.TrcPidController;
import trclib.TrcPidDrive;
import trclib.TrcRobot;

public class FtcRobot implements TrcPidController.PidInput,
                                 TrcAnalogTrigger.TriggerHandler
{
    //
    // Sensors.
    //
    public FtcMRGyro gyro;
    public FtcUltrasonicSensor sonarSensor;
    public FtcOpticalDistanceSensor lightSensor;
    public ColorSensor colorSensor;
    public FtcMRI2cColorSensor i2cColorSensor;
    public double prevSonarValue;
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

    public TrcPidController pidCtrlSonar;
    public TrcPidController pidCtrlLight;
    public TrcPidDrive pidDriveLineFollow;
    public TrcAnalogTrigger lightTrigger;
    //
    // Slider subsystem.
    //
    public Slider slider;
    //
    // Elevator subsystem.
    //
    public Elevator elevator;
    //
    // HangingHook subsystem.
    //
    public FtcServo hookServo;
    public TrcEnhancedServo hangingHook;
    //
    // ClimberRelease subsystem.
    //
    public FtcServo leftWing;
    public FtcServo rightWing;
    //
    // ButtonPusher subsystem.
    //
    public ButtonPusher leftButtonPusher;
    public ButtonPusher rightButtonPusher;

    public FtcRobot(TrcRobot.RunMode runMode)
    {
        HardwareMap hardwareMap = FtcOpMode.getInstance().hardwareMap;
        hardwareMap.logDevices();
        //
        // Initialize sensors.
        //
        gyro = new FtcMRGyro("gyroSensor");
        gyro.calibrate();
        sonarSensor = new FtcUltrasonicSensor("legoSonarSensor");
        sonarSensor.setScale(RobotInfo.SONAR_INCHES_PER_CM);
        lightSensor = new FtcOpticalDistanceSensor("lightSensor");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        i2cColorSensor = new FtcMRI2cColorSensor("i2cColorSensor");
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
                leftFrontWheel, leftRearWheel, rightFrontWheel, rightRearWheel, gyro);
        driveBase.setYPositionScale(RobotInfo.DRIVE_INCHES_PER_CLICK);
        //
        // PID Drive.
        //
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
        pidCtrlSonar = new TrcPidController(
                "sonarPid",
                RobotInfo.SONAR_KP, RobotInfo.SONAR_KI,
                RobotInfo.SONAR_KD, RobotInfo.SONAR_KF,
                RobotInfo.SONAR_TOLERANCE, RobotInfo.SONAR_SETTLING,
                this);
        pidCtrlSonar.setAbsoluteSetPoint(true);
        pidCtrlSonar.setInverted(true);
        pidCtrlLight = new TrcPidController(
                "lightPid",
                RobotInfo.LINEFOLLOW_KP, RobotInfo.LINEFOLLOW_KI,
                RobotInfo.LINEFOLLOW_KD, RobotInfo.LINEFOLLOW_KF,
                RobotInfo.LINEFOLLOW_TOLERANCE, RobotInfo.LINEFOLLOW_SETTLING,
                this);
        pidCtrlLight.setAbsoluteSetPoint(true);
        pidDriveLineFollow = new TrcPidDrive(
                "lineFollowDrive", driveBase, null, pidCtrlSonar, pidCtrlLight);
        //
        // Triggers.
        //
        lightTrigger = new TrcAnalogTrigger(
                "lightTrigger", lightSensor, RobotInfo.LIGHT_TRIGGER_LEVEL, this);
        //
        // Slider subsystem.
        //
        slider = new Slider();
//        slider.zeroCalibrate(RobotInfo.SLIDER_CAL_POWER);
        //
        // Elevator subsystem.
        //
        elevator = new Elevator();
//        elevator.zeroCalibrate(RobotInfo.ELEVATOR_CAL_POWER);
        //
        // HangingHook subsystem.
        //
        hookServo = new FtcServo("hangingHook");
        hookServo.setInverted(true);
        hangingHook = new TrcEnhancedServo("hangingHook", hookServo);
        hookServo.setPosition(RobotInfo.HANGINGHOOK_RETRACT_POSITION);

        //
        // ClimberRelease subsystem.
        //
        leftWing = new FtcServo("leftWing");
        rightWing = new FtcServo("rightWing");
        rightWing.setInverted(true);
        leftWing.setPosition(RobotInfo.WING_LEFT_RETRACT_POSITION);
        rightWing.setPosition(RobotInfo.WING_RIGHT_RETRACT_POSITION);
        //
        // ButtonPusher subsystem.
        //
        leftButtonPusher = new ButtonPusher("leftPusher", true);
        rightButtonPusher = new ButtonPusher("rightPusher", false);
    }   //FtcRobot

    public void startMode(TrcRobot.RunMode runMode)
    {
        FtcOpMode.getOpModeTracer().traceInfo(
                FtcOpMode.getOpModeName(), "Starting: %.3f", HalUtil.getCurrentTime());
        gyro.resetZIntegrator();
        gyro.setEnabled(true);
        sonarSensor.setEnabled(true);
        prevSonarValue = (Double)sonarSensor.getData().value;
        lightSensor.setEnabled(true);
        i2cColorSensor.setLEDEnabled(true);
        driveBase.resetPosition();
    }   //startMode

    public void stopMode(TrcRobot.RunMode runMode)
    {
        FtcOpMode.getOpModeTracer().traceInfo(
                FtcOpMode.getOpModeName(), "Stopping: %.3f", HalUtil.getCurrentTime());
        gyro.setEnabled(false);
        sonarSensor.setEnabled(false);
        lightSensor.setEnabled(false);
        i2cColorSensor.setLEDEnabled(false);
    }   //stopMode

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
        else if (pidCtrl == pidCtrlSonar)
        {
            input = (Double)sonarSensor.getData().value;
            //
            // The Lego Ultrasonic sensor occasionally returns a zero.
            // This is causing havoc to PID control. Let's detect that
            // and discard it and reuse the previous value instead.
            //
            if (input == 0.0)
            {
                input = prevSonarValue;
            }
            else
            {
                prevSonarValue = input;
            }
        }
        else if (pidCtrl == pidCtrlLight)
        {
            input = (Double)lightSensor.getData().value;
            //
            // Give it a deadband to minimize fish tailing.
            //
            if (Math.abs(input - RobotInfo.LIGHT_THRESHOLD) < RobotInfo.LIGHT_DEADBAND)
            {
                input = RobotInfo.LIGHT_THRESHOLD;
            }
        }

        return input;
    }   //getInput

    //
    // Implements TrcAnalogTrigger.TriggerHandler
    //

    @Override
    public void AnalogTriggerEvent(
            TrcAnalogTrigger analogTrigger, TrcAnalogTrigger.Zone zone, double value)
    {
        if (analogTrigger == lightTrigger &&
            zone == TrcAnalogTrigger.Zone.HIGH_ZONE &&
            pidDrive.isEnabled())
        {
            pidDrive.cancel();
        }
    }   //AnalogTriggerEvent

}   //class FtcRobot
