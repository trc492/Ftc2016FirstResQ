package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import hallib.HalGyro;
import trclib.TrcDbgTrace;
import trclib.TrcKalmanFilter;

public class FtcGyro implements HalGyro
{
    private static final String moduleName = "FtcGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private String instanceName;
    private TrcKalmanFilter kalman = null;
    private HardwareMap hardwareMap;
    private GyroSensor gyro;

    public FtcGyro(String instanceName, boolean useFilter, HardwareMap hardwareMap)
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

        gyro.calibrate();
        while (gyro.isCalibrating())
        {
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
            }
        }
    }   //FtcGyro

    public FtcGyro(String instanceName, boolean useFilter)
    {
        this(instanceName, useFilter, FtcOpMode.getInstance().hardwareMap);
    }   //FtcGyro

    public FtcGyro(String instanceName)
    {
        this(instanceName, false);
    }   //FtcGyro

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
        final String funcName = "calibrate";

        gyro.calibrate();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //calibrate

    @Override
    public boolean isCalibrating()
    {
        final String funcName = "isCalibrating";
        boolean calibrating = gyro.isCalibrating();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(calibrating));
        }

        return calibrating;
    }   //isCalibrating

    @Override
    public void reset()
    {
        final String funcName = "reset";

        gyro.resetZAxisIntegrator();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //reset

    @Override
    public double getRawX()
    {
        final String funcName = "getRawX";
        double rawX = gyro.rawX();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%x", rawX);
        }

        return rawX;
    }   //getRawX

    @Override
    public double getRawY()
    {
        final String funcName = "getRawY";
        double rawY = gyro.rawY();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%x", rawY);
        }

        return rawY;
    }   //getRawY

    @Override
    public double getRawZ()
    {
        final String funcName = "getRawZ";
        double rawZ = gyro.rawZ();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%x", rawZ);
        }

        return rawZ;
    }   //getRawZ

    @Override
    public double getRotation()
    {
        final String funcName = "getRotation";
        double rate = gyro.getRotation();

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
        double heading = gyro.getHeading();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", heading);
        }

        return heading;
    }   //getHeading

}   //class FtcGyro
