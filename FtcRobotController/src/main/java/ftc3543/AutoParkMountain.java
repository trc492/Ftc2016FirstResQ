package ftc3543;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoParkMountain implements TrcRobot.AutoStrategy
{
    private FtcAuto autoMode = (FtcAuto)FtcOpMode.getInstance();
    private FtcRobot robot = autoMode.robot;
    private HalDashboard dashboard = HalDashboard.getInstance();

    private int alliance;
    private int startPos;
    private double delay;
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;

    public AutoParkMountain(int alliance, int startPos, double delay)
    {
        this.alliance = alliance;
        this.startPos = startPos;
        this.delay = delay;
        event = new TrcEvent("ParkMountainEvent");
        timer = new TrcTimer("ParkMountainTimer");
        sm = new TrcStateMachine("autoParkMountain");
        sm.start();
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkMountain: %s alliance, startPos=%s",
                                alliance == autoMode.ALLIANCE_RED? "Red": "Blue",
                                startPos == autoMode.STARTPOS_NEAR_MOUNTAIN? "Mountain": "Corner");
        dashboard.displayPrintf(2, "\tDelay=%.0f", delay);

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
                    // Move forward towards the mountain.
                    //
                    robot.pidDrive.setTarget(
                            startPos == autoMode.STARTPOS_NEAR_MOUNTAIN? 24.0: 36.0,
                            0.0,
                            false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 2:
                    //
                    // Turn to face the mountain.
                    //
                    robot.pidDrive.setTarget(
                            0.0,
                            alliance == autoMode.ALLIANCE_RED? -90.0: 90.0,
                            false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 3:
                    //
                    // Turn on the tread drive and run as hard as you could
                    // to climb up the mountain
                    //
                    robot.pidDrive.setTarget(
                            startPos == autoMode.STARTPOS_NEAR_MOUNTAIN? 60.0: 90.0,
                            0.0,
                            false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    //
                    // We are done, stop!
                    //
                    sm.stop();
                    break;
            }
        }
    }

}   //class AutoParkMountain
