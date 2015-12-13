package trclib;

import hallib.HalUtil;

public class TrcEnhancedServo implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcEnhancedServo";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static final double SERVO_CONTINUOUS_STOP = 0.5;
    private static final double SERVO_CONTINUOUS_FWD_MAX = 1.0;
    private static final double SERVO_CONTINUOUS_REV_MAX = 0.0;

    private String instanceName;
    private TrcServo servo1 = null;
    private TrcServo servo2 = null;
    private boolean continuousServo = false;
    private boolean servoStepping = false;
    private double targetPosition = 0.0;
    private double stepRate = 0.0;
    private double prevTime = 0.0;
    private double currPosition = 0.0;
    //
    // The following is for continuous servo.
    //
    private TrcDigitalInput lowerLimitSwitch = null;
    private TrcDigitalInput upperLimitSwitch = null;

    private void commonInit(
            String instanceName,
            TrcServo servo1, TrcServo servo2,
            TrcDigitalInput lowerLimitSwitch, TrcDigitalInput upperLimitSwitch)
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
        this.servo1 = servo1;
        this.servo2 = servo2;
        this.lowerLimitSwitch = lowerLimitSwitch;
        this.upperLimitSwitch = upperLimitSwitch;
    }   //commonInit

    public TrcEnhancedServo(String instanceName, TrcServo servo)
    {
        if (servo == null)
        {
            throw new NullPointerException("servo cannot be null.");
        }

        commonInit(instanceName, servo, null, null, null);
    }   //TrcEnhancedServo

    public TrcEnhancedServo(String instanceName, TrcServo servo1, TrcServo servo2)
    {
        if (servo1 == null || servo2 == null)
        {
            throw new NullPointerException("servo1/servo2 cannot be null.");
        }

        commonInit(instanceName, servo1, servo2, null, null);
    }   //TrcEnhancedServo

    public TrcEnhancedServo(
            String instanceName,
            TrcServo servo, TrcDigitalInput lowerLimitSwitch, TrcDigitalInput upperLimitSwitch)
    {
        if (servo1 == null)
        {
            throw new NullPointerException("servo cannot be null.");
        }

        commonInit(instanceName, servo, null, lowerLimitSwitch, upperLimitSwitch);
        continuousServo = true;
    }   //TrcEnhancedServo

    public String toString()
    {
        return instanceName;
    }   //toString

    public void stop()
    {
        if (continuousServo)
        {
            servo1.setPosition(SERVO_CONTINUOUS_STOP);
        }
        else if (servoStepping)
        {
            TrcTaskMgr.getInstance().unregisterTask(this, TrcTaskMgr.TaskType.STOP_TASK);
            TrcTaskMgr.getInstance().unregisterTask(this, TrcTaskMgr.TaskType.POSTPERIODIC_TASK);
            servoStepping = false;
        }
    }   //stop

    public void setPosition(double position)
    {
        if (!continuousServo)
        {
            if (servo1 != null)
            {
                servo1.setPosition(position);
            }

            if (servo2 != null)
            {
                servo2.setPosition(position);
            }
        }
    }   //setPosition

    public void setPosition(double position, double stepRate)
    {
        if (!continuousServo)
        {
            this.targetPosition = position;
            this.stepRate = Math.abs(stepRate);
            this.prevTime = HalUtil.getCurrentTime();
            this.currPosition = servo1.getPosition();
            servoStepping = true;
            TrcTaskMgr.getInstance().registerTask(
                    "ServoSteppingTask", this, TrcTaskMgr.TaskType.POSTPERIODIC_TASK);
            TrcTaskMgr.getInstance().registerTask(
                    "ServoSteppingTask", this, TrcTaskMgr.TaskType.STOP_TASK);
        }
    }   //setPosition

    public void setContinuousPower(double power)
    {
        if (continuousServo)
        {
            if (lowerLimitSwitch != null &&
                lowerLimitSwitch.isActive() ||
                upperLimitSwitch != null &&
                upperLimitSwitch.isActive())
            {
                //
                // One of the limit switches is hit, so stop!
                //
                servo1.setPosition(SERVO_CONTINUOUS_STOP);
            }
            else
            {
                power = TrcUtil.scaleRange(
                        power, -1.0, 1.0,
                        SERVO_CONTINUOUS_REV_MAX, SERVO_CONTINUOUS_FWD_MAX);
                servo1.setPosition(power);
            }
        }
    }   //setContinuousPower

    //
    // Implements TrcTaskMgr.Task
    //
    public void startTask(TrcRobot.RunMode runMode)
    {
    }   //startTask

    public void stopTask(TrcRobot.RunMode runMode)
    {
        stop();
    }   //stopTask

    public void prePeriodicTask(TrcRobot.RunMode runMode)
    {
    }   //prePeriodicTask

    public void postPeriodicTask(TrcRobot.RunMode runMode)
    {
        if (runMode != TrcRobot.RunMode.DISABLED_MODE)
        {
            double currTime = HalUtil.getCurrentTime();
            double deltaPos = stepRate * (currTime - prevTime);

            if (currPosition < targetPosition)
            {
                currPosition += deltaPos;
                if (currPosition > targetPosition)
                {
                    currPosition = targetPosition;
                }
            }
            else if (currPosition > targetPosition)
            {
                currPosition -= deltaPos;
                if (currPosition < targetPosition)
                {
                    currPosition = targetPosition;
                }
            }
            else
            {
                //
                // We have reached target.
                //
                stop();
            }
            prevTime = currTime;

            if (servo1 != null)
            {
                servo1.setPosition(currPosition);
            }

            if (servo2 != null)
            {
                servo2.setPosition(currPosition);
            }
        }
    }   //postPeriodicTask

    public void preContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //preContinuousTask

    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}   //class TrcEnhancedServo
