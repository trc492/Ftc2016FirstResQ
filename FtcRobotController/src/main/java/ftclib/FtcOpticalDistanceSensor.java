package ftclib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import hallib.HalAnalogInput;
import trclib.TrcDbgTrace;

public class FtcOpticalDistanceSensor implements HalAnalogInput
{
    private static final String moduleName = "FtcOpticalDistanceSensor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private HardwareMap hardwareMap;
    private OpticalDistanceSensor sensor;

    public FtcOpticalDistanceSensor(String instanceName, HardwareMap hardwareMap)
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
        this.hardwareMap = hardwareMap;
        sensor = hardwareMap.opticalDistanceSensor.get(instanceName);
    }   //FtcAnalogInput

    public FtcOpticalDistanceSensor(String instanceName)
    {
        this(instanceName, FtcOpMode.getInstance().hardwareMap);
    }   //FtcOpticalDistanceSensor

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
        int value = sensor.getLightDetectedRaw();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%d", value);
        }

        return value;
    }   //getValue

}   //class FtcOpticalDistanceSensor
