package trclib;

public class TrcWrapAroundSensor implements TrcTaskMgr.Task
{
    public interface WrapAroundValue
    {
        public double getWrapAroundValue();
    }   //WrapAroundValue

    private static final String moduleName = "TrcWrapAroundSensor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private WrapAroundValue wrapAroundValue;
    private double lowValue;
    private double highValue;
    private double revThreshold;
    private double prevValue = 0.0;
    private int numRevolutions = 0;

    public TrcWrapAroundSensor(
            final String instanceName, WrapAroundValue wrapAroundValue,
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

        if (wrapAroundValue == null)
        {
            throw new NullPointerException("wrapAroundValue must be provided");
        }

        this.instanceName = instanceName;
        this.wrapAroundValue = wrapAroundValue;
        this.lowValue = lowValue;
        this.highValue = highValue;
        this.revThreshold = (highValue - lowValue)/2.0;

        TrcTaskMgr.getInstance().registerTask(
                instanceName,
                this,
                TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
    }   //TrcWrapAroundSensor

    public double getCumulatedValue()
    {
        final String funcName = "getCumulatedValue";
        double value = (highValue - lowValue)*numRevolutions +
                       (wrapAroundValue.getWrapAroundValue() - lowValue);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }

    public void reset()
    {
        numRevolutions = 0;
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

        double currValue = wrapAroundValue.getWrapAroundValue();
        if (Math.abs(currValue - prevValue) > revThreshold)
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

}   //class TrcWrapAroundSensor
