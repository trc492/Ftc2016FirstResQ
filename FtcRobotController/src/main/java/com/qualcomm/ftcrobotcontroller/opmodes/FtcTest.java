package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import hallib.FtcDcMotor;
import hallib.FtcGamepad;
import hallib.FtcGyro;
import hallib.FtcMenu;
import hallib.FtcRobot;
import hallib.HalDashboard;
import hallib.HalPlatform;
import hallib.HalSpeedController;
import trclib.TrcDriveBase;
import trclib.TrcEvent;
import trclib.TrcMotorPosition;
import trclib.TrcPidController;
import trclib.TrcPidDrive;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class FtcTest extends FtcRobot implements FtcMenu.MenuButtons,
                                                 TrcPidController.PidInput,
                                                 TrcMotorPosition
{
    private HalPlatform platform;
    private HalDashboard dashboard;
    private FtcGamepad driverGamepad;
    private FtcGyro gyro;
    private ColorSensor colorSensor;
    private OpticalDistanceSensor lightSensor;
    private TouchSensor touchSensor;
    private FtcDcMotor leftFrontWheel;
    private FtcDcMotor rightFrontWheel;
    private FtcDcMotor leftRearWheel;
    private FtcDcMotor rightRearWheel;
    //
    // DriveBase subsystem.
    //
    private TrcDriveBase driveBase;
    private TrcPidController pidCtrlDrive;
    private TrcPidController pidCtrlTurn;
    private TrcPidDrive pidDrive;
    //
    // Miscellaneous.
    //
    private TrcStateMachine sm;
    private TrcTimer timer;
    private TrcEvent event;
    //
    // Choice menus.
    //
    private static final int TEST_SENSORS           = 0;
    private static final int TEST_DRIVE_TIME        = 1;
    private static final int TEST_DRIVE_DISTANCE    = 2;
    private static final int TEST_TURN_DEGREES      = 3;
    private static final int TEST_LINE_FOLLOWING    = 4;

    private int testChoice = TEST_SENSORS;
    private double driveTime = 0.0;
    private double driveDistance = 0.0;
    private double turnDegrees = 0.0;

    @Override
    public void robotInit()
    {
        //
        // Initializing global objects.
        //
        hardwareMap.logDevices();
        platform = new HalPlatform(this);
        dashboard = HalDashboard.getInstance();
        //
        // Initialize input subsystems.
        //
        driverGamepad = new FtcGamepad("DriverGamepad", gamepad1, null);
        gyro = new FtcGyro("gyroSensor");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        lightSensor = hardwareMap.opticalDistanceSensor.get("lightSensor");
        touchSensor = hardwareMap.touchSensor.get("touchSensor");
        //
        // DriveBase subsystem.
        //
        leftFrontWheel = new FtcDcMotor("leftFrontWheel");
        rightFrontWheel = new FtcDcMotor("rightFrontWheel");
        leftRearWheel = new FtcDcMotor("leftRearWheel");
        rightRearWheel = new FtcDcMotor("rightRearWheel");
        leftFrontWheel.setInverted(true);
        leftRearWheel.setInverted(true);
        //
        // DriveBase subsystem.
        //
        driveBase = new TrcDriveBase(
                leftFrontWheel,
                leftRearWheel,
                rightFrontWheel,
                rightRearWheel,
                this,
                gyro);
        pidCtrlDrive = new TrcPidController(
                "DrivePid",
                RobotInfo.DRIVE_KP, RobotInfo.DRIVE_KI, RobotInfo.DRIVE_KD,
                RobotInfo.DRIVE_KF, RobotInfo.DRIVE_TOLERANCE, RobotInfo.DRIVE_SETTLING,
                this, 0);
        pidCtrlTurn = new TrcPidController(
                "TurnPid",
                RobotInfo.TURN_KP, RobotInfo.TURN_KI, RobotInfo.TURN_KD,
                RobotInfo.TURN_KF, RobotInfo.TURN_TOLERANCE, RobotInfo.TURN_SETTLING,
                this, 0);
        pidDrive = new TrcPidDrive("PidDrive", driveBase, null, pidCtrlDrive, pidCtrlTurn);
        //
        // Miscellaneous.
        //
        sm = new TrcStateMachine("TestSM");
        timer = new TrcTimer("TestTimer");
        event = new TrcEvent("TestEvent");
        //
        // Choice menus.
        //
        doMenus();
    }   //robotInit

    @Override
    public void startMode()
    {
    }   //startMode

    @Override
    public void stopMode()
    {
    }   //stopMode

    @Override
    public void runPeriodic()
    {
        switch (testChoice)
        {
            case TEST_SENSORS:
                doTestSensors();
                break;

            case TEST_DRIVE_TIME:
                doDriveTime(driveTime);
                break;

            case TEST_DRIVE_DISTANCE:
                doDriveDistance(driveDistance);
                break;

            case TEST_TURN_DEGREES:
                doTurnDegrees(turnDegrees);
                break;

            case TEST_LINE_FOLLOWING:
                doLineFollowing();
                break;
        }
    }   //runPeriodic

    @Override
    public void runContinuous()
    {
    }   //runContinuous

    //
    // Implements MenuButtons
    //

    @Override
    public boolean isMenuUp()
    {
        return gamepad1.dpad_up;
    }   //isMenuUp

    @Override
    public boolean isMenuDown()
    {
        return gamepad1.dpad_down;
    }   //isMenuDown

    @Override
    public boolean isMenuOk()
    {
        return gamepad1.a;
    }   //isMenuOk

    @Override
    public boolean isMenuCancel()
    {
        return gamepad1.b;
    }   //isMenuCancel

    private void doMenus()
    {
        FtcMenu testMenu = new FtcMenu("Tests:", this);
        testMenu.addChoice("Test sensors", TEST_SENSORS);
        testMenu.addChoice("Timed Drive", TEST_DRIVE_TIME);
        testMenu.addChoice("Drive forward 8 ft", TEST_DRIVE_DISTANCE);
        testMenu.addChoice("Turn right 360 deg", TEST_TURN_DEGREES);
        testMenu.addChoice("Line following", TEST_LINE_FOLLOWING);

        FtcMenu driveTimeMenu = new FtcMenu("Drive time:", this);
        driveTimeMenu.addChoice("1 sec", 1.0);
        driveTimeMenu.addChoice("2 sec", 2.0);
        driveTimeMenu.addChoice("4 sec", 4.0);
        driveTimeMenu.addChoice("8 sec", 8.0);

        FtcMenu driveDistanceMenu = new FtcMenu("Drive distance:", this);
        driveDistanceMenu.addChoice("2 ft", 24.0);
        driveDistanceMenu.addChoice("4 ft", 48.0);
        driveDistanceMenu.addChoice("8 ft", 96.0);
        driveDistanceMenu.addChoice("10 ft", 120.0);

        FtcMenu turnDegreesMenu = new FtcMenu("Turn degrees:", this);
        turnDegreesMenu.addChoice("-90 degrees", -90.0);
        turnDegreesMenu.addChoice("-180 degrees", -180.0);
        turnDegreesMenu.addChoice("-360 degrees", -360.0);
        turnDegreesMenu.addChoice("90 degrees", 90.0);
        turnDegreesMenu.addChoice("180 degrees", 180.0);
        turnDegreesMenu.addChoice("360 degrees", 360.0);

        boolean done = false;
        while (!done)
        {
            if (testMenu.getChoice() != -1)
            {
                testChoice = (int)testMenu.getSelectedChoiceValue();
                switch (testChoice)
                {
                    case TEST_SENSORS:
                        done = true;
                        break;

                    case TEST_DRIVE_TIME:
                        driveTime = driveTimeMenu.getChoiceValue();
                        if (driveTime != -1.0)
                        {
                            sm.start();
                            done = true;
                        }
                        break;

                    case TEST_DRIVE_DISTANCE:
                        driveDistance = driveDistanceMenu.getChoiceValue();
                        if (driveDistance != -1.0)
                        {
                            sm.start();
                            done = true;
                        }
                        break;

                    case TEST_TURN_DEGREES:
                        turnDegrees = turnDegreesMenu.getChoiceValue();
                        if (driveDistance != -1.0)
                        {
                            sm.start();
                            done = true;
                        }
                        break;

                    case TEST_LINE_FOLLOWING:
                        sm.start();
                        done = true;
                        break;
                }
            }
        }
        HalDashboard.getInstance().displayPrintf(15, "Test selected = %s",
                                                 testMenu.getSelectedChoiceText());
    }   //doMenus

    private void doTestSensors()
    {
        dashboard.displayPrintf(1, "Calibrating sensors:");
        double leftPower  = driverGamepad.getLeftStickY(true);
        double rightPower = driverGamepad.getRightStickY(true);
        driveBase.tankDrive(leftPower, rightPower);
        dashboard.displayPrintf(2, "Gyro = %f", gyro.getAngle());
        dashboard.displayPrintf(3, "Color = [R:%d,G:%d,B:%d]",
                                colorSensor.red(), colorSensor.green(), colorSensor.blue());
        dashboard.displayPrintf(4, "RawLightValue = %d", lightSensor.getLightDetectedRaw());
        dashboard.displayPrintf(5, "Touch = %s", touchSensor.isPressed()? "pressed": "released");
    }   //doTestSensors

    private void doDriveTime(double time)
    {
        dashboard.displayPrintf(1, "Drive %.1f sec", time);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    driveBase.tankDrive(0.5, 0.5);
                    timer.set(time, event);
                    sm.addEvent(event);
                    sm.waitForEvents(TrcStateMachine.STATE_STARTED + 1);
                    break;

                default:
                case TrcStateMachine.STATE_STARTED + 1:
                    driveBase.stop();
                    sm.stop();
                    break;
            }
        }
    }   //doDriveTime

    private void doDriveDistance(double distance)
    {
        dashboard.displayPrintf(1, "Drive %.1f ft", distance/12.0);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    pidDrive.setTarget(distance, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(TrcStateMachine.STATE_STARTED + 1);
                    break;

                default:
                case TrcStateMachine.STATE_STARTED + 1:
                    sm.stop();
                    break;
            }
        }
    }   //doDriveDistance

    private void doTurnDegrees(double degrees)
    {
        dashboard.displayPrintf(1, "Turn %.1f degrees", degrees);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    pidDrive.setTarget(0.0, degrees, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(TrcStateMachine.STATE_STARTED + 1);
                    break;

                default:
                case TrcStateMachine.STATE_STARTED + 1:
                    sm.stop();
                    break;
            }
        }
    }   //doTurnDegrees

    private void doLineFollowing()
    {

    }   //doLineFollowing

    //
    // Implements TrcPidController.PidInput
    //

    @Override
    public double getInput(TrcPidController pidCtrl)
    {
        double input = 0.0;

        if (pidCtrl == pidCtrlDrive)
        {
            input = driveBase.getYPosition()*RobotInfo.DRIVE_INCHES_PER_CLICK;
        }
        else if (pidCtrl == pidCtrlTurn)
        {
            input = driveBase.getHeading();
        }

        return input;
    }   //getInput

    //
    // Implements TrcMotorPosition
    //
    @Override
    public double getMotorPosition(HalSpeedController speedController)
    {
        return speedController.getCurrentPosition();
    }   //getMotorPosition

    @Override
    public double getMotorSpeed(HalSpeedController speedController)
    {
        return 0.0;
    }   //getMotorSpeed

    @Override
    public void resetMotorPosition(HalSpeedController speedController)
    {
        speedController.resetCurrentPosition();
    }   //resetMotorPosition

    @Override
    public void reversePositionSensor(HalSpeedController speedController, boolean flip)
    {
    }   //reversePositionSensor

    @Override
    public boolean isForwardLimitSwitchActive(HalSpeedController speedController)
    {
        return false;
    }   //isForwardLimitSwitchActive

    @Override
    public boolean isReverseLimitSwitchActive(HalSpeedController speedController)
    {
        return false;
    }   //isReverseLimitSwitchActive

}   //class FtcTest
