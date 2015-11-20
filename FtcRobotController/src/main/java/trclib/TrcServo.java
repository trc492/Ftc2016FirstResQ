package trclib;

public abstract class TrcServo
{
    public abstract void setReverse(boolean reverse);
    public abstract boolean getReverse();
    public abstract void setPosition(double position);
    public abstract double getPosition();

    private static final double DEF_PHYSICAL_MIN    = 0.0;
    private static final double DEF_PHYSICAL_MAX    = 1.0;
    private static final double DEF_LOGICAL_MIN     = 0.0;
    private static final double DEF_LOGICAL_MAX     = 1.0;

    private static final String moduleName = "TrcServo";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private double physicalMin = DEF_PHYSICAL_MIN;
    private double physicalMax = DEF_PHYSICAL_MAX;
    private double logicalMin = DEF_LOGICAL_MIN;
    private double logicalMax = DEF_LOGICAL_MAX;

    public TrcServo(String instanceName)
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
    }   //TrcServo

    public String toString()
    {
        return instanceName;
    }   //toString

    public void setPhysicalRange(double physicalMin, double physicalMax)
    {
        final String funcName = "setPhysicalRange";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "phyMin=%f,phyMax=%f", physicalMin, physicalMax);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (physicalMin >= physicalMax)
        {
            throw new IllegalArgumentException("max must be greater than min.");
        }

        this.physicalMin = physicalMin;
        this.physicalMax = physicalMax;
    }   //setPhysicalRange

    public void setLogicalRange(double logicalMin, double logicalMax)
    {
        final String funcName = "setLogicalRange";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "logicalMin=%f,logicalMax=%f", logicalMin, logicalMax);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (logicalMin >= logicalMax)
        {
            throw new IllegalArgumentException("max must be greater than min.");
        }

        this.logicalMin = logicalMin;
        this.logicalMax = logicalMax;
    }   //setLogicalRange

    protected double toLogicalPosition(double physicalPosition)
    {
        final String funcName = "toLogicalPosition";
        physicalPosition = TrcUtil.limit(physicalPosition, physicalMin, physicalMax);
        double logicalPosition = TrcUtil.scaleRange(
                physicalPosition, physicalMin, physicalMax, logicalMin, logicalMax);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC,
                                "phyPos=%f", physicalPosition);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC, "=%f", logicalPosition);
        }

        return logicalPosition;
    }   //toLogicalPosition

    protected double toPhysicalPosition(double logicalPosition)
    {
        final String funcName = "toPhysicalPosition";

        logicalPosition = TrcUtil.limit(logicalPosition, logicalMin, logicalMax);
        double physicalPosition = TrcUtil.scaleRange(
                logicalPosition, logicalMin, logicalMax, physicalMin, physicalMax);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC,
                                "logPos=%f", logicalPosition);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC, "=%f", physicalPosition);
        }

        return physicalPosition;
    }   //toPhysicalPosition

}   //class TrcServo
