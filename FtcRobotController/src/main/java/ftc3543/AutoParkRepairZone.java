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

    private FtcRobot robot = ((FtcAuto)FtcOpMode.getInstance()).robot;
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
        dashboard.displayPrintf(2, "RGBAH: [%d,%d,%d,%d,%x]",
                                robot.colorSensor.red(),
                                robot.colorSensor.green(),
                                robot.colorSensor.blue(),
                                robot.colorSensor.alpha(),
                                robot.colorSensor.argb());
        robot.pidCtrlSonar.displayPidInfo(3);
        robot.pidCtrlLight.displayPidInfo(5);

        if (sm.isReady())
        {
            State state = (State)sm.getState();
            dashboard.displayPrintf(7, "State=%s", state != null? state.toString(): "STOPPED!");

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
                            startPos == FtcAuto.StartPosition.NEAR_MOUNTAIN? 70.0: 90.0, 0.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.FIND_LINE);
                    break;

                case FIND_LINE:
                    //
                    // Drive forward slowly until we reach the line.
                    //
                    robot.lightTrigger.setEnabled(true);
                    robot.pidCtrlDrive.setOutputRange(-0.3, 0.3);
                    robot.pidDrive.setTarget(25.0, 0.0, false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.TURN_TO_LINE);
                    break;

                case TURN_TO_LINE:
                    //
                    // Turn slowly to find the edge of the line.
                    //
                    robot.pidDrive.setTarget(
                            0.0, alliance == FtcAuto.Alliance.RED_ALLIANCE? -90.0: 90.0,
                            false, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.FOLLOW_LINE);
                    break;

                case FOLLOW_LINE:
                    //
                    // Follow the line until the touch sensor is hit.
                    //
                    robot.lightTrigger.setEnabled(false);
                    robot.pidCtrlSonar.setOutputRange(-0.3, 0.3);;
                    robot.pidCtrlLight.setOutputRange(-0.5, 0.5);
                    robot.pidDriveLineFollow.setTarget(
                            RobotInfo.BEACON_DISTANCE, RobotInfo.LINE_THRESHOLD,
                            false, event, 3.0);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DEPOSIT_CLIMBERS);
                    break;

                case DEPOSIT_CLIMBERS:
                    robot.hookServo.setPosition(RobotInfo.HANGINGHOOK_EXTEND_POSITION);
                    timer.set(5.0, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DONE);
                    break;

                case DONE:
                default:
                    //
                    // We are done.
                    //
                    robot.hookServo.setPosition(RobotInfo.HANGINGHOOK_RETRACT_POSITION);
                    robot.pidCtrlDrive.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlTurn.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlSonar.setOutputRange(-1.0, 1.0);
                    robot.pidCtrlLight.setOutputRange(-1.0, 1.0);
                    sm.stop();
                    break;
            }
        }
    }

}   //class AutoParkRepairZone
