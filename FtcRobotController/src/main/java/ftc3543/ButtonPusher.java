package ftc3543;

import ftclib.FtcServo;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcServo;
import trclib.TrcStateMachine;
import trclib.TrcTaskMgr;
import trclib.TrcTimer;

public class ButtonPusher implements TrcTaskMgr.Task
{
    private enum State
    {
        SET_FORWARD,
        SET_REVERSE,
        SET_STOP
    }   //enum State

    private String instanceName;
    private double travelTime;
    private FtcServo pusherServo;
    private TrcTimer timer;
    private TrcEvent event;
    private TrcStateMachine sm;

    public ButtonPusher(String instanceName, boolean inverted, double travelTime)
    {
        this.instanceName = instanceName;
        this.travelTime = travelTime;
        pusherServo = new FtcServo(instanceName);
        pusherServo.setInverted(inverted);
        pusherServo.setPosition(TrcServo.CONTINUOUS_SERVO_STOP);
        timer = new TrcTimer(instanceName);
        event = new TrcEvent(instanceName);
        sm = new TrcStateMachine(instanceName);
    }   //ButtonPusher

    private void setEnabled(boolean enabled)
    {
        if (enabled)
        {
            TrcTaskMgr.getInstance().registerTask(
                    instanceName, this, TrcTaskMgr.TaskType.POSTCONTINUOUS_TASK);
        }
        else
        {
            TrcTaskMgr.getInstance().unregisterTask(
                    this, TrcTaskMgr.TaskType.POSTCONTINUOUS_TASK);
        }
    }

    public void stop()
    {
        pusherServo.setPosition(TrcServo.CONTINUOUS_SERVO_STOP);
    }

    private void cancel()
    {
        if (sm.isEnabled())
        {
            pusherServo.setPosition(TrcServo.CONTINUOUS_SERVO_STOP);
            timer.cancel();
            sm.stop();
        }
    }

    public void extend()
    {
        cancel();
        sm.start(State.SET_FORWARD);
        setEnabled(true);
    }

    public void retract()
    {
        cancel();
        sm.start(State.SET_REVERSE);
        setEnabled(true);
    }

    //
    // Implements TrcTaskMgr.Task
    //

    @Override
    public void startTask(TrcRobot.RunMode runMode)
    {
    }   //startTask

    @Override
    public void stopTask(TrcRobot.RunMode runMode)
    {
    }   //stopTask

    @Override
    public void prePeriodicTask(TrcRobot.RunMode runMode)
    {
    }   //prePeriodicTask

    @Override
    public void postPeriodicTask(TrcRobot.RunMode runMode)
    {
    }   //postPeriodicTask

    @Override
    public void preContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //preContinuousTask

    @Override
    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
        if (sm.isReady())
        {
            State state = (State)sm.getState();
            switch (state)
            {
                case SET_FORWARD:
                    pusherServo.setPosition(TrcServo.CONTINUOUS_SERVO_FORWARD_MAX);
                    timer.set(travelTime, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.SET_STOP);
                    break;

                case SET_REVERSE:
                    pusherServo.setPosition(TrcServo.CONTINUOUS_SERVO_REVERSE_MAX);
                    timer.set(travelTime, event);
                    sm.addEvent(event);
                    sm.waitForEvents(State.SET_STOP);
                    break;

                case SET_STOP:
                    pusherServo.setPosition(TrcServo.CONTINUOUS_SERVO_STOP);
                    sm.stop();
                    break;
            }
        }
    }   //postContinuousTask

}
