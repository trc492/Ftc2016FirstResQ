package trclib;

/**
 * This class implements the SensorData object that consists of the sensor value
 * as well as a timestamp when the data sample is taken.
 */
public class TrcSensorData
{
    /**
     * This interface is implemented by sensor classes to provide raw sensor data
     * with timestamp.
     */
    public interface DataProvider
    {
        /**
         * This method is called to get sensor data. If the sensor provides
         * multiple data values (e.g. data for different axes), dataName is
         * used to specify what value to get.
         *
         * @param dataName specifies the name of the data.
         * @return raw sensor data encapsulated in the TrcSensorData object.
         */
        public TrcSensorData getSensorData(String dataName);

    }   //interface DataProvider

    public double timestamp;
    public double value;

    /**
     * Constructor: Creates an instance of the object with the given
     * timestamp and data value.
     *
     * @param timestamp specifies the timestamp.
     * @param value specifies the data value.
     */
    public TrcSensorData(double timestamp, double value)
    {
        this.timestamp = timestamp;
        this.value = value;
    }   //TrcSensorData

}   //class TrcSensorData
