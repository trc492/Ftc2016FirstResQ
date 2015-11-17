package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalUtil;
import trclib.TrcSensorAxisData;
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
        setEnabled(true);
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
    public TrcSensorAxisData getRawRates()
    {
        final String funcName = "getRawRates";
        TrcSensorAxisData rawRates = new TrcSensorAxisData(
                0.0, 0.0, gyro.getRotation(), HalUtil.getCurrentTime());

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "x=%f,y=%f,z=%f", rawRates.x, rawRates.y, rawRates.z);
        }

        return rawRates;
    }   //getRawRates

    @Override
    public TrcSensorAxisData getRawHeadings()
    {
        final String funcName = "getRawHeadings";
        TrcSensorAxisData rawHeadings = new TrcSensorAxisData(
                0.0, 0.0, gyro.getHeading(), HalUtil.getCurrentTime());

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "x=%f,y=%f,z=%f", rawHeadings.x, rawHeadings.y, rawHeadings.z);
        }

        return rawHeadings;
    }   //getRawHeadings

}   //class FtcMRGyro
