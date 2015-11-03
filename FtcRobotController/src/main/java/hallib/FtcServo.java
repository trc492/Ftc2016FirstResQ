package hallib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import trclib.TrcDbgTrace;
import trclib.TrcUtil;

public class FtcServo implements HalServo
{
    private static final String moduleName = "FtcServo";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final double DEF_PHYSICAL_MIN = 0.0;
    private static final double DEF_PHYSICAL_MAX = 1.0;
    private static final double DEF_LOGICAL_MIN = 0.0;
    private static final double DEF_LOGICAL_MAX = 1.0;

    private String instanceName;
    private HardwareMap hardwareMap;
    private Servo servo;
    private double physicalMin = DEF_PHYSICAL_MIN;
    private double physicalMax = DEF_PHYSICAL_MAX;
    private double logicalMin = DEF_LOGICAL_MIN;
    private double logicalMax = DEF_LOGICAL_MAX;

    public FtcServo(String instanceName)
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
        hardwareMap = FtcOpMode.getInstance().hardwareMap;
        servo = hardwareMap.servo.get(instanceName);
    }   //FtcServo

    public String toString()
    {
        return instanceName;
    }   //toString

    //
    // Implements HalServo.
    //

    @Override
    public void setReverse(boolean reverse)
    {
        final String funcName = "setReverse";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "reverse=%s", Boolean.toString(reverse));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        servo.setDirection(reverse? Servo.Direction.REVERSE: Servo.Direction.FORWARD);
    }   //setReverse

    @Override
    public boolean getReverse()
    {
        final String funcName = "getReverse";
        boolean isReversed = servo.getDirection() == Servo.Direction.REVERSE;;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%s", isReversed);
        }

        return isReversed;
    }   //getReverse

    @Override
    public void setPosition(double position)
    {
        final String funcName = "setPosition";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "position=%f", position);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        position = TrcUtil.limit(position, physicalMin, physicalMax);
        position = TrcUtil.scaleRange(position, physicalMin, physicalMax, logicalMin, logicalMax);
        servo.setPosition(position);
    }   //setPosition

    @Override
    public double getPosition()
    {
        final String funcName = "getPosition";
        double position = TrcUtil.scaleRange(servo.getPosition(),
                                             logicalMin, logicalMax,
                                             physicalMin, physicalMax);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", position);
        }

        return position;
    }   //getPosition

    @Override
    public void setScale(double physicalMin, double physicalMax)
    {
        final String funcName = "setScale";

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
    }   //setScale

    @Override
    public void setScale(
            double physicalMin,
            double physicalMax,
            double logicalMin,
            double logicalMax)
    {
        final String funcName = "setScale";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "phyMin=%f,phyMax=%f,logicalMin=%f,logicalMax=%f",
                                physicalMin, physicalMax, logicalMin, logicalMax);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (physicalMin >= physicalMax || logicalMin >= logicalMax)
        {
            throw new IllegalArgumentException("max must be greater than min.");
        }

        this.physicalMin = physicalMin;
        this.physicalMax = physicalMax;
        this.logicalMin = logicalMin;
        this.logicalMax = logicalMax;
    }   //setScale

}   //class FtcServo
