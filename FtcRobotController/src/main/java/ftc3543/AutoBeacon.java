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
        TURN_TO_LINE,
        FOLLOW_LINE,
        PUSH_BUTTON,
        RETRACT,
        MOVE_SOMEWHERE,
        GO_BUMP,
        PARK_FLOOR_GOAL,
        DONE
    }   //enum State

    private static final String moduleName = "AutoBeacon";

    private FtcRobot robot = ((FtcAuto)FtcOpMode.getInstance()).robot;
    private HalDashboard dashboard = HalDashboard.getInstance();
    private TrcDbgTrace tracer = FtcOpMode.getOpModeTracer();

    private FtcAuto.Alliance alliance;
    private FtcAuto.StartPosition startPos;
    private double delay;
    private boolean pushButton;
    private FtcAuto.BeaconOption option;
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;
    private boolean leftPusherExtended = false;
    private boolean rightPusherExtended = false;

    public AutoBeacon(
            FtcAuto.Alliance alliance,
            FtcAuto.StartPosition startPos,
            double delay,
            boolean pushButton,
            FtcAuto.BeaconOption option)
    {
        this.alliance = alliance;
        this.startPos = startPos;
        this.delay = delay;
        this.pushButton = pushButton;
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
            robot.pidCtrlDrive.printPidInfo(tracer);
            robot.pidCtrlTurn.printPidInfo(tracer);
        }
        else if (robot.pidDriveLineFollow.isEnabled())
        {
            robot.pidCtrlSonar.printPidInfo(tracer);
            robot.pidCtrlLight.printPidInfo(tracer);
        }

        dashboard.displayPrintf(1, moduleName + ": %s, %s,delay=%.0f,pushButton=%s,option=%s",
                                alliance.toString(), startPos.toString(), delay,
                                Boolean.toString(pushButton), option.toString());
        dashboard.displayPrintf(2, "RGBAH: [%d,%d,%d,%d,%x]",
                                robot.colorSensor.red(),
                                robot.colorSensor.green(),
                                robot.colorSensor.blue(),
                                robot.colorSensor.alpha(),
                                robot.colorSensor.argb());
        robot.pidCtrlSonar.displayPidInfo(3);
        robot.pidCtrlLight.displayPidInfo(5);

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
                            startPos == FtcAuto.StartPosition.NEAR_MOUNTAIN? 70.0: 90.0, 0.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.FIND_LINE);
                    break;

                case FIND_LINE:
                    //
                    // Drive forward slowly until we reach the line.
                    // If line is detected, it will interrupt PID drive.
                    //
                    robot.lightTrigger.setEnabled(true);
                    robot.pidCtrlDrive.setOutputRange(-0.3, 0.3);
                    robot.pidDrive.setTarget(25.0, 0.0, false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.TURN_TO_LINE);
                    break;

                case TURN_TO_LINE:
                    //
                    // Turn slowly to find the edge of the line.
                    // If line is detected, it will interrupt PID turn.
                    //
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
                    robot.lightTrigger.setEnabled(false);
                    robot.pidCtrlSonar.setOutputRange(-0.3, 0.3);;
                    robot.pidCtrlLight.setOutputRange(-0.5, 0.5);
                    robot.pidDriveLineFollow.setTarget(
                            RobotInfo.BEACON_DISTANCE, RobotInfo.LIGHT_THRESHOLD,
                            false, event, 3.0);
                    sm.addEvent(event);
                    sm.waitForEvents(State.PUSH_BUTTON);
                    break;

                case PUSH_BUTTON:
                    //
                    // Determine which button to press and press it.
                    // Simultaneously dump the climbers into the bin and
                    // wait for it to finish.
                    //
                    robot.pidCtrlLight.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlSonar.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlTurn.setOutputRange(-1.0, 1.0);;
                    robot.pidCtrlDrive.setOutputRange(-1.0, 1.0);
                    if (pushButton)
                    {
                        int redValue = robot.colorSensor.red();
                        int greenValue = robot.colorSensor.green();
                        int blueValue = robot.colorSensor.blue();
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
                    robot.hookServo.setPosition(RobotInfo.HANGINGHOOK_EXTEND_POSITION);
                    //
                    // It takes sometime for the arm to move and deposit the climber.
                    // So set a timer to wait for it.
                    //
                    timer.set(5.0, event);
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

                    robot.hookServo.setPosition(RobotInfo.HANGINGHOOK_RETRACT_POSITION);

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
                                -8.0, 0.0, false, event);
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
                            sm.setState(State.GO_BUMP);
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
                    break;

                case GO_BUMP:
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
                    robot.pidDrive.setTarget(-25.0, 0.0, false, event);
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