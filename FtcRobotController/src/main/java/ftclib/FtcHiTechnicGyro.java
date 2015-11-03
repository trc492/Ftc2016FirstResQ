package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalGyro;
import hallib.HalUtil;
import trclib.TrcDbgTrace;
import trclib.TrcKalmanFilter;
import trclib.TrcRobot;
import trclib.TrcTaskMgr;
import trclib.TrcUtil;

public class FtcHiTechnicGyro implements HalGyro, TrcTaskMgr.Task
{
    private static final String moduleName = "FtcHiTechnicGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final int NUM_CAL_SAMPLES = 100;
    private static final int CAL_INTERVAL = 10;

    private String instanceName;
    private TrcKalmanFilter kalman = null;
    private HardwareMap hardwareMap;
    private GyroSensor gyro;
    private double zeroOffset = 0.0;
    private double deadband = 0.0;
    private double rate = 0.0;
    private double heading = 0.0;
    private double prevTime = 0.0;
    private double sign = 1.0;

    public FtcHiTechnicGyro(String instanceName, boolean useFilter)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
        if (useFilter)
        {
            kalman = new TrcKalmanFilter();
        }
        hardwareMap = FtcOpMode.getInstance().hardwareMap;
        gyro = hardwareMap.gyroSensor.get(instanceName);
        calibrate(NUM_CAL_SAMPLES, CAL_INTERVAL);
        prevTime = HalUtil.getCurrentTime();

        TrcTaskMgr.getInstance().registerTask(
                instanceName, this, TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
    }   //FtcHiTechnicGyro

    public FtcHiTechnicGyro(String instanceName)
    {
        this(instanceName, false);
    }   //FtcHiTechnicGyro

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

    public void calibrate(int samples, long interval)
    {
        final String funcName = "calibrate";

        zeroOffset = 0.0;
        deadband = 0.0;
        double minValue = 1024.0;
        double maxValue = -1024.0;
        double sum = 0.0;

        for (int i = 0; i < samples; i++)
        {
            double rate = gyro.getRotation();
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
                Thread.sleep(interval);
            }
            catch (InterruptedException e)
            {
            }
        }

        zeroOffset = sum/samples;
        deadband = (maxValue - minValue)/2.0;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "samples=%d,interval=%d", samples, interval);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "! (offset=%f,deadband=%f)", zeroOffset, deadband);
        }
    }   //calibrate

    private void sampleData()
    {
        final String funcName = "sampleData";

        double currTime = HalUtil.getCurrentTime();
        rate = gyro.getRotation();

        rate -= zeroOffset;
        rate = sign*TrcUtil.applyDeadband(rate, deadband);
        if (kalman != null)
        {
            rate = kalman.filter(rate);
        }
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

    //
    // Implements HalGyro.
    //

    @Override
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

    @Override
    public double getAngle()
    {
        final String funcName = "getAngle";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", heading);
        }

        return heading;
    }   //getAngle

    @Override
    public double getRate()
    {
        final String funcName = "getRate";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", rate);
        }

        return rate;
    }   //getRate

}   //class FtcHiTechnicGyro
