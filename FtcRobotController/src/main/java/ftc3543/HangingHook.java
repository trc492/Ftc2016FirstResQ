package ftc3543;

import com.qualcomm.robotcore.hardware.ServoController;

import ftclib.FtcServo;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTaskMgr;
import trclib.TrcTimer;

public class HangingHook implements TrcTaskMgr.Task
{
    private enum State
    {
        ENABLE_CONTROLLER,
        SET_POSITION,
        DISABLE_CONTROLLER,
        DONE
    }

    private String instanceName;
    private FtcServo servo;
    private ServoController controller;
    private double servoPos = RobotInfo.HANGINGHOOK_RETRACT_POSITION;
    private TrcTimer timer;
    private TrcEvent event;
    private TrcStateMachine sm;

    public HangingHook(String instanceName)
    {
        this.instanceName = instanceName;
        servo = new FtcServo(instanceName);
        servo.setInverted(true);
        controller = servo.getController();
        timer = new TrcTimer(instanceName);
        event = new TrcEvent(instanceName);
        sm = new TrcStateMachine(instanceName);
    }

    public void cancel()
    {
        if (sm.isEnabled())
        {
            timer.cancel();
            sm.stop();
            setTaskEnabled(false);
        }
    }

    public void setTaskEnabled(boolean enabled)
    {
        if (enabled)
        {
            TrcTaskMgr.getInstance().registerTask(
                    instanceName, this, TrcTaskMgr.TaskType.POSTPERIODIC_TASK);
        }
        else
        {
            TrcTaskMgr.getInstance().unregisterTask(this, TrcTaskMgr.TaskType.POSTPERIODIC_TASK);
        }
    }

    public void setPosition(double pos)
    {
        cancel();
        servoPos = pos;
        sm.start(State.ENABLE_CONTROLLER);
        setTaskEnabled(true);
    }

    public void extend()
    {
        setPosition(RobotInfo.HANGINGHOOK_EXTEND_POSITION);
    }

    public void retract()
    {
        setPosition(RobotInfo.HANGINGHOOK_RETRACT_POSITION);
    }

    //
    // Implements TrcTaskMgr.Task
    //

    public void startTask(TrcRobot.RunMode runMode)
    {
    }   //startTask

    public void stopTask(TrcRobot.RunMode runMode)
    {
    }   //stopTask

    public void prePeriodicTask(TrcRobot.RunMode runMode)
    {
    }   //prePeriodicTask

    public void postPeriodicTask(TrcRobot.RunMode runMode)
    {
        HalDashboard.getInstance().displayPrintf(
                8, "HookState: %s", ((State)sm.getState()).toString());
        HalDashboard.getInstance().displayPrintf(
                9, "ServoState: %s", controller.getPwmStatus().toString());
        HalDashboard.getInstance().displayPrintf(10, "ServoPos: %f", servoPos);
        if (sm.isReady())
        {
            State state = (State)sm.getState();
            switch (state)
            {
                case ENABLE_CONTROLLER:
                    controller.pwmEnable();
                    sm.setState(State.SET_POSITION);
                    break;

                case SET_POSITION:
                    servo.setPosition(servoPos);
                    timer.set(RobotInfo.HANGINGHOOK_MOVE_TIME, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.DISABLE_CONTROLLER);
                    break;

                case DISABLE_CONTROLLER:
                    controller.pwmDisable();
                    sm.setState(State.DONE);
                    break;

                case DONE:
                default:
                    sm.stop();
                    setTaskEnabled(false);
            }
        }
    }   //postPeriodicTask

    public void preContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //preContinuousTask

    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}
