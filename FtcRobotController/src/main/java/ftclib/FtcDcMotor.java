package ftclib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalSpeedController;
import trclib.TrcDbgTrace;

public class FtcDcMotor implements HalSpeedController
{
    private static final String moduleName = "FtcDcMotor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private HardwareMap hardwareMap;
    private DcMotor motor;

    public FtcDcMotor(String instanceName)
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
        motor = hardwareMap.dcMotor.get(instanceName);
    }   //FtcDcMotor

    public String toString()
    {
        return instanceName;
    }   //toString

    //
    // Implements HalSpeedController.
    //

    @Override
    public void setPower(double power)
    {
        final String funcName = "setPower";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "power=%f", power);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        motor.setPower(power);
    }   //set

    @Override
    public void setInverted(boolean isInverted)
    {
        final String funcName = "setInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(isInverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (isInverted)
        {
            motor.setDirection(DcMotor.Direction.REVERSE);
        }
        else
        {
            motor.setDirection(DcMotor.Direction.FORWARD);
        }
    }   //setInverted

    @Override
    public int getCurrentPosition()
    {
        final String funcName = "getCurrentPosition";
        int position = motor.getCurrentPosition();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%d", position);
        }

        return position;
    }   //getCurrentPosition

    @Override
    public void resetCurrentPosition()
    {
        final String funcName = "resetCurrentPosition";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        motor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }   //resetCurrentPosition

}   //class FtcDcMotor
