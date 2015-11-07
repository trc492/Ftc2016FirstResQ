package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalGyro;
import trclib.TrcDbgTrace;
import trclib.TrcGyroIntegrator;
import trclib.TrcKalmanFilter;

public class FtcHiTechnicGyro implements HalGyro
{
    private static final String moduleName = "FtcHiTechnicGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final int NUM_CAL_SAMPLES = 100;

    private String instanceName;
    private TrcKalmanFilter kalman = null;
    private HardwareMap hardwareMap;
    private GyroSensor gyro;
    private TrcGyroIntegrator integrator;

    public FtcHiTechnicGyro(String instanceName, boolean useFilter, HardwareMap hardwareMap)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
        if (useFilter)
        {
            kalman = new TrcKalmanFilter();
        }
        this.hardwareMap = hardwareMap;
        gyro = hardwareMap.gyroSensor.get(instanceName);
        integrator = new TrcGyroIntegrator(instanceName, this);
        integrator.calibrate(NUM_CAL_SAMPLES);
    }   //FtcHiTechnicGyro

    public FtcHiTechnicGyro(String instanceName, boolean useFilter)
    {
        this(instanceName, useFilter, FtcOpMode.getInstance().hardwareMap);
    }   //FtcHiTechnicGyro

    public FtcHiTechnicGyro(String instanceName)
    {
        this(instanceName, false);
    }   //FtcHiTechnicGyro

    public String toString()
    {
        return instanceName;
    }   //toString

    //
    // Implements HalGyro.
    //

    @Override
    public void calibrate()
    {
        integrator.calibrate(NUM_CAL_SAMPLES);
    }   //calibrate

    @Override
    public boolean isCalibrating()
    {
        return integrator.isCalibrating();
    }   //isCalibrating

    @Override
    public void reset()
    {
        integrator.reset();
    }   //reset

    @Override
    public double getRawX()
    {
        return 0.0;
    }   //getRawX

    @Override
    public double getRawY()
    {
        return 0.0;
    }   //getRawY

    @Override
    public double getRawZ()
    {
        return gyro.getRotation();
    }   //getRawZ

    @Override
    public double getRotation()
    {
        final String funcName = "getRotation";
        double rate = integrator.getRotation();

        if (kalman != null)
        {
            rate = kalman.filter(rate);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", rate);
        }

        return rate;
    }   //getRotation

    @Override
    public double getHeading()
    {
        final String funcName = "getHeading";
        double heading = integrator.getHeading();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", heading);
        }

        return heading;
    }   //getHeading

}   //class FtcHiTechnicGyro
