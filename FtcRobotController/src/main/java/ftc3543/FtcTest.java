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

    private enum State
    {
        START,
        TURN_TO_LINE,
        FOLLOW_LINE,
        DONE
    }   //enum State

    //
    // Miscellaneous.
    //
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;
    //
    // Menu choices.
    //
    private Test test = Test.SENSOR_TESTS;
    private double driveTime = 0.0;
    private double driveDistance = 0.0;
    private double turnDegrees = 0.0;
    private Alliance alliance = Alliance.RED_ALLIANCE;
    private double wallDistance = 0.0;

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
        sm.start(State.START);
    }   //robotInit

    @Override
    public void runPeriodic()
    {
        State state = (State)sm.getState();
        dashboard.displayPrintf(
                8, "%s: %s", test.toString(), state != null? state.toString(): "STOPPED!");
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
                doLineFollow(alliance, wallDistance);
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
        FtcMenu wallDistanceMenu = new FtcMenu("Wall distance:", testMenu, this);

        testMenu.addChoice("Sensor tests", Test.SENSOR_TESTS);
        testMenu.addChoice("Timed drive", Test.TIMED_DRIVE, driveTimeMenu);
        testMenu.addChoice("Distance drive", Test.DISTANCE_DRIVE, driveDistanceMenu);
        testMenu.addChoice("Degrees turn", Test.DEGREES_TURN, turnDegreesMenu);
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

        allianceMenu.addChoice("Red", Alliance.RED_ALLIANCE, wallDistanceMenu);
        allianceMenu.addChoice("Blue", Alliance.BLUE_ALLIANCE, wallDistanceMenu);

        wallDistanceMenu.addChoice("4 inches", 4.0);
        wallDistanceMenu.addChoice("6 inches", 6.0);
        wallDistanceMenu.addChoice("8 inches", 8.0);
        wallDistanceMenu.addChoice("12 inches", 12.0);

        FtcMenu.walkMenuTree(testMenu);

        test = (Test)testMenu.getCurrentChoiceObject();
        driveTime = (Double)driveTimeMenu.getCurrentChoiceObject();
        driveDistance = (Double)driveDistanceMenu.getCurrentChoiceObject();
        turnDegrees = (Double)turnDegreesMenu.getCurrentChoiceObject();
        alliance = (Alliance)allianceMenu.getCurrentChoiceObject();
        wallDistance = (Double)wallDistanceMenu.getCurrentChoiceObject();

        dashboard.displayPrintf(0, "Test: %s", testMenu.getCurrentChoiceText());
    }   //doMenus

    private void doSensorTests()
    {
        //
        // Allow TeleOp to run so we can control the robot in test sensor mode.
        //
        super.runPeriodic();
        //
        // Read all sensors and display on the dashboard.
        // Drive the robot around to sample different locations of the field.
        //
        dashboard.displayPrintf(9, "Sensor Tests:");
        dashboard.displayPrintf(10, "Enc: lf=%.0f,rf=%.0f",
                                robot.leftFrontWheel.getPosition(),
                                robot.rightFrontWheel.getPosition());
        dashboard.displayPrintf(11, "Enc: lr=%.0f,rr=%.0f",
                                robot.leftRearWheel.getPosition(),
                                robot.rightRearWheel.getPosition());
        dashboard.displayPrintf(12, "Gyro: Rate=%.1f,Heading=%.1f",
                                robot.gyro.getZRotationRate().value,
                                robot.gyro.getZHeading().value);
        dashboard.displayPrintf(13, "RGBAH: %d,%d,%d,%d,%x",
                                robot.colorSensor.red(),
                                robot.colorSensor.green(),
                                robot.colorSensor.blue(),
                                robot.colorSensor.alpha(),
                                robot.colorSensor.argb());
        dashboard.displayPrintf(14, "Light=%.0f,Sonar=%.1f",
                                robot.lightSensor.getData().value,
                                robot.sonarSensor.getData().value);
        dashboard.displayPrintf(15, "ElevatorLimit=%d,%d SliderLimit=%d,%d",
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
        dashboard.displayPrintf(9, "Timed Drive: %.0f sec", time);
        dashboard.displayPrintf(10, "Enc:lf=%.0f,rf=%.0f", lfEnc, rfEnc);
        dashboard.displayPrintf(11, "Enc:lr=%.0f,rr=%.0f", lrEnc, rrEnc);
        dashboard.displayPrintf(12, "average=%f", (lfEnc + rfEnc + lrEnc + rrEnc)/4.0);
        dashboard.displayPrintf(13, "xPos=%.1f,yPos=%.1f,heading=%.1f",
                                robot.driveBase.getXPosition(),
                                robot.driveBase.getYPosition(),
                                robot.driveBase.getHeading());

        if (sm.isReady())
        {
            State state = (State)sm.getState();
            switch (state)
            {
                case START:
                    //
                    // Drive the robot forward and set a timer for the given time.
                    //
                    robot.driveBase.tankDrive(0.2, 0.2);
                    timer.set(time, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DONE);
                    break;

                case DONE:
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
        dashboard.displayPrintf(9, "Distance Drive: %.1f ft", distance/12.0);
        dashboard.displayPrintf(10, "xPos=%.1f,yPos=%.1f,heading=%.1f",
                                robot.driveBase.getXPosition(),
                                robot.driveBase.getYPosition(),
                                robot.driveBase.getHeading());
        robot.pidCtrlDrive.displayPidInfo(11);
        robot.pidCtrlTurn.displayPidInfo(13);

        if (sm.isReady())
        {
            State state = (State)sm.getState();
            switch (state)
            {
                case START:
                    //
                    // Drive the given distance.
                    //
                    robot.pidDrive.setTarget(distance, 0.0, false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DONE);
                    break;

                case DONE:
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
        dashboard.displayPrintf(9, "Degrees Turn: %.1f", degrees);
        dashboard.displayPrintf(10, "xPos=%.1f,yPos=%.1f,heading=%.1f",
                                robot.driveBase.getXPosition(),
                                robot.driveBase.getYPosition(),
                                robot.driveBase.getHeading());
        robot.pidCtrlDrive.displayPidInfo(11);
        robot.pidCtrlTurn.displayPidInfo(13);

        if (sm.isReady())
        {
            State state = (State)sm.getState();
            switch (state)
            {
                case START:
                    //
                    // Turn the given degrees.
                    //
                    robot.pidDrive.setTarget(0.0, degrees, false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DONE);
                    break;

                case DONE:
                default:
                    //
                    // We are done.
                    //
                    sm.stop();
                    break;
            }
        }
    }   //doTurnDegrees

    private void doLineFollow(Alliance alliance, double wallDistance)
    {
        dashboard.displayPrintf(9, "Line following: %s, distance=%.1f",
                                alliance.toString(), wallDistance);
        dashboard.displayPrintf(10, "Light=%.0f,Sonar=%.1f",
                                robot.lightSensor.getData().value,
                                robot.sonarSensor.getData().value);
        dashboard.displayPrintf(11, "Color: R=%d,G=%d,B=%d,Alpha=%d,Hue=%x",
                                robot.colorSensor.red(),
                                robot.colorSensor.green(),
                                robot.colorSensor.blue(),
                                robot.colorSensor.alpha(),
                                robot.colorSensor.argb());
        robot.pidCtrlSonar.displayPidInfo(12);
        robot.pidCtrlLight.displayPidInfo(14);

        if (sm.isReady())
        {
            State state = (State)sm.getState();
            switch (state)
            {
                case START:
                    //
                    // Drive forward until we reached 24 inches or found the line.
                    // Enable light trigger so that detecting the line will stop the drive.
                    // Limit all PID controllers to half power so we go slowly and hopefully
                    // not passing the line that much.
                    //
                    robot.lightTrigger.setEnabled(true);
                    robot.pidCtrlDrive.setOutputRange(-0.5, 0.5);
                    robot.pidCtrlTurn.setOutputRange(-0.5, 0.5);
                    robot.pidCtrlSonar.setOutputRange(-0.5, 0.5);
                    robot.pidCtrlLight.setOutputRange(-0.5, 0.5);
                    robot.pidDrive.setTarget(24.0, 0.0, false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.TURN_TO_LINE);
                    break;

                case TURN_TO_LINE:
                    //
                    // We have past the line, turn slowly to find the line again.
                    //
                    if (alliance == Alliance.RED_ALLIANCE)
                    {
                        //
                        // Turn left to find the line and set to follow the
                        // right edge of the line.
                        //
                        robot.pidCtrlLight.setInverted(true);
                        robot.pidDrive.setTarget(0.0, -90.0, false, event);
                    }
                    else
                    {
                        //
                        // Turn right to find the line and set to follow the
                        // left edge of the line.
                        //
                        robot.pidCtrlLight.setInverted(false);
                        robot.pidDrive.setTarget(0.0, 90.0, false, event);
                    }
                    sm.addEvent(event);
                    sm.waitForEvents(State.FOLLOW_LINE);
                    break;

                case FOLLOW_LINE:
                    //
                    // Disable light trigger.
                    // Follow the line until we are at the given distance from
                    // the wall.
                    //
                    robot.lightTrigger.setEnabled(false);
                    robot.pidDriveLineFollow.setTarget(
                            wallDistance, RobotInfo.LINE_THRESHOLD, false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DONE);
                    break;

                case DONE:
                default:
                    //
                    // We are done, restore everything.
                    //
                    robot.pidCtrlDrive.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlTurn.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlSonar.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlLight.setOutputRange(-1.0, 1.0);
                    sm.stop();
                    break;
            }
        }
    }   //doLineFollowing

}   //class FtcTest
