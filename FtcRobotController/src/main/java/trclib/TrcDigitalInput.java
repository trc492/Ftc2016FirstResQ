package trclib;

public abstract class TrcDigitalInput
{
    public abstract boolean isActive();

    private static final String moduleName = "TrcDigitalInput";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;

    public TrcDigitalInput(String instanceName)
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
    }   //TrcDigitalInput

    public String toString()
    {
        return instanceName;
    }   //toString

}   //class TrcDigitalInput
