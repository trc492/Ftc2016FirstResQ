package trclib;

public abstract class TrcMotorController
{
    public abstract void setPower(double power);
    public abstract void setInverted(boolean inverted);

    public abstract void resetPosition();
    public abstract void setPositionSensorInverted(boolean inverted);
    public abstract double getPosition();
    public abstract double getSpeed();

    public abstract boolean isReverseLimitSwitchActive();
    public abstract boolean isForwardLimitSwitchActive();

    private static final String moduleName = "TrcMotorController";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;

    public TrcMotorController(String instanceName)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
    }   //TrcMotorController

    public String toString()
    {
        return instanceName;
    }   //toString

}   //class TrcMotorController
