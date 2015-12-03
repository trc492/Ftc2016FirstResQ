package ftc3543;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoParkRepairZone implements TrcRobot.AutoStrategy
{
    private enum State
    {
        DO_DELAY,
        MOVE_FORWARD,
        FIND_LINE,
        TURN_TO_LINE,
        FOLLOW_LINE,
        DEPOSIT_CLIMBERS,
        DONE
    }   //enum State

    private FtcAuto autoMode = (FtcAuto)FtcOpMode.getInstance();
    private FtcRobot robot = autoMode.robot;
    private HalDashboard dashboard = HalDashboard.getInstance();

    private FtcAuto.Alliance alliance;
    private FtcAuto.StartPosition startPos;
    private double delay;
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;

    public AutoParkRepairZone(
            FtcAuto.Alliance alliance, FtcAuto.StartPosition startPos, double delay)
    {
        this.alliance = alliance;
        this.startPos = startPos;
        this.delay = delay;
        event = new TrcEvent("ParkRepairZoneEvent");
        timer = new TrcTimer("ParkRepairZoneTimer");
        sm = new TrcStateMachine("autoParkRepairZone");
        sm.start(State.DO_DELAY);
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkRepairZone: %s, %s, delay=%.0f",
                                alliance.toString(), startPos.toString(), delay);

        if (sm.isReady())
        {
            State state = (State)sm.getState();

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
                            startPos == FtcAuto.StartPosition.NEAR_MOUNTAIN? 45.0: 60.0,
                            0.0,
                            false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(State.FIND_LINE);
                    break;

                case FIND_LINE:
                    //
                    // Drive forward slowly until we reach the line.
                    //
                    robot.lineTrigger.setEnabled(true);
                    robot.pidCtrlDrive.setOutputRange(-0.5, 0.5);
                    robot.pidDrive.setTarget(20.0, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(State.TURN_TO_LINE);
                    break;

                case TURN_TO_LINE:
                    //
                    // Turn slowly to find the edge of the line.
                    //
                    robot.pidCtrlTurn.setOutputRange(-0.5, 0.5);
                    robot.pidDrive.setTarget(
                            0.0,
                            alliance == FtcAuto.Alliance.RED_ALLIANCE? -90.0: 90.0,
                            false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(State.FOLLOW_LINE);
                    break;

                case FOLLOW_LINE:
                    //
                    // Follow the line until the touch sensor is hit.
                    //
                    robot.lineTrigger.setEnabled(false);
                    robot.touchTrigger.setEnabled(true);
                    robot.pidCtrlLineFollow.setOutputRange(-0.5, 0.5);
                    robot.pidCtrlDrive.setOutputRange(-0.3, 0.3);;
                    robot.pidLineFollow.setTarget(
                            12.0, RobotInfo.LINE_THRESHOLD, false, event, 3.0);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DEPOSIT_CLIMBERS);
                    break;

                case DEPOSIT_CLIMBERS:
                    robot.hangingHook.extend();
                    timer.set(5.0, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DONE);
                    break;

                case DONE:
                default:
                    //
                    // We are done.
                    //
                    robot.hangingHook.retract();
                    robot.pidCtrlDrive.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlTurn.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlLineFollow.setOutputRange(-1.0, 1.0);
                    sm.stop();
                    break;
            }
        }
    }

}   //class AutoParkRepairZone
