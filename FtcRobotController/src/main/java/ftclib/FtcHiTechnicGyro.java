package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalUtil;
import trclib.TrcDbgTrace;
import trclib.TrcFilter;
import trclib.TrcGyro;
import trclib.TrcSensorData;

/**
 * This class implements the HiTechnic gyro. It supports only the z axis.
 * It provides rotation rate data but not heading and it does not support
 * built-in calibration.
 */
public class FtcHiTechnicGyro extends TrcGyro
{
    private static final String moduleName = "FtcHiTechnicGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private GyroSensor gyro;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param hardwareMap specifies the global hardware map.
     * @param instanceName specifies the instance name.
     * @param filters specifies an array of filters to use for filtering
     *                sensor noise, one for each axis. Since we only have
     *                1 axis, the array should have 1 element. If no
     *                filters are used, it can be set to null.
     */
    public FtcHiTechnicGyro(HardwareMap hardwareMap, String instanceName, TrcFilter[] filters)
    {
        super(instanceName, GYRO_HAS_Z_AXIS | GYRO_INTEGRATE_Z | GYRO_DO_CALIBRATION, filters);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        gyro = hardwareMap.gyroSensor.get(instanceName);
        setEnabled(true);
    }   //FtcHiTechnicGyro

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param filters specifies an array of filters to use for filtering
     *                sensor noise, one for each axis. Since we only have
     *                1 axis, the array should have 1 element. If no
     *                filters are used, it can be set to null.
     */
    public FtcHiTechnicGyro(String instanceName, TrcFilter[] filters)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, filters);
    }   //FtcHiTechnicGyro

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public FtcHiTechnicGyro(String instanceName)
    {
        this(instanceName, null);
    }   //FtcHiTechnicGyro

    //
    // Implements TrcGyro abstract methods.
    //

    /**
     * This method returns the raw rotation rate of the x-axis which is not supported.
     *
     * @return null.
     */
    @Override
    public TrcSensorData getRawXRate()
    {
        final String funcName = "getRawXRate";

        gyro.notSupported();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        return null;
    }   //getRawXRate

    /**
     * This method returns the raw rotation rate of the y-axis which is not supported.
     *
     * @return null.
     */
    @Override
    public TrcSensorData getRawYRate()
    {
        final String funcName = "getRawYRate";

        gyro.notSupported();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        return null;
    }   //getRawYRate

    /**
     * This method returns the raw rotation rate of the z-axis.
     *
     * @return raw z rotation rate.
     */
    @Override
    public TrcSensorData getRawZRate()
    {
        final String funcName = "getRawZRate";
        TrcSensorData data = new TrcSensorData(HalUtil.getCurrentTime(), gyro.getRotation());

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
    }   //getRawZRate

    /**
     * This method returns the raw heading of the x-axis which is not supported.
     *
     * @return null.
     */
    @Override
    public TrcSensorData getRawXHeading()
    {
        final String funcName = "getRawXHeading";

        gyro.notSupported();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        return null;
    }   //getRawXHeading

    /**
     * This method returns the raw heading of the y-axis which is not supported.
     *
     * @return null.
     */
    @Override
    public TrcSensorData getRawYHeading()
    {
        final String funcName = "getRawYHeading";

        gyro.notSupported();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        return null;
    }   //getRawYHeading

    /**
     * This method returns the raw heading of the z-axis which is not supported.
     *
     * @return null.
     */
    @Override
    public TrcSensorData getRawZHeading()
    {
        final String funcName = "getRawZHeading";

        gyro.notSupported();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        return null;
    }   //getRawZHeading

}   //class FtcHiTechnicGyro
