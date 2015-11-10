package trclib;

public interface TrcMotorController
{
    public void setPower(double power);
    public void setInverted(boolean inverted);

    public double getPosition();
    public double getSpeed();
    public void resetPosition();
    public void setPositionSensorInverted(boolean inverted);

    public boolean isForwardLimitSwitchActive();
    public boolean isReverseLimitSwitchActive();
}   //interface TrcMotorController
