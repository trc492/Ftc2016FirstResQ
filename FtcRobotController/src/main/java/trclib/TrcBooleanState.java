package trclib;

public class TrcBooleanState
{
    private static final String moduleName = "TrcBooleanState";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private boolean state;

    public TrcBooleanState(final String instanceName)
    {
        this(instanceName, false);
    }   //TrcBooleanState

    public TrcBooleanState(final String instanceName, boolean state)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        this.state = state;
    }   //TrcBooleanState

    public boolean getState()
    {
        final String funcName = "getState";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%s", Boolean.toString(state));
        }

        return state;
    }   //getState

    public void setState(boolean state)
    {
        final String funcName = "setState";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "state=%s", Boolean.toString(state));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.state = state;
    }   //setState

    public boolean toggleState()
    {
        final String funcName = "toggleState";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        state = !state;

        if (debugEnabled)
        {
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%s", Boolean.toString(state));
        }
        return state;
    }   //toggleState

}   //class TrcToggle
