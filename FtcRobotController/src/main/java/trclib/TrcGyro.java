package trclib;

import hallib.HalUtil;

public abstract class TrcGyro
{
    public abstract TrcAxisData getRawRates();
    public abstract TrcAxisData getRawHeadings();

    private class RawGyroXRate implements TrcFilteredSensor
    {
        private TrcKalmanFilter kalman = null;
        private double sign = 1.0;

        public RawGyroXRate(boolean useFilter)
        {
            if (useFilter)
            {
                kalman = new TrcKalmanFilter();
            }
        }   //RawGyroXRate

        public void setInverted(boolean inverted)
        {
            sign = inverted? -1.0: 1.0;
        }   //setInverted

        public double getRawValue()
        {
            getRateData();
            return sign*rateData.x;
        }   //getRawValue

        public double getFilteredValue()
        {
            getRateData();
            return sign*filterData(kalman, rateData.x, xZeroOffset, xDeadband);
        }   //getFilteredValue

    }   //class RawGyroXRate

    private class RawGyroYRate implements TrcFilteredSensor
    {
        private TrcKalmanFilter kalman = null;
        private double sign = 1.0;

        public RawGyroYRate(boolean useFilter)
        {
            if (useFilter)
            {
                kalman = new TrcKalmanFilter();
            }
        }   //RawGyroYRate

        public void setInverted(boolean inverted)
        {
            sign = inverted? -1.0: 1.0;
        }   //setInverted

        public double getRawValue()
        {
            getRateData();
            return sign*rateData.y;
        }   //getRawValue

        public double getFilteredValue()
        {
            getRateData();
            return sign*filterData(kalman, rateData.y, yZeroOffset, yDeadband);
        }   //getFilteredValue

    }   //class RawGyroYRate

    private class RawGyroZRate implements TrcFilteredSensor
    {
        private TrcKalmanFilter kalman = null;
        private double sign = 1.0;

        public RawGyroZRate(boolean useFilter)
        {
            if (useFilter)
            {
                kalman = new TrcKalmanFilter();
            }
        }   //RawGyroZRate

        public void setInverted(boolean inverted)
        {
            sign = inverted? -1.0: 1.0;
        }   //setInverted

        public double getRawValue()
        {
            getRateData();
            return sign*rateData.z;
        }   //getRawValue

        public double getFilteredValue()
        {
            getRateData();
            return sign*filterData(kalman, rateData.z, zZeroOffset, zDeadband);
        }   //getFilteredValue

    }   //class RawGyroZRate

    private double filterData(
            TrcKalmanFilter kalman, double value, double zeroOffset, double deadband)
    {
        value = TrcUtil.applyDeadband(value - zeroOffset, deadband);
        if (kalman != null)
        {
            value = kalman.filter(value);
        }
        return value;
    }   //filterData

    private class RawGyroXHeading implements TrcFilteredSensor
    {
        public double getRawValue()
        {
            getHeadingData();
            return headingData.x;
        }   //getRawValue

        public double getFilteredValue()
        {
            getHeadingData();
            return headingData.x;
        }   //getFilteredValue

    }   //class RawGyroXHeading

    private class RawGyroYHeading implements TrcFilteredSensor
    {
        public double getRawValue()
        {
            getHeadingData();
            return headingData.y;
        }   //getRawValue

        public double getFilteredValue()
        {
            getHeadingData();
            return headingData.y;
        }   //getFilteredValue

    }   //class RawGyroYHeading

    private class RawGyroZHeading implements TrcFilteredSensor
    {
        public double getRawValue()
        {
            getHeadingData();
            return headingData.z;
        }   //getRawValue

        public double getFilteredValue()
        {
            getHeadingData();
            return headingData.z;
        }   //getFilteredValue

    }   //class RawGyroZHeading

    public static final int GYROOPTION_INTEGRATE_X          = (1 << 0);
    public static final int GYROOPTION_INTEGRATE_Y          = (1 << 1);
    public static final int GYROOPTION_INTEGRATE_Z          = (1 << 2);
    public static final int GYROOPTION_X_WRAPAROUND         = (1 << 3);
    public static final int GYROOPTION_Y_WRAPAROUND         = (1 << 4);
    public static final int GYROOPTION_Z_WRAPAROUND         = (1 << 5);
    public static final int GYROOPTION_FILTER               = (1 << 6);

    private static final String moduleName = "TrcGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final int NUM_CAL_SAMPLES = 100;

    private String instanceName;
    private RawGyroXRate rawGyroXRate = null;
    private RawGyroYRate rawGyroYRate = null;
    private RawGyroZRate rawGyroZRate = null;
    private TrcIntegrator xIntegrator = null;
    private TrcIntegrator yIntegrator = null;
    private TrcIntegrator zIntegrator = null;
    private TrcWrapAroundHandler xWrapAroundHandler = null;
    private TrcWrapAroundHandler yWrapAroundHandler = null;
    private TrcWrapAroundHandler zWrapAroundHandler = null;
    private double xZeroOffset = 0.0;
    private double xDeadband = 0.0;
    private double yZeroOffset = 0.0;
    private double yDeadband = 0.0;
    private double zZeroOffset = 0.0;
    private double zDeadband = 0.0;
    private boolean calibrating = false;
    private TrcAxisData rateData = null;
    private double rateDataTimestamp = 0.0;
    private TrcAxisData headingData = null;
    private double headingDataTimestamp = 0.0;
    private double dataStaleTime = 0.005;

    public TrcGyro(String instanceName, int options)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
        boolean useFilter = (options & GYROOPTION_FILTER) != 0;
        rawGyroXRate = new RawGyroXRate(useFilter);
        rawGyroYRate = new RawGyroYRate(useFilter);
        rawGyroZRate = new RawGyroZRate(useFilter);

        if ((options & GYROOPTION_INTEGRATE_X) != 0)
        {
            xIntegrator = new TrcIntegrator("x" + instanceName, rawGyroXRate);
        }

        if ((options & GYROOPTION_INTEGRATE_Y) != 0)
        {
            yIntegrator = new TrcIntegrator("y" + instanceName, rawGyroYRate);
        }

        if ((options & GYROOPTION_INTEGRATE_Z) != 0)
        {
            zIntegrator = new TrcIntegrator("z" + instanceName, rawGyroZRate);
        }

        if ((options & GYROOPTION_X_WRAPAROUND) != 0)
        {
            xWrapAroundHandler = new TrcWrapAroundHandler(
                    "x" + instanceName,
                    new RawGyroXHeading(),
                    0.0, 360.0);
        }

        if ((options & GYROOPTION_Y_WRAPAROUND) != 0)
        {
            yWrapAroundHandler = new TrcWrapAroundHandler(
                    "y" + instanceName,
                    new RawGyroYHeading(),
                    0.0, 360.0);
        }

        if ((options & GYROOPTION_Z_WRAPAROUND) != 0)
        {
            zWrapAroundHandler = new TrcWrapAroundHandler(
                    "z" + instanceName,
                    new RawGyroZHeading(),
                    0.0, 360.0);
        }
    }   //TrcGyro

    public TrcGyro(String instanceName)
    {
        this(instanceName, 0);
    }   //TrcGyro

    public String toString()
    {
        return instanceName;
    }   //toString

    public void setDataStaleTime(double dataStaleTime)
    {
        final String funcName = "setDataStaleTime";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "staleTime=%f", dataStaleTime);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.dataStaleTime = dataStaleTime;
    }   //setDataStaleTime

    private void getRateData()
    {
        final String funcName = "getRateData";
        double currTime = HalUtil.getCurrentTime();

        if (currTime - rateDataTimestamp > dataStaleTime)
        {
            rateData = getRawRates();
            rateDataTimestamp = currTime;
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }
    }   //getRateData

    private void getHeadingData()
    {
        final String funcName = "getHeadomgData";
        double currTime = HalUtil.getCurrentTime();

        if (currTime - headingDataTimestamp > dataStaleTime)
        {
            headingData = getRawHeadings();
            headingDataTimestamp = currTime;
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }
    }   //getHeadingData

    public void setEnabled(boolean enabled)
    {
        final String funcName = "setEnabled";
        boolean needCalibration = false;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(enabled));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (xIntegrator != null)
        {
            xIntegrator.setEnabled(enabled);
            needCalibration = true;
        }

        if (yIntegrator != null)
        {
            yIntegrator.setEnabled(enabled);
            needCalibration = true;
        }

        if (zIntegrator != null)
        {
            zIntegrator.setEnabled(enabled);
            needCalibration = true;
        }

        if (xWrapAroundHandler != null)
        {
            xWrapAroundHandler.setEnabled(enabled);
        }

        if (yWrapAroundHandler != null)
        {
            yWrapAroundHandler.setEnabled(enabled);
        }

        if (zWrapAroundHandler != null)
        {
            zWrapAroundHandler.setEnabled(enabled);
        }

        if (needCalibration)
        {
            calibrate();
            while (isCalibrating())
            {
                try
                {
                    Thread.sleep(50);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
    }   //setEnabled

    public void setXInverted(boolean inverted)
    {
        final String funcName = "setXInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        rawGyroXRate.setInverted(inverted);
    }   //setXInverted

    public void setYInverted(boolean inverted)
    {
        final String funcName = "setYInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        rawGyroYRate.setInverted(inverted);
    }   //setYInverted

    public void setZInverted(boolean inverted)
    {
        final String funcName = "setZInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        rawGyroZRate.setInverted(inverted);
    }   //setZInverted

    public void calibrate()
    {
        final String funcName = "calibrate";

        xZeroOffset = 0.0;
        xDeadband = 0.0;
        yZeroOffset = 0.0;
        yDeadband = 0.0;
        zZeroOffset = 0.0;
        zDeadband = 0.0;
        getRateData();
        double xMinValue = rateData.x;
        double xMaxValue = xMinValue;
        double xSum = 0.0;
        double yMinValue = rateData.y;
        double yMaxValue = yMinValue;
        double ySum = 0.0;
        double zMinValue = rateData.z;
        double zMaxValue = zMinValue;
        double zSum = 0.0;

        calibrating = true;
        for (int i = 0; i < NUM_CAL_SAMPLES; i++)
        {
            getRateData();
            xSum += rateData.x;
            ySum += rateData.y;
            zSum += rateData.z;

            if (rateData.x < xMinValue)
            {
                xMinValue = rateData.x;
            }
            else if (rateData.x > xMaxValue)
            {
                xMaxValue = rateData.x;
            }

            if (rateData.y < yMinValue)
            {
                yMinValue = rateData.y;
            }
            else if (rateData.y > yMaxValue)
            {
                yMaxValue = rateData.y;
            }

            if (rateData.z < zMinValue)
            {
                zMinValue = rateData.z;
            }
            else if (rateData.z > zMaxValue)
            {
                zMaxValue = rateData.z;
            }

            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
            }
        }

        xZeroOffset = xSum/NUM_CAL_SAMPLES;
        xDeadband = (xMaxValue - xMinValue);
        yZeroOffset = ySum/NUM_CAL_SAMPLES;
        yDeadband = (yMaxValue - yMinValue);
        zZeroOffset = zSum/NUM_CAL_SAMPLES;
        zDeadband = (zMaxValue - zMinValue);
        calibrating = false;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "! (x:%.1f,%.1f y:%.1f,%.1f z:%.1f,%.1f)",
                               xZeroOffset, xDeadband,
                               yZeroOffset, yDeadband,
                               zZeroOffset, zDeadband);
        }
    }   //calibrate

    public boolean isCalibrating()
    {
        final String funcName = "isCalibrating";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(calibrating));
        }

        return calibrating;
    }   //isCalibrating

    public void resetXIntegrator()
    {
        final String funcName = "resetXIntegrator";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (xIntegrator != null)
        {
            xIntegrator.reset();
        }
    }   //resetXIntegrator

    public void resetYIntegrator()
    {
        final String funcName = "resetYIntegrator";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (yIntegrator != null)
        {
            yIntegrator.reset();
        }
    }   //resetYIntegrator

    public void resetZIntegrator()
    {
        final String funcName = "resetZIntegrator";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (zIntegrator != null)
        {
            zIntegrator.reset();
        }
    }   //resetZIntegrator

    public double getXRotation()
    {
        final String funcName = "getXRotation";
        double value = rawGyroXRate.getFilteredValue();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getXRotation

    public double getYRotation()
    {
        final String funcName = "getYRotation";
        double value = rawGyroYRate.getFilteredValue();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getYRotation

    public double getZRotation()
    {
        final String funcName = "getZRotation";
        double value = rawGyroZRate.getFilteredValue();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getZRotation

    public double getXHeading()
    {
        final String funcName = "getXHeading";
        double value = 0.0;

        if (xIntegrator != null)
        {
            value = xIntegrator.getOutput();
        }
        else if (xWrapAroundHandler != null)
        {
            value = xWrapAroundHandler.getCumulatedValue();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getXHeading

    public double getYHeading()
    {
        final String funcName = "getYHeading";
        double value = 0.0;

        if (yIntegrator != null)
        {
            value = yIntegrator.getOutput();
        }
        else if (yWrapAroundHandler != null)
        {
            value = yWrapAroundHandler.getCumulatedValue();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getYHeading

    public double getZHeading()
    {
        final String funcName = "getZHeading";
        double value = 0.0;

        if (zIntegrator != null)
        {
            value = zIntegrator.getOutput();
        }
        else if (zWrapAroundHandler != null)
        {
            value = zWrapAroundHandler.getCumulatedValue();
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%f", value);
        }

        return value;
    }   //getZHeading

}   //class TrcGyro
