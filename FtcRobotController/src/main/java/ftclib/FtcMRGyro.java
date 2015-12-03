package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalUtil;
import trclib.TrcDbgTrace;
import trclib.TrcFilter;
import trclib.TrcGyro;
import trclib.TrcSensorData;

/**
 * This class implements the Modern Robotics gyro extending TrcGyro.
 * It provides implementation of the abstract methods in TrcGyro.
 * The Modern Robotics gyro supports 3 axes: x, y and z. It provides
 * rotation rate data for all 3 axes. However, it only provides heading
 * data for the z-axis and the heading data is wrap-around.
 */
public class FtcMRGyro extends TrcGyro
{
    private static final String moduleName = "FtcMRGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private GyroSensor gyro;

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
    public FtcMRGyro(HardwareMap hardwareMap, String instanceName, TrcFilter[] filters)
    {
        super(instanceName,
              GYRO_HAS_X_AXIS | GYRO_HAS_Y_AXIS | GYRO_HAS_Z_AXIS | GYRO_UNWRAP_ZHEADING,
              filters);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        gyro = hardwareMap.gyroSensor.get(instanceName);
        //
        // Set the wrap-around range of the Z heading.
        //
        setZValueRange(0.0, 360.0);
        setEnabled(true);
    }   //FtcMRGyro

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param filters specifies an array of filters to use for filtering
     *                sensor noise, one for each axis. Since we have 3 axes,
     *                the array should have 3 elements. If no filters are
     *                used, it can be set to null.
     */
    public FtcMRGyro(String instanceName, TrcFilter[] filters)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, filters);
    }   //FtcMRGyro

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public FtcMRGyro(String instanceName)
    {
        this(instanceName, null);
    }   //FtcMRGyro

    //
    // Overriding TrcGyro methods.
    //

    /**
     * This method overrides the TrcGyro's built-in calibrator and calls its own.
     */
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

    /**
     * This method overrides the TrcGyro's built-in calibrator and calls its own.
     */
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

    /**
     * This method overrides the TrcGyro class. It doesn't have an x-integrator.
     */
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

    /**
     * This method overrides the TrcGyro class. It doesn't have an y-integrator.
     */
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

    /**
     * This method overrides the TrcGyro class and calls its own.
     */
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

    /**
     * This method returns the raw rotation rate of the x-axis.
     *
     * @return raw x rotation rate.
     */
    @Override
    public TrcSensorData getRawXRate()
    {
        final String funcName = "getRawXRate";
        TrcSensorData data = new TrcSensorData(HalUtil.getCurrentTime(), gyro.rawX());

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
    }   //getRawXRate

    /**
     * This method returns the raw rotation rate of the y-axis.
     *
     * @return raw y rotation rate.
     */
    @Override
    public TrcSensorData getRawYRate()
    {
        final String funcName = "getRawYRate";
        TrcSensorData data = new TrcSensorData(HalUtil.getCurrentTime(), gyro.rawY());

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
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
        TrcSensorData data = new TrcSensorData(HalUtil.getCurrentTime(), gyro.rawZ());

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
     * This method returns the raw heading of the z-axis.
     *
     * @return raw z heading.
     */
    @Override
    public TrcSensorData getRawZHeading()
    {
        final String funcName = "getRawZHeading";
        TrcSensorData data = new TrcSensorData(HalUtil.getCurrentTime(), gyro.getHeading());

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value:%f", data.timestamp, data.value);
        }

        return data;
    }   //getRawZHeading

}   //class FtcMRGyro
