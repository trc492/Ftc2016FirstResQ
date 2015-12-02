package ftclib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import trclib.TrcServo;
import trclib.TrcDbgTrace;

public class FtcServo extends TrcServo
{
    private static final String moduleName = "FtcServo";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private Servo servo;

    public FtcServo(HardwareMap hardwareMap, String instanceName)
    {
        super(instanceName);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        servo = hardwareMap.servo.get(instanceName);
    }   //FtcServo

    public FtcServo(String instanceName)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName);
    }   //FtcServo

    //
    // Implements TrcServo abstract methods.
    //

    @Override
    public void setInverted(boolean inverted)
    {
        final String funcName = "setInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        servo.setDirection(inverted? Servo.Direction.REVERSE: Servo.Direction.FORWARD);
    }   //setInverted

    @Override
    public boolean getInverted()
    {
        final String funcName = "getInverted";
        boolean isInverted = servo.getDirection() == Servo.Direction.REVERSE;;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%s", isInverted);
        }

        return isInverted;
    }   //getInverted

    @Override
    public void setPosition(double position)
    {
        final String funcName = "setPosition";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "position=%f", position);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        servo.setPosition(toLogicalPosition(position));
    }   //setPosition

    @Override
    public double getPosition()
    {
        final String funcName = "getPosition";
        double position = toPhysicalPosition(servo.getPosition());

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", position);
        }

        return position;
    }   //getPosition

}   //class FtcServo
