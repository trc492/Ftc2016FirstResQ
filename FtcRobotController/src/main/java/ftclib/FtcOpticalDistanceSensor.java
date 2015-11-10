package ftclib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import trclib.TrcAnalogInput;
import trclib.TrcDbgTrace;

public class FtcOpticalDistanceSensor implements TrcAnalogInput
{
    private static final String moduleName = "FtcOpticalDistanceSensor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private HardwareMap hardwareMap;
    private String instanceName;
    private OpticalDistanceSensor sensor;

    public FtcOpticalDistanceSensor(HardwareMap hardwareMap, String instanceName)
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
        sensor = hardwareMap.opticalDistanceSensor.get(instanceName);
    }   //FtcOpticalDistanceSensor

    public FtcOpticalDistanceSensor(String instanceName)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName);
    }   //FtcOpticalDistanceSensor

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
        int value = sensor.getLightDetectedRaw();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%d", value);
        }

        return value;
    }   //getValue

}   //class FtcOpticalDistanceSensor
