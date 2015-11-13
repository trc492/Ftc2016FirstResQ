package ftc3543;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoTriggerBeacon implements TrcRobot.AutoStrategy
{
    private FtcAuto autoMode = (FtcAuto)FtcOpMode.getInstance();
    private FtcRobot robot = autoMode.robot;
    private HalDashboard dashboard = HalDashboard.getInstance();

    private int alliance;
    private double delay;
    private int option;
    private TrcEvent event;
    private TrcTimer timer;
    private TrcStateMachine sm;

    public AutoTriggerBeacon(int alliance, double delay, int option)
    {
        this.alliance = alliance;
        this.delay = delay;
        this.option = option;
        event = new TrcEvent("TriggerBeaconEvent");
        timer = new TrcTimer("TriggerBeaconTimer");
        sm = new TrcStateMachine("autoTriggerBeacon");
        sm.start();
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "TriggerBeacon: %s alliance, delay=%.1f",
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
                    // Drive forward until we reach the line.
                    //
                    robot.lineTrigger.setEnabled(true);
                    robot.pidDrive.setTarget(120.0, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 2:
                    //
                    // Turn slowly to find the edge of the line.
                    //
                    robot.pidCtrlTurn.setOutputRange(-0.5, 0.5);
                    if (alliance == autoMode.ALLIANCE_RED)
                    {
                        robot.pidDrive.setTarget(0.0, -60.0, false, event, 0.0);
                    }
                    else
                    {
                        robot.pidDrive.setTarget(0.0, 60.0, false, event, 0.0);
                    }
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 3:
                    //
                    // Follow the line until the touch sensor is hit.
                    //
                    robot.lineTrigger.setEnabled(false);
                    robot.touchTrigger.setEnabled(true);
                    robot.pidCtrlTurn.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlLineFollow.setOutputRange(-0.3, 0.3);
                    robot.pidCtrlDrive.setOutputRange(-0.3, 0.3);;
                    robot.pidLineFollow.setTarget(
                            30.0, RobotInfo.LINE_THRESHOLD, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 4:
                    //
                    // Determine which button to press and press it.
                    // Simultaneously dump the climbers into the bin and
                    // wait for it to finish.
                    //
                    robot.touchTrigger.setEnabled(false);
                    robot.pidCtrlLineFollow.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlDrive.setOutputRange(-1.0, 1.0);;
                    int redValue = robot.colorSensor.red();
                    int blueValue = robot.colorSensor.blue();
                    int greenValue = robot.colorSensor.green();
                    boolean isRed = redValue > 0 && blueValue == 0 && greenValue == 0;
                    boolean isBlue = blueValue > 0 && redValue == 0 && greenValue == 0;
                    if (alliance == autoMode.ALLIANCE_RED && isRed)
                    {
                        robot.buttonPusher.pushLeftButton();
                    }
                    else if (alliance == autoMode.ALLIANCE_BLUE && isBlue)
                    {
                        robot.buttonPusher.pushRightButton();
                    }
                    robot.hangingHook.extend();
                    timer.set(2.0, event);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 5:
                    //
                    // Release the button pusher and retract the hanging hook.
                    //
                    robot.buttonPusher.retract();
                    robot.hangingHook.retract();
                    if (option == autoMode.BEACON_OPTION_DO_NOTHING)
                    {
                        //
                        // Stay there, we are done!
                        //
                        sm.setState(1000);
                    }
                    else if (option == autoMode.BEACON_OPTION_DEFENSE)
                    {
                        //
                        // Run to the opponent side and bump them if necessary.
                        //
                        if (alliance == autoMode.ALLIANCE_RED)
                        {
                            robot.pidDrive.setTarget(-35.0, -45.0, false, event, 0.0);
                        }
                        else
                        {
                            robot.pidDrive.setTarget(-35.0, 45.0, false, event, 0.0);
                        }
                        sm.addEvent(event);
                        sm.waitForEvents(1000);
                    }
                    else if (option == autoMode.BEACON_OPTION_PARK_FLOORGOAL)
                    {
                        //
                        // Turn to face the floor goal.
                        //
                        if (alliance == autoMode.ALLIANCE_RED)
                        {
                            robot.pidDrive.setTarget(0.0, 90.0, false, event, 0.0);
                        }
                        else
                        {
                            robot.pidDrive.setTarget(0.0, -90.0, false, event, 0.0);
                        }
                        sm.addEvent(event);
                        sm.waitForEvents(state + 1);
                    }
                    break;

                case TrcStateMachine.STATE_STARTED + 6:
                    //
                    // Go into the floor goal.
                    //
                    robot.pidDrive.setTarget(-24.0, 0.0, false, event, 0.0);
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

}   //class AutoTriggerBeacon
