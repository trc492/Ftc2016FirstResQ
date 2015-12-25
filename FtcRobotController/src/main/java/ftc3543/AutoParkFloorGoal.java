package ftc3543;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcDbgTrace;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoParkFloorGoal implements TrcRobot.AutoStrategy
{
    private enum State
    {
        DO_DELAY,
        MOVE_FORWARD,
        TURN_TO_FLOOR_GOAL,
        GOTO_FLOOR_GOAL,
        DONE
    }   //enum State

    private static final String moduleName = "AutoParkFloorGoal";

    private FtcRobot robot = ((FtcAuto)FtcOpMode.getInstance()).robot;
    private HalDashboard dashboard = HalDashboard.getInstance();
    private TrcDbgTrace tracer = FtcOpMode.getOpModeTracer();

    private FtcAuto.Alliance alliance;
    private FtcAuto.StartPosition startPos;
    private double delay;
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;

    public AutoParkFloorGoal(
            FtcAuto.Alliance alliance, FtcAuto.StartPosition startPos, double delay)
    {
        this.alliance = alliance;
        this.startPos = startPos;
        this.delay = delay;
        event = new TrcEvent(moduleName);
        timer = new TrcTimer(moduleName);
        sm = new TrcStateMachine(moduleName);
        sm.start(State.DO_DELAY);
    }

    @Override
    public void autoPeriodic(double elapsedTime)
    {
        dashboard.displayPrintf(1, moduleName + ": %s, %s, delay=%.0f",
                                alliance.toString(), startPos.toString(), delay);

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
                        timer.set(delay, event);
                        sm.addEvent(event);
                        sm.waitForEvents(State.MOVE_FORWARD);
                    }
                    break;

                case MOVE_FORWARD:
                    //
                    // Move forward towards the floor goal.
                    //
                    robot.pidDrive.setTarget(
                            startPos == FtcAuto.StartPosition.NEAR_MOUNTAIN? 60.0: 70.0, 0.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.TURN_TO_FLOOR_GOAL);
                    break;

                case TURN_TO_FLOOR_GOAL:
                    //
                    // Turn to face the floor goal.
                    //
                    robot.pidDrive.setTarget(
                            0.0, alliance == FtcAuto.Alliance.RED_ALLIANCE? -50.0: 50.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.GOTO_FLOOR_GOAL);
                    break;

                case GOTO_FLOOR_GOAL:
                    //
                    // Go into the floor goal.
                    //
                    robot.pidDrive.setTarget(
                            startPos == FtcAuto.StartPosition.NEAR_MOUNTAIN? 22.0: 45.0, 0.0,
                            false, event);
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

}   //class AutoParkFloorGoal
