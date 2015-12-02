package trclib;

/**
 * This class does data processing for sensors that have one or more axes.
 * It applies calibration data and noise filter to the sensor data. It also
 * provides methods for inverting and/or scaling the sensor data.
 */
public class TrcDataProcessor
{
    private static final String moduleName = "TrcDataProcessor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private final String instanceName;
    private TrcFilter[] filters = null;
    private double[] zeroOffsets = null;
    private double[] deadbands = null;
    private double[] signs = null;
    private double[] scales = null;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param filters specifies an array of filter objects one for each axis.
     *                If an axis doesn't need filtering, that element in the
     *                array will be null.
     */
    public TrcDataProcessor(final String instanceName, TrcFilter[] filters)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        if (filters == null)
        {
            throw new NullPointerException("filters cannot be null.");
        }

        this.instanceName = instanceName;
        this.filters = filters;

        zeroOffsets = new double[filters.length];
        deadbands = new double[filters.length];
        signs = new double[filters.length];
        scales = new double[filters.length];

        for (int i = 0; i < filters.length; i++)
        {
            zeroOffsets[i] = 0.0;
            deadbands[i] = 0.0;
            signs[i] = 1.0;
            scales[i] = 1.0;
        }
    }   //TrcDataProcessor

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
     * This method sets the zeroOffset and deadBand from the result of a calibrator
     * for the indexed processor.
     *
     * @param index specifies the index.
     * @param zeroOffset specifies the zeroOffset.
     * @param deadband specifies the deadband.
     */
    public void setCalibrationData(int index, double zeroOffset, double deadband)
    {
        final String funcName = "setCalibrationData";

        zeroOffsets[index] = zeroOffset;
        deadbands[index] = deadband;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "zeroOffset=%f,deadband=%f", zeroOffset, deadband);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //setCalibrationData

    /**
     * This method inverts the sensor data for the indexed processor. It is useful
     * if the physical orientation of the sensor is reversed. For example, the gyro
     * is mounted up-side-down.
     *
     * @param index specifies the index.
     * @param inverted specifies true if the sensor is inverted, false otherwise.
     */
    public void setInverted(int index, boolean inverted)
    {
        final String funcName = "setInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        signs[index] = inverted? -1.0: 1.0;
    }   //setInverted

    /**
     * This method sets the sensor scale for the indexed processor. Some sensor
     * may give you raw data without particular unit. For example, the analog
     * ultrasonic sensor may return a raw value between 0 to 1023 reflecting a
     * 10-bit A to D converter. It may be more useful to translate that number
     * to the actual distance in cm or inches by calling this method to set the
     * proper scale factor.
     *
     * @param index specifies the index.
     * @param scale specifies the scale factor.
     */
    public void setScale(int index, double scale)
    {
        final String funcName = "setScale";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "scale=%f", scale);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        scales[index] = scale;
    }   //setScale

    /**
     * This method processes the sensor data using the indexed processor.
     * It will first apply the calibration data. Then it will apply the
     * noise filter if there is one. Finally, it will adjust the sign of
     * the data and scale it properly.
     *
     * @param index specifies the index.
     * @param value specifies the value to be processed.
     */
    public double processData(int index, double value)
    {
        final String funcName = "processedData";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "value=%f", value);
        }

        //
        // Apply calibration data.
        //
        value = TrcUtil.applyDeadband(value - zeroOffsets[index], deadbands[index]);

        //
        // Apply filter if necessary.
        //
        if (filters[index] != null)
        {
            value = filters[index].filterData(value);
        }

        //
        // Change sign and scale data if necessary.
        //
        value *= signs[index]*scales[index];

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //processData

}   //class TrcDataProcessor
