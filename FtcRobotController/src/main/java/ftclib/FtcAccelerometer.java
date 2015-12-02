package ftclib;

import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalUtil;
import trclib.TrcAccelerometer;
import trclib.TrcDbgTrace;
import trclib.TrcFilter;
import trclib.TrcSensorData;

/**
 * This class implements the platform dependent accelerometer.
 * It supports 3 axes: x, y and z. It provides acceleration data
 * for all 3 axes. However, it doesn't provide any velocity or
 * distance data.
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
              ACCEL_HAS_X_AXIS | ACCEL_HAS_Y_AXIS | ACCEL_HAS_Z_AXIS |
              ACCEL_INTEGRATE_X | ACCEL_INTEGRATE_Y | ACCEL_INTEGRATE_Z |
              ACCEL_DOUBLE_INTEGRATE_X | ACCEL_DOUBLE_INTEGRATE_Y | ACCEL_DOUBLE_INTEGRATE_Z |
              ACCEL_DO_CALIBRATION,
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
     * This method returns the raw acceleration of the x-axis.
     *
     * @return raw x acceleration.
     */
    @Override
    public TrcSensorData getRawXAcceleration()
    {
        final String funcName = "getRawXAcceleration";
        AccelerationSensor.Acceleration accelData = accel.getAcceleration();
        TrcSensorData data = new TrcSensorData(HalUtil.getCurrentTime(), accelData.x);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
    }   //getRawXAcceleration

    /**
     * This method returns the raw acceleration of the y-axis.
     *
     * @return raw y acceleration.
     */
    @Override
    public TrcSensorData getRawYAcceleration()
    {
        final String funcName = "getRawYAcceleration";
        AccelerationSensor.Acceleration accelData = accel.getAcceleration();
        TrcSensorData data = new TrcSensorData(HalUtil.getCurrentTime(), accelData.y);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
    }   //getRawYAcceleration

    /**
     * This method returns the raw acceleration of the z-axis.
     *
     * @return raw z acceleration.
     */
    @Override
    public TrcSensorData getRawZAcceleration()
    {
        final String funcName = "getRawZAcceleration";
        AccelerationSensor.Acceleration accelData = accel.getAcceleration();
        TrcSensorData data = new TrcSensorData(HalUtil.getCurrentTime(), accelData.z);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
    }   //getRawZAcceleration

    /**
     * This method returns the raw velocity on the x-axis which is not supported.
     *
     * @throws UnsupportedOperationException exception.
     */
    @Override
    public TrcSensorData getRawXVelocity()
    {
        final String funcName = "getRawXVelocity";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException(
                "This sensor does not support velocity data.");
    }   //getRawXVelocity

    /**
     * This method returns the raw velocity on the y-axis which is not supported.
     *
     * @throws UnsupportedOperationException exception.
     */
    @Override
    public TrcSensorData getRawYVelocity()
    {
        final String funcName = "getRawYVelocity";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException(
                "This sensor does not support velocity data.");
    }   //getRawYVelocity

    /**
     * This method returns the raw velocity on the z-axiswhich is not supported.
     *
     * @throws UnsupportedOperationException exception.
     */
    @Override
    public TrcSensorData getRawZVelocity()
    {
        final String funcName = "getRawZVelocity";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException(
                "This sensor does not support velocity data.");
    }   //getRawZVelocity

    /**
     * This method returns the raw distance on the x-axis which is not supported.
     *
     * @throws UnsupportedOperationException exception.
     */
    @Override
    public TrcSensorData getRawXDistance()
    {
        final String funcName = "getRawXDistance";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException(
                "This sensor does not support distance data.");
    }   //getRawXDistance

    /**
     * This method returns the raw distance on the y-axis which is not supported.
     *
     * @throws UnsupportedOperationException exception.
     */
    @Override
    public TrcSensorData getRawYDistance()
    {
        final String funcName = "getRawYDistance";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException(
                "This sensor does not support distance data.");
    }   //getRawYDistance

    /**
     * This method returns the raw distance on the z-axis which is not supported.
     *
     * @throws UnsupportedOperationException exception.
     */
    @Override
    public TrcSensorData getRawZDistance()
    {
        final String funcName = "getRawZDistance";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=null");
        }

        throw new UnsupportedOperationException(
                "This sensor does not support distance data.");
    }   //getRawZDistance

}   //class FtcAccelerometer
