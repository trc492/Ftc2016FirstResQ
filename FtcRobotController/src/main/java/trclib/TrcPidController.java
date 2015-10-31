package trclib;

import hallib.HalDashboard;
import hallib.HalUtil;

public class TrcPidController
{
    private static final String moduleName = "TrcPidController";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;
    private HalDashboard dashboard;

    public static final int PIDCTRLO_INVERTED   = (1 << 0);
    public static final int PIDCTRLO_ABS_SETPT  = (1 << 1);
    public static final int PIDCTRLO_SPEED_CTRL = (1 << 2);
    public static final int PIDCTRLO_NO_OSC     = (1 << 3);

    public interface PidInput
    {
        public double getInput(TrcPidController pidCtrl);
    }   //interface PidInput

    private double kP;
    private double kI;
    private double kD;
    private double kF;
    private double tolerance;
    private double settlingTime;
    private PidInput pidInput;
    private int options;

    private double minInput;
    private double maxInput;
    private double minOutput;
    private double maxOutput;

    private double prevError;
    private double totalError;
    private double settlingStartTime;
    private double setPoint;
    private double output;

    public TrcPidController(
            final String instanceName,
            double       kP,
            double       kI,
            double       kD,
            double       kF,
            double       tolerance,
            double       settlingTime,
            PidInput     pidInput,
            int          options)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        dashboard = HalDashboard.getInstance();
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
        this.tolerance = tolerance;
        this.settlingTime = settlingTime;
        this.pidInput = pidInput;
        this.options = options;
        this.minInput = 0.0;
        this.maxInput = 0.0;
        this.minOutput = -1.0;
        this.maxOutput = 1.0;
        this.prevError = 0.0;
        this.totalError = 0.0;
        this.settlingStartTime = 0.0;
        this.setPoint = 0.0;
        this.output = 0.0;
    }   //TrcPidController

    public void displayPidInfo(int lineNum)
    {
        dashboard.displayPrintf(
                lineNum,
                "Target=%6.1f, Input=%6.1f, Error=%6.1f",
                setPoint, pidInput.getInput(this), prevError);
        dashboard.displayPrintf(
                lineNum + 1,
                "output=%6.3f, minOutput=%6.3f, maxOutput=%6.3f",
                output, minOutput, maxOutput);
    }   //displayPidInfo

    public void printPidInfo()
    {
        final String funcName = "printPidInfo";

        if (debugEnabled)
        {
            dbgTrace.traceInfo(
                    funcName,
                    "Target=%6.1f, Input=%6.1f, Error=%6.1f, Output=%6.3f(%6.3f/%5.3f)",
                    setPoint, pidInput.getInput(this), prevError, output,
                    minOutput, maxOutput);
        }
    }   //printPidInfo

    public double getKp()
    {
        final String funcName = "getKp";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", kP);
        }

        return kP;
    }   //getKp

    public double getKi()
    {
        final String funcName = "getKi";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", kI);
        }

        return kI;
    }   //getKi

    public double getKd()
    {
        final String funcName = "getKd";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", kD);
        }

        return kD;
    }   //getKd

    public double getKf()
    {
        final String funcName = "getKf";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", kF);
        }

        return kF;
    }   //getKf

    public void setKp(double kP)
    {
        final String funcName = "setKp";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                    "Kp=%f", kP);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.kP = kP;
    }   //setKp

    public void setKi(double kI)
    {
        final String funcName = "setKi";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "Ki=%f", kI);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.kI = kI;
    }   //setKi

    public void setKd(double kD)
    {
        final String funcName = "setKd";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "Kd=%f", kD);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.kD = kD;
    }   //setKd

    public void setKf(double kF)
    {
        final String funcName = "setKf";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "Kf=%f", kF);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.kF = kF;
    }   //setKf

    public void setPID(double kP, double kI, double kD, double kF)
    {
        final String funcName = "setPID";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "Kp=%f,Ki=%f,Kd=%f,Kf=%f",
                    kP, kI, kD, kF);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }   //setPID

    public void setInputRange(double minInput, double maxInput)
    {
        final String funcName = "setInputRange";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "min=%f,max=%f",
                    minInput, maxInput);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.minInput = minInput;
        this.maxInput = maxInput;
    }   //setInputRange

    public void setOutputRange(double minOutput, double maxOutput)
    {
        final String funcName = "setOutputRange";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "min=%f,max=%f",
                    minOutput, maxOutput);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
    }   //setOutputRange

    public double getTarget()
    {
        final String funcName = "getTarget";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", setPoint);
        }

        return setPoint;
    }   //getTarget

    public void setTarget(double target)
    {
        final String funcName = "setTarget";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "target=%f", target);
        }

        double input = pidInput.getInput(this);
        setPoint = target;
        if ((options & PIDCTRLO_ABS_SETPT) == 0)
        {
            setPoint += input;
        }

        if (maxInput > minInput)
        {
            if (setPoint > maxInput)
            {
                setPoint = maxInput;
            }
            else if (setPoint < minInput)
            {
                setPoint = minInput;
            }
        }

        prevError = setPoint - input;
        if ((options & PIDCTRLO_INVERTED) != 0)
        {
            prevError = -prevError;
        }
        totalError = 0.0;
        settlingStartTime = HalUtil.getCurrentTime();

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //setTarget

    public double getError()
    {
        final String funcName = "getError";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", prevError);
        }

        return prevError;
    }   //getError

    public void reset()
    {
        final String funcName = "reset";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        prevError = 0.0;
        totalError = 0.0;
        setPoint = 0.0;
        output = 0.0;
    }   //reset

    public boolean isOnTarget()
    {
        final String funcName = "isOnTarget";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        boolean onTarget = false;

        if ((options & PIDCTRLO_NO_OSC) != 0)
        {
            if (Math.abs(prevError) <= tolerance)
            {
                onTarget = true;
            }
        }
        else if (Math.abs(prevError) > tolerance)
        {
            settlingStartTime = HalUtil.getCurrentTime();
        }
        else if (HalUtil.getCurrentTime() >= settlingStartTime + settlingTime)
        {
            onTarget = true;
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%s", Boolean.toString(onTarget));
        }

        return onTarget;
    }   //isOnTarget

    public double getOutput()
    {
        final String funcName = "getOutput";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        double error = setPoint - pidInput.getInput(this);
        if ((options & PIDCTRLO_INVERTED) != 0)
        {
            error = -error;
        }

        if (kI != 0.0)
        {
            double potentialGain = (totalError + error)*kI;
            if (potentialGain >= maxOutput)
            {
                totalError = maxOutput/kI;
            }
            else if (potentialGain > minOutput)
            {
                totalError += error;
            }
            else
            {
                totalError = minOutput/kI;
            }
        }

        output =
                kP*error +
                kI*totalError +
                kD*(error - prevError) +
                kF*setPoint;

        prevError = error;
        if (output > maxOutput)
        {
            output = maxOutput;
        }
        else if (output < minOutput)
        {
            output = minOutput;
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", output);
        }

        return output;
    }   //getOutput

}   //class TrcPidController
