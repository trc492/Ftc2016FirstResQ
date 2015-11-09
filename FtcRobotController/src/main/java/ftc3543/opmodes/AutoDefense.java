package ftc3543.opmodes;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoDefense implements TrcRobot.AutoStrategy
{
    private FtcAuto autoMode = (FtcAuto)FtcOpMode.getInstance();
    private FtcRobot robot = autoMode.robot;
    private HalDashboard dashboard = HalDashboard.getInstance();

    private int alliance;
    private double delay;
    private double distance;
    private TrcStateMachine sm;
    private TrcTimer timer;
    private TrcEvent event;

    public AutoDefense(int alliance, double delay, double distance)
    {
        this.alliance = alliance;
        this.delay = delay;
        this.distance = distance;
        sm = new TrcStateMachine("autoDefense");
        sm.start();
        timer = new TrcTimer("DefenseTimer");
        event = new TrcEvent("DefenseEvent");
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "Defense: %s alliance, delay=%.1f, distance=%.1f",
                                alliance == autoMode.ALLIANCE_RED? "Red": "Blue",
                                delay, distance/12.0);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    //
                    // If there is a delay, set the timer for it.
                    //
                    if (delay == 0.0)
                    {
                        sm.setState(state + 1);
                    }
                    else
                    {
                        timer.set(delay, event);
                        sm.addEvent(event);
                        sm.waitForEvents(state + 1);
                    }
                    break;

                case TrcStateMachine.STATE_STARTED + 1:
                    //
                    // Drive the set distance.
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
    }

}   //class AutoDefense
