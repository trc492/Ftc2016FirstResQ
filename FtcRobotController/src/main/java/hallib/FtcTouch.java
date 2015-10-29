package hallib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import trclib.TrcDbgTrace;

public class FtcTouch implements HalTouch
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
        hardwareMap = ((FtcRobot)HalPlatform.getPlatformObject()).hardwareMap;
        this.touchSensor = hardwareMap.touchSensor.get(instanceName);
    }   //FtcTouch

    public String toString()
    {
        return instanceName;
    }   //toString

    //
    // Implements HalTouch.
    //

    @Override
    public boolean isPressed()
    {
        final String funcName = "isPressed";
        boolean pressed = touchSensor.isPressed();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(pressed));
        }

        return pressed;
    }   //isPressed

}   //class FtcTouch
