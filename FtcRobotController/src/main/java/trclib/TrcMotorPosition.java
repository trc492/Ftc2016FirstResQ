package trclib;

import hallib.HalSpeedController;

public interface TrcMotorPosition
{
    public double getMotorPosition(HalSpeedController speedController);
    public double getMotorSpeed(HalSpeedController speedController);
    public void resetMotorPosition(HalSpeedController speedController);
    public void reversePositionSensor(
            HalSpeedController speedController,
            boolean flip);
    public boolean isForwardLimitSwitchActive(HalSpeedController speedController);
    public boolean isReverseLimitSwitchActive(HalSpeedController speedController);
}   //interface TrcMotorPosition
