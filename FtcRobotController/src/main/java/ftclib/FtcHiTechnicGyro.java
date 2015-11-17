package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalUtil;
import trclib.TrcSensorAxisData;
import trclib.TrcDbgTrace;
import trclib.TrcGyro;

public class FtcHiTechnicGyro extends TrcGyro
{
    private static final String moduleName = "FtcHiTechnicGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private HardwareMap hardwareMap;
    private GyroSensor gyro;

    public FtcHiTechnicGyro(HardwareMap hardwareMap, String instanceName, boolean useFilter)
    {
        super(instanceName, GYROOPTION_INTEGRATE_Z | (useFilter? GYROOPTION_FILTER: 0));

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.hardwareMap = hardwareMap;
        gyro = hardwareMap.gyroSensor.get(instanceName);
        setEnabled(true);
    }   //FtcHiTechnicGyro

    public FtcHiTechnicGyro(String instanceName, boolean useFilter)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, useFilter);
    }   //FtcHiTechnicGyro

    public FtcHiTechnicGyro(String instanceName)
    {
        this(instanceName, false);
    }   //FtcHiTechnicGyro

    //
    // Implements TrcGyro abstract methods.
    //

    @Override
    public TrcSensorAxisData getRawRates()
    {
        final String funcName = "getRawRates";
        TrcSensorAxisData rawRates = new TrcSensorAxisData(
                0.0, 0.0, gyro.getRotation(), HalUtil.getCurrentTime());

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "x=%f,y=%f,z=%f", rawRates.x, rawRates.y, rawRates.z);
        }

        return rawRates;
    }   //getRawRates

    @Override
    public TrcSensorAxisData getRawHeadings()
    {
        final String funcName = "getRawHeadings";
        TrcSensorAxisData rawHeadings = new TrcSensorAxisData(0.0, 0.0, 0.0, 0.0);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "x=%f,y=%f,z=%f", rawHeadings.x, rawHeadings.y, rawHeadings.z);
        }

        return rawHeadings;
    }   //getRawHeadings

}   //class FtcHiTechnicGyro
