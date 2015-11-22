package trclib;

public class TrcAnalogTrigger implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcAnalogTrigger";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    public enum Zone
    {
        UNKNOWN_ZONE,
        LOW_ZONE,
        MID_ZONE,
        HIGH_ZONE
    }   //enum Zone

    public interface TriggerHandler
    {
        public void AnalogTriggerEvent(
                TrcAnalogTrigger analogTrigger,
                Zone zone,
                double value);
    }   //interface TriggerHandler

    private String instanceName;
    private TrcAnalogInput analogInput;
    private double lowThreshold;
    private double highThreshold;
    private TriggerHandler eventHandler;
    private TrcKalmanFilter kalman = null;
    private boolean inverted = false;
    private double unitScale = 1.0;
    private Zone prevZone;

    public TrcAnalogTrigger(
            final String instanceName,
            TrcAnalogInput analogInput,
            double lowThreshold,
            double highThreshold,
            TriggerHandler eventHandler,
            boolean useFilter)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        if (analogInput == null || eventHandler == null)
        {
            throw new NullPointerException("AnalogInput/EventHandler must be provided");
        }

        this.instanceName = instanceName;
        this.analogInput = analogInput;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        this.eventHandler = eventHandler;
        if (useFilter)
        {
            kalman = new TrcKalmanFilter();
        }
        prevZone = Zone.UNKNOWN_ZONE;
    }   //TrcAnalogTrigger

    public TrcAnalogTrigger(
            final String instanceName,
            TrcAnalogInput analogInput,
            double threshold,
            TriggerHandler eventHandler,
            boolean useFilter)
    {
        this(instanceName, analogInput, threshold, threshold, eventHandler, useFilter);
    }   //TrcAnalogTrigger

    public void setThresholds(double lowThreshold, double highThreshold)
    {
        final String funcName = "setThreshold";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                    "lowThreshold=%f,highThreshold=%f",
                    lowThreshold, highThreshold);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
    }   //setThresholds

    public void setInverted(boolean inverted)
    {
        final String funcName = "setInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.inverted = inverted;
    }   //setInverted

    public void setScale(double scale)
    {
        final String funcName = "setScale";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "scale=%f", scale);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.unitScale = scale;
    }   //setScale

    public double getData()
    {
        final String funcName = "getData";
        double data = analogInput.getValue()*unitScale;

        if (kalman != null)
        {
            data = kalman.filter(data);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.FUNC, "=%f", data);
        }

        return data;
    }   //getValue

    public void setEnabled(boolean enabled)
    {
        final String funcName = "setEnabled";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.FUNC,
                    "enabled=%s", Boolean.toString(enabled));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }

        TrcTaskMgr taskMgr = TrcTaskMgr.getInstance();
        if (enabled)
        {
            taskMgr.registerTask(instanceName, this, TrcTaskMgr.TaskType.PREPERIODIC_TASK);
        }
        else
        {
            taskMgr.unregisterTask(this, TrcTaskMgr.TaskType.PREPERIODIC_TASK);
        }
    }   //setEnabled

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
        final String funcName = "prePeriodic";
        double data = getData();

        Zone zone;
        if (data <= lowThreshold)
        {
            zone = inverted? Zone.HIGH_ZONE: Zone.LOW_ZONE;
        }
        else if (data <= highThreshold)
        {
            zone = Zone.MID_ZONE;
        }
        else
        {
            zone = inverted? Zone.LOW_ZONE: Zone.HIGH_ZONE;
        }

        if (zone != prevZone)
        {
            //
            // We have crossed to another zone, let's notify somebody.
            //
            prevZone = zone;
            if (eventHandler != null)
            {
                eventHandler.AnalogTriggerEvent(this, zone, data);
            }

            if (debugEnabled)
            {
                dbgTrace.traceInfo(
                        funcName, "%s entering %s (data=%f)",
                        instanceName, zone.toString(), data);
            }
        }
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
    }   //postContinuousTask

}   //class TrcAnalogTrigger
