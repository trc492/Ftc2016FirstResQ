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

    /**
     * This method calibrates the sensor.
     */
    public void calibrate()
    {
        calibrate(DataType.ROTATION_RATE);
    }   //calibrate

    //
    // Implements TrcGyro abstract methods.
    //

    /**
     * This method returns the raw data of the specified type for the x-axis
     * which is not supported.
     *
     * @param dataType specifies the data type.
     * @return throws UnsupportedOperation exception.
     */
    @Override
    public SensorData getRawXData(DataType dataType)
    {
        final String funcName = "getRawXData";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException("HiTechnic gyro does not support x-axis.");
    }   //getRawXData

    /**
     * This method returns the raw data of the specified type for the y-axis
     * which is not supported.
     *
     * @param dataType specifies the data type.
     * @return throws UnsupportedOperation exception.
     */
    @Override
    public SensorData getRawYData(DataType dataType)
    {
        final String funcName = "getRawYData";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException("HiTechnic gyro does not support y-axis.");
    }   //getRawYData

    /**
     * This method returns the raw data of the specified type for the z-axis.
     *
     * @param dataType specifies the data type.
     * @return raw data of the specified type for the z-axis.
     */
    @Override
    public SensorData getRawZData(DataType dataType)
    {
        final String funcName = "getRawZData";
        double value = 0.0;

        //
        // HiTechnic gyro supports only rotation rate.
        //
        if (dataType == DataType.ROTATION_RATE)
        {
            value = gyro.getRotation();
        }
        SensorData data = new SensorData(HalUtil.getCurrentTime(), value);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
    }   //getRawZData

}   //class FtcHiTechnicGyro
