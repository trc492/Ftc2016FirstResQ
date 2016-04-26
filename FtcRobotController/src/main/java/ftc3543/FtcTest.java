package ftc3543;

import ftclib.FtcChoiceMenu;
import ftclib.FtcMenu;
import ftclib.FtcOpMode;
import ftclib.FtcValueMenu;
import trclib.TrcEvent;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class FtcTest extends FtcTeleOp implements FtcMenu.MenuButtons
{
    private enum Test
    {
        SENSORS_TEST,
        MOTORS_TEST,
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
    // State machine.
    //
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;
    //
    // Menu choices.
    //
    private Test test = Test.SENSORS_TEST;
    private double driveTime = 0.0;
    private double driveDistance = 0.0;
    private double turnDegrees = 0.0;
    private Alliance alliance = Alliance.RED_ALLIANCE;
    private double wallDistance = 0.0;

    private int motorIndex = 0;

    //
    // Implements FtcOpMode interface.
    //

    @Override
    public void initRobot()
    {
        super.initRobot();
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
    }   //initRobot

    //
    // Overrides TrcRobot.RobotMode methods.
    //

    //
    // Must override TeleOp so it doesn't fight with us.
    //
    @Override
    public void runPeriodic(double elapsedTime)
    {
        //
        // Allow TeleOp to run so we can control the robot in test sensor mode.
        //
        if (test == Test.SENSORS_TEST)
        {
            super.runPeriodic(elapsedTime);
        }
    }   //runPeriodic

    @Override
    public void runContinuous(double elapsedTime)
    {
        State state = (State)sm.getState();
        dashboard.displayPrintf(
                8, "%s: %s", test.toString(), state != null? state.toString(): "STOPPED!");
        switch (test)
        {
            case SENSORS_TEST:
                doSensorsTest();
                break;

            case MOTORS_TEST:
                doMotorsTest();
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
        FtcChoiceMenu testMenu = new FtcChoiceMenu("Tests:", null, this);
        FtcValueMenu driveTimeMenu = new FtcValueMenu("Drive time:", testMenu, this,
                                                      1.0, 10.0, 1.0, 8.0, " %.0f sec");
        FtcValueMenu driveDistanceMenu = new FtcValueMenu("Drive distance:", testMenu, this,
                                                          1.0, 10.0, 1.0, 8.0, " %.0f ft");
        FtcValueMenu turnDegreesMenu =
                new FtcValueMenu("Turn degrees:", testMenu, this,
                                 -360.0, 360.0, 90.0, 360.0, " %.0f deg");
        FtcChoiceMenu allianceMenu = new FtcChoiceMenu("Alliance:", testMenu, this);
        FtcValueMenu wallDistanceMenu = new FtcValueMenu("Wall distance:", allianceMenu, this,
                                                         2.0, 12.0, 2.0, 2.0, " %.0f in");

        testMenu.addChoice("Sensors test", Test.SENSORS_TEST);
        testMenu.addChoice("Motors test", Test.MOTORS_TEST);
        testMenu.addChoice("Timed drive", Test.TIMED_DRIVE, driveTimeMenu);
        testMenu.addChoice("Distance drive", Test.DISTANCE_DRIVE, driveDistanceMenu);
        testMenu.addChoice("Degrees turn", Test.DEGREES_TURN, turnDegreesMenu);
        testMenu.addChoice("Line follow", Test.LINE_FOLLOW, allianceMenu);

        allianceMenu.addChoice("Red", Alliance.RED_ALLIANCE, wallDistanceMenu);
        allianceMenu.addChoice("Blue", Alliance.BLUE_ALLIANCE, wallDistanceMenu);

        FtcMenu.walkMenuTree(testMenu);

        test = (Test)testMenu.getCurrentChoiceObject();
        driveTime = driveTimeMenu.getCurrentValue();
        driveDistance = driveDistanceMenu.getCurrentValue();
        turnDegrees = turnDegreesMenu.getCurrentValue();
        alliance = (Alliance)allianceMenu.getCurrentChoiceObject();
        wallDistance = wallDistanceMenu.getCurrentValue();

        dashboard.displayPrintf(0, "Test: %s", testMenu.getCurrentChoiceText());
    }   //doMenus

    private void doSensorsTest()
    {
        //
        // Read all sensors and display on the dashboard.
        // Drive the robot around to sample different locations of the field.
        //
        dashboard.displayPrintf(9, "Enc:lf=%.0f,rf=%.0f",
                                robot.leftFrontWheel.getPosition(),
                                robot.rightFrontWheel.getPosition());
        dashboard.displayPrintf(10, "Enc:lr=%.0f,rr=%.0f",
                                robot.leftRearWheel.getPosition(),
                                robot.rightRearWheel.getPosition());
        dashboard.displayPrintf(11, "Gyro:Rate=%.1f,Heading=%.1f",
                                robot.gyro.getZRotationRate().value,
                                robot.gyro.getZHeading().value);
        dashboard.displayPrintf(12, "Winch:Enc=%.0f,Len=%.1f,LimitSW=%d",
                                robot.winch.getEncoderPosition(),
                                robot.winch.getLength(),
                                robot.winch.isLowerLimitSwitchPressed()? 1: 0);
        dashboard.displayPrintf(13, "Beacon:RGBAH=[%d,%d,%d,%d,%x]",
                                robot.beaconColorSensor.red(),
                                robot.beaconColorSensor.green(),
                                robot.beaconColorSensor.blue(),
                                robot.beaconColorSensor.alpha(),
                                robot.beaconColorSensor.argb());
        dashboard.displayPrintf(14, "Color=%d,White=%d,Sonar=%.1f",
                                (Integer)robot.lineFollowColorSensor.getColorNumber().value,
                                (Integer)robot.lineFollowColorSensor.getWhiteValue().value,
                                robot.sonarSensor.getData(0).value);
    }   //doSensorsTest

    private void doMotorsTest()
    {
        dashboard.displayPrintf(9, "Motors Test: index=%d", motorIndex);
        dashboard.displayPrintf(10, "Enc: lf=%.0f, rf=%.0f",
                                robot.leftFrontWheel.getPosition(),
                                robot.rightFrontWheel.getPosition());
        dashboard.displayPrintf(11, "Enc: lr=%.0f, rr=%.0f",
                                robot.leftRearWheel.getPosition(),
                                robot.rightRearWheel.getPosition());

        if (sm.isReady())
        {
            State state = (State)sm.getState();
            switch (state)
            {
                case START:
                    //
                    // Spin a wheel for 5 seconds.
                    //
                    switch (motorIndex)
                    {
                        case 0:
                            robot.leftFrontWheel.setPower(0.5);
                            robot.rightFrontWheel.setPower(0.0);
                            robot.leftRearWheel.setPower(0.0);
                            robot.rightRearWheel.setPower(0.0);
                            break;

                        case 1:
                            robot.leftFrontWheel.setPower(0.0);
                            robot.rightFrontWheel.setPower(0.5);
                            robot.leftRearWheel.setPower(0.0);
                            robot.rightRearWheel.setPower(0.0);
                            break;

                        case 2:
                            robot.leftFrontWheel.setPower(0.0);
                            robot.rightFrontWheel.setPower(0.0);
                            robot.leftRearWheel.setPower(0.5);
                            robot.rightRearWheel.setPower(0.0);
                            break;

                        case 3:
                            robot.leftFrontWheel.setPower(0.0);
                            robot.rightFrontWheel.setPower(0.0);
                            robot.leftRearWheel.setPower(0.0);
                            robot.rightRearWheel.setPower(0.5);
                            break;
                    }
                    motorIndex = motorIndex + 1;
                    timer.set(5.0, event);
                    sm.addEvent(event);
                    sm.waitForEvents(motorIndex < 4? State.START: State.DONE);
                    break;

                case DONE:
                default:
                    //
                    // We are done.
                    //
                    robot.leftFrontWheel.setPower(0.0);
                    robot.rightFrontWheel.setPower(0.0);
                    robot.leftRearWheel.setPower(0.0);
                    robot.rightRearWheel.setPower(0.0);
                    sm.stop();
                    break;
            }
        }
    }   //doMotorsTest

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
    }   //doTimedDrive

    private void doDistanceDrive(double distance)
    {
        dashboard.displayPrintf(9, "Distance Drive: %.1f ft", distance);
        dashboard.displayPrintf(10, "xPos=%.1f,yPos=%.1f,heading=%.1f",
                                robot.driveBase.getXPosition(),
                                robot.driveBase.getYPosition(),
                                robot.driveBase.getHeading());
        robot.encoderPidCtrl.displayPidInfo(11);
        robot.gyroPidCtrl.displayPidInfo(13);

        if (sm.isReady())
        {
            State state = (State)sm.getState();
            switch (state)
            {
                case START:
                    //
                    // Drive the given distance.
                    //
                    robot.pidDrive.setTarget(distance*12.0, 0.0, false, event);
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
    }   //doDistanceDrive

    private void doDegreesTurn(double degrees)
    {
        dashboard.displayPrintf(9, "Degrees Turn: %.1f", degrees);
        dashboard.displayPrintf(10, "xPos=%.1f,yPos=%.1f,heading=%.1f",
                                robot.driveBase.getXPosition(),
                                robot.driveBase.getYPosition(),
                                robot.driveBase.getHeading());
        robot.encoderPidCtrl.displayPidInfo(11);
        robot.gyroPidCtrl.displayPidInfo(13);

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
    }   //doDegreesTurn

    private void doLineFollow(Alliance alliance, double wallDistance)
    {
        dashboard.displayPrintf(9, "Line Follow: %s, wallDist=%.1f",
                                alliance.toString(), wallDistance);
        dashboard.displayPrintf(10, "Color=%d,W/R/G/B=%d/%d/%d/%d,Sonar=%.1f",
                                (Integer)robot.lineFollowColorSensor.getColorNumber().value,
                                (Integer)robot.lineFollowColorSensor.getWhiteValue().value,
                                (Integer)robot.lineFollowColorSensor.getRedValue().value,
                                (Integer)robot.lineFollowColorSensor.getGreenValue().value,
                                (Integer)robot.lineFollowColorSensor.getBlueValue().value,
                                robot.sonarSensor.getData(0).value);
        dashboard.displayPrintf(11, "Color: R=%d,G=%d,B=%d,Alpha=%d,Hue=%x",
                                robot.beaconColorSensor.red(),
                                robot.beaconColorSensor.green(),
                                robot.beaconColorSensor.blue(),
                                robot.beaconColorSensor.alpha(),
                                robot.beaconColorSensor.argb());
        robot.sonarPidCtrl.displayPidInfo(12);
        robot.colorPidCtrl.displayPidInfo(14);

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
                    robot.colorTrigger.setEnabled(true);
                    robot.encoderPidCtrl.setOutputRange(-0.5, 0.5);
                    robot.gyroPidCtrl.setOutputRange(-0.75, 0.75);
                    robot.sonarPidCtrl.setOutputRange(-0.3, 0.3);
                    robot.colorPidCtrl.setOutputRange(-0.5, 0.5);
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
                        robot.colorPidCtrl.setInverted(true);
                        robot.pidDrive.setTarget(0.0, -90.0, false, event);
                    }
                    else
                    {
                        //
                        // Turn right to find the line and set to follow the
                        // left edge of the line.
                        //
                        robot.colorPidCtrl.setInverted(false);
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
                    robot.colorTrigger.setEnabled(false);
                    robot.pidLineFollow.setTarget(
                            wallDistance, RobotInfo.COLOR_LINE_EDGE_LEVEL, false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DONE);
                    break;

                case DONE:
                default:
                    //
                    // We are done, restore everything.
                    //
                    robot.encoderPidCtrl.setOutputRange(-1.0, 1.0);
                    robot.gyroPidCtrl.setOutputRange(-1.0, 1.0);
                    robot.sonarPidCtrl.setOutputRange(-1.0, 1.0);
                    robot.colorPidCtrl.setOutputRange(-1.0, 1.0);
                    sm.stop();
                    break;
            }
        }
    }   //doLineFollow

}   //class FtcTest
