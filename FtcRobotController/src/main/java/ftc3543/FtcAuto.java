package ftc3543;

import ftclib.FtcChoiceMenu;
import ftclib.FtcMenu;
import ftclib.FtcOpMode;
import ftclib.FtcValueMenu;
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
        PARK_FLOOR_GOAL,
        PARK_MOUNTAIN
    }   //enum BeaconOption

    public enum Strategy
    {
        DO_NOTHING,
        DEFENSE,
        PARK_FLOOR_GOAL,
        PARK_MOUNTAIN,
        BEACON
    }   //enum Strategy

    private HalDashboard dashboard;
    private Robot robot = null;
    private TrcRobot.AutoStrategy autoStrategy = null;
    private Alliance alliance = Alliance.RED_ALLIANCE;
    private StartPosition startPos = StartPosition.NEAR_MOUNTAIN;
    private double delay = 0.0;
    private Strategy strategy = Strategy.DO_NOTHING;
    private double driveDistance = 0.0;
    private boolean pushButton = true;
    private boolean depositClimbers = false;
    private BeaconOption beaconOption = BeaconOption.DO_NOTHING;

    //
    // Implements FtcOpMode abstract method.
    //

    @Override
    public void initRobot()
    {
        //
        // Initializing global objects.
        //
        dashboard = HalDashboard.getInstance();
        robot = new Robot(TrcRobot.RunMode.AUTO_MODE);
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
                autoStrategy = new AutoDefense(robot, delay, driveDistance);
                break;

            case PARK_FLOOR_GOAL:
                autoStrategy = new AutoParkFloorGoal(robot, alliance, startPos, delay);
                break;

            case PARK_MOUNTAIN:
                autoStrategy = new AutoParkMountain(robot, alliance, startPos, delay);
                break;

            case BEACON:
                autoStrategy = new AutoBeacon(
                        robot, alliance, startPos, delay,
                        pushButton, depositClimbers, beaconOption);
                break;

            case DO_NOTHING:
            default:
                autoStrategy = null;
                break;
        }

        getOpModeTracer().traceInfo(
                getOpModeName(),
                "Strategy: %s(alliance=%s, startPos=%s, delay=%.0f, pushButton=%s, depositClimbers=%s, beaconOption=%s",
                strategy.toString(), alliance.toString(), startPos.toString(), delay,
                Boolean.toString(pushButton), Boolean.toString(depositClimbers),
                beaconOption.toString());
    }   //initRobot

    //
    // Overrides TrcRobot.RobotMode methods.
    //

    @Override
    public void startMode()
    {
        dashboard.clearDisplay();
        robot.startMode(TrcRobot.RunMode.AUTO_MODE);
    }   //startMode

    @Override
    public void stopMode()
    {
        robot.stopMode(TrcRobot.RunMode.AUTO_MODE);
    }   //stopMode

    @Override
    public void runContinuous(double elapsedTime)
    {
        if (autoStrategy != null)
        {
            autoStrategy.autoPeriodic(elapsedTime);
        }
    }   //runContinuous

    //
    // Implements FtcMenu.MenuButtons interface.
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
        FtcChoiceMenu allianceMenu = new FtcChoiceMenu("Alliance:", null, this);
        FtcChoiceMenu startPosMenu = new FtcChoiceMenu("Start position:", allianceMenu, this);
        FtcValueMenu delayMenu = new FtcValueMenu("Delay time:", startPosMenu, this,
                                                  0.0, 15.0, 1.0, 0.0, " %.0f sec");
        FtcChoiceMenu strategyMenu = new FtcChoiceMenu("Strategies:", delayMenu, this);
        FtcValueMenu distanceMenu = new FtcValueMenu("Distance:", strategyMenu, this,
                                                     1.0, 10.0, 1.0, 1.0, " %.0f ft");
        FtcChoiceMenu beaconButtonMenu =
                new FtcChoiceMenu("Push beacon button:", strategyMenu, this);
        FtcChoiceMenu depositClimbersMenu =
                new FtcChoiceMenu("Deposit climbers:", beaconButtonMenu, this);
        FtcChoiceMenu beaconOptionMenu =
                new FtcChoiceMenu("Beacon options", depositClimbersMenu, this);

        allianceMenu.addChoice("Red", Alliance.RED_ALLIANCE, startPosMenu);
        allianceMenu.addChoice("Blue", Alliance.BLUE_ALLIANCE, startPosMenu);

        startPosMenu.addChoice("Near mountain", StartPosition.NEAR_MOUNTAIN, delayMenu);
        startPosMenu.addChoice("Far corner", StartPosition.FAR_CORNER, delayMenu);

        delayMenu.setChildMenu(strategyMenu);

        strategyMenu.addChoice("Do nothing", Strategy.DO_NOTHING);
        strategyMenu.addChoice("Defense", Strategy.DEFENSE, distanceMenu);
        strategyMenu.addChoice("Park floor goal", Strategy.PARK_FLOOR_GOAL);
        strategyMenu.addChoice("Park mountain", Strategy.PARK_MOUNTAIN);
        strategyMenu.addChoice("Beacon", Strategy.BEACON, beaconButtonMenu);

        beaconButtonMenu.addChoice("Yes", true, depositClimbersMenu);
        beaconButtonMenu.addChoice("No", false, depositClimbersMenu);

        depositClimbersMenu.addChoice("Yes", true, beaconOptionMenu);
        depositClimbersMenu.addChoice("No", false, beaconOptionMenu);

        beaconOptionMenu.addChoice("Do nothing", BeaconOption.DO_NOTHING);
        beaconOptionMenu.addChoice("Do defense", BeaconOption.DEFENSE);
        beaconOptionMenu.addChoice("Park floor goal", BeaconOption.PARK_FLOOR_GOAL);
        beaconOptionMenu.addChoice("Park mountain", BeaconOption.PARK_MOUNTAIN);

        FtcMenu.walkMenuTree(allianceMenu);

        alliance = (Alliance)allianceMenu.getCurrentChoiceObject();
        startPos = (StartPosition)startPosMenu.getCurrentChoiceObject();
        delay = delayMenu.getCurrentValue();
        strategy = (Strategy)strategyMenu.getCurrentChoiceObject();
        driveDistance = distanceMenu.getCurrentValue();
        pushButton = (Boolean)beaconButtonMenu.getCurrentChoiceObject();
        depositClimbers = (Boolean)depositClimbersMenu.getCurrentChoiceObject();
        beaconOption = (BeaconOption)beaconOptionMenu.getCurrentChoiceObject();

        dashboard.displayPrintf(0, "Auto Strategy: %s (%s)",
                                strategyMenu.getCurrentChoiceText(), alliance.toString());
        dashboard.displayPrintf(1, "Start position: %s", startPos.toString());
        dashboard.displayPrintf(2, "Delay = %.0f sec", delay);
        dashboard.displayPrintf(3, "Defense: distance=%.0f ft", driveDistance);
        dashboard.displayPrintf(4, "Beacon: PushButton=%s,DepositClimber=%s,Option=%s",
                                Boolean.toString(pushButton), Boolean.toString(depositClimbers),
                                beaconOption.toString());
    }   //doMenus

}   //class FtcAuto
