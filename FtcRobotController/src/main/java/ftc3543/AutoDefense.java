package ftc3543;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcDbgTrace;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoDefense implements TrcRobot.AutoStrategy
{
    private enum State
    {
        DO_DELAY,
        DRIVE_DISTANCE,
        DONE
    }   //enum State

    private static final String moduleName = "AutoDefense";

    private HalDashboard dashboard = HalDashboard.getInstance();
    private TrcDbgTrace tracer = FtcOpMode.getOpModeTracer();

    private Robot robot;
    private double delay;
    private double distance;
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;

    public AutoDefense(Robot robot, double delay, double distance)
    {
        this.robot = robot;
        this.delay = delay;
        this.distance = distance;
        event = new TrcEvent(moduleName);
        timer = new TrcTimer(moduleName);
        sm = new TrcStateMachine(moduleName);
        sm.start(State.DO_DELAY);
    }

    @Override
    public void autoPeriodic(double elapsedTime)
    {
        dashboard.displayPrintf(1, moduleName + ": delay=%.0f, distance=%.0f",
                                delay, distance);

        if (sm.isReady())
        {
            State state = (State)sm.getState();
            tracer.traceInfo(moduleName, "State: %s [%.3f]", state.toString(), elapsedTime);
            dashboard.displayPrintf(7, "State: %s [%.3f]", state.toString(), elapsedTime);

            switch (state)
            {
                case DO_DELAY:
                    //
                    // If the delay is less than 10, make it at least 10 seconds.
                    // We cannot cross to the opponent's side before 10 seconds.
                    //
                    if (delay < 10.0)
                    {
                        delay = 10.0;
                    }
                    timer.set(delay, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DRIVE_DISTANCE);
                    break;

                case DRIVE_DISTANCE:
                    //
                    // Drive the set distance.
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
    }

}   //class AutoDefense
