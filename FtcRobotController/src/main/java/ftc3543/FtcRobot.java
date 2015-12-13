package ftc3543;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import ftclib.FtcDcMotor;
import ftclib.FtcMRGyro;
import ftclib.FtcOpMode;
import ftclib.FtcOpticalDistanceSensor;
import ftclib.FtcServo;
import ftclib.FtcUltrasonicSensor;
import trclib.TrcAnalogInput;
import trclib.TrcAnalogTrigger;
import trclib.TrcDriveBase;
import trclib.TrcGyro;
import trclib.TrcPidController;
import trclib.TrcPidDrive;
import trclib.TrcRobot;

public class FtcRobot implements TrcPidController.PidInput,
                                 TrcAnalogTrigger.TriggerHandler
{
    //
    // Sensors.
    //
    public FtcMRGyro mrGyro;
//    public FtcHiTechnicGyro hitechnicGyro;
    public TrcGyro gyro;
//    public FtcAnalogInput maxSonarSensor;
    public FtcUltrasonicSensor legoSonarSensor;
    public TrcAnalogInput sonarSensor;
    public FtcOpticalDistanceSensor lightSensor;
    public ColorSensor colorSensor;
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
//    public TrcEnhancedServo hangingHook;
    //
    // ClimberRelease subsystem.
    //
    public FtcServo leftWing;
    public FtcServo rightWing;
    //
    // ButtonPusher subsystem.
    //
    public FtcServo leftButtonPusher;
    public FtcServo rightButtonPusher;

    public FtcRobot(TrcRobot.RunMode runMode)
    {
        HardwareMap hardwareMap = FtcOpMode.getInstance().hardwareMap;
        hardwareMap.logDevices();
        //
        // Initialize sensors.
        //
        mrGyro = new FtcMRGyro("gyroSensor");
//        hitechnicGyro = new FtcHiTechnicGyro("hitechnicGyro");
        gyro = mrGyro;
//        maxSonarSensor = new FtcAnalogInput("maxSonarSensor");
//        maxSonarSensor.setScale(RobotInfo.SONAR_SCALE_TO_INCHES);
        legoSonarSensor = new FtcUltrasonicSensor("legoSonarSensor");
        legoSonarSensor.setScale(RobotInfo.SONAR_INCHES_PER_CM);
        prevSonarValue = legoSonarSensor.getData().value;
        sonarSensor = legoSonarSensor;
        lightSensor = new FtcOpticalDistanceSensor("lightSensor");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
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
        driveBase.resetPosition();
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
                "lightTrigger", lightSensor, RobotInfo.LINE_THRESHOLD, this);
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
        hookServo.setPosition(RobotInfo.HANGINGHOOK_RETRACT_POSITION);
//        hangingHook = new TrcEnhancedServo("hangingHook", hookServo);

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
        leftButtonPusher = new FtcServo("leftPusher");
        rightButtonPusher = new FtcServo("rightPusher");
        leftButtonPusher.setInverted(true);
        leftButtonPusher.setPosition(RobotInfo.PUSHER_RETRACT_LEFT);
        rightButtonPusher.setPosition(RobotInfo.PUSHER_RETRACT_RIGHT);
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
        else if (pidCtrl == pidCtrlSonar)
        {
            input = sonarSensor.getData().value;
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
            input = lightSensor.getData().value;
        }

        return input;
    }   //getInput

    //
    // Implements TrcAnalogTrigger.TriggerHandler
    //
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
