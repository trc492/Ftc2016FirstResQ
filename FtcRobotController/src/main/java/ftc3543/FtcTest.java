package ftc3543;

import ftclib.FtcMenu;
import trclib.TrcEvent;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class FtcTest extends FtcTeleOp implements FtcMenu.MenuButtons
{
    private enum Test
    {
        SENSOR_TESTS,
        TIMED_DRIVE,
        DISTANCE_DRIVE,
        DEGREES_TURN,
        LINE_FOLLOW
    }   //enum Test

    private enum Alliance
    {
        RED_ALLIANCE,
        BLUE_ALLIANCE
    }   //enum Alliance

    //
    // Miscellaneous.
    //
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;

    private Test test = Test.SENSOR_TESTS;
    private double driveTime = 0.0;
    private double driveDistance = 0.0;
    private double turnDegrees = 0.0;
    private Alliance alliance = Alliance.RED_ALLIANCE;

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

        switch (test)
        {
            case SENSOR_TESTS:
                doSensorTests();
                break;

            case TIMED_DRIVE:
                doTimedDrive(driveTime);
                break;

            case DISTANCE_DRIVE:
                doDistanceDrive(driveDistance);
                break;

            case DEGREES_TURN:
                doDegreesTurn(turnDegrees);
                break;

            case LINE_FOLLOW:
                doLineFollow(alliance);
                break;
        }
    }   //runPeriodic

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
        FtcMenu testMenu = new FtcMenu("Tests:", null, this);
        FtcMenu driveTimeMenu = new FtcMenu("Drive time:", testMenu, this);
        FtcMenu driveDistanceMenu = new FtcMenu("Drive distance:", testMenu, this);
        FtcMenu turnDegreesMenu = new FtcMenu("Turn degrees:", testMenu, this);
        FtcMenu allianceMenu = new FtcMenu("Alliance:", testMenu, this);

        testMenu.addChoice("Sensor Tests", Test.SENSOR_TESTS);
        testMenu.addChoice("Timed Drive", Test.TIMED_DRIVE, driveTimeMenu);
        testMenu.addChoice("Distance Drive", Test.DISTANCE_DRIVE, driveDistanceMenu);
        testMenu.addChoice("Degrees Turn", Test.DEGREES_TURN, turnDegreesMenu);
        testMenu.addChoice("Line follow", Test.LINE_FOLLOW, allianceMenu);

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

        allianceMenu.addChoice("Red", Alliance.RED_ALLIANCE);
        allianceMenu.addChoice("Blue", Alliance.BLUE_ALLIANCE);

        FtcMenu.walkMenuTree(testMenu);

        test = (Test)testMenu.getCurrentChoiceObject();
        driveTime = (Double)driveTimeMenu.getCurrentChoiceObject();
        driveDistance = (Double)driveDistanceMenu.getCurrentChoiceObject();
        turnDegrees = (Double)turnDegreesMenu.getCurrentChoiceObject();
        alliance = (Alliance)allianceMenu.getCurrentChoiceObject();

        dashboard.displayPrintf(0, "Test: %s", testMenu.getCurrentChoiceText());
    }   //doMenus

    private void doSensorTests()
    {
        //
        // Read all sensors and display on the dashboard.
        // Drive the robot around to sample different locations of the field.
        //
        dashboard.displayPrintf(8, "Sensor Tests:");
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

    private void doTimedDrive(double time)
    {
        double lfEnc = robot.leftFrontWheel.getPosition();
        double rfEnc = robot.rightFrontWheel.getPosition();
        double lrEnc = robot.leftRearWheel.getPosition();
        double rrEnc = robot.rightRearWheel.getPosition();
        dashboard.displayPrintf(8, "Timed Drive: %.0f sec", time);
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

    private void doDistanceDrive(double distance)
    {
        dashboard.displayPrintf(8, "Distance Drive: %.1f ft", distance/12.0);
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

    private void doDegreesTurn(double degrees)
    {
        dashboard.displayPrintf(8, "Degrees Turn: %.1f", degrees);
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

    private void doLineFollow(Alliance alliance)
    {
        dashboard.displayPrintf(8, "Line following: %s", alliance.toString());
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
                    if (alliance == Alliance.RED_ALLIANCE)
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
