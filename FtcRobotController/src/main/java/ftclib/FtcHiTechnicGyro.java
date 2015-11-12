package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import trclib.TrcDbgTrace;
import trclib.TrcGyro;

public class FtcHiTechnicGyro extends TrcGyro
{
    private static final String moduleName = "FtcHiTechnicGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private HardwareMap hardwareMap;
    private GyroSensor gyro;

    public FtcHiTechnicGyro(HardwareMap hardwareMap, String instanceName, boolean useFilter)
    {
        super(instanceName, GYROOPTION_INTEGRATE_Z | (useFilter? GYROOPTION_FILTER: 0));

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.hardwareMap = hardwareMap;
        gyro = hardwareMap.gyroSensor.get(instanceName);
        setEnabled(true);
    }   //FtcHiTechnicGyro

    public FtcHiTechnicGyro(String instanceName, boolean useFilter)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, useFilter);
    }   //FtcHiTechnicGyro

    public FtcHiTechnicGyro(String instanceName)
    {
        this(instanceName, false);
    }   //FtcHiTechnicGyro

    //
    // Implements TrcGyro abstract methods.
    //

    @Override
    public double getXRawRate()
    {
        final String funcName = "getXRawRate";
        double value = 0.0;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getXRawRate

    @Override
    public double getYRawRate()
    {
        final String funcName = "getYRawRate";
        double value = 0.0;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getYRawRate

    @Override
    public double getZRawRate()
    {
        final String funcName = "getZRawRate";
        double value = gyro.getRotation();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getZRawRate

    @Override
    public double getXRawHeading()
    {
        final String funcName = "getXRawHeading";
        double value = 0.0;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getXRawHeading

    @Override
    public double getYRawHeading()
    {
        final String funcName = "getYRawHeading";
        double value = 0.0;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getYRawHeading

    @Override
    public double getZRawHeading()
    {
        final String funcName = "getZRawHeading";
        double value = 0.0;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getZRawHeading

}   //class FtcHiTechnicGyro
