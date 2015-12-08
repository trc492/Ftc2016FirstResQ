package trclib;

/**
 * This class implements a platform independent accelerometer. Typically, this
 * class is extended by a platform dependent accelerometer class. The platform
 * dependent accelerometer class must implement the abstract methods required
 * by this class. The abstract methods allow this class to get raw data for each
 * accelerometer axis.
 * Depending on the options specified in the constructor, this class creates a
 * calibrator, a data processor and an integrator.
 * The platform dependent accelerometer can specify how many axes it supports
 * by setting the HAS_AXIS options. If it does not provide velocity or distance
 * data, it can set the INTEGRATE and DOUBLE_INTEGRATE options and let the
 * built-in integrator handle it. If it supports its own calibration, it can
 * override the calibrate() and isCalibrating() methods to call its own. Otherwise,
 * it can set the DO_CALIBRATION option
 * to enable the built-in calibrator.
 */
public abstract class TrcAccelerometer implements TrcSensorData.DataProvider
{
    /**
     * This abstract method returns the raw acceleration of the x-axis.
     *
     * @return raw acceleration of x-axis.
     */
    public abstract TrcSensorData getRawXAcceleration();

    /**
     * This abstract method returns the raw acceleration of the y-axis.
     *
     * @return raw acceleration of y-axis.
     */
    public abstract TrcSensorData getRawYAcceleration();

    /**
     * This abstract method returns the raw acceleration of the z-axis.
     *
     * @return raw acceleration of z-axis.
     */
    public abstract TrcSensorData getRawZAcceleration();

    /**
     * This abstract method returns the raw velocity of the x-axis.
     *
     * @return raw velocity of x-axis.
     */
    public abstract TrcSensorData getRawXVelocity();

    /**
     * This abstract method returns the raw velocity of the y-axis.
     *
     * @return raw velocity of y-axis.
     */
    public abstract TrcSensorData getRawYVelocity();

    /**
     * This abstract method returns the raw velocity of the z-axis.
     *
     * @return raw velocity of z-axis.
     */
    public abstract TrcSensorData getRawZVelocity();

    /**
     * This abstract method returns the raw distance of the x-axis.
     *
     * @return raw distance of x-axis.
     */
    public abstract TrcSensorData getRawXDistance();

    /**
     * This abstract method returns the raw distance of the y-axis.
     *
     * @return raw distance of y-axis.
     */
    public abstract TrcSensorData getRawYDistance();

    /**
     * This abstract method returns the raw distance of the z-axis.
     *
     * @return raw distance of z-axis.
     */
    public abstract TrcSensorData getRawZDistance();

    //
    // Accelerometer options.
    //
    public static final int ACCEL_HAS_X_AXIS            = (1 << 0);
    public static final int ACCEL_HAS_Y_AXIS            = (1 << 1);
    public static final int ACCEL_HAS_Z_AXIS            = (1 << 2);
    public static final int ACCEL_INTEGRATE_X           = (1 << 3);
    public static final int ACCEL_INTEGRATE_Y           = (1 << 4);
    public static final int ACCEL_INTEGRATE_Z           = (1 << 5);
    public static final int ACCEL_DOUBLE_INTEGRATE_X    = (1 << 6);
    public static final int ACCEL_DOUBLE_INTEGRATE_Y    = (1 << 7);
    public static final int ACCEL_DOUBLE_INTEGRATE_Z    = (1 << 8);
    public static final int ACCEL_DO_CALIBRATION        = (1 << 9);

    //
    // Data names that the data provider must provide data for.
    //
    private static final String DATANAME_RAW_XACCEL         = "rawXAccel";
    private static final String DATANAME_RAW_YACCEL         = "rawYAccel";
    private static final String DATANAME_RAW_ZACCEL         = "rawZAccel";
    private static final String DATANAME_PROCESSED_XACCEL   = "processedXAccel";
    private static final String DATANAME_PROCESSED_YACCEL   = "processedYAccel";
    private static final String DATANAME_PROCESSED_ZACCEL   = "processedZAccel";

    //
    // Built-in calibrator parameters.
    //
    private static final int NUM_CAL_SAMPLES            = 100;
    private static final long CAL_INTERVAL              = 10;   //in msec.

    private static final String moduleName = "TrcAccelerometer";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private int xIndex = -1;
    private int yIndex = -1;
    private int zIndex = -1;
    private final String instanceName;
    private int options;
    private TrcDataProcessor dataProcessor = null;
    private TrcDataIntegrator dataIntegrator = null;
    private TrcDataCalibrator calibrator = null;
    private double[] zeroOffsets = null;
    private double[] deadbands = null;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param options specifies the gyro options. Multiple options can be OR'd together.
     *                ACCEL_HAS_X_AXIS - supports x-axis.
     *                ACCEL_HAS_Y_AXIS - supports y-axis.
     *                ACCEL_HAS_Z_AXIS - supports z-axis.
     *                ACCEL_INTEGRATE_X - do integration on x-axis to get x velocity.
     *                ACCEL_INTEGRATE_Y - do integration on y-axis to get y velocity.
     *                ACCEL_INTEGRATE_Z - do integration on z-axis to get z velocity.
     *                ACCEL_DOUBLE_INTEGRATE_X - do double integration on x-axis to get x distance.
     *                ACCEL_DOUBLE_INTEGRATE_Y - do double integration on y-axis to get y distance.
     *                ACCEL_DOUBLE_INTEGRATE_Z - do double integration on z-axis to get z distance.
     *                ACCEL_DO_CALIBRATION - do calibration on the accelerometer.
     * @param filters specifies an array of filter objects one for each supported axis.
     *                It is assumed that the order of the filters in the array is x, y
     *                and then z. If an axis is specified in the options but no filter
     *                will be used on that axis, the corresponding element in the array
     *                should be set to null. If no filter is used at all, filters can
     *                be set to null.
     */
    public TrcAccelerometer(final String instanceName, final int options, TrcFilter[] filters)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        //
        // Count the number of axes and set up the indices for each axis.
        //
        int numAxes = 0;

        if ((options & ACCEL_HAS_X_AXIS) != 0)
        {
            xIndex = numAxes;
            numAxes++;
        }

        if ((options & ACCEL_HAS_Y_AXIS) != 0)
        {
            yIndex = numAxes;
            numAxes++;
        }

        if ((options & ACCEL_HAS_Z_AXIS) != 0)
        {
            zIndex = numAxes;
            numAxes++;
        }

        //
        // Make sure we have at least one axis.
        //
        if (numAxes == 0)
        {
            throw new IllegalArgumentException("options must specify at least one axis.");
        }

        //
        // If no filters are provided, create an array of null filters.
        //
        if (filters == null)
        {
            filters = new TrcFilter[numAxes];
        }

        //
        // Make sure the filter array must have numAxes elements.
        // Even if we don't filter on some axes, we still must have numAxes elements
        // but the elements of those axes can be null.
        //
        if (filters.length != numAxes)
        {
            throw new IllegalArgumentException(
                    String.format("filters must be an array of %d elements.", numAxes));
        }

        this.instanceName = instanceName;
        this.options = options;

        //
        // Create and initialize the array of data providers, one for each axis.
        // This class is the data provider for all.
        //
        TrcSensorData.DataProvider[] dataProviders = new TrcSensorData.DataProvider[numAxes];

        if (xIndex != -1)
        {
            dataProviders[xIndex] = this;
        }

        if (yIndex != -1)
        {
            dataProviders[yIndex] = this;
        }

        if (zIndex != -1)
        {
            dataProviders[zIndex] = this;
        }

        //
        // Create the data processor with the given filter array.
        //
        dataProcessor = new TrcDataProcessor(instanceName, filters);

        //
        // Create the data integrator. Data integrator needs data providers to
        // provide processed acceleration data for each axis.
        //
        if ((options & (ACCEL_INTEGRATE_X | ACCEL_INTEGRATE_Y | ACCEL_INTEGRATE_Z |
                        ACCEL_DOUBLE_INTEGRATE_X | ACCEL_DOUBLE_INTEGRATE_Y |
                        ACCEL_DOUBLE_INTEGRATE_Z)) != 0)
        {
            String[] dataNames = new String[numAxes];

            if ((xIndex != -1) && (options & (ACCEL_INTEGRATE_X | ACCEL_DOUBLE_INTEGRATE_X)) != 0)
            {
                dataNames[xIndex] = DATANAME_PROCESSED_XACCEL;
            }

            if ((yIndex != -1) && (options & (ACCEL_INTEGRATE_Y | ACCEL_DOUBLE_INTEGRATE_Y)) != 0)
            {
                dataNames[yIndex] = DATANAME_PROCESSED_YACCEL;
            }

            if ((zIndex != -1) && (options & (ACCEL_INTEGRATE_Z | ACCEL_DOUBLE_INTEGRATE_Z)) != 0)
            {
                dataNames[zIndex] = DATANAME_PROCESSED_ZACCEL;
            }

            dataIntegrator = new TrcDataIntegrator(
                    instanceName,
                    dataProviders,
                    dataNames,
                    (options & (ACCEL_DOUBLE_INTEGRATE_X |
                                ACCEL_DOUBLE_INTEGRATE_Y |
                                ACCEL_DOUBLE_INTEGRATE_Z)) != 0);
        }

        //
        // Create the data calibrator. Data calibrator needs data providers to
        // provide raw rate data for each axis.
        //
        if ((options & ACCEL_DO_CALIBRATION) != 0)
        {
            String[] dataNames = new String[numAxes];
            zeroOffsets = new double[numAxes];
            deadbands = new double[numAxes];

            if (xIndex != -1)
            {
                dataNames[xIndex] = DATANAME_RAW_XACCEL;
            }

            if (yIndex != -1)
            {
                dataNames[yIndex] = DATANAME_RAW_YACCEL;
            }

            if (zIndex != -1)
            {
                dataNames[zIndex] = DATANAME_RAW_ZACCEL;
            }

            calibrator = new TrcDataCalibrator(instanceName, dataProviders, dataNames);
        }
    }   //TrcAccelerometer

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param options specifies the gyro options. Multiple options can be OR'd together.
     *                ACCEL_HAS_X_AXIS - supports x-axis.
     *                ACCEL_HAS_Y_AXIS - supports y-axis.
     *                ACCEL_HAS_Z_AXIS - supports z-axis.
     *                ACCEL_INTEGRATE_X - do integration on x-axis to get x velocity.
     *                ACCEL_INTEGRATE_Y - do integration on y-axis to get y velocity.
     *                ACCEL_INTEGRATE_Z - do integration on z-axis to get z velocity.
     *                ACCEL_DOUBLE_INTEGRATE_X - do double integration on x-axis to get x distance.
     *                ACCEL_DOUBLE_INTEGRATE_Y - do double integration on y-axis to get y distance.
     *                ACCEL_DOUBLE_INTEGRATE_Z - do double integration on z-axis to get z distance.
     *                ACCEL_DO_CALIBRATION - do calibration on the accelerometer.
     */
    public TrcAccelerometer(final String instanceName, final int options)
    {
        this(instanceName, options, null);
    }   //TrcAccelerometer

    /**
     * This method returns the instance name.
     *
     * @return instance name.
     */
    public String toString()
    {
        return instanceName;
    }   //toString

    /**
     * This method enables/disables the processing of accelerometer data. It is not
     * automatically enabled when the TrcAccelerometer object is created. You need
     * to explicitly enable the it before data processing will start. As part of
     * enabling the accelerometer, calibrate() is also called. calibrate() may be
     * overridden by the platform dependent accelerometer if it is capable of doing
     * its own. Otherwise, calibrate will call the built-in calibrator to do the
     * calibration.
     * Enabling/disabling data processing for the gyro involves enabling/disabling
     * the integrator if it exist.
     *
     * @param enabled specifies true if enabling, false otherwise.
     */
    protected void setEnabled(boolean enabled)
    {
        final String funcName = "setEnabled";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "enabled=%s", Boolean.toString(enabled));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (enabled)
        {
            calibrate();
        }

        //
        // Enable/disable integrator.
        //
        if (dataIntegrator != null)
        {
            dataIntegrator.setEnabled(enabled);
        }
    }   //setEnabled

    /**
     * This method inverts the x-axis. This is useful if the orientation of
     * the accelerometer x-axis is such that the data goes the wrong direction.
     *
     * @param inverted specifies true to invert x-axis, false otherwise.
     */
    public void setXInverted(boolean inverted)
    {
        final String funcName = "setXInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        dataProcessor.setInverted(xIndex, inverted);
    }   //setXInverted

    /**
     * This method inverts the y-axis. This is useful if the orientation of
     * the accelerometer y-axis is such that the data goes the wrong direction.
     *
     * @param inverted specifies true to invert y-axis, false otherwise.
     */
    public void setYInverted(boolean inverted)
    {
        final String funcName = "setYInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        dataProcessor.setInverted(yIndex, inverted);
    }   //setYInverted

    /**
     * This method inverts the z-axis. This is useful if the orientation of
     * the accelerometer z-axis is such that the data goes the wrong direction.
     *
     * @param inverted specifies true to invert z-axis, false otherwise.
     */
    public void setZInverted(boolean inverted)
    {
        final String funcName = "setZInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        dataProcessor.setInverted(zIndex, inverted);
    }   //setZInverted

    /**
     * This method sets the scale factor for the data of the x-axis.
     *
     * @param scale specifies the x scale factor.
     */
    public void setXScale(double scale)
    {
        final String funcName = "setXScale";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "scale=%f", scale);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        dataProcessor.setScale(xIndex, scale);
    }   //setXScale

    /**
     * This method sets the scale factor for the data of the y-axis.
     *
     * @param scale specifies the y scale factor.
     */
    public void setYScale(double scale)
    {
        final String funcName = "setYScale";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "scale=%f", scale);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        dataProcessor.setScale(yIndex, scale);
    }   //setYScale

    /**
     * This method sets the scale factor for the data of the z-axis.
     *
     * @param scale specifies the z scale factor.
     */
    public void setZScale(double scale)
    {
        final String funcName = "setZScale";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "scale=%f", scale);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        dataProcessor.setScale(zIndex, scale);
    }   //setZScale

    /**
     * This method calls the built-in calibrator to calibrates the accelerometer.
     *
     * @param numCalSamples specifies the number of calibration samples to take.
     * @param calInterval specifies the interval in msec between samples.
     */
    public void calibrate(int numCalSamples, long calInterval)
    {
        final String funcName = "calibrate";

        if (calibrator != null)
        {
            calibrator.calibrate(numCalSamples, calInterval, zeroOffsets, deadbands);

            if ((options & ACCEL_HAS_X_AXIS) != 0)
            {
                dataProcessor.setCalibrationData(xIndex, zeroOffsets[xIndex], deadbands[xIndex]);
            }

            if ((options & ACCEL_HAS_Y_AXIS) != 0)
            {
                dataProcessor.setCalibrationData(yIndex, zeroOffsets[yIndex], deadbands[yIndex]);
            }

            if ((options & ACCEL_HAS_Z_AXIS) != 0)
            {
                dataProcessor.setCalibrationData(zIndex, zeroOffsets[zIndex], deadbands[zIndex]);
            }
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "numSamples=%d,calInterval=%d", numCalSamples, calInterval);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //calibrate

    /**
     * This method returns the acceleration on the x-axis.
     *
     * @return X acceleration.
     */
    public TrcSensorData getXAcceleration()
    {
        final String funcName = "getXAcceleration";
        TrcSensorData data = getSensorData(DATANAME_PROCESSED_XACCEL);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getXAcceleration

    /**
     * This method returns the acceleration on the y-axis.
     *
     * @return Y acceleration.
     */
    public TrcSensorData getYAcceleration()
    {
        final String funcName = "getYAcceleration";
        TrcSensorData data = getSensorData(DATANAME_PROCESSED_YACCEL);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getYAcceleration

    /**
     * This method returns the acceleration on the z-axis.
     *
     * @return Z acceleration.
     */
    public TrcSensorData getZAcceleration()
    {
        final String funcName = "getZAcceleration";
        TrcSensorData data = getSensorData(DATANAME_PROCESSED_ZACCEL);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getZAcceleration

    /**
     * This method returns the velocity of the x-axis. If there is an integrator,
     * we call the integrator to get the velocity else we call the platform dependent
     * accelerometer to get the raw velocity value.
     *
     * @return X velocity.
     */
    public TrcSensorData getXVelocity()
    {
        final String funcName = "getXVelocity";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getIntegratedData(xIndex);
        }
        else
        {
            data = getRawXVelocity();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getXVelocity

    /**
     * This method returns the velocity of the y-axis. If there is an integrator,
     * we call the integrator to get the velocity else we call the platform dependent
     * accelerometer to get the raw velocity value.
     *
     * @return Y velocity.
     */
    public TrcSensorData getYVelocity()
    {
        final String funcName = "getYVelocity";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getIntegratedData(yIndex);
        }
        else
        {
            data = getRawYVelocity();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getYVelocity

    /**
     * This method returns the velocity of the z-axis. If there is an integrator,
     * we call the integrator to get the velocity else we call the platform dependent
     * accelerometer to get the raw velocity value.
     *
     * @return Z velocity.
     */
    public TrcSensorData getZVelocity()
    {
        final String funcName = "getZVelocity";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getIntegratedData(zIndex);
        }
        else
        {
            data = getRawZVelocity();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getZVelocity

    /**
     * This method returns the distance of the x-axis. If there is an integrator,
     * we call the integrator to get the distance else we call the platform dependent
     * accelerometer to get the raw distance value.
     *
     * @return X distance.
     */
    public TrcSensorData getXDistance()
    {
        final String funcName = "getXDistance";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getDoubleIntegratedData(xIndex);
        }
        else
        {
            data = getRawXDistance();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getXDistance

    /**
     * This method returns the distance of the y-axis. If there is an integrator,
     * we call the integrator to get the distance else we call the platform dependent
     * accelerometer to get the raw distance value.
     *
     * @return Y distance.
     */
    public TrcSensorData getYDistance()
    {
        final String funcName = "getYDistance";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getDoubleIntegratedData(yIndex);
        }
        else
        {
            data = getRawYDistance();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getYDistance

    /**
     * This method returns the distance of the z-axis. If there is an integrator,
     * we call the integrator to get the distance else we call the platform dependent
     * accelerometer to get the raw distance value.
     *
     * @return Z distance.
     */
    public TrcSensorData getZDistance()
    {
        final String funcName = "getZDistance";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getDoubleIntegratedData(zIndex);
        }
        else
        {
            data = getRawZDistance();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getZDistance

    //
    // The following methods can be overridden by a platform dependent accelerometer class.
    //

    /**
     * This method calls the built-in calibrator to calibrates the accelerometer.
     * This method can be overridden by the platform dependent accelerometer to
     * provide its own calibration.
     */
    public void calibrate()
    {
        calibrate(NUM_CAL_SAMPLES, CAL_INTERVAL);
    }   //calibrate

    /**
     * This method always returns false because the built-in calibrator is synchronous.
     *
     * @return false.
     */
    public boolean isCalibrating()
    {
        final String funcName = "isCalibrating";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=false");
        }

        //
        // The built-in calibrator is synchronous, so we always return false.
        //

        return false;
    }   //isCalibrating

    /**
     * This method resets the integrator on the x-axis.
     */
    public void resetXIntegrator()
    {
        final String funcName = "resetXIntegrator";

        if (dataIntegrator != null)
        {
            dataIntegrator.reset(xIndex);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetXIntegrator

    /**
     * This method resets the integrator on the y-axis.
     */
    public void resetYIntegrator()
    {
        final String funcName = "resetYIntegrator";

        if (dataIntegrator != null)
        {
            dataIntegrator.reset(yIndex);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetYIntegrator

    /**
     * This method resets the integrator on the z-axis.
     */
    public void resetZIntegrator()
    {
        final String funcName = "resetZIntegrator";

        if (dataIntegrator != null)
        {
            dataIntegrator.reset(zIndex);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetZIntegrator

    //
    // Implements TrcSensorData.DataProvider interface.
    //

    /**
     * This method returns the sensor data idnetified by the given dataName. The
     * possible data returned can be raw acceleration, processed acceleration for
     * each axis.
     *
     * @param dataName specifies the data names to identify what sensor data to get.
     * @return sensor data.
     */
    @Override
    public TrcSensorData getSensorData(String dataName)
    {
        final String funcName = "getSensorData";
        TrcSensorData data = null;

        if (dataName.equals(DATANAME_RAW_XACCEL))
        {
            data = getRawXAcceleration();
        }
        else if (dataName.equals(DATANAME_RAW_YACCEL))
        {
            data = getRawYAcceleration();
        }
        else if (dataName.equals(DATANAME_RAW_ZACCEL))
        {
            data = getRawZAcceleration();
        }
        else if (dataName.equals(DATANAME_PROCESSED_XACCEL))
        {
            data = getRawXAcceleration();
            data.value = dataProcessor.processData(xIndex, data.value);
        }
        else if (dataName.equals(DATANAME_PROCESSED_YACCEL))
        {
            data = getRawYAcceleration();
            data.value = dataProcessor.processData(yIndex, data.value);
        }
        else if (dataName.equals(DATANAME_PROCESSED_ZACCEL))
        {
            data = getRawZAcceleration();
            data.value = dataProcessor.processData(zIndex, data.value);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getSensorData

}   //class TrcAccelerometer
