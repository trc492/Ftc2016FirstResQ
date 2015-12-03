package ftclib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import trclib.TrcDigitalInput;
import trclib.TrcDbgTrace;

/**
 * This class implements a platform dependent touch sensor extending
 * TrcDigitalInput. It provides implementation of the abstract methods
 * in TrcDigitalInput.
 */
public class FtcTouchSensor extends TrcDigitalInput
{
    private static final String moduleName = "FtcTouchSensor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private TouchSensor touchSensor;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param hardwareMap specifies the global hardware map.
     * @param instanceName specifies the instance name.
     */
    public FtcTouchSensor(HardwareMap hardwareMap, String instanceName)
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

        this.touchSensor = hardwareMap.touchSensor.get(instanceName);
    }   //FtcTouchSensor

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public FtcTouchSensor(String instanceName)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName);
    }   //FtcTouchSensor

    //
    // Implements TrcDigitalInput abstract methods.
    //

    /**
     * This method returns the state of the touch sensor.
     *
     * @return true if the touch sensor is active, false otherwise.
     */
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

}   //class FtcTouchSensor
