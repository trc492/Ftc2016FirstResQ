package ftc3543;

import ftclib.FtcMenu;
import trclib.TrcEvent;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class FtcTest extends FtcTeleOp implements FtcMenu.MenuButtons
{
    //
    // Miscellaneous.
    //
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;
    //
    // Test menu.
    //
    private static final int TEST_SENSORS           = 0;
    private static final int TEST_DRIVE_TIME        = 1;
    private static final int TEST_DRIVE_DISTANCE    = 2;
    private static final int TEST_TURN_DEGREES      = 3;
    private static final int TEST_LINE_FOLLOWING    = 4;

    private static final int ALLIANCE_RED           = 0;
    private static final int ALLIANCE_BLUE          = 1;

    private int testChoice = TEST_SENSORS;
    private double driveTime = 0.0;
    private double driveDistance = 0.0;
    private double turnDegrees = 0.0;
    private int alliance = ALLIANCE_RED;

    //
    // Implements FtcOpMode abstract methods.
    //

    @Override
    public void robotInit()
    {
        super.robotInit();
        //
        // Miscellaneous.
        //
        event = new TrcEvent("TestEvent");
        timer = new TrcTimer("TestTimer");
        sm = new TrcStateMachine("TestSM");
        //
        // Choice menus.
        //
        doMenus();
        sm.start();
    }   //robotInit

    @Override
    public void runPeriodic()
    {
        super.runPeriodic();

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
                doLineFollowing(alliance);
                break;
        }
    }   //runPeriodic

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
    public boolean isMenuEnter()
    {
        return gamepad1.a;
    }   //isMenuEnter

    @Override
    public boolean isMenuBack()
    {
        return gamepad1.dpad_left;
    }   //isMenuBack

    private void doMenus()
    {
        FtcMenu testMenu = new FtcMenu(null, "Tests:", this);
        FtcMenu driveTimeMenu = new FtcMenu(testMenu, "Drive time:", this);
        FtcMenu driveDistanceMenu = new FtcMenu(testMenu, "Drive distance:", this);
        FtcMenu turnDegreesMenu = new FtcMenu(testMenu, "Turn degrees:", this);
        FtcMenu allianceMenu = new FtcMenu(testMenu, "Alliance:", this);

        testMenu.addChoice("Test sensors", TEST_SENSORS);
        testMenu.addChoice("Timed Drive", TEST_DRIVE_TIME, driveTimeMenu);
        testMenu.addChoice("Drive x ft", TEST_DRIVE_DISTANCE, driveDistanceMenu);
        testMenu.addChoice("Turn x deg", TEST_TURN_DEGREES, turnDegreesMenu);
        testMenu.addChoice("Line following", TEST_LINE_FOLLOWING, allianceMenu);

        driveTimeMenu.addChoice("1 sec", 1.0);
        driveTimeMenu.addChoice("2 sec", 2.0);
        driveTimeMenu.addChoice("4 sec", 4.0);
        driveTimeMenu.addChoice("8 sec", 8.0);

        driveDistanceMenu.addChoice("2 ft", 24.0);
        driveDistanceMenu.addChoice("4 ft", 48.0);
        driveDistanceMenu.addChoice("8 ft", 96.0);
        driveDistanceMenu.addChoice("10 ft", 120.0);

        turnDegreesMenu.addChoice("-90 degrees", -90.0);
        turnDegreesMenu.addChoice("-180 degrees", -180.0);
        turnDegreesMenu.addChoice("-360 degrees", -360.0);
        turnDegreesMenu.addChoice("90 degrees", 90.0);
        turnDegreesMenu.addChoice("180 degrees", 180.0);
        turnDegreesMenu.addChoice("360 degrees", 360.0);

        allianceMenu.addChoice("Red", ALLIANCE_RED);
        allianceMenu.addChoice("Blue", ALLIANCE_BLUE);

        FtcMenu.walkMenuTree(testMenu);

        testChoice = (int)testMenu.getSelectedChoiceValue();
        driveTime = driveTimeMenu.getSelectedChoiceValue();
        driveDistance = driveDistanceMenu.getSelectedChoiceValue();
        turnDegrees = turnDegreesMenu.getSelectedChoiceValue();
        alliance = (int)allianceMenu.getSelectedChoiceValue();

        dashboard.displayPrintf(0, "Test: %s", testMenu.getSelectedChoiceText());
        dashboard.displayPrintf(1, "Drive Time: %.1f", driveTime);
        dashboard.displayPrintf(2, "Drive Distance: %.1f", driveDistance);
        dashboard.displayPrintf(3, "Turn Degrees: %.1f", turnDegrees);
        dashboard.displayPrintf(4, "Alliance: %s", alliance == ALLIANCE_RED? "Red": "Blue");
    }   //doMenus

    private void doTestSensors()
    {
        //
        // Read all sensors and display on the dashboard.
        // Drive the robot around to sample different locations of the field.
        //
        dashboard.displayPrintf(8, "Testing sensors:");
        dashboard.displayPrintf(9, "lfEnc=%.0f,rfEnc=%.0f,lrEnc=%.0f,rrEnc=%.0f",
                                robot.leftFrontWheel.getPosition(),
                                robot.rightFrontWheel.getPosition(),
                                robot.leftRearWheel.getPosition(),
                                robot.rightRearWheel.getPosition());
        dashboard.displayPrintf(10, "Gyro: Rate=%.1f,Heading=%.1f",
                                robot.gyro.getZRotationRate().value,
                                robot.gyro.getZHeading().value);
        dashboard.displayPrintf(11, "Color: R=%d,G=%d,B=%d,Alpha=%d,Hue=%x",
                                robot.colorSensor.red(),
                                robot.colorSensor.green(),
                                robot.colorSensor.blue(),
                                robot.colorSensor.alpha(),
                                robot.colorSensor.argb());
        dashboard.displayPrintf(12, "Light=%.0f,Sonar=%.1f,Touch=%d",
                                robot.lightSensor.getData().value,
                                robot.sonarSensor.getUltrasonicLevel(),
                                robot.touchSensor.isActive()? 1: 0);
        dashboard.displayPrintf(13, "ElevatorLimit=%d,%d SliderLimit=%d,%d",
                                robot.elevator.isLowerLimitSwitchPressed()? 1: 0,
                                robot.elevator.isUpperLimitSwitchPressed()? 1: 0,
                                robot.slider.isLowerLimitSwitchPressed()? 1: 0,
                                robot.slider.isUpperLimitSwitchPressed()? 1: 0);
    }   //doTestSensors

    private void doDriveTime(double time)
    {
        double lfEnc = robot.leftFrontWheel.getPosition();
        double rfEnc = robot.rightFrontWheel.getPosition();
        double lrEnc = robot.leftRearWheel.getPosition();
        double rrEnc = robot.rightRearWheel.getPosition();
        dashboard.displayPrintf(8, "Drive %.1f sec", time);
        dashboard.displayPrintf(9, "lfEnc=%.0f,rfEnc=%.0f", lfEnc, rfEnc);
        dashboard.displayPrintf(10, "lrEnc=%.0f,rrEnc=%.0f", lrEnc, rrEnc);
        dashboard.displayPrintf(11, "average=%f", (lfEnc + rfEnc + lrEnc + rrEnc)/4.0);
        dashboard.displayPrintf(12, "xPos=%.1f,yPos=%.1f,heading=%.1f",
                                robot.driveBase.getXPosition(),
                                robot.driveBase.getYPosition(),
                                robot.driveBase.getHeading());

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    //
                    // Drive the robot forward and set a timer for the given time.
                    //
                    robot.driveBase.tankDrive(0.2, 0.2);
                    timer.set(time, event);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    //
                    // We are done, stop the robot.
                    //
                    robot.driveBase.stop();
                    sm.stop();
                    break;
            }
        }
    }   //doDriveTime

    private void doDriveDistance(double distance)
    {
        dashboard.displayPrintf(8, "Drive %.1f ft", distance/12.0);
        dashboard.displayPrintf(9, "xPos=%.1f,yPos=%.1f,heading=%.1f",
                                robot.driveBase.getXPosition(),
                                robot.driveBase.getYPosition(),
                                robot.driveBase.getHeading());
        robot.pidCtrlDrive.displayPidInfo(10);
        robot.pidCtrlTurn.displayPidInfo(12);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    //
                    // Drive the given distance.
                    //
                    robot.pidDrive.setTarget(distance, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    //
                    // We are done.
                    //
                    sm.stop();
                    break;
            }
        }
    }   //doDriveDistance

    private void doTurnDegrees(double degrees)
    {
        dashboard.displayPrintf(8, "Turn %.1f degrees", degrees);
        dashboard.displayPrintf(9, "xPos=%.1f,yPos=%.1f,heading=%.1f",
                                robot.driveBase.getXPosition(),
                                robot.driveBase.getYPosition(),
                                robot.driveBase.getHeading());
        robot.pidCtrlDrive.displayPidInfo(10);
        robot.pidCtrlTurn.displayPidInfo(12);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    //
                    // Turn the given degrees.
                    //
                    robot.pidDrive.setTarget(0.0, degrees, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    //
                    // We are done.
                    //
                    sm.stop();
                    break;
            }
        }
    }   //doTurnDegrees

    private void doLineFollowing(int alliance)
    {
        dashboard.displayPrintf(8, "Line following: %s", alliance == ALLIANCE_RED? "Red": "Blue");
        dashboard.displayPrintf(9, "Light=%.0f,Sonar=%.1f,Touch=%d",
                                robot.lightSensor.getData().value,
                                robot.sonarSensor.getUltrasonicLevel(),
                                robot.touchSensor.isActive()? 1: 0);
        dashboard.displayPrintf(10, "Color: R=%d,G=%d,B=%d,Alpha=%d,Hue=%x",
                                robot.colorSensor.red(),
                                robot.colorSensor.green(),
                                robot.colorSensor.blue(),
                                robot.colorSensor.alpha(),
                                robot.colorSensor.argb());
        robot.pidCtrlDrive.displayPidInfo(11);
        robot.pidCtrlLineFollow.displayPidInfo(13);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    //
                    // Drive forward until we found the line.
                    //
                    robot.lineTrigger.setEnabled(true);
                    robot.pidCtrlDrive.setOutputRange(-0.5, 0.5);
                    robot.pidCtrlTurn.setOutputRange(-0.5, 0.5);
                    robot.pidCtrlLineFollow.setOutputRange(-0.5, 0.5);

                    robot.pidDrive.setTarget(24.0, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 1:
                    //
                    // Turn slowly to find the line again.
                    //
                    if (alliance == ALLIANCE_RED)
                    {
                        robot.pidCtrlLineFollow.setInverted(true);
                        robot.pidDrive.setTarget(0.0, -90.0, false, event, 0.0);
                    }
                    else
                    {
                        robot.pidCtrlLineFollow.setInverted(false);
                        robot.pidDrive.setTarget(0.0, 90.0, false, event, 0.0);
                    }
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 2:
                    //
                    // Follow the line until the touch switch is activated.
                    //
                    robot.lineTrigger.setEnabled(false);
                    robot.touchTrigger.setEnabled(true);
                    robot.pidLineFollow.setTarget(
                            60.0, RobotInfo.LINE_THRESHOLD, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    //
                    // We are done, restore everything.
                    //
                    robot.touchTrigger.setEnabled(false);
                    robot.pidCtrlDrive.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlLineFollow.setOutputRange(-1.0, 1.0);
                    sm.stop();
                    break;
            }
        }
    }   //doLineFollowing

}   //class FtcTest
