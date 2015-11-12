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
    private double output = 0.0;

    public TrcIntegrator(String instanceName, TrcFilteredSensor filteredSensor)
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
        prevTime = HalUtil.getCurrentTime();

        TrcTaskMgr.getInstance().registerTask(
                instanceName, this, TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
    }   //TrcIntegrator

    public String toString()
    {
        return instanceName;
    }   //toString

    public void reset()
    {
        final String funcName = "reset";

        sampleData();
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

    private void sampleData()
    {
        final String funcName = "sampleData";

        double currTime = HalUtil.getCurrentTime();
        double input = filteredSensor.getFilteredValue();
        output += input*(currTime - prevTime);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC,
                               "! (input=%f,output=%f,deltaTime=%f)",
                               input, output, currTime - prevTime);
        }

        prevTime = currTime;
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
