package ftc3543;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
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

    private FtcRobot robot = ((FtcAuto)FtcOpMode.getInstance()).robot;
    private HalDashboard dashboard = HalDashboard.getInstance();

    private double delay;
    private double distance;
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;

    public AutoDefense(double delay, double distance)
    {
        this.delay = delay;
        this.distance = distance;
        event = new TrcEvent("DefenseEvent");
        timer = new TrcTimer("DefenseTimer");
        sm = new TrcStateMachine("autoDefense");
        sm.start(State.DO_DELAY);
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "Defense: delay=%.0f, distance=%.0f", delay, distance/12.0);

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
                        sm.setState(State.DRIVE_DISTANCE);
                    }
                    else
                    {
                        timer.set(delay, event);
                        sm.addEvent(event);
                        sm.waitForEvents(State.DRIVE_DISTANCE);
                    }
                    break;

                case DRIVE_DISTANCE:
                    //
                    // Drive the set distance.
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
    }

}   //class AutoDefense
