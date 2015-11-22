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
    private double delay;
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;

    public AutoParkMountain(int alliance, double delay)
    {
        this.alliance = alliance;
        this.delay = delay;
        event = new TrcEvent("ParkMountainEvent");
        timer = new TrcTimer("ParkMountainTimer");
        sm = new TrcStateMachine("autoParkMountain");
        sm.start();
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkMountain: %s alliance, delay=%.1f",
                                alliance == autoMode.ALLIANCE_RED? "Red": "Blue", delay);

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
                    robot.pidDrive.setTarget(70.0, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 2:
                    //
                    // Turn to face the mountain.
                    //
                    if (alliance == autoMode.ALLIANCE_RED)
                    {
                        robot.pidDrive.setTarget(0.0, -90.0, false, event, 0.0);
                    }
                    else
                    {
                        robot.pidDrive.setTarget(0.0, 90.0, false, event, 0.0);
                    }
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 3:
                    //
                    // Turn on the tread drive and run as hard as you could
                    // to climb up the mountain
                    //
                    robot.pidDrive.setTarget(120.0, 0.0, false, event, 0.0);
//                    robot.trackHook.setPower(1.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    //
                    // We are done, stop the tread drive.
                    //
                    robot.slideHook.setPower(0.0);
                    sm.stop();
                    break;
            }
        }
    }

}   //class AutoParkMountain
