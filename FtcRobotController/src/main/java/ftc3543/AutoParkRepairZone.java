package ftc3543;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoParkRepairZone implements TrcRobot.AutoStrategy
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

    public AutoParkRepairZone(int alliance, int startPos, double delay)
    {
        this.alliance = alliance;
        this.startPos = startPos;
        this.delay = delay;
        event = new TrcEvent("ParkRepairZoneEvent");
        timer = new TrcTimer("ParkRepairZoneTimer");
        sm = new TrcStateMachine("autoParkRepairZone");
        sm.start();
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkRepairZone: %s alliance, startPos=%s",
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
                    // Go forward fast.
                    //
                    robot.pidDrive.setTarget(
                            startPos == autoMode.STARTPOS_NEAR_MOUNTAIN? 45.0: 60.0,
                            0.0,
                            false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 2:
                    //
                    // Drive forward slowly until we reach the line.
                    //
                    robot.lineTrigger.setEnabled(true);
                    robot.pidCtrlDrive.setOutputRange(-0.5, 0.5);
                    robot.pidDrive.setTarget(20.0, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 3:
                    //
                    // Turn slowly to find the edge of the line.
                    //
                    robot.pidCtrlTurn.setOutputRange(-0.5, 0.5);
                    robot.pidDrive.setTarget(
                            0.0,
                            alliance == autoMode.ALLIANCE_RED? -90.0: 90.0,
                            false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 4:
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
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 5:
                    robot.hangingHook.extend();
                    timer.set(5.0, event);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

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
