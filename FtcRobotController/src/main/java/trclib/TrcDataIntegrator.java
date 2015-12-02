package trclib;

import hallib.HalUtil;

/**
 * This class does data integration for sensors that have one or more axes.
 * Some value sensors such as gyros and accelerometers may need to integrate
 * their data to provide heading from gyro rotation rate, and velocity or
 * distance from accelerometer acceleration data. This class uses a periodic
 * task to do integration and optionally double integration.
 */
public class TrcDataIntegrator implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcDataIntegrator";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private final String instanceName;
    private TrcSensorData.DataProvider[] dataProviders = null;
    private String[] dataNames = null;
    private TrcSensorData[] inputData = null;
    private TrcSensorData[] integratedData = null;
    private TrcSensorData[] doubleIntegratedData = null;
    private double[] prevTimes = null;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param dataProviders specifies an array of data provider objects.
     * @param dataNames specifies an array of data names to be used to identify the data when
     *                  calling the data provider.
     * @param doubleIntegration specifies true to do double integration, false otherwise.
     */
    public TrcDataIntegrator(
            final String instanceName,
            TrcSensorData.DataProvider[] dataProviders,
            String[] dataNames,
            boolean doubleIntegration)
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

        inputData = new TrcSensorData[dataProviders.length];
        integratedData = new TrcSensorData[dataProviders.length];
        if (doubleIntegration)
        {
            doubleIntegratedData = new TrcSensorData[dataProviders.length];
        }
        prevTimes = new double[dataProviders.length];

        for (int i = 0; i < dataProviders.length; i++)
        {
            if (dataProviders[i] == null)
            {
                throw new NullPointerException("Elements in dataProviders cannot be null.");
            }

            integratedData[i] = new TrcSensorData(0.0, 0.0);
            if (doubleIntegratedData != null)
            {
                doubleIntegratedData[i] = new TrcSensorData(0.0, 0.0);
            }
            prevTimes[i] = 0.0;
        }
    }   //TrcDataProcessor

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param dataProviders specifies an array of data provider objects.
     * @param dataNames specifies an array of data names to be used to identify the data when
     *                  calling the data provider.
     */
    public TrcDataIntegrator(
            final String instanceName,
            TrcSensorData.DataProvider[] dataProviders,
            String[] dataNames)
    {
        this(instanceName, dataProviders, dataNames, false);
    }   //TrcDataProcessor

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
     * This method enables the data integrator. The data integrator is not
     * automatically enabled when created. You must explicitly call this
     * method to enable the data integrator.
     *
     * @param enabled specifies true for enabling the data processor, disabling it otherwise.
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
     * This method resets the indexed integratedData and doubleIntegratedData.
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

        prevTimes[index] = HalUtil.getCurrentTime();
        integratedData[index].value = 0.0;
        if (doubleIntegratedData != null)
        {
            doubleIntegratedData[index].value = 0.0;
        }
    }   //reset

    /**
     * This method resets all integratorData and doubleIntegratedData.
     */
    public void reset()
    {
        final String funcName = "reset";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        for (int i = 0; i < integratedData.length; i++)
        {
            prevTimes[i] = HalUtil.getCurrentTime();
            integratedData[i].value = 0.0;
            if (doubleIntegratedData != null)
            {
                doubleIntegratedData[i].value = 0.0;
            }
        }
    }   //reset

    /**
     * This method returns the last indexed input data.
     *
     * @param index specifies the index.
     * @return the last indexed input data.
     */
    public TrcSensorData getInputData(int index)
    {
        final String funcName = "getInputData";
        TrcSensorData data = new TrcSensorData(inputData[index].timestamp, inputData[index].value);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f",
                               data != null? data.timestamp: 0.0,
                               data != null? data.value: 0.0);
        }

        return data;
    }   //getInputData

    /**
     * This method returns the last indexed integrated data.
     *
     * @param index specifies the index.
     * @return last indexed integrated data.
     */
    public TrcSensorData getIntegratedData(int index)
    {
        final String funcName = "getIntegratedData";
        TrcSensorData data =
                new TrcSensorData(integratedData[index].timestamp, integratedData[index].value);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f",
                               data != null? data.timestamp: 0.0,
                               data != null? data.value: 0.0);
        }

        return data;
    }   //getIntegratedData

    /**
     * This method returns the last indexed double integrated data.
     *
     * @param index specifies the index.
     * @return last indexed double integrated data.
     */
    public TrcSensorData getDoubleIntegratedData(int index)
    {
        final String funcName = "getDoubleIntegratedData";
        TrcSensorData data = new TrcSensorData(
                    doubleIntegratedData[index].timestamp, doubleIntegratedData[index].value);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f",
                               data != null? data.timestamp: 0.0,
                               data != null? data.value: 0.0);
        }

        return data;
    }   //getDoubleIntegratedData

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
     * This method is called periodically to do data integration.
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

        for (int i = 0; i < inputData.length; i++)
        {
            //
            // Get sensor data.
            //
            inputData[i] = dataProviders[i].getSensorData(dataNames[i]);
            double deltaTime = inputData[i].timestamp - prevTimes[i];
            //
            // Do integration.
            //
            integratedData[i].timestamp = inputData[i].timestamp;
            integratedData[i].value += inputData[i].value*deltaTime;
            //
            // Do double integration if necessary.
            //
            if (doubleIntegratedData != null)
            {
                doubleIntegratedData[i].timestamp = inputData[i].timestamp;
                doubleIntegratedData[i].value = integratedData[i].value*deltaTime;
            }

            prevTimes[i] = inputData[i].timestamp;
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }
    }   //preContinuousTask

    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}   //class TrcDataIntegrator
