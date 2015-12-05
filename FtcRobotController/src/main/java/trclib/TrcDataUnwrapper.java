package trclib;

/**
 * This class unwraps data for sensors that have one or more axes. Some value
 * sensors such as the Modern Robotics gyro returns the heading values between
 * 0.0 and 360.0. When the gyro crosses the value range boundary, it wraps around.
 * For example, if the current heading is 0.0 and the gyro turns 1 degree to the
 * left, instead of giving you a value of -1.0, it wraps to the value of 359.0.
 * Similarly, if the current heading is 359.0 and the gyro turns 1, 2, ... degrees
 * to the right, instead of giving you a value of 360.0, 361.0, ... etc, it gives
 * you 0.0, 1.0, ... This is undesirable especially when the heading value is used
 * in PID controlled driving. For example, if the robot wants to go straight and
 * maintain the heading of zero and the robot turned left slightly with a heading
 * of 358.0, instead of turning right 2 degrees to get back to zero heading, the
 * robot will turn left all the way around to get back to zero.
 * This class implements a periodic task that monitor the sensor data. If it
 * crosses the value range boundary, it will keep track of the number of crossovers
 * and will adjust the value so it doesn't wrap.
 */
public class TrcDataUnwrapper implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcDataUnwrapper";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private final String instanceName;
    private TrcSensorData.DataProvider[] dataProviders = null;
    private String[] dataNames = null;
    private double[] valueRangeLows = null;
    private double[] valueRangeHighs = null;
    private TrcSensorData[] prevData = null;
    private int[] numCrossovers = null;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param dataProviders specifies an array of data provider objects.
     * @param dataNames specifies an array of data names to be used to identify the data when
     *                  calling the data provider.
     */
    public TrcDataUnwrapper(
            final String instanceName,
            TrcSensorData.DataProvider[] dataProviders,
            String[] dataNames)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        if (dataProviders == null || dataNames == null)
        {
            throw new NullPointerException("dataProviders/dataNames cannot be null.");
        }
        else if (dataProviders.length <= 0)
        {
            throw new IllegalArgumentException(
                    "dataProviders array must have at least one element.");
        }
        else if (dataProviders.length != dataNames.length)
        {
            throw new IllegalArgumentException(
                    "dataProviders/dataNames arrays must have same number of elements.");
        }

        this.instanceName = instanceName;
        this.dataProviders = dataProviders;
        this.dataNames = dataNames;

        valueRangeLows = new double[dataProviders.length];
        valueRangeHighs = new double[dataProviders.length];
        prevData = new TrcSensorData[dataProviders.length];
        numCrossovers = new int[dataProviders.length];

        for (int i = 0; i < dataProviders.length; i++)
        {
            if (dataProviders[i] == null)
            {
                throw new NullPointerException("Elements in dataProviders cannot be null.");
            }
            valueRangeLows[i] = 0.0;
            valueRangeHighs[i] = 0.0;
            prevData[i] = null;
            numCrossovers[i] = 0;
        }
    }   //TrcDataUnwrapper

    /**
     * This method returns the instance name.
     *
     * @return instance name.
     */
    public String toString()
    {
        return instanceName;
    }   //toString

    /**
     * This method enables the data unwrapper. The data unwrapper is not
     * automatically enabled when created. You must explicitly call this
     * method to enable the data unwrapper.
     *
     * @param enabled specifies true for enabling the data unwrapper, disabling it otherwise.
     */
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
            reset();
            TrcTaskMgr.getInstance().registerTask(
                    instanceName, this, TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
        }
        else
        {
            TrcTaskMgr.getInstance().unregisterTask(this, TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
        }
    }   //setEnabled

    /**
     * This method resets the indexed unwrapper.
     *
     * @param index specifies the index.
     */
    public void reset(int index)
    {
        final String funcName = "reset";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        prevData[index] = dataProviders[index].getSensorData(dataNames[index]);
        numCrossovers[index] = 0;
    }   //reset

    /**
     * This method resets the unwrapper of all axes.
     */
    public void reset()
    {
        final String funcName = "reset";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        for (int i = 0; i < dataProviders.length; i++)
        {
            if (dataNames[i] != null)
            {
                prevData[i] = dataProviders[i].getSensorData(dataNames[i]);
                numCrossovers[i] = 0;
            }
        }
    }   //reset

    /**
     * This method sets the value range of the indexed unwrapper.
     *
     * @param index specifes the index.
     * @param valueRangeLow specifies the low value of the range.
     * @param valueRangeHigh specifies the high value of the range.
     */
    public void setValueRange(int index, double valueRangeLow, double valueRangeHigh)
    {
        final String funcName = "setValueRange";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "low=%f,high=%f", valueRangeLow, valueRangeHigh);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (valueRangeLow > valueRangeHigh)
        {
            throw new IllegalArgumentException(
                    "valueRangeLow must not be greater than valueRangeHigh.");
        }

        valueRangeLows[index] = valueRangeLow;
        valueRangeHighs[index] = valueRangeHigh;
    }   //setValueRange

    /**
     * This method returns the indexed unwrapped data.
     *
     * @param index specifies the index.
     * @return unwrapped data.
     */
    public TrcSensorData getUnwrappedData(int index)
    {
        final String funcName = "getUnwrappedData";
        TrcSensorData data = dataProviders[index].getSensorData(dataNames[index]);

        data.value = (valueRangeHighs[index] - valueRangeLows[index])*numCrossovers[index] +
                     (data.value - valueRangeLows[index]);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f", data.timestamp, data.value);
        }

        return data;
    }   //getUnwrappedData

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

    /**
     * This method is called periodically to check for range crossovers.
     *
     * @param runMode specifies the competition mode that is running.
     */
    public void preContinuousTask(TrcRobot.RunMode runMode)
    {
        final String funcName = "preContinuousTask";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.TASK,
                    "mode=%s", runMode.toString());
        }

        for (int i = 0; i < dataProviders.length; i++)
        {
            if (dataNames[i] != null)
            {
                TrcSensorData currData = dataProviders[i].getSensorData(dataNames[i]);
                if (Math.abs(currData.value - prevData[i].value) >
                    (valueRangeHighs[i] - valueRangeLows[i])/2.0)
                {
                    if (currData.value > prevData[i].value)
                    {
                        numCrossovers[i]--;
                    }
                    else
                    {
                        numCrossovers[i]++;
                    }
                }

                prevData[i] = currData;
            }
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK,
                               "! (numCrossovers=%d)", numCrossovers);
        }
    }   //preContinuousTask

    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}   //class TrcDataUnwrapper
