package trclib;

public class TrcWrapAroundHandler implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcWrapAroundHandler";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private TrcFilteredSensor filteredSensor;
    private double lowValue;
    private double highValue;
    private double wrapThreshold;
    private double prevValue = 0.0;
    private int numRevolutions = 0;

    public TrcWrapAroundHandler(
            final String instanceName,
            TrcFilteredSensor filteredSensor,
            double lowValue, double highValue)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        if (filteredSensor == null)
        {
            throw new NullPointerException("filteredSensor must be provided");
        }

        this.instanceName = instanceName;
        this.filteredSensor = filteredSensor;
        this.lowValue = lowValue;
        this.highValue = highValue;
        this.wrapThreshold = (highValue - lowValue)/2.0;
    }   //TrcWrapAroundHandler

    public void setEnabled(boolean enabled)
    {
        final String funcName = "setEnabled";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "enabled=%s", Boolean.toString(enabled));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (enabled)
        {
            TrcTaskMgr.getInstance().registerTask(
                    instanceName, this, TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
        }
        else
        {
            TrcTaskMgr.getInstance().unregisterTask(this, TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
        }
    }   //setEnabled

    public double getCumulatedValue()
    {
        final String funcName = "getCumulatedValue";
        double value = (highValue - lowValue)*numRevolutions +
                       (filteredSensor.getFilteredValue().data - lowValue);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getCumulatedValue

    public void reset()
    {
        final String funcName = "reset";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        numRevolutions = 0;
    }   //reset

    public String toString()
    {
        return instanceName;
    }   //toString

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

        double currValue = filteredSensor.getFilteredValue().data;
        if (Math.abs(currValue - prevValue) > wrapThreshold)
        {
            if (currValue > prevValue)
            {
                numRevolutions--;
            }
            else
            {
                numRevolutions++;
            }
        }
        prevValue = currValue;

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }
    }   //preContinuousTask

    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}   //class TrcWrapAroundHandler
