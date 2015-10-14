package trclib;

import hallib.HalTimer;

public class TrcTimer implements TrcTaskMgr.Task
{
    private static final String moduleName = "DbgTimer";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private double expiredTime;
    private boolean enabled;
    private boolean expired;
    private TrcEvent notifyEvent;

    public TrcTimer(final String instanceName)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
        this.expiredTime = 0.0;
        this.enabled = false;
        this.expired = false;
        this.notifyEvent = null;
    }   //DbgTimer

    public void set(double time, TrcEvent event)
    {
        final String funcName = "set";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "time=%f,event=%s",
                    time, event != null? event.getName(): "null");
        }

        expired = false;
        expiredTime = HalTimer.getCurrentTime() + time;
        if (event != null)
        {
            event.clear();
        }
        notifyEvent = event;
        setEnabled(true);

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //set

    public void cancel()
    {
        final String funcName = "cancel";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        setEnabled(false);
        if (enabled && !expired)
        {
            expiredTime = 0.0;
            enabled = false;
            notifyEvent.cancel();
            notifyEvent = null;
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //cancel

    private void setEnabled(boolean enabled)
    {
        final String funcName = "setEnabled";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.FUNC,
                    "enabled=%s", Boolean.toString(enabled));
        }

        this.enabled = enabled;
        if (enabled)
        {
            TrcTaskMgr.registerTask(
                    instanceName,
                    this,
                    TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
        }
        else
        {
            TrcTaskMgr.unregisterTask(
                    this,
                    TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }
    }   //setEnabled

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
    }   //postPeriodicTask

    public void preContinuousTask(TrcRobot.RunMode runMode)
    {
        final String funcName = "preContinuousTask";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.TASK,
                    "mode=%s", runMode.toString());
        }

        if (enabled && !expired && HalTimer.getCurrentTime() >= expiredTime)
        {
            setEnabled(false);

            if (debugEnabled)
            {
                dbgTrace.traceInfo(
                        funcName,
                        "Time expired, notifying %s.",
                        notifyEvent != null? notifyEvent.getName(): "null");
            }

            if (notifyEvent != null)
            {
                notifyEvent.set(true);
                notifyEvent = null;
            }
            expiredTime = 0.0;
            expired = true;
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }
    }   //preContinuousTask

    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}   //class DbgTimer
