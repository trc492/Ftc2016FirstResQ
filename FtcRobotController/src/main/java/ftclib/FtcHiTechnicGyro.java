package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalUtil;
import trclib.TrcDbgTrace;
import trclib.TrcFilter;
import trclib.TrcGyro;

/**
 * This class implements the HiTechnic gyro extending TrcGyro.
 * It provides implementation of the abstract methods in TrcGyro.
 * It supports only the z axis. It provides rotation rate data
 * but not heading and it does not support built-in calibration.
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
        super(instanceName, 1, GYRO_HAS_Z_AXIS | GYRO_INTEGRATE, filters);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        gyro = hardwareMap.gyroSensor.get(instanceName);
        calibrate();
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
     * @return throws UnsupportedOperation exception.
     */
    @Override
    public SensorData getRawXRate()
    {
        final String funcName = "getRawXRate";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException("HiTechnic gyro does not have an x-axis.");
    }   //getRawXRate

    /**
     * This method returns the raw rotation rate of the y-axis which is not supported.
     *
     * @return throws UnsupportedOperation exception.
     */
    @Override
    public SensorData getRawYRate()
    {
        final String funcName = "getRawYRate";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException("HiTechnic gyro does not have an y-axis.");
    }   //getRawYRate

    /**
     * This method returns the raw rotation rate of the z-axis.
     *
     * @return raw z rotation rate.
     */
    @Override
    public SensorData getRawZRate()
    {
        final String funcName = "getRawZRate";
        SensorData data = new SensorData(HalUtil.getCurrentTime(), gyro.getRotation());

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
     * @return throws UnsupportedOperation exception.
     */
    @Override
    public SensorData getRawXHeading()
    {
        final String funcName = "getRawXHeading";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException("HiTechnic gyro does not support heading data.");
    }   //getRawXHeading

    /**
     * This method returns the raw heading of the y-axis which is not supported.
     *
     * @return throws UnsupportedOperation exception.
     */
    @Override
    public SensorData getRawYHeading()
    {
        final String funcName = "getRawYHeading";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException("HiTechnic gyro does not support heading data.");
    }   //getRawYHeading

    /**
     * This method returns the raw heading of the z-axis which is not supported.
     *
     * @return throws UnsupportedOperation exception.
     */
    @Override
    public SensorData getRawZHeading()
    {
        final String funcName = "getRawZHeading";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException("HiTechnic gyro does not support heading data.");
    }   //getRawZHeading

}   //class FtcHiTechnicGyro
