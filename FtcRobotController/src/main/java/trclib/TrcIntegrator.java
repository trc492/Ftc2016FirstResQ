package trclib;

import hallib.HalUtil;

public class TrcIntegrator implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcIntegrator";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private TrcFilteredSensor filteredSensor;
    private double prevTime = 0.0;
    private double intermediateOutput = 0.0;
    private double output = 0.0;
    private boolean doDoubleIntegration = false;

    public TrcIntegrator(
            String instanceName, TrcFilteredSensor filteredSensor, boolean doDoubleIntegration)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
        this.filteredSensor = filteredSensor;
        this.doDoubleIntegration = doDoubleIntegration;
        prevTime = HalUtil.getCurrentTime();
    }   //TrcIntegrator

    public TrcIntegrator(String instanceName, TrcFilteredSensor filteredSensor)
    {
        this(instanceName, filteredSensor, false);
    }   //TrcIntegrator

    public String toString()
    {
        return instanceName;
    }   //toString

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

    public void reset()
    {
        final String funcName = "reset";

        sampleData();
        intermediateOutput = 0.0;
        output = 0.0;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //reset

    public double getOutput()
    {
        final String funcName = "getOutput";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", output);
        }

        return output;
    }   //getOutput

    public double getIntermediateOutput()
    {
        final String funcName = "getIntermediateOutput";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", intermediateOutput);
        }

        return intermediateOutput;
    }   //getIntermediateOutput

    private void sampleData()
    {
        final String funcName = "sampleData";

        TrcSensorData input = filteredSensor.getFilteredValue();
        double deltaTime = input.timestamp - prevTime;
        intermediateOutput += input.data*deltaTime;
        if (doDoubleIntegration)
        {
            output += intermediateOutput*deltaTime;
        }
        else
        {
            output = intermediateOutput;
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC,
                               "! (input=%f,intermediateOutput=%f,output=%f,deltaTime=%f)",
                               input, intermediateOutput, output, input.timestamp - prevTime);
        }

        prevTime = input.timestamp;
    }   //sampleData

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

        sampleData();

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }
    }   //preContinuousTask

    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}   //class TrcIntegrator
