package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import hallib.FtcDcMotor;
import hallib.FtcGyro;
import hallib.FtcMenu;
import hallib.FtcRobot;
import hallib.HalDashboard;
import hallib.HalSpeedController;
import trclib.TrcDriveBase;
import trclib.TrcMotorPosition;
import trclib.TrcPidController;
import trclib.TrcPidDrive;
import trclib.TrcRobot;
import trclib.TrcStateMachine;

public class FtcAuto extends FtcRobot implements FtcMenu.MenuButtons,
                                                 TrcPidController.PidInput,
                                                 TrcMotorPosition
{
    private HalDashboard dashboard;

    private FtcGyro gyro;
    private ColorSensor colorSensor;
    private OpticalDistanceSensor lightSensor;
    private TouchSensor touchSensor;
    private UltrasonicSensor sonarSensor;

    private FtcDcMotor leftFrontWheel;
    private FtcDcMotor rightFrontWheel;
    private FtcDcMotor leftRearWheel;
    private FtcDcMotor rightRearWheel;
    //
    // DriveBase ubsystems.
    //
    private TrcDriveBase driveBase;
    private TrcPidController pidCtrlDrive;
    private TrcPidController pidCtrlTurn;
    public TrcPidDrive pidDrive;
    //
    // Chainsaw subsystem.
    //
    public Chainsaw chainsaw;
    //
    // Elevator subsystem.
    //
    public Elevator elevator;
    //
    // HangingHook subsystem.
    //
    public HangingHook hangingHook;
    //
    // Mscellaneous.
    //
    public TrcStateMachine sm;
    //
    // Strategies.
    //
    private TrcRobot.AutoStrategy defense = null;
    private TrcRobot.AutoStrategy parkRepairZone = null;
    private TrcRobot.AutoStrategy parkFloorGoal = null;
    private TrcRobot.AutoStrategy parkMoutain = null;
    private TrcRobot.AutoStrategy triggerBeacon = null;
    //
    // Alliance menu.
    //
    public static final int ALLIANCE_RED                = 0;
    public static final int ALLIANCE_BLUE               = 1;
    //
    // Strategies menu.
    //
    private static final int STRATEGY_DO_NOTHING        = 0;
    private static final int STRATEGY_DEFENSE           = 1;
    private static final int STRATEGY_PARK_REPAIR_ZONE  = 2;
    private static final int STRATEGY_PARK_FLOOR_GOAL   = 3;
    private static final int STRATEGY_PARK_MOUNTAIN     = 4;
    private static final int STRATEGY_TRIGGER_BEACON    = 5;

    public static final int MOUNTAIN_FLOOR              = 0;
    public static final int MOUNTAIN_LOW_ZONE           = 1;
    public static final int MOUNTAIN_MID_ZONE           = 2;
    public static final int MOUNTAIN_HIGH_ZONE          = 3;

    private int alliance = ALLIANCE_RED;
    private int strategy = STRATEGY_DO_NOTHING;
    private double delay = 0.0;
    private double driveDistance = 0.0;
    private int mountainZone = MOUNTAIN_FLOOR;

    private void doMenus()
    {
        FtcMenu allianceMenu = new FtcMenu("Alliance:", this);
        allianceMenu.addChoice("Red", ALLIANCE_RED);
        allianceMenu.addChoice("Blue", ALLIANCE_BLUE);

        FtcMenu delayMenu = new FtcMenu("Delay time:", this);
        delayMenu.addChoice("1 sec", 1.0);
        delayMenu.addChoice("2 sec", 2.0);
        delayMenu.addChoice("4 sec", 4.0);
        delayMenu.addChoice("8 sec", 8.0);
        delayMenu.addChoice("10 sec", 10.0);

        FtcMenu strategyMenu = new FtcMenu("Strategies:", this);
        strategyMenu.addChoice("Do nothing", STRATEGY_DO_NOTHING);
        strategyMenu.addChoice("Defense", STRATEGY_DEFENSE);
        strategyMenu.addChoice("Park repair zone", STRATEGY_PARK_REPAIR_ZONE);
        strategyMenu.addChoice("Park floor goal", STRATEGY_PARK_FLOOR_GOAL);
        strategyMenu.addChoice("Park mountain", STRATEGY_PARK_MOUNTAIN);
        strategyMenu.addChoice("Trigger beacon", STRATEGY_TRIGGER_BEACON);

        FtcMenu distanceMenu = new FtcMenu("Distance:", this);
        distanceMenu.addChoice("1 ft", 12.0);
        distanceMenu.addChoice("2 ft", 24.0);
        distanceMenu.addChoice("3 ft", 36.0);
        distanceMenu.addChoice("4 ft", 48.0);
        distanceMenu.addChoice("5 ft", 60.0);
        distanceMenu.addChoice("6 ft", 72.0);
        distanceMenu.addChoice("8 ft", 96.0);
        distanceMenu.addChoice("10 ft", 120.0);

        FtcMenu mountainZoneMenu = new FtcMenu("Mountain zone:", this);
        mountainZoneMenu.addChoice("Floor", MOUNTAIN_FLOOR);
        mountainZoneMenu.addChoice("Low zone", MOUNTAIN_LOW_ZONE);
        mountainZoneMenu.addChoice("Mid zone", MOUNTAIN_MID_ZONE);
        mountainZoneMenu.addChoice("High zone", MOUNTAIN_HIGH_ZONE);

        do
        {
            alliance = (int)allianceMenu.getChoiceValue();
        } while (alliance == -1);

        do
        {
            delay = delayMenu.getChoiceValue();
        } while (delay == -1.0);

        boolean done = false;
        while (!done)
        {
            if (strategyMenu.getChoice() != -1)
            {
                strategy = (int)strategyMenu.getSelectedChoiceValue();
                switch (strategy)
                {
                    case STRATEGY_DO_NOTHING:
                    case STRATEGY_PARK_REPAIR_ZONE:
                    case STRATEGY_PARK_FLOOR_GOAL:
                        done = true;
                        break;

                    case STRATEGY_DEFENSE:
                        driveDistance = distanceMenu.getChoiceValue();
                        if (driveDistance != -1.0)
                        {
                            sm.start();
                            done = true;
                        }
                        break;

                    case STRATEGY_PARK_MOUNTAIN:
                        mountainZone = (int)mountainZoneMenu.getChoiceValue();
                        if (mountainZone != -1.0)
                        {
                            sm.start();
                            done = true;
                        }
                        break;

                    case STRATEGY_TRIGGER_BEACON:
                        //???
                        sm.start();
                        done = true;
                        break;
                }
            }
        }
        HalDashboard.getInstance().displayPrintf(
                15, "Strategy selected = %s",
                strategyMenu.getSelectedChoiceText());
    }   //doMenus

    @Override
    public void robotInit()
    {
        //
        // Initializing global objects.
        //
        hardwareMap.logDevices();
        dashboard = HalDashboard.getInstance();
        //
        // Initialize input subsystems.
        //
        gyro = new FtcGyro("gyroSensor");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        lightSensor = hardwareMap.opticalDistanceSensor.get("lightSensor");
        touchSensor = hardwareMap.touchSensor.get("touchSensor");
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
        // Chainsaw subsystem.
        //
        chainsaw = new Chainsaw();
        //
        // Elevator subsystem.
        //
        elevator = new Elevator();
        elevator.reverseEncoder(true);
        elevator.zeroCalibrate(RobotInfo.ELEVATOR_CAL_POWER);
        //
        // Hanging Hook subsystem.
        //
        hangingHook = new HangingHook();
        //
        // Miscellaneous.
        //
        sm = new TrcStateMachine("TestSM");
        //
        // Choice menus.
        //
        doMenus();
        //
        // Strategies.
        //
        defense = new AutoDefense(alliance, delay, driveDistance);
        parkRepairZone = new AutoParkRepairZone(alliance, delay);
        parkFloorGoal = new AutoParkFloorGoal(alliance, delay);
        parkMoutain = new AutoParkMountain(alliance, delay, mountainZone);
        triggerBeacon = new AutoTriggerBeacon(alliance, delay);

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
        switch (strategy)
        {
            case STRATEGY_DO_NOTHING:
                break;

            case STRATEGY_DEFENSE:
                defense.autoPeriodic();
                break;

            case STRATEGY_PARK_REPAIR_ZONE:
                parkRepairZone.autoPeriodic();
                break;

            case STRATEGY_PARK_FLOOR_GOAL:
                parkFloorGoal.autoPeriodic();
                break;

            case STRATEGY_PARK_MOUNTAIN:
                parkMoutain.autoPeriodic();
                break;

            case STRATEGY_TRIGGER_BEACON:
                triggerBeacon.autoPeriodic();
                break;        }
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

}   //class FtcAuto
