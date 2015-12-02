package ftc3543;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoParkFloorGoal implements TrcRobot.AutoStrategy
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

    public AutoParkFloorGoal(int alliance, int startPos, double delay)
    {
        this.alliance = alliance;
        this.startPos = startPos;
        this.delay = delay;
        event = new TrcEvent("ParkFloorGoalEvent");
        timer = new TrcTimer("ParkFloorGoalTimer");
        sm = new TrcStateMachine("autoParkFloorGoal");
        sm.start();
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkFloorGoal: %s alliance, startPos=%s",
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
                    // Move forward towards the floor goal.
                    //
                    robot.pidDrive.setTarget(
                            startPos == autoMode.STARTPOS_NEAR_MOUNTAIN? 50.0: 60.0,
                            0.0,
                            false, event, 10.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 2:
                    //
                    // Turn to face the floor goal.
                    //
                    robot.pidDrive.setTarget(
                            0.0,
                            alliance == autoMode.ALLIANCE_RED? -60.0: 60.0,
                            false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 3:
                    //
                    // Go into the floor goal.
                    //
                    robot.pidDrive.setTarget(
                            startPos == autoMode.STARTPOS_NEAR_MOUNTAIN? 45.0: 20.0,
                            0.0,
                            false, event, 10.0);
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

}   //class AutoParkFloorGoal
