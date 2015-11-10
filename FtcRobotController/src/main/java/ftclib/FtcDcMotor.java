package ftclib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import trclib.TrcMotorController;
import trclib.TrcDbgTrace;
import trclib.TrcMotorLimitSwitches;

public class FtcDcMotor implements TrcMotorController
{
    private static final String moduleName = "FtcDcMotor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private HardwareMap hardwareMap;
    private String instanceName;
    private TrcMotorLimitSwitches limitSwitches;
    private DcMotor motor;
    private int zeroEncoderValue;
    private int positionSensorSign;

    public FtcDcMotor(
            HardwareMap hardwareMap,
            String instanceName,
            TrcMotorLimitSwitches limitSwitches)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        this.hardwareMap = hardwareMap;
        this.instanceName = instanceName;
        this.limitSwitches = limitSwitches;
        motor = hardwareMap.dcMotor.get(instanceName);
        zeroEncoderValue = motor.getCurrentPosition();
        positionSensorSign = 1;
    }   //FtcDcMotor

    public FtcDcMotor(String instanceName, TrcMotorLimitSwitches limitSwitches)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, limitSwitches);
    }   //FtcDcMotor

    public FtcDcMotor(String instanceName)
    {
        this(instanceName, null);
    }   //FtcDcMotor

    public String toString()
    {
        return instanceName;
    }   //toString

    //
    // Implements TrcMotorController.
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
    public void setInverted(boolean inverted)
    {
        final String funcName = "setInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        motor.setDirection(inverted? DcMotor.Direction.REVERSE: DcMotor.Direction.FORWARD);
    }   //setInverted

    //
    // Implements TrcMotorPositionSensor
    //

    @Override
    public double getPosition()
    {
        final String funcName = "getPosition";
        int position = positionSensorSign*motor.getCurrentPosition() - zeroEncoderValue;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%d", position);
        }

        return (double)position;
    }   //getPosition

    @Override
    public double getSpeed()
    {
        final String funcName = "getSpeed";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=0.0");
        }

        return 0.0;
    }   //getSpeed

    @Override
    public void resetPosition()
    {
        final String funcName = "resetPosition";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        zeroEncoderValue = motor.getCurrentPosition();
//        motor.setMode(DcMotorController.RunMode.RESET_ENCODERS);
//        motor.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
    }   //resetPosition

    @Override
    public void setPositionSensorInverted(boolean inverted)
    {
        final String funcName = "setPositionSensorInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        positionSensorSign = inverted? -1: 1;
    }   //setPositionSensorInverted

    //
    // Implements TrcMotorLimitSwitches
    //

    public boolean isForwardLimitSwitchActive()
    {
        final String funcName = "isForwardLimitSwitchActive";
        boolean isActive = false;

        if (limitSwitches != null)
        {
            isActive = limitSwitches.isForwardLimitSwitchActive(this);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(isActive));
        }

        return isActive;
    }   //isForwardLimitSwitchActive

    public boolean isReverseLimitSwitchActive()
    {
        final String funcName = "isReverseLimitSwitchActive";
        boolean isActive = false;

        if (limitSwitches != null)
        {
            isActive = limitSwitches.isReverseLimitSwitchActive(this);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(isActive));
        }

        return isActive;
    }   //isReverseLimitSwitchActive

}   //class FtcDcMotor
