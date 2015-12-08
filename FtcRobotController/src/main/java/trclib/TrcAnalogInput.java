package trclib;

/**
 * This class implements a platform independent AnalogInput. Typically, this
 * class is extended by a platform dependent sensor class that produces value
 * data. The sensor doesn't have to be connected to the AnalogInput port. It
 * could be connected to an I2C port as long as it produces a value data.
 * The platform dependent sensor class must implement the abstract methods
 * required by this class. The abstract methods allow this class to get raw
 * data from the sensor.
 * Depending on the options specified in the constructor, this class creates a
 * calibrator, a data processor and an integrator. If it needs data integration,
 * it can set the INTEGRATE or the DOUBLE_INTEGRATE options. If it supports its
 * own calibration, it can override the calibrate() and isCalibrating() methods
 * to call its own. Otherwise, it can set the DO_CALIBRATION option to enable
 * the built-in calibrator.
 */
public abstract class TrcAnalogInput implements TrcSensorData.DataProvider
{
    /**
     * This abstract method returns the raw data from the sensor.
     *
     * @return raw data.
     */
    public abstract TrcSensorData getRawData();

    /**
     * This abstract method returns the raw integrated data from the sensor
     * if the sensor supports it. Otherwise, it may return zero or throw
     * an UnsupportedOperationException.
     *
     * @return raw integrated data.
     */
    public abstract TrcSensorData getRawIntegratedData();

    /**
     * This abstract method returns the raw double integrated data from
     * the sensor if the sensor supports it. Otherwise, it may return zero
     * or throw an UnsupportedOperationException.
     *
     * @return raw double integrated data.
     */
    public abstract TrcSensorData getRawDoubleIntegratedData();

    //
    // AnalogInput options.
    //
    public static final int ANALOGINPUT_INTEGRATE       = (1 << 0);
    public static final int ANALOGINPUT_DOUBLE_INTEGRATE= (1 << 1);
    public static final int ANALOGINPUT_DO_CALIBRATION  = (1 << 2);

    //
    // Data names that the data provider must provide data for.
    //
    private static final String DATANAME_RAW_DATA       = "rawData";
    private static final String DATANAME_PROCESSED_DATA = "processedData";

    //
    // Built-in calibrator parameters.
    //
    private static final int NUM_CAL_SAMPLES            = 100;
    private static final long CAL_INTERVAL              = 10;   //in msec.

    private static final String moduleName = "TrcAnalogInput";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private final String instanceName;
    private int options;
    private TrcDataProcessor dataProcessor = null;
    private TrcDataIntegrator dataIntegrator = null;
    private TrcDataCalibrator calibrator = null;
    private double zeroOffsets[] = {0.0};
    private double deadbands[] = {0.0};

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param options specifies the AnalogInput options. Multiple options can be OR'd together.
     *                ANALOGINPUT_INTEGRATE - do integration on sensor data.
     *                ANALOGINPUT_DOUBLE_INTEGRATE - do double integration on sensor data.
     *                ANALOGINPUT_DO_CALIBRATION - do calibration on the sensor.
     * @param filter specifies a filter object, null if none.
     */
    public TrcAnalogInput(final String instanceName, final int options, TrcFilter filter)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
        this.options = options;

        //
        // Create the filter array of one element.
        // Even if no filter is used, we will put a null in it.
        //
        TrcFilter filters[] = {filter};

        //
        // Create the data provider array of one element and
        // set it to this class as the data provider.
        //
        TrcSensorData.DataProvider dataProviders[] = {this};

        //
        // Create the data processor with the given filter array.
        //
        dataProcessor = new TrcDataProcessor(instanceName, filters);

        //
        // Create the data integrator. Data integrator needs data providers to
        // provide processed data from the sensor.
        //
        if ((options & ANALOGINPUT_INTEGRATE) != 0)
        {
            String dataNames[] = {DATANAME_PROCESSED_DATA};
            dataIntegrator = new TrcDataIntegrator(instanceName,
                                                   dataProviders,
                                                   dataNames,
                                                   (options & ANALOGINPUT_DOUBLE_INTEGRATE) != 0);
        }

        //
        // Create the data calibrator. Data calibrator needs data providers to
        // provide raw rate data for each axis.
        //
        if ((options & ANALOGINPUT_DO_CALIBRATION) != 0)
        {
            String dataNames[] = {DATANAME_RAW_DATA};
            calibrator = new TrcDataCalibrator(instanceName, dataProviders, dataNames);
        }
    }   //TrcAnalogInput

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param options specifies the AnalogInput options. Multiple options can be OR'd together.
     *                ANALOGINPUT_INTEGRATE - do integration on sensor data.
     *                ANALOGINPUT_DOUBLE_INTEGRATE - do double integration on sensor data.
     *                ANALOGINPUT_DO_CALIBRATION - do calibration on the sensor.
     */
    public TrcAnalogInput(final String instanceName, final int options)
    {
        this(instanceName, options, null);
    }   //TrcAnalogInput

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public TrcAnalogInput(final String instanceName)
    {
        this(instanceName, 0, null);
    }   //TrcAnalogInput

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
     * The method enables/disables the processing of sensor data. It is not
     * automatically enabled when the TrcAnalogInput object is created. You
     * need to explicitly enable the it before data processing will start. As
     * part of enabling the sensor, calibrate() is also called. calibrate()
     * may be overridden by the platform dependent sensor if it is capable
     * of doing its own. Otherwise, calibrate will call the built-in
     * calibrator to do the calibration.
     * Enabling/disabling data processing for the sensor involves
     * enabling/disabling the integrator if it exists.
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
     * This method inverts the sensor data. This is useful if the orientation of
     * the sensor is such that the data goes the wrong direction.
     *
     * @param inverted specifies true to invert sensor data, false otherwise.
     */
    public void setInverted(boolean inverted)
    {
        final String funcName = "setInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        dataProcessor.setInverted(0, inverted);
    }   //setInverted

    /**
     * This method sets the scale factor on the sensor data.
     *
     * @param scale specifies the scale factor.
     */
    public void setScale(double scale)
    {
        final String funcName = "setScale";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "scale=%f", scale);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        dataProcessor.setScale(0, scale);
    }   //setScale

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
            dataProcessor.setCalibrationData(0, zeroOffsets[0], deadbands[0]);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "numSamples=%d,calInterval=%d", numCalSamples, calInterval);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //calibrate

    /**
     * This method returns the processed sensor data.
     *
     * @return sensor data.
     */
    public TrcSensorData getData()
    {
        final String funcName = "getData";
        TrcSensorData data = getSensorData(DATANAME_PROCESSED_DATA);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getData

    /**
     * This method returns the integrated sensor data.
     *
     * @return integrated sensor data.
     */
    public TrcSensorData getIntegratedData()
    {
        final String funcName = "getIntegratedData";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getIntegratedData(0);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getIntegratedData

    /**
     * This method returns the double integrated sensor data.
     *
     * @return double integrated sensor data.
     */
    public TrcSensorData getDoubleIntegratedData()
    {
        final String funcName = "getDoubleIntegratedData";
        TrcSensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getDoubleIntegratedData(0);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getDoubleIntegratedData

    //
    // The following methods can be overridden by a platform dependent AnalogInput class.
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
     * This method resets the integrator.
     */
    public void resetIntegrator()
    {
        final String funcName = "resetIntegrator";

        if (dataIntegrator != null)
        {
            dataIntegrator.reset(0);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetIntegrator

    //
    // Implements TrcSensorData.DataProvider interface.
    //

    /**
     * This method returns the sensor data idnetified by the given dataName. The
     * possible data returned can be raw sensor data and  processed sensor data.
     *
     * @param dataName specifies the data names to identify what sensor data to get.
     * @return sensor data.
     */
    @Override
    public TrcSensorData getSensorData(String dataName)
    {
        final String funcName = "getSensorData";
        TrcSensorData data = null;

        if (dataName.equals(DATANAME_RAW_DATA))
        {
            data = getRawData();
        }
        else if (dataName.equals(DATANAME_PROCESSED_DATA))
        {
            data = getRawData();
            data.value = dataProcessor.processData(0, data.value);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getSensorData

}   //class TrcAnalogInput
