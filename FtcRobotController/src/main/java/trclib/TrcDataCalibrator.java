package trclib;

import hallib.HalUtil;

/**
 * This class implements a calibrator for calibrating a value sensor
 * that may have one or more axes.
 */
public class TrcDataCalibrator
{
    private static final String moduleName = "TrcDataCalibrator";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private final String instanceName;
    private TrcSensorData.DataProvider[] dataProviders = null;
    private String[] dataNames = null;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param dataProviders specifies an array of data provider objects, one for each axis.
     * @param dataNames specifies an array of data names to be used to identify the data when
     *                  calling the data provider.
     */
    public TrcDataCalibrator(
            final String instanceName,
            TrcSensorData.DataProvider[] dataProviders,
            String[] dataNames)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        if (dataProviders == null || dataNames == null)
        {
            throw new NullPointerException("dataProviders/providerNames cannot be null.");
        }
        else if (dataProviders.length == 0)
        {
            throw new IllegalArgumentException(
                    "dataProviders array must have at least one element.");
        }
        else if (dataProviders.length != dataNames.length)
        {
            throw new IllegalArgumentException(
                    String.format("dataNames array must have %d elements.",
                                  dataProviders.length));
        }

        for (int i = 0; i < dataProviders.length; i++)
        {
            if (dataProviders[i] == null)
            {
                throw new NullPointerException("Elements in dataProviders cannot be null.");
            }
        }

        this.instanceName = instanceName;
        this.dataProviders = dataProviders;
        this.dataNames = dataNames;
    }   //TrcDataCalibrator

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
     * This method calibrates the value sensor by reading a number of
     * sensor data samples, averaging the data to determine the zero
     * offset. It also determines the min and max values of the data
     * samples to form the deadband.
     *
     * @param numCalSamples specifies the number of calibration sample to take.
     * @param calInterval specifies the interval between each calibration sample
     *                    in msec.
     * @param zeroOffsets specifies an array of doubles that will hold the zero
     *                    offset of each axis after the calibration is completed.
     * @param deadbands specifies an array of doubles that will hold the deadband
     *                  of each axis after the calibration is completed.
     */
    public void calibrate(
            int numCalSamples, long calInterval, double[]zeroOffsets, double[] deadbands)
    {
        final String funcName = "calibrate";

        if (zeroOffsets == null || deadbands == null)
        {
            throw new NullPointerException("zeroOffsets/deadbands cannot be null.");
        }

        if (dataProviders.length != zeroOffsets.length || dataProviders.length != deadbands.length)
        {
            throw new IllegalArgumentException(
                    String.format("zeroOffsets/deadbands arrays must have %d elements.",
                                  dataProviders.length));
        }

        double minValues[] = new double[dataProviders.length];
        double maxValues[] = new double[dataProviders.length];
        double sums[] = new double[dataProviders.length];

        for (int i = 0; i < dataProviders.length; i++)
        {
            TrcSensorData data = dataProviders[i].getSensorData(dataNames[i]);
            minValues[i] = data.value;
            maxValues[i] = data.value;
            sums[i] = 0.0;
        }

        for (int n = 0; n < numCalSamples; n++)
        {
            for (int i = 0; i < dataProviders.length; i++)
            {
                TrcSensorData data = dataProviders[i].getSensorData(dataNames[i]);
                sums[i] += data.value;

                if (data.value < minValues[i])
                {
                    minValues[i] = data.value;
                }
                else if (data.value > maxValues[i])
                {
                    maxValues[i] = data.value;
                }
            }
            HalUtil.sleep(calInterval);
        }

        for (int i = 0; i < dataProviders.length; i++)
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

}   //class TrcDataCalibrator
