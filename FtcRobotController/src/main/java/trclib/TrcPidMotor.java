package trclib;

import hallib.HalUtil;

public class TrcPidMotor implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcPidMotor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    public static final int PIDMOTORF_ENABLED           = (1 << 0);
    public static final int PIDMOTORF_HOLD_TARGET       = (1 << 1);
    public static final int PIDMOTORF_STALLED           = (1 << 2);
    public static final int PIDMOTORF_CANCELED          = (1 << 3);
    public static final int PIDMOTORF_INVERTED          = (1 << 4);

    private String instanceName;
    private TrcMotorController motor1;
    private TrcMotorController motor2;
    private byte syncGroup;
    private TrcPidController pidCtrl;
    private double positionScale;

    private int flags;
    private TrcEvent notifyEvent;
    private double motorPower;
    private double prevTarget;
    private double expiredTime;
    private double prevPos;
    private double prevTime;
    private double calPower;
    private double minPower;
    private double maxPower;
    private double stallMinPower;
    private double stallTimeout;
    private double resetTimeout;

    public TrcPidMotor(
            final String instanceName,
            TrcMotorController motor,
            TrcPidController pidCtrl)
    {
        this(instanceName, motor, null, (byte)0, pidCtrl);
    }   //TrcPidMotor

    public TrcPidMotor(
            final String instanceName,
            TrcMotorController motor1,
            TrcMotorController motor2,
            byte syncGroup,
            TrcPidController pidCtrl)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        if (motor1 == null && motor2 == null)
        {
            throw new IllegalArgumentException("Must have at least one motor.");
        }

        if (pidCtrl == null)
        {
            throw new IllegalArgumentException("Must have a PID controller.");
        }

        this.instanceName = instanceName;
        this.motor1 = motor1;
        this.motor2 = motor2;
        this.syncGroup = syncGroup;
        this.pidCtrl = pidCtrl;
        this.positionScale = 1.0;

        flags = 0;
        notifyEvent = null;
        motorPower = 0.0;
        prevTarget = 0.0;
        expiredTime = 0.0;
        prevPos = 0.0;
        prevTime = 0.0;
        calPower = 0.0;
        minPower = -1.0;
        maxPower = 1.0;
        stallMinPower = 0.0;
        stallTimeout = 0.0;
        resetTimeout = 0.0;
    }   //TrcPidMotor

    public boolean isEnabled()
    {
        final String funcName = "isEnabled";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%s", Boolean.toString((flags & PIDMOTORF_ENABLED) != 0));
        }

        return (flags & PIDMOTORF_ENABLED) != 0;
    }   //isEnabled

    public void cancel()
    {
        final String funcName = "cancel";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if ((flags & PIDMOTORF_ENABLED) != 0)
        {
            stop(true);
            if (notifyEvent != null)
            {
                notifyEvent.cancel();
                notifyEvent = null;
            }
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //cancel

    public void setTarget(double target, boolean holdTarget)
    {
        setTarget(target, holdTarget, null, 0.0);
    }   //setTarget

    public void setTarget(double target, TrcEvent event, double timeout)
    {
        setTarget(target, false, event, timeout);
    }   //setTarget

    private void setTarget(
            double target,
            boolean holdTarget,
            TrcEvent event,
            double timeout)
    {
        final String funcName = "setTarget";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "target=%f,hold=%s,event=%s,timeout=%f",
                    target, Boolean.toString(holdTarget),
                    event != null? event.toString(): "null", timeout);
        }

        if ((flags & PIDMOTORF_ENABLED) != 0)
        {
            stop(false);
        }

        pidCtrl.setTarget(target);

        if (event != null)
        {
            event.clear();
        }
        notifyEvent = event;
        expiredTime = timeout;
        if (timeout != 0.0)
        {
            expiredTime += HalUtil.getCurrentTime();
        }

        if (holdTarget)
        {
            flags |= PIDMOTORF_HOLD_TARGET;
        }

        setEnabled(true);

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //setTarget

    public void setStallProtection(
            double stallMinPower,
            double stallTimeout,
            double resetTimeout)
    {
        final String funcName = "setStallProtection";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "stallMinPower=%f,stallTimeout=%f,resetTimeout=%f",
                    stallMinPower, stallTimeout, resetTimeout);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.stallMinPower = stallMinPower;
        this.stallTimeout = stallTimeout;
        this.resetTimeout = resetTimeout;
    }   //setStallProtection

    public void setPositionScale(double positionScale)
    {
        final String funcName = "setPositionScale";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "scale=%f", positionScale);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.positionScale = positionScale;
    }   //setTargetScale

    public void setPower(double power)
    {
        setPower(power, minPower, maxPower, true);
    }   //setPower

    public void setPower(double power, double lowerBound, double upperBound)
    {
        setPower(power, lowerBound, upperBound, true);
    }   //setPower

    private void setPower(
            double power,
            double lowerBound,
            double upperBound,
            boolean stopPid)
    {
        final String funcName = "setPower";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "power=%f,lowBound=%f,hiBound=%f,stopPid=%s",
                    power, lowerBound, upperBound, Boolean.toString(stopPid));
        }

        if (power > 0.0 && !motor1.isForwardLimitSwitchActive() ||
            power < 0.0 && !motor1.isReverseLimitSwitchActive())
        {
            if ((flags & PIDMOTORF_ENABLED) != 0 && stopPid)
            {
                //
                // There was a previous unfinished PID operation, cancel it.
                // Don't stop the motor to prevent jerkiness.
                //
                stop(false);
            }

            if ((flags & PIDMOTORF_INVERTED) != 0)
            {
                power *= -1.0;
            }

            power = TrcUtil.limit(power, lowerBound, upperBound);

            if ((flags & PIDMOTORF_STALLED) != 0)
            {
                if (power == 0.0)
                {
                    //
                    // We had a stall but if power is removed for at least
                    // reset timeout, we clear it.
                    //
                    if (resetTimeout == 0.0 ||
                        HalUtil.getCurrentTime() - prevTime > resetTimeout)
                    {
                        prevPos = motor1.getPosition();
                        prevTime = HalUtil.getCurrentTime();
                        flags &= ~PIDMOTORF_STALLED;
                    }
                }
                else
                {
                    prevTime = HalUtil.getCurrentTime();
                }
            }
            else
            {
                motorPower = power;
                if (stallMinPower > 0.0 && stallTimeout > 0.0)
                {
                    //
                    // Stall protection is ON, check for stall condition.
                    // - power is above stallMinPower
                    // - motor has not moved for at least stallTimeout.
                    //
                    double currPos = motor1.getPosition();
                    if (Math.abs(power) < Math.abs(stallMinPower) ||
                        currPos != prevPos)
                    {
                        prevPos = currPos;
                        prevTime = HalUtil.getCurrentTime();
                    }

                    if (HalUtil.getCurrentTime() - prevTime > stallTimeout)
                    {
                        //
                        // We have detected a stalled condition for at least
                        // stallTimeout. Kill power to protect the motor.
                        //
                        motorPower = 0.0;
                        flags |= PIDMOTORF_STALLED;
                    }
                }

                setMotorPower(motorPower);
            }
        }
        else
        {
            motorPower = 0.0;
            setMotorPower(motorPower);
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //setPower

    public void setPidPower(
            double power,
            double minPos,
            double maxPos,
            boolean holdTarget)
    {
        final String funcName = "setPidPower";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "power=%f,minPos=%f,maxPos=%f",
                    power, minPos, maxPos);
        }

        if (motor1.isReverseLimitSwitchActive() && power < 0.0 ||
            motor1.isForwardLimitSwitchActive() && power > 0.0)
        {
            if (power < 0.0)
            {
                motor1.resetPosition();
            }
            power = 0.0;
        }

        double currTarget =
                power < 0.0? minPos:
                power > 0.0? maxPos: 0.0;
        if (currTarget != prevTarget)
        {
            if (power == 0.0)
            {
                pidCtrl.setOutputRange(minPower, maxPower);
                if (holdTarget)
                {
                    setTarget(
                            motor1.getPosition()*positionScale,
                            true,
                            null,
                            0.0);
                }
                else
                {
                    cancel();
                }
            }
            else
            {
                power = Math.abs(power);
                pidCtrl.setOutputRange(-power, power);
                setTarget(currTarget, holdTarget, null, 0.0);
            }
            prevTarget = currTarget;
        }
        else if (power == 0.0)
        {
            pidCtrl.setOutputRange(minPower, maxPower);
        }
        else
        {
            power = Math.abs(power);
            pidCtrl.setOutputRange(-power, power);
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //setPidPower

    public void zeroCalibrate(double calPower)
    {
        final String funcName = "zeroCalibrate";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "calPower=%f", calPower);
        }

        this.calPower = calPower;
        setEnabled(true);

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //zeroCalibrate

    private void stop(boolean stopMotor)
    {
        final String funcName = "stop";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.FUNC,
                    "stopMotor=%s",
                    Boolean.toString(stopMotor));
        }

        setEnabled(false);

        if (stopMotor)
        {
            setMotorPower(0.0);
        }

        if (pidCtrl != null)
        {
            pidCtrl.reset();
        }

        flags = 0;
        motorPower = 0.0;
        calPower = 0.0;

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }
    }   //stop

    private void setMotorPower(double power)
    {
        final String funcName = "setMotorPower";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "power=%f", power);
        }

        motor1.setPower(power);
        if (motor2 != null)
        {
            motor2.setPower(power);
            /*
            if (motor2 instanceof CANJaguar)
            {
                CANJaguar.updateSyncGroup(syncGroup);
            }
            */
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //setMotorPower

    private void setEnabled(boolean enabled)
    {
        final String funcName = "setEnabled";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.FUNC,
                    "enabled=%s",
                    Boolean.toString(enabled));
        }

        TrcTaskMgr taskMgr = TrcTaskMgr.getInstance();
        if (enabled)
        {
            taskMgr.registerTask(
                    instanceName,
                    this,
                    TrcTaskMgr.TaskType.STOP_TASK);
            taskMgr.registerTask(
                    instanceName,
                    this,
                    TrcTaskMgr.TaskType.POSTPERIODIC_TASK);
            flags |= PIDMOTORF_ENABLED;
        }
        else
        {
            taskMgr.unregisterTask(
                    this,
                    TrcTaskMgr.TaskType.STOP_TASK);
            taskMgr.unregisterTask(
                    this,
                    TrcTaskMgr.TaskType.POSTPERIODIC_TASK);
            flags &= ~PIDMOTORF_ENABLED;
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }
    }   //setEnabled

    //
    // Implements TrcTaskMgr.Task
    //
    public void startTask(TrcRobot.RunMode runMode)
    {
    }   //startTask

    public void stopTask(TrcRobot.RunMode runMode)
    {
        final String funcName = "stopTask";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.TASK,
                    "mode=%s", runMode.toString());
        }

        stop(true);

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }
    }   //stopTask

    public void prePeriodicTask(TrcRobot.RunMode runMode)
    {
    }   //prePeriodicTask

    public void postPeriodicTask(TrcRobot.RunMode runMode)
    {
        final String funcName = "postPeriodic";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.TASK,
                    "mode=%s", runMode.toString());
        }

        if (calPower != 0.0)
        {
            //
            // We are in zero calibration mode.
            //
            if (calPower < 0.0 &&
                !motor1.isReverseLimitSwitchActive() ||
                calPower > 0.0 &&
                !motor1.isForwardLimitSwitchActive())
            {
                setPower(calPower, minPower, maxPower, false);
            }
            else
            {
                //
                // Done with zero calibration.
                //
                calPower = 0.0;
                setMotorPower(0.0);
                if (motor1.isReverseLimitSwitchActive())
                {
                    //
                    // Reset encoder only if lower limit switch is active.
                    //
                    motor1.resetPosition();
                }
                setEnabled(false);
            }
        }
        else if ((flags & PIDMOTORF_ENABLED) != 0)
        {
            if ((flags & PIDMOTORF_HOLD_TARGET) == 0 && pidCtrl.isOnTarget() ||
                expiredTime != 0.0 && HalUtil.getCurrentTime() >= expiredTime)
            {
                stop(true);
                if (notifyEvent != null)
                {
                    notifyEvent.set(true);
                    notifyEvent = null;
                }
            }
            else
            {
                motorPower = pidCtrl.getOutput();
                if ((flags & PIDMOTORF_INVERTED) != 0)
                {
                    motorPower *= -1.0;
                }
                setPower(motorPower, minPower, maxPower, false);
            }
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }
    }   //postPeriodicTask

    public void preContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //preContinuousTask

    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}   //class TrcPidMotor
