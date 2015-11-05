package ftclib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import hallib.HalDigitalInput;
import trclib.TrcDbgTrace;

public class FtcTouch implements HalDigitalInput
{
    private static final String moduleName = "FtcTouch";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private HardwareMap hardwareMap;
    private TouchSensor touchSensor;

    public FtcTouch(String instanceName)
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
        this.touchSensor = hardwareMap.touchSensor.get(instanceName);
    }   //FtcTouch

    public String toString()
    {
        return instanceName;
    }   //toString

    //
    // Implements HalDigitalInput.
    //

    @Override
    public boolean isActive()
    {
        final String funcName = "isActive";
        boolean active = touchSensor.isPressed();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(active));
        }

        return active;
    }   //isActive

}   //class FtcTouch
