package trclib;

public abstract class TrcFilter
{
    public abstract double filterData(double data);

    private static final String moduleName = "TrcFilter";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;

    public TrcFilter(String instanceName)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
    }   //TrcFilter

    public String toString()
    {
        return instanceName;
    }   //toString

}   //class TrcFilter
