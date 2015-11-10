package ftclib;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

import trclib.TrcAnalogInput;
import trclib.TrcDbgTrace;

public class FtcAnalogInput implements TrcAnalogInput
{
    private static final String moduleName = "FtcAnalogInput";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private HardwareMap hardwareMap;
    private String instanceName;
    private AnalogInput analogInput;

    public FtcAnalogInput(HardwareMap hardwareMap, String instanceName)
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
        this.analogInput = hardwareMap.analogInput.get(instanceName);
    }   //FtcAnalogInput

    public FtcAnalogInput(String instanceName)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName);
    }   //FtcAnalogInput

    public String toString()
    {
        return instanceName;
    }   //toString

    //
    // Implements TrcAnalogInput.
    //

    @Override
    public int getValue()
    {
        final String funcName = "getValue";
        int value = analogInput.getValue();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%d", value);
        }

        return value;
    }   //getValue

}   //class FtcAnalogInput
