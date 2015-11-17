package trclib;

public interface TrcFilteredSensor
{
    public TrcSensorData getRawValue();
    public TrcSensorData getFilteredValue();
}   //interface TrcFilteredSensor
