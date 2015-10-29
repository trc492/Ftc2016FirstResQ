package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.FtcDcMotor;
import hallib.FtcMenu;
import hallib.FtcRobot;
import hallib.HalDashboard;
import hallib.HalPlatform;
import trclib.TrcDriveBase;

public class FtcAuto extends FtcRobot implements FtcMenu.MenuButtons
{
    private HalPlatform platform;
    private HalDashboard dashboard;
    private FtcDcMotor leftFrontWheel;
    private FtcDcMotor rightFrontWheel;
    private FtcDcMotor leftRearWheel;
    private FtcDcMotor rightRearWheel;
    //
    // Subsystems.
    //
    public TrcDriveBase driveBase;
    public Chainsaw chainsaw;
    public Elevator elevator;
    public HangingHook hangingHook;
    public ClimberRelease leftArm;
    public ClimberRelease rightArm;
    public CattleGuard cattleGuard;
    public ButtonPusher buttonPusher;
    //
    // Menus.
    //
    public static final int ALLIANCE_RED = 0;
    public static final int ALLIANCE_BLUE = 1;
    public static final int STRATEGY_NONE = 0;
    public static final int STRATEGY_DEFENSE = 1;

    private FtcMenu allianceMenu;
    public int alliance = ALLIANCE_RED;
    private FtcMenu strategyMenu;
    public int strategy = STRATEGY_NONE;

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
                null,
                null);
        //
        // Chainsaw subsystem.
        //
        chainsaw = new Chainsaw();
        //
        // Elevator subsystem.
        //
        elevator = new Elevator();
        //
        // Hanging Hook subsystem.
        //
        hangingHook = new HangingHook();
        //
        // Climber Release subsystem.
        //
        leftArm = new ClimberRelease("leftArm");
        rightArm = new ClimberRelease("rightArm");
        //
        // Cattle Guard subsystem.
        //
        cattleGuard = new CattleGuard();
        //
        // Button Pusher subsystem.
        //
        buttonPusher = new ButtonPusher();
        //
        // Menus.
        //
        allianceMenu = new FtcMenu("Alliance:", this);
        allianceMenu.addChoice("Red", ALLIANCE_RED);
        allianceMenu.addChoice("Blue", ALLIANCE_BLUE);

        strategyMenu = new FtcMenu("Strategy:", this);
        strategyMenu.addChoice("None", STRATEGY_NONE);
        strategyMenu.addChoice("Defense", STRATEGY_DEFENSE);

        alliance = (int)allianceMenu.getChoiceValue();
        strategy = (int)strategyMenu.getChoiceValue();
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
            case STRATEGY_NONE:
                break;

            case STRATEGY_DEFENSE:
                break;
        }
    }   //runPeriodic

    @Override
    public void runContinuous()
    {
    }   //runContinuous

    //
    // Implements FtcMenu.MenuButtons
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

}   //class FtcAuto
