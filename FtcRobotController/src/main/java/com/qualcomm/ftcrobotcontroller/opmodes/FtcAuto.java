package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.FtcMenu;
import hallib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcRobot;

public class FtcAuto extends FtcOpMode implements FtcMenu.MenuButtons
{
    private HalDashboard dashboard;
    private FtcRobot robot;
    //
    // Strategies.
    //
    private TrcRobot.AutoStrategy autoStrategy = null;
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

    //
    // Implements FtcOpMode abstract methods.
    //

    @Override
    public void robotInit()
    {
        //
        // Initializing global objects.
        //
        dashboard = HalDashboard.getInstance();
        robot = new FtcRobot(TrcRobot.RunMode.AUTO_MODE);
        //
        // Choice menus.
        //
        doMenus();
        //
        // Strategies.
        //
        switch (strategy)
        {
            case STRATEGY_DEFENSE:
                autoStrategy = new AutoDefense(alliance, delay, driveDistance);
                break;

            case STRATEGY_PARK_REPAIR_ZONE:
                autoStrategy = new AutoParkRepairZone(alliance, delay);
                break;

            case STRATEGY_PARK_FLOOR_GOAL:
                autoStrategy = new AutoParkFloorGoal(alliance, delay);
                break;

            case STRATEGY_PARK_MOUNTAIN:
                autoStrategy = new AutoParkMountain(alliance, delay, mountainZone);
                break;

            case STRATEGY_TRIGGER_BEACON:
                autoStrategy = new AutoTriggerBeacon(alliance, delay);
                break;

            default:
                autoStrategy = null;
                break;
        }
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
        if (autoStrategy != null)
        {
            autoStrategy.autoPeriodic();
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
        FtcMenu allianceMenu = new FtcMenu(null, "Alliance:", this);
        FtcMenu delayMenu = new FtcMenu(allianceMenu, "Delay time:", this);
        FtcMenu strategyMenu = new FtcMenu(delayMenu, "Strategies:", this);
        FtcMenu distanceMenu = new FtcMenu(strategyMenu, "Distance:", this);
        FtcMenu mountainZoneMenu = new FtcMenu(strategyMenu, "Mountain zone:", this);

        allianceMenu.addChoice("Red", ALLIANCE_RED, delayMenu);
        allianceMenu.addChoice("Blue", ALLIANCE_BLUE, delayMenu);

        delayMenu.addChoice("1 sec", 1.0, strategyMenu);
        delayMenu.addChoice("2 sec", 2.0, strategyMenu);
        delayMenu.addChoice("4 sec", 4.0, strategyMenu);
        delayMenu.addChoice("8 sec", 8.0, strategyMenu);
        delayMenu.addChoice("10 sec", 10.0, strategyMenu);

        strategyMenu.addChoice("Do nothing", STRATEGY_DO_NOTHING);
        strategyMenu.addChoice("Defense", STRATEGY_DEFENSE, distanceMenu);
        strategyMenu.addChoice("Park repair zone", STRATEGY_PARK_REPAIR_ZONE);
        strategyMenu.addChoice("Park floor goal", STRATEGY_PARK_FLOOR_GOAL);
        strategyMenu.addChoice("Park mountain", STRATEGY_PARK_MOUNTAIN, mountainZoneMenu);
        strategyMenu.addChoice("Trigger beacon", STRATEGY_TRIGGER_BEACON);

        distanceMenu.addChoice("1 ft", 12.0);
        distanceMenu.addChoice("2 ft", 24.0);
        distanceMenu.addChoice("3 ft", 36.0);
        distanceMenu.addChoice("4 ft", 48.0);
        distanceMenu.addChoice("5 ft", 60.0);
        distanceMenu.addChoice("6 ft", 72.0);
        distanceMenu.addChoice("8 ft", 96.0);
        distanceMenu.addChoice("10 ft", 120.0);

        mountainZoneMenu.addChoice("Floor", MOUNTAIN_FLOOR);
        mountainZoneMenu.addChoice("Low zone", MOUNTAIN_LOW_ZONE);
        mountainZoneMenu.addChoice("Mid zone", MOUNTAIN_MID_ZONE);
        mountainZoneMenu.addChoice("High zone", MOUNTAIN_HIGH_ZONE);

        allianceMenu.walkMenuTree();

        alliance = (int)allianceMenu.getSelectedChoiceValue();
        delay = delayMenu.getSelectedChoiceValue();
        strategy = (int)strategyMenu.getSelectedChoiceValue();
        driveDistance = distanceMenu.getSelectedChoiceValue();
        mountainZone = (int)mountainZoneMenu.getSelectedChoiceValue();

        HalDashboard.getInstance().displayPrintf(
                15, "Strategy selected = %s",
                strategyMenu.getSelectedChoiceText());
    }   //doMenus

}   //class FtcAuto
