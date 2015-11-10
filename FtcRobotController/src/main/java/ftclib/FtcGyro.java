package ftclib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import trclib.TrcGyro;
import trclib.TrcDbgTrace;
import trclib.TrcKalmanFilter;

public class FtcGyro implements TrcGyro
{
    private static final String moduleName = "FtcGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private HardwareMap hardwareMap;
    private String instanceName;
    private TrcKalmanFilter kalman = null;
    private GyroSensor gyro;

    public FtcGyro(HardwareMap hardwareMap, String instanceName, boolean useFilter)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.hardwareMap = hardwareMap;
        this.instanceName = instanceName;
        if (useFilter)
        {
            kalman = new TrcKalmanFilter();
        }
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
        this(FtcOpMode.getInstance().hardwareMap, instanceName, useFilter);
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
    // Implements TrcGyro.
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
