package ftclib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import trclib.TrcDigitalInput;
import trclib.TrcDbgTrace;

public class FtcTouch implements TrcDigitalInput
{
    private static final String moduleName = "FtcTouch";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private HardwareMap hardwareMap;
    private String instanceName;
    private TouchSensor touchSensor;

    public FtcTouch(HardwareMap hardwareMap, String instanceName)
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
        this.touchSensor = hardwareMap.touchSensor.get(instanceName);
    }   //FtcTouch

    public FtcTouch(String instanceName)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName);
    }   //FtcTouch

    public String toString()
    {
        return instanceName;
    }   //toString

    //
    // Implements TrcDigitalInput.
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
