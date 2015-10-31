package hallib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import trclib.TrcDbgTrace;

public class FtcGyro implements HalGyro
{
    private static final String moduleName = "FtcGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private HardwareMap hardwareMap;
    private GyroSensor gyro;
    private double zeroAngle;

    public FtcGyro(String instanceName)
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
        this.gyro = hardwareMap.gyroSensor.get(instanceName);
        zeroAngle = gyro.getRotation();
    }   //FtcGyro

    public String toString()
    {
        return instanceName;
    }   //toString

    //
    // Implements HalGyro.
    //

    @Override
    public void reset()
    {
        final String funcName = "reset";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "! (zeroAngle=%f)", zeroAngle);
        }

        zeroAngle = gyro.getRotation();
    }   //reset

    @Override
    public double getAngle()
    {
        final String funcName = "getAngle";
        double angle = gyro.getRotation() - zeroAngle;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", angle);
        }

        return angle;
    }   //getAngle

}   //class FtcGyro
