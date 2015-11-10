package trclib;

public interface TrcMotorLimitSwitches
{
    public boolean isForwardLimitSwitchActive(TrcMotorController motorController);
    public boolean isReverseLimitSwitchActive(TrcMotorController motorController);
}   //interface TrcMotorLimitSwitches
