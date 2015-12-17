package ftclib;

import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalUtil;
import trclib.TrcAccelerometer;
import trclib.TrcDbgTrace;
import trclib.TrcFilter;

/**
 * This class implements the platform dependent accelerometer extending
 * TrcAccelerometer. It provides implementation of the abstract methods
 * in TrcAccelerometer. It supports 3 axes: x, y and z. It provides
 * acceleration data for all 3 axes. However, it doesn't provide any
 * velocity or distance data.
 */
public class FtcAccelerometer extends TrcAccelerometer
{
    private static final String moduleName = "FtcAccelerometer";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private AccelerationSensor accel;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param hardwareMap specifies the global hardware map.
     * @param instanceName specifies the instance name.
     * @param filters specifies an array of filters to use for filtering
     *                sensor noise, one for each axis. Since we have 3 axes,
     *                the array should have 3 elements. If no filters are
     *                used, it can be set to null.
     */
    public FtcAccelerometer(HardwareMap hardwareMap, String instanceName, TrcFilter[] filters)
    {
        super(instanceName,
              3,
              ACCEL_HAS_X_AXIS | ACCEL_HAS_Y_AXIS | ACCEL_HAS_Z_AXIS |
              ACCEL_INTEGRATE | ACCEL_DOUBLE_INTEGRATE,
              filters);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        accel = hardwareMap.accelerationSensor.get(instanceName);
        setEnabled(true);
    }   //FtcAccelerometer

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param filters specifies an array of filters to use for filtering
     *                sensor noise, one for each axis. Since we have 3 axes,
     *                the array should have 3 elements. If no filters are
     *                used, it can be set to null.
     */
    public FtcAccelerometer(String instanceName, TrcFilter[] filters)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, filters);
    }   //FtcAccelerometer

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public FtcAccelerometer(String instanceName)
    {
        this(instanceName, null);
    }   //FtcAccelerometer

    //
    // Implements TrcAccelerometer abstract methods.
    //

    /**
     * This method returns the raw data of the specified type for the x-axis.
     *
     * @param dataType specifies the data type.
     * @return raw data of the specified type for the x-axis.
     */
    @Override
    public SensorData getRawXData(DataType dataType)
    {
        final String funcName = "getRawXData";
        SensorData data = null;

        if (dataType == DataType.ACCELERATION)
        {
            AccelerationSensor.Acceleration accelData = accel.getAcceleration();
            data = new SensorData(HalUtil.getCurrentTime(), accelData.x);
        }
        else
        {
            throw new UnsupportedOperationException(
                    "Accelerometer sensor does not provide velocity or distance data.");
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
    }   //getRawXData

    /**
     * This method returns the raw data of the specified type for the y-axis.
     *
     * @param dataType specifies the data type.
     * @return raw data of the specified type for the y-axis.
     */
    @Override
    public SensorData getRawYData(DataType dataType)
    {
        final String funcName = "getRawYData";
        SensorData data = null;

        if (dataType == DataType.ACCELERATION)
        {
            AccelerationSensor.Acceleration accelData = accel.getAcceleration();
            data = new SensorData(HalUtil.getCurrentTime(), accelData.y);
        }
        else
        {
            throw new UnsupportedOperationException(
                    "Accelerometer sensor does not provide velocity or distance data.");
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
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
        SensorData data = null;

        if (dataType == DataType.ACCELERATION)
        {
            AccelerationSensor.Acceleration accelData = accel.getAcceleration();
            data = new SensorData(HalUtil.getCurrentTime(), accelData.z);
        }
        else
        {
            throw new UnsupportedOperationException(
                    "Accelerometer sensor does not provide velocity or distance data.");
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
    }   //getRawZData

}   //class FtcAccelerometer
