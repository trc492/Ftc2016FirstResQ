package ftclib;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import hallib.HalUtil;
import trclib.TrcDbgTrace;
import trclib.TrcFilter;
import trclib.TrcSensor;

/**
 * This class implements an Android sensor that may have multiple axes.
 */
public class FtcAndroidSensor extends TrcSensor implements SensorEventListener
{
    private static final String moduleName = "FtcAndroidSensor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private SensorManager sensorManager;
    private Sensor sensor;
    private int numAxes;
    private SensorData[] sensorData;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param context specifies the activity context.
     * @param instanceName specifies the instance name.
     * @param sensorType specifies the sensor type.
     * @param numAxes specifies the number of axes of the sensor.
     * @param filters specifies an array of filter object used for filtering data of each axis.
     *                If none needed, it can be set to null.
     */
    public FtcAndroidSensor(
            Context context, String instanceName, int sensorType, int numAxes, TrcFilter[] filters)
    {
        super(instanceName, numAxes, filters);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        if (sensor == null)
        {
            throw new UnsupportedOperationException(
                    String.format("There is no sensor of type %d in the system.", sensorType));
        }

        this.numAxes = numAxes;
        sensorData = new SensorData[numAxes];
        for (int i = 0; i < numAxes; i++)
        {
            sensorData[i] = new SensorData(HalUtil.getCurrentTime(), 0.0);
        }
    }   //FtcAndroidSensor

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param sensorType specifies the sensor type.
     * @param numAxes specifies the number of axes of the sensor.
     * @param filters specifies an array of filter object used for filtering data of each axis.
     *                If none needed, it can be set to null.
     */
    public FtcAndroidSensor(
            String instanceName, int sensorType, int numAxes, TrcFilter[] filters)
    {
        this(FtcOpMode.getContext(), instanceName, sensorType, numAxes, filters);
    }   //FtcAndroidSensor

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param sensorType specifies the sensor type.
     * @param numAxes specifies the number of axes of the sensor.
     */
    public FtcAndroidSensor(String instanceName, int sensorType, int numAxes)
    {
        this(instanceName, sensorType, numAxes, null);
    }   //FtcAndroidSensor

    /**
     * This method enables/disables the sensor data listener.
     *
     * @param enabled specifies true to enable data listener, false otherwise.
     * @param samplingInterval specifies the maximum sampling interval in microseconds.
     */
    public void setEnabled(boolean enabled, int samplingInterval)
    {
        if (enabled)
        {
            sensorManager.registerListener(this, sensor, samplingInterval);
        }
        else
        {
            sensorManager.unregisterListener(this);
        }
    }   //setEnabled

    //
    // Implements TrcAnalogInput abstract methods.
    //

    /**
     * This method returns the raw sensor data of the specified axis.
     *
     * @param index specifies the axis index.
     * @return raw sensor data of the specified axis.
     */
    @Override
    public SensorData getRawData(int index)
    {
        final String funcName = "getRawData";
        SensorData data = new SensorData(sensorData[index].timestamp, sensorData[index].value);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp:%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getRawData

    //
    // Implements SensorEventListener interface.
    //

    /**
     * This method is called when the sensor data accuracy has changed.
     * We don't do anything here.
     *
     * @param sensor specifies the sensor object that generates this event.
     * @param accuracy specifies the new accuracy.
     */
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        FtcOpMode.getOpModeTraceInstance().traceInfo("AndroidSensor", "onAccuracyChanged(%s,%d)",
                                                     sensor.getName(), accuracy);
    }   //onAccuracyChanged

    /**
     * This method is called when new data is available from the sensor. It reads the
     * data for each axis and stores them.
     *
     * @param event specifies the sensor data.
     */
    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        for (int i = 0; i < numAxes; i++)
        {
            sensorData[i].timestamp = event.timestamp/1000.0;
            sensorData[i].value = event.values[i];
        }
    }   //onSensorChanged

}   //class FtcAndroidSensor
