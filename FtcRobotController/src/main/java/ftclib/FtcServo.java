package ftclib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import trclib.TrcServo;
import trclib.TrcDbgTrace;

/**
 * This class implements a platform dependent servo extending TrcServo.
 * It provides implementation of the abstract methods in TrcServo.
 */
public class FtcServo extends TrcServo
{
    private static final String moduleName = "FtcServo";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private Servo servo;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param hardwareMap specifies the global hardware map.
     * @param instanceName specifies the instance name.
     */
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

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public FtcServo(String instanceName)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName);
    }   //FtcServo

    //
    // Implements TrcServo abstract methods.
    //

    /**
     * This methods inverts the servo motor direction.
     *
     * @param inverted specifies true if the servo direction is inverted, false otherwise.
     */
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

    /**
     * This method returns true if the servo direction is inverted.
     *
     * @return true if the servo direction is inverted, false otherwise.
     */
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

    /**
     * This method sets the servo motor position.
     *
     * @param position specifies the physical position of the servo motor.
     *                 This value may be in degrees if setPhysicalRange
     *                 is called with the degree range.
     */
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

    /**
     * This method returns the physical position value of the servo motor.
     *
     * @return physical position of the servo, could be in degrees if
     *         setPhysicalRangis called to set the range in degrees.
     */
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
