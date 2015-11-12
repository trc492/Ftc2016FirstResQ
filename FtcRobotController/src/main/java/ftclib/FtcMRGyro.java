package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import trclib.TrcDbgTrace;
import trclib.TrcGyro;

public class FtcMRGyro extends TrcGyro
{
    private static final String moduleName = "FtcMRGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private HardwareMap hardwareMap;
    private GyroSensor gyro;

    public FtcMRGyro(HardwareMap hardwareMap, String instanceName, boolean useFilter)
    {
        super(instanceName, GYROOPTION_Z_WRAPAROUND | (useFilter? GYROOPTION_FILTER: 0));

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.hardwareMap = hardwareMap;
        gyro = hardwareMap.gyroSensor.get(instanceName);
    }   //FtcMRGyro

    public FtcMRGyro(String instanceName, boolean useFilter)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, useFilter);
    }   //FtcMRGyro

    public FtcMRGyro(String instanceName)
    {
        this(instanceName, false);
    }   //FtcMRGyro

    //
    // Overriding TrcGyro methods.
    //

    @Override
    public void calibrate()
    {
        final String funcName = "calibrate";

        gyro.calibrate();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //calibrate

    @Override
    public boolean isCalibrating()
    {
        final String funcName = "isCalibrating";
        boolean calibrating = gyro.isCalibrating();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(calibrating));
        }

        return calibrating;
    }   //isCalibrating

    @Override
    public void resetXIntegrator()
    {
        final String funcName = "resetXIntegrator";

        gyro.notSupported();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetXIntegrator

    @Override
    public void resetYIntegrator()
    {
        final String funcName = "resetYIntegrator";

        gyro.notSupported();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetYIntegrator

    @Override
    public void resetZIntegrator()
    {
        final String funcName = "resetZIntegrator";

        gyro.resetZAxisIntegrator();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetZIntegrator

    //
    // Implements TrcGyro abstract methods.
    //

    @Override
    public double getXRawRate()
    {
        final String funcName = "getXRawRate";
        double value = gyro.rawX();

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
        double value = gyro.rawY();

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
        double value = gyro.rawZ();

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
        double value = gyro.getHeading();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getZRawHeading

}   //class FtcMRGyro
