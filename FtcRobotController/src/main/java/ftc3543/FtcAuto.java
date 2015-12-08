package ftc3543;

import ftclib.FtcMenu;
import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcRobot;

public class FtcAuto extends FtcOpMode implements FtcMenu.MenuButtons
{
    public enum Alliance
    {
        RED_ALLIANCE,
        BLUE_ALLIANCE
    }   //enum Alliance

    public enum StartPosition
    {
        NEAR_MOUNTAIN,
        FAR_CORNER
    }   //enum StartPosition

    public enum BeaconOption
    {
        DO_NOTHING,
        DEFENSE,
        PARK_FLOOR_GOAL
    }   //enum BeaconOption

    public enum Strategy
    {
        DO_NOTHING,
        DEFENSE,
        PARK_REPAIR_ZONE,
        PARK_FLOOR_GOAL,
        PARK_MOUNTAIN,
        TRIGGER_BEACON
    }   //enum Strategy

    public FtcRobot robot;

    private HalDashboard dashboard;
    private TrcRobot.AutoStrategy autoStrategy = null;
    private Alliance alliance = Alliance.RED_ALLIANCE;
    private StartPosition startPos = StartPosition.NEAR_MOUNTAIN;
    private double delay = 0.0;
    private Strategy strategy = Strategy.DO_NOTHING;
    private double driveDistance = 0.0;
    private BeaconOption beaconOption = BeaconOption.DO_NOTHING;

    //
    // Implements FtcOpMode abstract methods.
    //

    @Override
    public void robotInit()
    {
        //
        // Initializing global objects.
        //
        robot = new FtcRobot(TrcRobot.RunMode.AUTO_MODE);
        dashboard = HalDashboard.getInstance();
        //
        // Choice menus.
        //
        doMenus();
        //
        // Strategies.
        //
        switch (strategy)
        {
            case DEFENSE:
                autoStrategy = new AutoDefense(delay, driveDistance);
                break;

            case PARK_REPAIR_ZONE:
                autoStrategy = new AutoParkRepairZone(alliance, startPos, delay);
                break;

            case PARK_FLOOR_GOAL:
                autoStrategy = new AutoParkFloorGoal(alliance, startPos, delay);
                break;

            case PARK_MOUNTAIN:
                autoStrategy = new AutoParkMountain(alliance, startPos, delay);
                break;

            case TRIGGER_BEACON:
                autoStrategy = new AutoTriggerBeacon(alliance, startPos, delay, beaconOption);
                break;

            case DO_NOTHING:
            default:
                autoStrategy = null;
                break;
        }
    }   //robotInit

    @Override
    public void startMode()
    {
        dashboard.clearDisplay();
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
    public boolean isMenuUpButton()
    {
        return gamepad1.dpad_up;
    }   //isMenuUpButton

    @Override
    public boolean isMenuDownButton()
    {
        return gamepad1.dpad_down;
    }   //isMenuDownButton

    @Override
    public boolean isMenuEnterButton()
    {
        return gamepad1.a;
    }   //isMenuEnterButton

    @Override
    public boolean isMenuBackButton()
    {
        return gamepad1.dpad_left;
    }   //isMenuBackButton

    private void doMenus()
    {
        FtcMenu allianceMenu = new FtcMenu("Alliance:", null, this);
        FtcMenu startPosMenu = new FtcMenu("Start position:", allianceMenu, this);
        FtcMenu delayMenu = new FtcMenu("Delay time:", startPosMenu, this);
        FtcMenu strategyMenu = new FtcMenu("Strategies:", delayMenu, this);
        FtcMenu distanceMenu = new FtcMenu("Distance:", strategyMenu, this);
        FtcMenu beaconOptionMenu = new FtcMenu("Beacon options", strategyMenu, this);

        allianceMenu.addChoice("Red", Alliance.RED_ALLIANCE, startPosMenu);
        allianceMenu.addChoice("Blue", Alliance.BLUE_ALLIANCE, startPosMenu);

        startPosMenu.addChoice("Near mountain", StartPosition.NEAR_MOUNTAIN, delayMenu);
        startPosMenu.addChoice("Far corner", StartPosition.FAR_CORNER, delayMenu);

        delayMenu.addChoice("No delay", 0.0, strategyMenu);
        delayMenu.addChoice("1 sec", 1.0, strategyMenu);
        delayMenu.addChoice("2 sec", 2.0, strategyMenu);
        delayMenu.addChoice("4 sec", 4.0, strategyMenu);
        delayMenu.addChoice("8 sec", 8.0, strategyMenu);
        delayMenu.addChoice("10 sec", 10.0, strategyMenu);

        strategyMenu.addChoice("Do nothing", Strategy.DO_NOTHING);
        strategyMenu.addChoice("Defense", Strategy.DEFENSE, distanceMenu);
        strategyMenu.addChoice("Park repair zone", Strategy.PARK_REPAIR_ZONE);
        strategyMenu.addChoice("Park floor goal", Strategy.PARK_FLOOR_GOAL);
        strategyMenu.addChoice("Park mountain", Strategy.PARK_MOUNTAIN);
        strategyMenu.addChoice("Trigger beacon", Strategy.TRIGGER_BEACON, beaconOptionMenu);

        distanceMenu.addChoice("1 ft", 12.0);
        distanceMenu.addChoice("2 ft", 24.0);
        distanceMenu.addChoice("3 ft", 36.0);
        distanceMenu.addChoice("4 ft", 48.0);
        distanceMenu.addChoice("5 ft", 60.0);
        distanceMenu.addChoice("6 ft", 72.0);
        distanceMenu.addChoice("8 ft", 96.0);
        distanceMenu.addChoice("10 ft", 120.0);

        beaconOptionMenu.addChoice("Do nothing", BeaconOption.DO_NOTHING);
        beaconOptionMenu.addChoice("Do defense", BeaconOption.DEFENSE);
        beaconOptionMenu.addChoice("Park floor goal", BeaconOption.PARK_FLOOR_GOAL);

        FtcMenu.walkMenuTree(allianceMenu);

        alliance = (Alliance)allianceMenu.getCurrentChoiceObject();
        startPos = (StartPosition)startPosMenu.getCurrentChoiceObject();
        delay = (Double)delayMenu.getCurrentChoiceObject();
        strategy = (Strategy)strategyMenu.getCurrentChoiceObject();
        driveDistance = (Double)distanceMenu.getCurrentChoiceObject();
        beaconOption = (BeaconOption)beaconOptionMenu.getCurrentChoiceObject();

        dashboard.displayPrintf(0, "Auto Strategy: %s (%s)",
                                strategyMenu.getCurrentChoiceText(),
                                alliance == Alliance.RED_ALLIANCE? "Red": "Blue");
        dashboard.displayPrintf(2, "Start position: %s",
                                startPos == StartPosition.NEAR_MOUNTAIN? "Mountain": "Corner");
        dashboard.displayPrintf(3, "Delay = %.0f sec", delay);
    }   //doMenus

}   //class FtcAuto
