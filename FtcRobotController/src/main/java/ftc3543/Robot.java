package ftc3543;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import ftclib.FtcDcMotor;
import ftclib.FtcMRI2cColorSensor;
import ftclib.FtcMRGyro;
import ftclib.FtcOpMode;
import ftclib.FtcServo;
import ftclib.FtcUltrasonicSensor;
import hallib.HalUtil;
import trclib.TrcAnalogTrigger;
import trclib.TrcDriveBase;
import trclib.TrcPidController;
import trclib.TrcPidDrive;
import trclib.TrcRobot;

public class Robot implements TrcPidController.PidInput, TrcAnalogTrigger.TriggerHandler
{
    //
    // Sensors.
    //
    public FtcMRGyro gyro;
    public ColorSensor beaconColorSensor;
    public FtcMRI2cColorSensor lineFollowColorSensor;
    public FtcUltrasonicSensor sonarSensor;
    public double prevSonarValue;

    //
    // DriveBase subsystem.
    //
    public FtcDcMotor leftFrontWheel;
    public FtcDcMotor rightFrontWheel;
    public FtcDcMotor leftRearWheel;
    public FtcDcMotor rightRearWheel;
    public TrcDriveBase driveBase;

    public TrcPidController encoderPidCtrl;
    public TrcPidController gyroPidCtrl;
    public TrcPidDrive pidDrive;

    public TrcPidController sonarPidCtrl;
    public TrcPidController colorPidCtrl;
    public TrcPidDrive pidLineFollow;
    public TrcAnalogTrigger colorTrigger;

    //
    // Winch subsystem.
    //
    public Winch winch;

    //
    // Climber Depositor subsystem.
    //
    public FtcServo climberDepositor;

    //
    // Climber Release subsystem.
    //
    public FtcServo leftWing;
    public FtcServo rightWing;

    //
    // ButtonPusher subsystem.
    //
    public ButtonPusher leftButtonPusher;
    public ButtonPusher rightButtonPusher;

    public Robot(TrcRobot.RunMode runMode)
    {
        HardwareMap hardwareMap = FtcOpMode.getInstance().hardwareMap;
        hardwareMap.logDevices();
        //
        // Initialize sensors.
        //
        gyro = new FtcMRGyro("gyroSensor");
        gyro.calibrate();
        beaconColorSensor = hardwareMap.colorSensor.get("colorSensor");
        beaconColorSensor.enableLed(false);
        lineFollowColorSensor = new FtcMRI2cColorSensor("i2cColorSensor", 0x40);
        lineFollowColorSensor.setLEDEnabled(false);
        sonarSensor = new FtcUltrasonicSensor("legoSonarSensor");
        sonarSensor.setScale(RobotInfo.SONAR_INCHES_PER_CM);
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
        encoderPidCtrl = new TrcPidController(
                "encoderPidCtrl",
                RobotInfo.DRIVE_KP, RobotInfo.DRIVE_KI,
                RobotInfo.DRIVE_KD, RobotInfo.DRIVE_KF,
                RobotInfo.DRIVE_TOLERANCE, RobotInfo.DRIVE_SETTLING,
                this);
        gyroPidCtrl = new TrcPidController(
                "gyroPidCtrl",
                RobotInfo.GYRO_KP, RobotInfo.GYRO_KI,
                RobotInfo.GYRO_KD, RobotInfo.GYRO_KF,
                RobotInfo.GYRO_TOLERANCE, RobotInfo.GYRO_SETTLING,
                this);
        pidDrive = new TrcPidDrive("pidDrive", driveBase, null, encoderPidCtrl, gyroPidCtrl);
        //
        // PID Line following.
        //
        sonarPidCtrl = new TrcPidController(
                "sonarPidCtrl",
                RobotInfo.SONAR_KP, RobotInfo.SONAR_KI,
                RobotInfo.SONAR_KD, RobotInfo.SONAR_KF,
                RobotInfo.SONAR_TOLERANCE, RobotInfo.SONAR_SETTLING,
                this);
        sonarPidCtrl.setAbsoluteSetPoint(true);
        sonarPidCtrl.setInverted(true);
        colorPidCtrl = new TrcPidController(
                "colorPidCtrl",
                RobotInfo.COLOR_KP, RobotInfo.COLOR_KI,
                RobotInfo.COLOR_KD, RobotInfo.COLOR_KF,
                RobotInfo.COLOR_TOLERANCE, RobotInfo.COLOR_SETTLING,
                this);
        colorPidCtrl.setAbsoluteSetPoint(true);
        pidLineFollow = new TrcPidDrive(
                "pidLineFollow", driveBase, null, sonarPidCtrl, colorPidCtrl);
        colorTrigger = new TrcAnalogTrigger(
                "colorTrigger", lineFollowColorSensor, 0,
                RobotInfo.COLOR_RED_THRESHOLD,
                RobotInfo.COLOR_WHITE_THRESHOLD, this);
        //
        // Winch subsystem.
        //
        winch = new Winch();
        winch.zeroCalibrate();
        //
        // Climber Depositor subsystem.
        //
        climberDepositor = new FtcServo("depositorServo");
        climberDepositor.setInverted(true);
        climberDepositor.setPosition(RobotInfo.DEPOSITOR_RETRACT_POSITION);
        //
        // Climber Release subsystem.
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
        leftButtonPusher.stop();
        rightButtonPusher.stop();
    }   //Robot

    public void startMode(TrcRobot.RunMode runMode)
    {
        FtcOpMode.getOpModeTracer().traceInfo(
                FtcOpMode.getOpModeName(), "Starting: %.3f", HalUtil.getCurrentTime());
        gyro.resetZIntegrator();
        gyro.setEnabled(true);
        lineFollowColorSensor.setLEDEnabled(true);
        sonarSensor.setEnabled(true);
        prevSonarValue = (Double)sonarSensor.getData(0).value;
        driveBase.resetPosition();
    }   //startMode

    public void stopMode(TrcRobot.RunMode runMode)
    {
        FtcOpMode.getOpModeTracer().traceInfo(
                FtcOpMode.getOpModeName(), "Stopping: %.3f", HalUtil.getCurrentTime());
        gyro.setEnabled(false);
        lineFollowColorSensor.setLEDEnabled(false);
        sonarSensor.setEnabled(false);
    }   //stopMode

    //
    // Implements TrcPidController.PidInput
    //

    @Override
    public double getInput(TrcPidController pidCtrl)
    {
        double input = 0.0;

        if (pidCtrl == encoderPidCtrl)
        {
            input = driveBase.getYPosition();
        }
        else if (pidCtrl == gyroPidCtrl)
        {
            input = driveBase.getHeading();
        }
        else if (pidCtrl == sonarPidCtrl)
        {
            input = (Double)sonarSensor.getData(0).value;
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
        else if (pidCtrl == colorPidCtrl)
        {
            input = (double)(Integer)lineFollowColorSensor.getWhiteValue().value;
            //
            // Give it a deadband to minimize fish tailing.
            //
            if (Math.abs(input - RobotInfo.COLOR_LINE_EDGE_LEVEL) < RobotInfo.COLOR_LINE_EDGE_DEADBAND)
            {
                input = RobotInfo.COLOR_LINE_EDGE_LEVEL;
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
        if (analogTrigger == colorTrigger &&
            zone == TrcAnalogTrigger.Zone.HIGH_ZONE &&
            pidDrive.isEnabled())
        {
            pidDrive.cancel();
        }
    }   //AnalogTriggerEvent

}   //class Robot