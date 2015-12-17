package trclib;

import hallib.HalUtil;

/**
 * This class implements a platform independent value sensor that has one or
 * more axes. Typically, this class is extended by a platform dependent value
 * sensor class. The platform dependent sensor class must implement the abstract
 * methods required by this class. The abstract methods allow this class to get
 * raw data for each axis. If the platform dependent sensor class doesn't provide
 * its own calibration, this class provides a generic calibrator that can be
 * called to compute the zero offset and noise deadband for each axis.
 */
public abstract class TrcSensor
{
    /**
     * This class implements the SensorData object that consists of the sensor
     * value as well as a timestamp when the data sample is taken.
     */
    public static class SensorData
    {
        public double timestamp;
        public double value;

        /**
         * Constructor: Creates an instance of the object with the given
         * timestamp and data value.
         *
         * @param timestamp specifies the timestamp.
         * @param value     specifies the data value.
         */
        public SensorData(double timestamp, double value)
        {
            this.timestamp = timestamp;
            this.value = value;
        }   //SensorData

    }   //class SensorData

    /**
     * This abstract method returns the raw sensor data of the specified axis index and type.
     *
     * @param index specifies the axis index.
     * @param dataType specifies the data type object.
     * @return raw sensor data of the specified axis index and type.
     */
    public abstract SensorData getRawData(int index, Object dataType);

    //
    // Built-in calibrator parameters.
    //
    private static final int NUM_CAL_SAMPLES    = 100;
    private static final long CAL_INTERVAL      = 10;   //in msec.

    private static final String moduleName = "TrcSensor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private final String instanceName;
    private int numAxes;
    private TrcFilter[] filters;
    private double[] zeroOffsets = null;
    private double[] deadbands = null;
    private int signs[] = null;
    private double scales[] = null;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param numAxes specifies the number of axes.
     * @param filters specifies an array of filter objects, one for each axis, to filter
     *                sensor data. If no filter is used, this can be set to null.
     */
    public TrcSensor(final String instanceName, int numAxes, TrcFilter[] filters)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        //
        // Make sure we have at least one axis.
        //
        if (numAxes <= 0)
        {
            throw new IllegalArgumentException("Sensor must have at least one axis.");
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
        this.numAxes = numAxes;
        this.filters = filters;

        zeroOffsets = new double[numAxes];
        deadbands = new double[numAxes];
        signs = new int[numAxes];
        scales = new double[numAxes];
        for (int i = 0; i < numAxes; i++)
        {
            zeroOffsets[i] = 0.0;
            deadbands[i] = 0.0;
            signs[i] = 1;
            scales[i] = 1.0;
        }
    }   //TrcSensor

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param numAxes specifies the number of axes.
     */
    public TrcSensor(final String instanceName, int numAxes)
    {
        this(instanceName, numAxes, null);
    }   //TrcSensor

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
     * This method returns the number of axes of the sensor.
     *
     * @return number of axes.
     */
    public int getNumAxes()
    {
        final String funcName = "getNumAxes";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%d", numAxes);
        }

        return numAxes;
    }   //getNumAxes

    /**
     * This method inverts the specified axis of the sensor. This is useful if the
     * orientation of the sensor axis is such that the data goes the wrong direction,
     * if the sensor is mounted up-side-down, for example.
     *
     * @param index specifies the axis index.
     * @param inverted specifies true to invert the axis, false otherwise.
     */
    public void setInverted(int index, boolean inverted)
    {
        final String funcName = "setInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "index=%d,inverted=%s", index, Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        signs[index] = inverted? -1: 1;
    }   //setInverted

    /**
     * This method sets the scale factor for the data of the specified axis.
     *
     * @param index specifies the axis index.
     * @param scale specifies the scale factorn for the axis.
     */
    public void setScale(int index, double scale)
    {
        final String funcName = "setScale";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "index=%d,scale=%f", index, scale);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        scales[index] = scale;
    }   //setScale

    /**
     * This method returns the processed data for the specified axis and type.
     * The data will go through a filter if a filter is supplied for the axis.
     * The calibration data will be applied to the sensor data if applicable.
     * The sign and scale will also be applied.
     *
     * @param index specifies the axis index.
     * @param dataType specifies the data type object.
     * @return processed sensor data for the axis.
     */
    public SensorData getData(int index, Object dataType)
    {
        final String funcName = "getData";

        SensorData data = getRawData(index, dataType);
        //
        // Apply filter if necessary.
        //
        if (filters[index] != null)
        {
            data.value = filters[index].filterData(data.value);
        }
        //
        // Apply calibration data.
        //
        data.value = TrcUtil.applyDeadband(data.value - zeroOffsets[index], deadbands[index]);
        //
        // Change sign and scale data if necessary.
        //
        data.value *= signs[index]*scales[index];

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "index=%d", index);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%0.3f,value=%f", data.timestamp, data.value);
        }

        return data;
    }   //getData

    /**
     * This method calibrates the sensor by reading a number of sensor data samples,
     * averaging the data to determine the zero offset. It also determines the min
     * and max values of the data samples to form the deadband.
     *
     * @param numCalSamples specifies the number of calibration sample to take.
     * @param calInterval specifies the interval between each calibration sample in msec.
     * @param dataType specifies the data type needed calibration.
     */
    public void calibrate(int numCalSamples, long calInterval, Object dataType)
    {
        final String funcName = "calibrate";
        double[] minValues = new double[numAxes];
        double[] maxValues = new double[numAxes];
        double[] sums = new double[numAxes];

        for (int i = 0; i < numAxes; i++)
        {
            double value = getRawData(i, dataType).value;
            minValues[i] = value;
            maxValues[i] = value;
            sums[i] = 0.0;
        }

        for (int n = 0; n < numCalSamples; n++)
        {
            for (int i = 0; i < numAxes; i++)
            {
                double value = getRawData(i, dataType).value;
                sums[i] += value;

                if (value < minValues[i])
                {
                    minValues[i] = value;
                }
                else if (value > maxValues[i])
                {
                    maxValues[i] = value;
                }
            }
            HalUtil.sleep(calInterval);
        }

        for (int i = 0; i < numAxes; i++)
        {
            zeroOffsets[i] = sums[i]/numCalSamples;
            deadbands[i] = maxValues[i] - minValues[i];
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "numSamples=%d,calInterval=%d", numCalSamples, calInterval);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //calibrate

    //
    // The following methods can be overridden by a platform dependent sensor class.
    //

    /**
     * This method calls the built-in calibrator to calibrates the gyro.
     * This method can be overridden by the platform dependent gyro to
     * provide its own calibration.
     *
     * @param dataType specifies the data type needed calibration.
     */
    public void calibrate(Object dataType)
    {
        calibrate(NUM_CAL_SAMPLES, CAL_INTERVAL, dataType);
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

}   //class TrcSensor
