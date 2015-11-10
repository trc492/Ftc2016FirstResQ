package trclib;

public interface TrcMotorPositionSensor
{
    public double getPosition(TrcMotorController motorController);
    public double getSpeed(TrcMotorController motorController);
    public void resetPosition(TrcMotorController motorController);
    public void setPositionSensorInverted(TrcMotorController motorController, boolean inverted);
}   //interface TrcMotorPositionSensor
