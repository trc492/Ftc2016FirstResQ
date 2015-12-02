package trclib;

public class TrcIIRFilter extends TrcFilter
{
    private static final String moduleName = "TrcIIRFilter";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private double weight;
    private double filteredData;

    public TrcIIRFilter(String instanceName, double weight)
    {
        super(instanceName);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        if (weight < 0.0 || weight > 1.0)
        {
            throw new IllegalArgumentException(
                    "Weight must be a positive fraction within 1.0.");
        }

        this.weight = weight;
        filteredData = 0.0;
    }   //TrcIIRFilter

    public TrcIIRFilter(String instanceName)
    {
        this(instanceName, 0.9);
    }   //TrcIIRFilter

    //
    // Implements TrcFilter abstract methods.
    //

    @Override
    public double filterData(double data)
    {
        final String funcName = "filter";

        filteredData = filteredData*(1.0 - weight) + data*weight;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "data=%f", data);
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", filteredData);
        }

        return filteredData;
    }   //filterData

}   //class TrcIIRFilter
