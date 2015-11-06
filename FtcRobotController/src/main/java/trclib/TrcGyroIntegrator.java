package trclib;

import ftclib.FtcOpMode;
import hallib.HalGyro;
import hallib.HalUtil;

public class TrcGyroIntegrator implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcGyroIntegrator";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private HalGyro gyro;
    private double prevTime = 0.0;
    private double zeroOffset = 0.0;
    private double deadband = 0.0;
    private double rate = 0.0;
    private double heading = 0.0;
    private boolean calibrating = false;
    private double sign = 1.0;

    public TrcGyroIntegrator(String instanceName, HalGyro gyro)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
        this.gyro = gyro;
        prevTime = HalUtil.getCurrentTime();

        TrcTaskMgr.getInstance().registerTask(
                instanceName, this, TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
    }   //TrcGyroIntegrator

    public void setInverted(boolean inverted)
    {
        final String funcName = "setInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        sign = inverted? -1.0: 1.0;
    }   //setInverted

    public void calibrate(int samples)
    {
        final String funcName = "calibrate";

        zeroOffset = 0.0;
        deadband = 0.0;
        double minValue = gyro.getRawZ();
        double maxValue = minValue;
        double sum = 0.0;

        calibrating = true;
        for (int i = 0; i < samples; i++)
        {
            double rate = gyro.getRawZ();
            sum += rate;
            if (rate < minValue)
            {
                minValue = rate;
            }
            else if (rate > maxValue)
            {
                maxValue = rate;
            }

            try
            {
                FtcOpMode.getInstance().waitOneFullHardwareCycle();
            }
            catch (InterruptedException e)
            {
            }
        }

        zeroOffset = sum/samples;
        deadband = (maxValue - minValue)/2.0;
        calibrating = false;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "samples=%d", samples);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "! (offset=%f,deadband=%f)", zeroOffset, deadband);
        }
    }   //calibrate

    public boolean isCalibrating()
    {
        final String funcName = "isCalibrating";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%s", Boolean.toString(calibrating));
        }

        return calibrating;
    }   //isCalibrating

    public void reset()
    {
        final String funcName = "reset";

        sampleData();
        heading = 0.0;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //reset

    public double getRotation()
    {
        final String funcName = "getRotation";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", rate);
        }

        return rate;
    }   //getRotation

    public double getHeading()
    {
        final String funcName = "getHeading";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", heading);
        }

        return heading;
    }   //getHeading

    private void sampleData()
    {
        final String funcName = "sampleData";

        double currTime = HalUtil.getCurrentTime();
        rate = sign*TrcUtil.applyDeadband(gyro.getRawZ() - zeroOffset, deadband);
        heading += rate*(currTime - prevTime);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC,
                               "! (rate=%f,heading=%f,deltaTime=%f)",
                               rate, heading, currTime - prevTime);
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

}   //class TrcGyroIntegrator
