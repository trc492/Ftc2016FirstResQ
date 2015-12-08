package trclib;

/**
 * This class implements a platform independent gyro. Typically, this
 * class is extended by a platform dependent gyro class. The platform
 * dependent gyro class must implement the abstract methods required
 * by this class. The abstract methods allow this class to get raw
 * data for each gyro axis.
 * Depending on the options specified in the constructor, this class
 * creates a calibrator, a data processors, an integrator and an unwrapper.
 * The platform dependent gyro can specify how many axes it supports by
 * setting the HAS_AXIS options. If it does not provide heading data, it can
 * set the INTEGRATE options and let the built-in integrator handle it. Or
 * if the heading data it provides wrap-around, it can set the UNWRAP_HEADING
 * options to enable the unwrapper to unwrap the heading data. If it supports
 * its own calibration, it can override the calibrate() and isCalibrating()
 * methods to call its own. Otherwise, it can set the DO_CALIBRATION option
 * to enable the built-in calibrator.
 */
public abstract class TrcGyro implements TrcSensorData.DataProvider
{
    /**
     * This abstract method returns the raw rate of the x-axis.
     *
     * @return raw rate of x-axis.
     */
    public abstract TrcSensorData getRawXRate();

    /**
     * This abstract method returns the raw rate of the y-axis.
     *
     * @return raw rate of y-axis.
     */
    public abstract TrcSensorData getRawYRate();

    /**
     * This abstract method returns the raw rate of the z-axis.
     *
     * @return raw rate of z-axis.
     */
    public abstract TrcSensorData getRawZRate();

    /**
     * This abstract method returns the raw heading of the x-axis.
     *
     * @return raw heading of x-axis.
     */
    public abstract TrcSensorData getRawXHeading();

    /**
     * This abstract method returns the raw heading of the y-axis.
     *
     * @return raw heading of y-axis.
     */
    public abstract TrcSensorData getRawYHeading();

    /**
     * This abstract method returns the raw heading of the z-axis.
     *
     * @return raw heading of z-axis.
     */
    public abstract TrcSensorData getRawZHeading();

    //
    // Gyro options.
    //
    public static final int GYRO_HAS_X_AXIS             = (1 << 0);
    public static final int GYRO_HAS_Y_AXIS             = (1 << 1);
    public static final int GYRO_HAS_Z_AXIS             = (1 << 2);
    public static final int GYRO_INTEGRATE_X            = (1 << 3);
    public static final int GYRO_INTEGRATE_Y            = (1 << 4);
    public static final int GYRO_INTEGRATE_Z            = (1 << 5);
    public static final int GYRO_UNWRAP_XHEADING        = (1 << 6);
    public static final int GYRO_UNWRAP_YHEADING        = (1 << 7);
    public static final int GYRO_UNWRAP_ZHEADING        = (1 << 8);
    public static final int GYRO_DO_CALIBRATION         = (1 << 9);

    //
    // Data names that the data provider must provide data for.
    //
    private static final String DATANAME_RAW_XRATE          = "rawXRate";
    private static final String DATANAME_RAW_YRATE          = "rawYRate";
    private static final String DATANAME_RAW_ZRATE          = "rawZRate";
    private static final String DATANAME_PROCESSED_XRATE    = "processedXRate";
    private static final String DATANAME_PROCESSED_YRATE    = "processedYRate";
    private static final String DATANAME_PROCESSED_ZRATE    = "processedZRate";
    private static final String DATANAME_PROCESSED_XHEADING = "processedXHeading";
    private static final String DATANAME_PROCESSED_YHEADING = "processedYHeading";
    private static final String DATANAME_PROCESSED_ZHEADING = "processedZHeading";

    //
    // Built-in calibrator parameters.
    //
    private static final int NUM_CAL_SAMPLES            = 100;
    private static final long CAL_INTERVAL              = 10;   //in msec.

    private static final String moduleName = "TrcGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private int xIndex = -1;
    private int yIndex = -1;
    private int zIndex = -1;
    private final String instanceName;
    private int options;
    private TrcDataProcessor dataProcessor = null;
    private TrcDataIntegrator dataIntegrator = null;
    private TrcDataUnwrapper dataUnwrapper = null;
    private TrcDataCalibrator calibrator = null;
    private double[] zeroOffsets = null;
    private double[] deadbands = null;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param options specifies the gyro options. Multiple options can be OR'd together.
     *                GYRO_HAS_X_AXIS - supports x-axis.
     *                GYRO_HAS_Y_AXIS - supports y-axis.
     *                GYRO_HAS_Z_AXIS - supports z-axis.
     *                GYRO_INTEGRATE_X - do integration on x-axis to get x heading.
     *                GYRO_INTEGRATE_Y - do integration on y-axis to get y heading.
     *                GYRO_INTEGRATE_Z - do integration on z-axis to get z heading.
     *                GYRO_UNWRAP_XHEADING - unwrap heading of x-axis.
     *                GYRO_UNWRAP_YHEADING - unwrap heading of y-axis.
     *                GYRO_UNWRAP_ZHEADING - unwrap heading of z-axis.
     *                GYRO_DO_CALIBRATION - do calibration on the gyro.
     * @param filters specifies an array of filter objects one for each supported axis.
     *                It is assumed that the order of the filters in the array is x, y
     *                and then z. If an axis is specified in the options but no filter
     *                will be used on that axis, the corresponding element in the array
     *                should be set to null. If no filter is used at all, filters can
     *                be set to null.
     */
    public TrcGyro(final String instanceName, final int options, TrcFilter[] filters)
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

        if ((options & GYRO_HAS_X_AXIS) != 0)
        {
            xIndex = numAxes;
            numAxes++;
        }

        if ((options & GYRO_HAS_Y_AXIS) != 0)
        {
            yIndex = numAxes;
            numAxes++;
        }

        if ((options & GYRO_HAS_Z_AXIS) != 0)
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
        // provide processed rate data for each axis.
        //
        if ((options & (GYRO_INTEGRATE_X | GYRO_INTEGRATE_Y | GYRO_INTEGRATE_Z)) != 0)
        {
            String[] dataNames = new String[numAxes];

            if ((xIndex != -1) && (options & GYRO_INTEGRATE_X) != 0)
            {
                dataNames[xIndex] = DATANAME_PROCESSED_XRATE;
            }

            if (yIndex != -1 && (options & GYRO_INTEGRATE_Y) != 0)
            {
                dataNames[yIndex] = DATANAME_PROCESSED_YRATE;
            }

            if (zIndex != -1 && (options & GYRO_INTEGRATE_Z) != 0)
            {
                dataNames[zIndex] = DATANAME_PROCESSED_ZRATE;
            }

            dataIntegrator = new TrcDataIntegrator(instanceName, dataProviders, dataNames, false);
        }

        //
        // Create the data unwrapper. Data unwrapper needs data providers to
        // provide processed heading data for each axis. Integration of rate
        // and unwrapping of heading are mutually exclusive. If we are doing
        // software integration, the resulting heading is not wrap-around.
        // If we need to unwrap heading, the heading is from the physical
        // sensor and not from the integrator.
        //
        if ((options & (GYRO_UNWRAP_XHEADING | GYRO_UNWRAP_YHEADING | GYRO_UNWRAP_ZHEADING)) != 0)
        {
            String[] dataNames = new String[numAxes];

            if ((xIndex != -1) && (options & GYRO_UNWRAP_XHEADING) != 0)
            {
                if ((options & GYRO_INTEGRATE_X) != 0)
                {
                    throw new IllegalArgumentException(
                            "Options IntegrateX and UnwrapX cannot coexist.");
                }
                dataNames[xIndex] = DATANAME_PROCESSED_XHEADING;
            }

            if ((yIndex != -1) && (options & GYRO_UNWRAP_YHEADING) != 0)
            {
                if ((options & GYRO_INTEGRATE_Y) != 0)
                {
                    throw new IllegalArgumentException(
                            "Options IntegrateY and UnwrapY cannot coexist.");
                }
                dataNames[yIndex] = DATANAME_PROCESSED_YHEADING;
            }

            if ((zIndex != -1) && (options & GYRO_UNWRAP_ZHEADING) != 0)
            {
                if ((options & GYRO_INTEGRATE_Z) != 0)
                {
                    throw new IllegalArgumentException(
                            "Options IntegrateZ and UnwrapZ cannot coexist.");
                }
                dataNames[zIndex] = DATANAME_PROCESSED_ZHEADING;
            }

            dataUnwrapper = new TrcDataUnwrapper(instanceName, dataProviders, dataNames);
        }

        //
        // Create the data calibrator. Data calibrator needs data providers to
        // provide raw rate data for each axis.
        //
        if ((options & GYRO_DO_CALIBRATION) != 0)
        {
            String[] dataNames = new String[numAxes];
            zeroOffsets = new double[numAxes];
            deadbands = new double[numAxes];

            if (xIndex != -1)
            {
                dataNames[xIndex] = DATANAME_RAW_XRATE;
            }

            if (yIndex != -1)
            {
                dataNames[yIndex] = DATANAME_RAW_YRATE;
            }

            if (zIndex != -1)
            {
                dataNames[zIndex] = DATANAME_RAW_ZRATE;
            }

            calibrator = new TrcDataCalibrator(instanceName, dataProviders, dataNames);
        }
    }   //TrcGyro

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param options specifies the gyro options. Multiple options can be OR'd together.
     *                GYRO_HAS_X_AXIS - supports x-axis.
     *                GYRO_HAS_Y_AXIS - supports y-axis.
     *                GYRO_HAS_Z_AXIS - supports z-axis.
     *                GYRO_INTEGRATE_X - do integration on x-axis to get x heading.
     *                GYRO_INTEGRATE_Y - do integration on y-axis to get y heading.
     *                GYRO_INTEGRATE_Z - do integration on z-axis to get z heading.
     *                GYRO_UNWRAP_XHEADING - unwrap heading of x-axis.
     *                GYRO_UNWRAP_YHEADING - unwrap heading of y-axis.
     *                GYRO_UNWRAP_ZHEADING - unwrap heading of z-axis.
     *                GYRO_DO_CALIBRATION - do calibration on the gyro.
     */
    public TrcGyro(final String instanceName, final int options)
    {
        this(instanceName, options, null);
    }   //TrcGyro

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
     * This method enables/disables the processing of gyro data. It is not
     * automatically enabled when the TrcGyro object is created. You need
     * to explicitly enable the it before data processing will start. As
     * part of enabling the gyro, calibrate() is also called. calibrate()
     * may be overridden by the platform dependent gyro if it is capable
     * of doing its own. Otherwise, calibrate will call the built-in
     * calibrator to do the calibration.
     * Enabling/disabling data processing for the gyro involves
     * enabling/disabling the integrator and the unwrapper if
     * they exist.
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

        //
        // Enable/disable unwrapper.
        //
        if (dataUnwrapper != null)
        {
            dataUnwrapper.setEnabled(enabled);
        }
    }   //setEnabled

    /**
     * This method inverts the x-axis. This is useful if the orientation of
     * the gyro x-axis is such that the data goes the wrong direction.
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
     * the gyro y-axis is such that the data goes the wrong direction.
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
     * the gyro z-axis is such that the data goes the wrong direction.
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
     * This method sets the heading value range of the x-axis.
     * The value range is used by the unwrapper to unwrap heading
     * data.
     *
     * @param valueRangeLow specifies the low value of the x-axis range.
     * @param valueRangeHigh specifies the high value of the x-axis range.
     */
    public void setXValueRange(double valueRangeLow, double valueRangeHigh)
    {
        final String funcName = "setXValueRange";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "low=%f,high=%f", valueRangeLow, valueRangeHigh);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (dataUnwrapper != null)
        {
            dataUnwrapper.setValueRange(xIndex, valueRangeLow, valueRangeHigh);
        }
    }   //setXValueRange

    /**
     * This method sets the heading value range of the y-axis.
     * The value range is used by the unwrapper to unwrap heading
     * data.
     *
     * @param valueRangeLow specifies the low value of the y-axis range.
     * @param valueRangeHigh specifies the high value of the y-axis range.
     */
    public void setYValueRange(double valueRangeLow, double valueRangeHigh)
    {
        final String funcName = "setYValueRange";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "low=%f,high=%f", valueRangeLow, valueRangeHigh);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (dataUnwrapper != null)
        {
            dataUnwrapper.setValueRange(yIndex, valueRangeLow, valueRangeHigh);
        }
    }   //setYValueRange

    /**
     * This method sets the heading value range of the z-axis.
     * The value range is used by the unwrapper to unwrap heading
     * data.
     *
     * @param valueRangeLow specifies the low value of the z-axis range.
     * @param valueRangeHigh specifies the high value of the z-axis range.
     */
    public void setZValueRange(double valueRangeLow, double valueRangeHigh)
    {
        final String funcName = "setZValueRange";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "low=%f,high=%f", valueRangeLow, valueRangeHigh);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (dataUnwrapper != null)
        {
            dataUnwrapper.setValueRange(zIndex, valueRangeLow, valueRangeHigh);
        }
    }   //setZValueRange

    /**
     * This method calls the built-in calibrator to calibrates the gyro.
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

            if ((options & GYRO_HAS_X_AXIS) != 0)
            {
                dataProcessor.setCalibrationData(xIndex, zeroOffsets[xIndex], deadbands[xIndex]);
            }

            if ((options & GYRO_HAS_Y_AXIS) != 0)
            {
                dataProcessor.setCalibrationData(yIndex, zeroOffsets[yIndex], deadbands[yIndex]);
            }

            if ((options & GYRO_HAS_Z_AXIS) != 0)
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
     * This method resets the unwrapper on the x-axis.
     */
    public void resetXUnwrapper()
    {
        final String funcName = "resetXUnwrapper";

        if (dataUnwrapper != null)
        {
            dataUnwrapper.reset(xIndex);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetXUnwrapper

    /**
     * This method resets the unwrapper on the y-axis.
     */
    public void resetYUnwrapper()
    {
        final String funcName = "resetYUnwrapper";

        if (dataUnwrapper != null)
        {
            dataUnwrapper.reset(yIndex);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetYUnwrapper

    /**
     * This method resets the unwrapper on the z-axis.
     */
    public void resetZUnwrapper()
    {
        final String funcName = "resetZUnwrapper";

        if (dataUnwrapper != null)
        {
            dataUnwrapper.reset(zIndex);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetZUnwrapper

    /**
     * This method returns the rotation rate on the x-axis.
     *
     * @return X rotation rate.
     */
    public TrcSensorData getXRotationRate()
    {
        final String funcName = "getXRotationRate";
        TrcSensorData data = getSensorData(DATANAME_PROCESSED_XRATE);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getXRotationRate

    /**
     * This method returns the rotation rate on the y-axis.
     *
     * @return Y rotation rate.
     */
    public TrcSensorData getYRotationRate()
    {
        final String funcName = "getYRotationRate";
        TrcSensorData data = getSensorData(DATANAME_PROCESSED_YRATE);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getYRotationRate

    /**
     * This method returns the rotation rate on the z-axis.
     *
     * @return Z rotation rate.
     */
    public TrcSensorData getZRotationRate()
    {
        final String funcName = "getZRotationRate";
        TrcSensorData data = getSensorData(DATANAME_PROCESSED_ZRATE);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getZRotationRate

    /**
     * This method returns the heading of the x-axis. If there is an integrator,
     * we call the integrator to get the heading. Else if we have an unwrapper,
     * we call the unwrapper to get the heading else we call the platform dependent
     * gyro to get the raw heading value.
     *
     * @return X heading.
     */
    public TrcSensorData getXHeading()
    {
        final String funcName = "getXHeading";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getIntegratedData(xIndex);
        }
        else if (dataUnwrapper != null)
        {
            data = dataUnwrapper.getUnwrappedData(xIndex);
        }
        else
        {
            data = getRawXHeading();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getXHeading

    /**
     * This method returns the heading of the y-axis. If there is an integrator,
     * we call the integrator to get the heading. Else if we have an unwrapper,
     * we call the unwrapper to get the heading else we call the platform dependent
     * gyro to get the raw heading value.
     *
     * @return Y heading.
     */
    public TrcSensorData getYHeading()
    {
        final String funcName = "getYHeading";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getIntegratedData(yIndex);
        }
        else if (dataUnwrapper != null)
        {
            data = dataUnwrapper.getUnwrappedData(yIndex);
        }
        else
        {
            data = getRawYHeading();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getYHeading

    /**
     * This method returns the heading of the z-axis. If there is an integrator,
     * we call the integrator to get the heading. Else if we have an unwrapper,
     * we call the unwrapper to get the heading else we call the platform dependent
     * gyro to get the raw heading value.
     *
     * @return Z heading.
     */
    public TrcSensorData getZHeading()
    {
        final String funcName = "getZHeading";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getIntegratedData(zIndex);
        }
        else if (dataUnwrapper != null)
        {
            data = dataUnwrapper.getUnwrappedData(zIndex);
        }
        else
        {
            data = getRawZHeading();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getZHeading

    //
    // The following methods can be overridden by a platform dependent gyro class.
    //

    /**
     * This method calls the built-in calibrator to calibrates the gyro.
     * This method can be overridden by the platform dependent gyro to
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
     * possible data returned can be raw gyro rate, processed gyro rate or processed
     * unwrapped gyro heading for each axis.
     *
     * @param dataName specifies the data names to identify what sensor data to get.
     * @return sensor data.
     */
    @Override
    public TrcSensorData getSensorData(String dataName)
    {
        final String funcName = "getSensorData";
        TrcSensorData data = null;

        if (dataName.equals(DATANAME_RAW_XRATE))
        {
            data = getRawXRate();
        }
        else if (dataName.equals(DATANAME_RAW_YRATE))
        {
            data = getRawYRate();
        }
        else if (dataName.equals(DATANAME_RAW_ZRATE))
        {
            data = getRawZRate();
        }
        else if (dataName.equals(DATANAME_PROCESSED_XRATE))
        {
            data = getRawXRate();
            data.value = dataProcessor.processData(xIndex, data.value);
        }
        else if (dataName.equals(DATANAME_PROCESSED_YRATE))
        {
            data = getRawYRate();
            data.value = dataProcessor.processData(yIndex, data.value);
        }
        else if (dataName.equals(DATANAME_PROCESSED_ZRATE))
        {
            data = getRawZRate();
            data.value = dataProcessor.processData(zIndex, data.value);
        }
        else if (dataName.equals(DATANAME_PROCESSED_XHEADING))
        {
            data = getRawXHeading();
            data.value = dataProcessor.processData(xIndex, data.value);
        }
        else if (dataName.equals(DATANAME_PROCESSED_YHEADING))
        {
            data = getRawYHeading();
            data.value = dataProcessor.processData(yIndex, data.value);
        }
        else if (dataName.equals(DATANAME_PROCESSED_ZHEADING))
        {
            data = getRawZHeading();
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

}   //class TrcGyro
