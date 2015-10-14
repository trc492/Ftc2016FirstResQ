package trclib;

public class TrcKalmanFilter
{
    private static final String moduleName = "TrcKalmanFilter";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private double kQ;
    private double kR;
    private double prevP;
    private double prevXEst;
    private boolean initialized;

    public TrcKalmanFilter()
    {
        this(0.022, 0.617);
    }   //TrcKalmanFilter

    public TrcKalmanFilter(double kQ, double kR)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        this.kQ = kQ;
        this.kR = kR;
        prevP = 0.0;
        prevXEst = 0.0;
        initialized = false;
    }   //TrcKalmanFilter

    public double filter(double data)
    {
        final String funcName = "filter";

        if (!initialized)
        {
            prevXEst = data;
            initialized = true;
        }

        double tempP = prevP + kQ;
        double k = tempP/(tempP + kR);
        double xEst = prevXEst + k*(data - prevXEst);
        double p = (1 - k)*tempP;

        prevP = p;
        prevXEst = xEst;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "data=%f", data);
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", prevXEst);
        }

        return prevXEst;
    }   //filter

}   //class TrcKalmanFilter
