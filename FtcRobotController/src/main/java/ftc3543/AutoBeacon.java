package ftc3543;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcDbgTrace;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoBeacon implements TrcRobot.AutoStrategy
{
    private enum State
    {
        DO_DELAY,
        MOVE_FORWARD,
        FIND_LINE,
        CLEAR_DEBRIS,
        BACK_TO_LINE,
        FIND_LINE_AGAIN,
        TURN_TO_LINE,
        FOLLOW_LINE,
        PUSH_BUTTON,
        RETRACT,
        MOVE_SOMEWHERE,
        GO_DEFENSE,
        PARK_FLOOR_GOAL,
        BACK_TO_MOUNTAIN,
        TURN_TO_MOUNTAIN,
        GO_UP_MOUNTAIN,
        DONE
    }   //enum State

    private static final String moduleName = "AutoBeacon";

    private HalDashboard dashboard = HalDashboard.getInstance();
    private TrcDbgTrace tracer = FtcOpMode.getOpModeTracer();

    private Robot robot;
    private FtcAuto.Alliance alliance;
    private FtcAuto.StartPosition startPos;
    private double delay;
    private boolean pushButton;
    private boolean depositClimbers;
    private FtcAuto.BeaconOption option;
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;
    private boolean leftPusherExtended = false;
    private boolean rightPusherExtended = false;

    public AutoBeacon(
            Robot robot,
            FtcAuto.Alliance alliance,
            FtcAuto.StartPosition startPos,
            double delay,
            boolean pushButton,
            boolean depositClimbers,
            FtcAuto.BeaconOption option)
    {
        this.robot = robot;
        this.alliance = alliance;
        this.startPos = startPos;
        this.delay = delay;
        this.pushButton = pushButton;
        this.depositClimbers = depositClimbers;
        this.option = option;
        event = new TrcEvent(moduleName);
        timer = new TrcTimer(moduleName);
        sm = new TrcStateMachine(moduleName);
        sm.start(State.DO_DELAY);
    }

    @Override
    public void autoPeriodic(double elapsedTime)
    {
        //
        // Do trace logging and debug tracing.
        //
        if (robot.pidDrive.isEnabled())
        {
            robot.encoderPidCtrl.printPidInfo(tracer);
            robot.gyroPidCtrl.printPidInfo(tracer);
            tracer.traceInfo(moduleName, "[%.3f] LineFollow: color=%d, W/R/G/B=%d/%d/%d/%d",
                             elapsedTime,
                             (Integer)robot.lineFollowColorSensor.getColorNumber().value,
                             (Integer)robot.lineFollowColorSensor.getWhiteValue().value,
                             (Integer)robot.lineFollowColorSensor.getRedValue().value,
                             (Integer)robot.lineFollowColorSensor.getGreenValue().value,
                             (Integer)robot.lineFollowColorSensor.getBlueValue().value);
        }
        else if (robot.pidLineFollow.isEnabled())
        {
            robot.sonarPidCtrl.printPidInfo(tracer);
            robot.colorPidCtrl.printPidInfo(tracer);
            tracer.traceInfo(moduleName, "[%.3f] LineFollow: color=%d, W/R/G/B=%d/%d/%d/%d",
                             elapsedTime,
                             (Integer)robot.lineFollowColorSensor.getColorNumber().value,
                             (Integer)robot.lineFollowColorSensor.getWhiteValue().value,
                             (Integer)robot.lineFollowColorSensor.getRedValue().value,
                             (Integer)robot.lineFollowColorSensor.getGreenValue().value,
                             (Integer)robot.lineFollowColorSensor.getBlueValue().value);
            dashboard.displayPrintf(3, "LineFollow:color=%d,white=%d",
                                    (Integer)robot.lineFollowColorSensor.getColorNumber().value,
                                    (Integer)robot.lineFollowColorSensor.getWhiteValue().value);
            robot.sonarPidCtrl.displayPidInfo(4);
            robot.colorPidCtrl.displayPidInfo(6);
        }

        dashboard.displayPrintf(1, moduleName + ": %s,%s,delay=%.0f,pushButton=%s,option=%s",
                                alliance.toString(), startPos.toString(), delay,
                                Boolean.toString(pushButton), option.toString());
        dashboard.displayPrintf(2, "RGBAH: [%d,%d,%d,%d,%x]",
                                robot.beaconColorSensor.red(),
                                robot.beaconColorSensor.green(),
                                robot.beaconColorSensor.blue(),
                                robot.beaconColorSensor.alpha(),
                                robot.beaconColorSensor.argb());

        if (sm.isReady())
        {
            State state = (State)sm.getState();
            tracer.traceInfo(moduleName, "State: %s [%.3f]", state.toString(), elapsedTime);
            dashboard.displayPrintf(7, "State: %s [%.3f]", state.toString(), elapsedTime);

            switch (state)
            {
                case DO_DELAY:
                    //
                    // If there is a delay, set the timer for it.
                    //
                    if (delay == 0.0)
                    {
                        sm.setState(State.MOVE_FORWARD);
                    }
                    else
                    {
                        //
                        // Set timer for delay time.
                        //
                        timer.set(delay, event);
                        sm.addEvent(event);
                        sm.waitForEvents(State.MOVE_FORWARD);
                    }
                    break;

                case MOVE_FORWARD:
                    //
                    // Go forward fast.
                    //
                    robot.pidDrive.setTarget(
                            startPos == FtcAuto.StartPosition.NEAR_MOUNTAIN? 65.0: 95.0, 0.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.FIND_LINE);
                    break;

                case FIND_LINE:
                    //
                    // Drive forward slowly until we reach the line.
                    // If line is detected, it will interrupt PID drive.
                    //
                    robot.colorTrigger.setEnabled(true);
                    robot.encoderPidCtrl.setOutputRange(-0.3, 0.3);
                    robot.pidDrive.setTarget(15.0, 0.0, false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.CLEAR_DEBRIS);
                    break;

                case CLEAR_DEBRIS:
                    robot.colorTrigger.setEnabled(false);
                    robot.pidDrive.setTarget(20.0, 0.0, false, event, 2.0);
                    sm.addEvent(event);
                    sm.waitForEvents(State.BACK_TO_LINE);
                    break;

                case BACK_TO_LINE:
                    robot.pidDrive.setTarget(-20.0,
                                             alliance == FtcAuto.Alliance.RED_ALLIANCE? 5.0: -5.0,
                                             false, event, 2.0);
                    sm.addEvent(event);
                    sm.waitForEvents(State.FIND_LINE_AGAIN);
                    break;

                case FIND_LINE_AGAIN:
                    robot.colorTrigger.setTriggerPoints(robot.color2Zones);
                    robot.colorTrigger.setEnabled(true);
                    robot.encoderPidCtrl.setOutputRange(-0.2, 0.2);
                    robot.pidDrive.setTarget(15.0, 0.0, false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.TURN_TO_LINE);
                    break;

                case TURN_TO_LINE:
                    //
                    // Turn slowly to find the edge of the line.
                    // If line is detected, it will interrupt PID turn.
                    //
                    robot.gyroPidCtrl.setOutputRange(-0.75, 0.75);
                    robot.pidDrive.setTarget(
                            0.0, alliance == FtcAuto.Alliance.RED_ALLIANCE? -90.0: 90.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.FOLLOW_LINE);
                    break;

                case FOLLOW_LINE:
                    //
                    // Follow the line until we are in front of the beacon .
                    //
                    robot.colorTrigger.setEnabled(false);
                    robot.sonarPidCtrl.setOutputRange(-0.3, 0.3);;
                    robot.colorPidCtrl.setOutputRange(-0.5, 0.5);
                    robot.colorPidCtrl.setInverted(alliance == FtcAuto.Alliance.RED_ALLIANCE);
                    robot.pidLineFollow.setTarget(
                            RobotInfo.SONAR_BEACON_DISTANCE,
                            RobotInfo.COLOR_LINE_EDGE_LEVEL,
                            false, event, 4.0);
                    sm.addEvent(event);
                    sm.waitForEvents(State.PUSH_BUTTON);
                    break;

                case PUSH_BUTTON:
                    //
                    // Determine which button to press and press it.
                    // Simultaneously dump the climbers into the bin and
                    // wait for it to finish.
                    //
                    robot.colorPidCtrl.setOutputRange(-1.0, 1.0);
                    robot.sonarPidCtrl.setOutputRange(-1.0, 1.0);
                    robot.gyroPidCtrl.setOutputRange(-1.0, 1.0);;
                    robot.encoderPidCtrl.setOutputRange(-1.0, 1.0);
                    if (pushButton)
                    {
                        int redValue = robot.beaconColorSensor.red();
                        int greenValue = robot.beaconColorSensor.green();
                        int blueValue = robot.beaconColorSensor.blue();
                        boolean isRed = redValue > blueValue && redValue > greenValue;
                        boolean isBlue = blueValue > redValue && blueValue > greenValue;
                        tracer.traceInfo(
                                moduleName, "[%d,%d,%d]isRed=%s,isBlue=%s",
                                redValue, greenValue, blueValue,
                                isRed? "true": "false",
                                isBlue? "true": "false");
                        //
                        // Determine which button to push and do it.
                        //
                        if (alliance == FtcAuto.Alliance.RED_ALLIANCE && isRed ||
                            alliance == FtcAuto.Alliance.BLUE_ALLIANCE && isBlue)
                        {
                            robot.rightButtonPusher.extend();
                            rightPusherExtended = true;
                        }
                        else if (alliance == FtcAuto.Alliance.RED_ALLIANCE && isBlue ||
                                 alliance == FtcAuto.Alliance.BLUE_ALLIANCE && isRed)
                        {
                            robot.leftButtonPusher.extend();
                            leftPusherExtended = true;
                        }
                    }
                    //
                    // Deposit the climbers into the bin.
                    //
                    if (depositClimbers)
                    {
                        robot.climberDepositor.setPosition(RobotInfo.DEPOSITOR_EXTEND_POSITION);
//                                                           RobotInfo.DEPOSITOR_STEPRATE);
                    }
                    //
                    // It takes sometime for the arm to move and deposit the climber.
                    // So set a timer to wait for it.
                    //
                    timer.set(3.0, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.RETRACT);
                    break;

                case RETRACT:
                    //
                    // Release the button pusher and retract the hanging hook.
                    //
                    if (leftPusherExtended)
                    {
                        robot.leftButtonPusher.retract();
                        leftPusherExtended = false;
                    }

                    if (rightPusherExtended)
                    {
                        robot.rightButtonPusher.retract();
                        rightPusherExtended = false;
                    }

                    robot.climberDepositor.setPosition(RobotInfo.DEPOSITOR_RETRACT_POSITION);


                    if (option == FtcAuto.BeaconOption.DO_NOTHING)
                    {
                        //
                        // Stay there, we are done!
                        //
                        sm.setState(State.DONE);
                    }
                    else
                    {
                        //
                        // We are going to move out of the way.
                        // First we need to back up a little bit so we have some room to turn.
                        //
                        robot.pidDrive.setTarget(
                                -6.0, 0.0, false, event, 1.0);
                        sm.addEvent(event);
                        sm.waitForEvents(State.MOVE_SOMEWHERE);
                    }
                    break;

                case MOVE_SOMEWHERE:
                    if (option == FtcAuto.BeaconOption.DEFENSE)
                    {
                        //
                        // We can only go to the opponent's side after 10 seconds.
                        // Check it just to be safe.
                        //
                        if (elapsedTime > 10.0)
                        {
                            sm.setState(State.GO_DEFENSE);
                        }
                    }
                    else if (option == FtcAuto.BeaconOption.PARK_FLOOR_GOAL)
                    {
                        //
                        // Turn to face the floor goal.
                        //
                        robot.pidDrive.setTarget(
                                0.0, alliance == FtcAuto.Alliance.RED_ALLIANCE? 90.0: -90.0,
                                false, event);
                        sm.addEvent(event);
                        sm.waitForEvents(State.PARK_FLOOR_GOAL);
                    }
                    else if (option == FtcAuto.BeaconOption.PARK_MOUNTAIN)
                    {
                        //
                        // Turn to parallel the mountain.
                        //
                        robot.pidDrive.setTarget(
                                0.0, alliance == FtcAuto.Alliance.RED_ALLIANCE? 45.0: -45.0,
                                false, event);
                        sm.addEvent(event);
                        sm.waitForEvents(State.BACK_TO_MOUNTAIN);
                    }
                    break;

                case GO_DEFENSE:
                    //
                    // Run to the opponent side and bump them if necessary.
                    //
                    robot.pidDrive.setTarget(
                            -35.0, alliance == FtcAuto.Alliance.RED_ALLIANCE? -45.0: 45.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DONE);
                    break;

                case PARK_FLOOR_GOAL:
                    //
                    // Go into the floor goal.
                    //
                    robot.pidDrive.setTarget(-24.0, 0.0, false, event, 3.0);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DONE);
                    break;

                case BACK_TO_MOUNTAIN:
                    //
                    // Back up to mountain foothill.
                    //
                    robot.pidDrive.setTarget(-42.0, 0.0, false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.TURN_TO_MOUNTAIN);
                    break;

                case TURN_TO_MOUNTAIN:
                    //
                    // Turn to face the mountain.
                    //
                    robot.pidDrive.setTarget(
                            0.0, alliance == FtcAuto.Alliance.RED_ALLIANCE? -90.0: 90.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.GO_UP_MOUNTAIN);
                    break;

                case GO_UP_MOUNTAIN:
                    robot.pidDrive.setTarget(50.0, 0.0, false, event, 5.0);
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
    }

}   //class AutoBeacon
