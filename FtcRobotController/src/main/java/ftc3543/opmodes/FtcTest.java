package ftc3543.opmodes;

import ftclib.FtcGamepad;
import ftclib.FtcMenu;
import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class FtcTest extends FtcOpMode implements FtcMenu.MenuButtons
{
    private HalDashboard dashboard;
    private FtcRobot robot;
    private FtcGamepad driverGamepad;
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

    private int testChoice = TEST_SENSORS;
    private double driveTime = 0.0;
    private double driveDistance = 0.0;
    private double turnDegrees = 0.0;

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
        robot = new FtcRobot(TrcRobot.RunMode.TEST_MODE);
        //
        // Initialize input subsystems.
        //
        driverGamepad = new FtcGamepad("DriverGamepad", gamepad1, null);
        driverGamepad.setYInverted(true);
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
        FtcMenu testMenu = new FtcMenu(null, "Tests:", this);
        FtcMenu driveTimeMenu = new FtcMenu(testMenu, "Drive time:", this);
        FtcMenu driveDistanceMenu = new FtcMenu(testMenu, "Drive distance:", this);
        FtcMenu turnDegreesMenu = new FtcMenu(testMenu, "Turn degrees:", this);

        testMenu.addChoice("Test sensors", TEST_SENSORS);
        testMenu.addChoice("Timed Drive", TEST_DRIVE_TIME, driveTimeMenu);
        testMenu.addChoice("Drive x ft", TEST_DRIVE_DISTANCE, driveDistanceMenu);
        testMenu.addChoice("Turn x deg", TEST_TURN_DEGREES, turnDegreesMenu);
        testMenu.addChoice("Line following", TEST_LINE_FOLLOWING);

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

        FtcMenu.walkMenuTree(testMenu);

        testChoice = (int)testMenu.getSelectedChoiceValue();
        driveTime = driveTimeMenu.getSelectedChoiceValue();
        driveDistance = driveDistanceMenu.getSelectedChoiceValue();
        turnDegrees = turnDegreesMenu.getSelectedChoiceValue();

        dashboard.displayPrintf(0, "Test: %s", testMenu.getSelectedChoiceText());
        dashboard.displayPrintf(1, "Drive Time: %.1f", driveTime);
        dashboard.displayPrintf(2, "Drive Distance: %.1f", driveDistance);
        dashboard.displayPrintf(3, "Turn Degrees: %.1f", turnDegrees);
    }   //doMenus

    private void doTestSensors()
    {
        //
        // Read all sensors and display on the dashboard.
        // Drive the robot around to sample different locations of the field.
        //
        dashboard.displayPrintf(1, "Testing sensors:");
        double leftPower  = driverGamepad.getLeftStickY(true);
        double rightPower = driverGamepad.getRightStickY(true);
        robot.driveBase.tankDrive(leftPower, rightPower);
        dashboard.displayPrintf(2, "leftPower = %.2f, rightPower = %.2f", leftPower, rightPower);
        dashboard.displayPrintf(3, "lfEnc=%.1f, rfEnc=%.1f, lrEnc=%.1f, rrEnc=%.1f",
                                robot.leftFrontWheel.getPosition(),
                                robot.rightFrontWheel.getPosition(),
                                robot.leftRearWheel.getPosition(),
                                robot.rightRearWheel.getPosition());
        dashboard.displayPrintf(4, "Gyro = X:%.1f,%.1f Y:%.1f,%.1f Z:%.1f,%.1f",
                                robot.mrGyro.getXRotation(), robot.gyro.getXHeading(),
                                robot.mrGyro.getYRotation(), robot.gyro.getYHeading(),
                                robot.mrGyro.getZRotation(), robot.gyro.getZHeading());
        dashboard.displayPrintf(5, "HiTechnicGyro = X:%.1f,%.1f Y:%.1f,%.1f Z:%.1f,%.1f",
                                robot.hitechnicGyro.getXRotation(),
                                robot.hitechnicGyro.getXHeading(),
                                robot.hitechnicGyro.getYRotation(),
                                robot.hitechnicGyro.getYHeading(),
                                robot.hitechnicGyro.getZRotation(),
                                robot.hitechnicGyro.getZHeading());
        dashboard.displayPrintf(6, "Color = [R:%d,G:%d,B:%d]",
                                robot.colorSensor.red(),
                                robot.colorSensor.green(),
                                robot.colorSensor.blue());
        dashboard.displayPrintf(7, "Color = [Hue:%x, Alpha:%d]",
                                robot.colorSensor.argb(),
                                robot.colorSensor.alpha());
        dashboard.displayPrintf(8, "RawLightValue = %d",
                                robot.lightSensor.getValue());
        dashboard.displayPrintf(9, "Touch = %s",
                                robot.touchSensor.isActive()? "pressed": "released");
        dashboard.displayPrintf(10, "Sonar = %f", robot.sonarSensor.getUltrasonicLevel());
        dashboard.displayPrintf(11, "lowerLimit=%d, upperLimit=%d",
                                robot.elevator.isLowerLimitSwitchPressed()? 1: 0,
                                robot.elevator.isUpperLimitSwitchPressed()? 1: 0);
    }   //doTestSensors

    private void doDriveTime(double time)
    {
        double lfEnc = robot.leftFrontWheel.getPosition();
        double rfEnc = robot.rightFrontWheel.getPosition();
        double lrEnc = robot.leftRearWheel.getPosition();
        double rrEnc = robot.rightRearWheel.getPosition();
        double avg = (lfEnc + rfEnc + lrEnc + rrEnc)/4.0;
        dashboard.displayPrintf(1, "Drive %.1f sec", time);
        dashboard.displayPrintf(2, "lfEnc=%.0f, rfEnc=%.0f", lfEnc, rfEnc);
        dashboard.displayPrintf(3, "lrEnc=%.0f, rrEnc=%.0f", lrEnc, rrEnc);
        dashboard.displayPrintf(4, "average=%f", (lfEnc + rfEnc + lrEnc + rrEnc)/4.0);
        dashboard.displayPrintf(5, "xPos=%f, yPos=%f, heading=%f",
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
        double lfEnc = robot.leftFrontWheel.getPosition();
        double rfEnc = robot.rightFrontWheel.getPosition();
        double lrEnc = robot.leftRearWheel.getPosition();
        double rrEnc = robot.rightRearWheel.getPosition();
        double avg = (lfEnc + rfEnc + lrEnc + rrEnc)/4.0;
        dashboard.displayPrintf(1, "Drive %.1f ft", distance/12.0);
        dashboard.displayPrintf(2, "lfEnc=%.0f, rfEnc=%.0f", lfEnc, rfEnc);
        dashboard.displayPrintf(3, "lrEnc=%.0f, rrEnc=%.0f", lrEnc, rrEnc);
        dashboard.displayPrintf(4, "average=%f", (lfEnc + rfEnc + lrEnc + rrEnc)/4.0);
        dashboard.displayPrintf(5, "xPos=%f, yPos=%f, heading=%f",
                                robot.driveBase.getXPosition(),
                                robot.driveBase.getYPosition(),
                                robot.driveBase.getHeading());
        robot.pidCtrlDrive.displayPidInfo(6);

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
        double lfEnc = robot.leftFrontWheel.getPosition();
        double rfEnc = robot.rightFrontWheel.getPosition();
        double lrEnc = robot.leftRearWheel.getPosition();
        double rrEnc = robot.rightRearWheel.getPosition();
        double avg = (lfEnc + rfEnc + lrEnc + rrEnc)/4.0;
        dashboard.displayPrintf(1, "Turn %.1f degrees", degrees);
        dashboard.displayPrintf(2, "lfEnc=%.0f, rfEnc=%.0f", lfEnc, rfEnc);
        dashboard.displayPrintf(3, "lrEnc=%.0f, rrEnc=%.0f", lrEnc, rrEnc);
        dashboard.displayPrintf(4, "average=%f", (lfEnc + rfEnc + lrEnc + rrEnc)/4.0);
        dashboard.displayPrintf(5, "xPos=%f, yPos=%f, heading=%f",
                                robot.driveBase.getXPosition(),
                                robot.driveBase.getYPosition(),
                                robot.driveBase.getHeading());
        robot.pidCtrlTurn.displayPidInfo(6);

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

    private void doLineFollowing()
    {
        dashboard.displayPrintf(1, "Line following");

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
                    robot.pidDrive.setTarget(120.0, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 1:
                    //
                    // Follow the line until the touch switch is activated.
                    //
                    robot.lineTrigger.setEnabled(false);
                    robot.touchTrigger.setEnabled(true);
                    robot.pidCtrlDrive.setOutputRange(-0.3, 0.3);
                    robot.pidCtrlLineFollow.setOutputRange(-0.3, 0.3);
                    robot.pidLineFollow.setTarget(
                            25.0, RobotInfo.LINE_THRESHOLD, false, event, 0.0);
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
