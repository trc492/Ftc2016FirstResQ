package hallib;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

import trclib.TrcDbgTrace;

public class FtcAnalogInput implements HalAnalogInput
{
    private static final String moduleName = "FtcAnalogInput";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private HardwareMap hardwareMap;
    private AnalogInput analogInput;

    public FtcAnalogInput(String instanceName)
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
        hardwareMap = FtcRobot.getInstance().hardwareMap;
        this.analogInput = hardwareMap.analogInput.get(instanceName);
    }   //FtcAnalogInput

    public String toString()
    {
        return instanceName;
    }   //toString

    //
    // Implements HalAnalogInput.
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
