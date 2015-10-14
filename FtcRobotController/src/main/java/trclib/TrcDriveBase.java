package trclib;

import hallib.HalGyro;
import hallib.HalRobotDrive;
import hallib.HalSpeedController;

public class TrcDriveBase extends HalRobotDrive implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcDriveBase";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private HalSpeedController leftFrontMotor;
    private HalSpeedController leftRearMotor;
    private HalSpeedController rightFrontMotor;
    private HalSpeedController rightRearMotor;
    private HalGyro gyro;
    private TrcMotorPosition motorPosition;

    private boolean fourMotors;
    private double xPos;
    private double yPos;
    private double rotPos;
    private double heading;
    private double xScale;
    private double yScale;
    private double rotScale;
    private double xSpeed;
    private double ySpeed;
//    private double turnSpeed;

    public TrcDriveBase(
            HalSpeedController leftMotor,
            HalSpeedController rightMotor,
            TrcMotorPosition   motorPosition,
            HalGyro            gyro)
    {
        this(null, leftMotor, null, rightMotor, motorPosition, gyro);
    }   //TrcDriveBase

    public TrcDriveBase(
            HalSpeedController leftFrontMotor,
            HalSpeedController leftRearMotor,
            HalSpeedController rightFrontMotor,
            HalSpeedController rightRearMotor,
            TrcMotorPosition   motorPosition,
            HalGyro            gyro)
    {
        super(leftFrontMotor, leftRearMotor, rightFrontMotor, rightRearMotor);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        this.leftFrontMotor = leftFrontMotor;
        this.leftRearMotor = leftRearMotor;
        this.rightFrontMotor = rightFrontMotor;
        this.rightRearMotor = rightRearMotor;
        this.motorPosition = motorPosition;
        this.gyro = gyro;
        fourMotors = leftFrontMotor != null && rightFrontMotor != null;
        xScale = 1.0;
        yScale = 1.0;
        rotScale = 1.0;
        resetPosition();

        TrcTaskMgr.registerTask(
                moduleName,
                this,
                TrcTaskMgr.TaskType.START_TASK);
        TrcTaskMgr.registerTask(
                moduleName,
                this,
                TrcTaskMgr.TaskType.STOP_TASK);
        if (motorPosition != null || gyro != null)
        {
            TrcTaskMgr.registerTask(
                    moduleName,
                    this,
                    TrcTaskMgr.TaskType.PREPERIODIC_TASK);
        }
    }   //TrcDriveBase

    public void resetPosition()
    {
        final String funcName = "resetPosition";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (motorPosition != null)
        {
            if (leftFrontMotor != null)
            {
                motorPosition.resetMotorPosition(leftFrontMotor);
            }

            if (leftRearMotor != null)
            {
                motorPosition.resetMotorPosition(leftRearMotor);
            }

            if (rightFrontMotor != null)
            {
                motorPosition.resetMotorPosition(rightFrontMotor);
            }

            if (rightRearMotor != null)
            {
                motorPosition.resetMotorPosition(rightRearMotor);
            }
        }

        if (gyro != null)
        {
            gyro.reset();
        }

        xPos = 0.0;
        yPos = 0.0;
        rotPos = 0.0;
        heading = 0.0;
        xSpeed = 0.0;
        ySpeed = 0.0;
//        turnSpeed = 0.0;

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetPosition

    public void setPositionPolarities(
            boolean leftReversed,
            boolean rightReversed)
    {
        final String funcName = "setPositionPolarities";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "l=%s,r=%s",
                    Boolean.toString(leftReversed),
                    Boolean.toString(rightReversed));
        }

        if (motorPosition != null)
        {
            if (leftRearMotor != null)
            {
                motorPosition.reversePositionSensor(
                        leftRearMotor, leftReversed);
            }

            if (rightRearMotor != null)
            {
                motorPosition.reversePositionSensor(
                        rightRearMotor, rightReversed);
            }
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //setPositionPolarities

    public void setPositionPolarities(
            boolean leftFrontReversed,
            boolean leftRearReversed,
            boolean rightFrontReversed,
            boolean rightRearReversed)
    {
        final String funcName = "setPositionPolarities";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "lf=%s,lr=%s,rf=%s,rr=%s",
                    Boolean.toString(leftFrontReversed),
                    Boolean.toString(leftRearReversed),
                    Boolean.toString(rightFrontReversed),
                    Boolean.toString(rightRearReversed));
        }

        if (motorPosition != null)
        {
            if (leftFrontMotor != null)
            {
                motorPosition.reversePositionSensor(
                        leftFrontMotor, leftFrontReversed);
            }

            if (leftRearMotor != null)
            {
                motorPosition.reversePositionSensor(
                        leftRearMotor, leftRearReversed);
            }

            if (rightFrontMotor != null)
            {
                motorPosition.reversePositionSensor(
                        rightFrontMotor, rightFrontReversed);
            }

            if (rightRearMotor != null)
            {
                motorPosition.reversePositionSensor(
                        rightRearMotor, rightRearReversed);
            }
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //setPositionPolarities

    public void setPositionScales(
            double xScale,
            double yScale,
            double rotScale)
    {
        final String funcName = "setPositionScales";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "x=%f,y=%f,rot=%f", xScale, yScale, rotScale);
        }

        this.xScale = xScale;
        this.yScale = yScale;
        this.rotScale = rotScale;

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //setPositionScales

    public double getXPosition()
    {
        final String funcName = "getXPosition";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", xPos);
        }

        return xPos;
    }   //getXPosition

    public double getYPosition()
    {
        final String funcName = "getYPosition";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", yPos);
        }

        return yPos;
    }   //getYPosition

    public double getRotatePosition()
    {
        final String funcName = "getRotatePosition";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", rotPos);
        }

        return rotPos;
    }   //getRotatePosition

    public double getHeading()
    {
        final String funcName = "getHeading";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", heading);
        }

        return heading;
    }   //getHeading

    public double getXSpeed()
    {
        final String funcName = "getXSpeed";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", xSpeed);
        }

        return xSpeed;
    }   //getXSpeed

    public double getYSpeed()
    {
        final String funcName = "getYSpeed";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", ySpeed);
        }

        return ySpeed;
    }   //getYSpeed

    /*
    public double getTurnSpeed()
    {
        final String funcName = "getTurnSpeed";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%f", turnSpeed);
        }

        return turnSpeed;
    }   //getTurnSpeed
    */

    /*
    public void setBrakeModeEnabled(boolean enabled)
    {
        final String funcName = "setBrakeModeEnabled";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "enabled=%s", Boolean.toString(enabled));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        if (leftFrontMotor != null)
        {
            if (leftFrontMotor instanceof CANTalon)
            {
                ((CANTalon)leftFrontMotor).enableBrakeMode(enabled);
            }
            else if (leftFrontMotor instanceof CANJaguar)
            {
                ((CANJaguar)leftFrontMotor).configNeutralMode(
                        enabled? NeutralMode.Brake: NeutralMode.Coast);
            }
        }

        if (rightFrontMotor != null)
        {
            if (rightFrontMotor instanceof CANTalon)
            {
                ((CANTalon)rightFrontMotor).enableBrakeMode(enabled);
            }
            else if (rightFrontMotor instanceof CANJaguar)
            {
                ((CANJaguar)rightFrontMotor).configNeutralMode(
                        enabled? NeutralMode.Brake: NeutralMode.Coast);
            }
        }

        if (leftRearMotor != null)
        {
            if (leftRearMotor instanceof CANTalon)
            {
                ((CANTalon)leftRearMotor).enableBrakeMode(enabled);
            }
            else if (leftRearMotor instanceof CANJaguar)
            {
                ((CANJaguar)leftRearMotor).configNeutralMode(
                        enabled? NeutralMode.Brake: NeutralMode.Coast);
            }
        }

        if (rightRearMotor != null)
        {
            if (rightRearMotor instanceof CANTalon)
            {
                ((CANTalon)rightRearMotor).enableBrakeMode(enabled);
            }
            else if (rightRearMotor instanceof CANJaguar)
            {
                ((CANJaguar)rightRearMotor).configNeutralMode(
                        enabled? NeutralMode.Brake: NeutralMode.Coast);
            }
        }
    }   //setBrakeModeEnabled
    */

    public void stop()
    {
        final String funcName = "stop";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        stopMotor();

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //stop

    //
    // Implements TrcTaskMgr.Task
    //
    public void startTask(TrcRobot.RunMode runMode)
    {
        final String funcName = "startTask";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.TASK,
                    "mode=%s", runMode.toString());
        }

        if (runMode != TrcRobot.RunMode.DISABLED_MODE)
        {
            resetPosition();
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }
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

        if (runMode != TrcRobot.RunMode.DISABLED_MODE)
        {
            stop();
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }
    }   //stopTask

    public void prePeriodicTask(TrcRobot.RunMode runMode)
    {
        final String funcName = "prePeriodicTask";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.TASK,
                    "mode=%s", runMode.toString());
        }

        if (motorPosition != null)
        {
            //
            // According to RobotDrive.mecanumDrive_Cartesian in WPILib:
            //
            // LF =  x + y + rot    RF = -x + y - rot
            // LR = -x + y + rot    RR =  x + y - rot
            //
            // (LF + RR) - (RF + LR) = (2x + 2y) - (-2x + 2y)
            // => (LF + RR) - (RF + LR) = 4x
            // => x = ((LF + RR) - (RF + LR))/4
            //
            // LF + RF + LR + RR = 4y
            // => y = (LF + RF + LR + RR)/4
            //
            // (LF + LR) - (RF + RR) = (2y + 2rot) - (2y - 2rot)
            // => (LF + LR) - (RF + RR) = 4rot
            // => rot = ((LF + LR) - (RF + RR))/4
            //
            double lfEnc = 0.0, lrEnc = 0.0, rfEnc = 0.0, rrEnc = 0.0;
            double lfSpeed = 0.0, lrSpeed = 0.0, rfSpeed = 0.0, rrSpeed = 0.0;
            if (leftFrontMotor != null)
            {
                lfEnc = motorPosition.getMotorPosition(leftFrontMotor);
                lfSpeed = motorPosition.getMotorSpeed(leftFrontMotor);
            }
            if (leftRearMotor != null)
            {
                lrEnc = motorPosition.getMotorPosition(leftRearMotor);
                lrSpeed = motorPosition.getMotorSpeed(leftRearMotor);
            }
            if (rightFrontMotor != null)
            {
                rfEnc = motorPosition.getMotorPosition(rightFrontMotor);
                rfSpeed = motorPosition.getMotorSpeed(rightFrontMotor);
            }
            if (rightRearMotor != null)
            {
                rrEnc = motorPosition.getMotorPosition(rightRearMotor);
                rrSpeed = motorPosition.getMotorSpeed(rightRearMotor);
            }

            if (fourMotors)
            {
                xPos = ((lfEnc + rrEnc) - (rfEnc + lrEnc))*xScale/4.0;
                yPos = (lfEnc + lrEnc + rfEnc + rrEnc)*yScale/4.0;
                rotPos = ((lfEnc + lrEnc) - (rfEnc + rrEnc))*rotScale/4.0;
                xSpeed = ((lfSpeed + rrSpeed) - (rfSpeed + lrSpeed))*xScale/4.0;
                ySpeed = (lfSpeed + lrSpeed + rfSpeed + rrSpeed)*yScale/4.0;
            }
            else
            {
                yPos = (lrEnc + rrEnc)*yScale/2.0;
                rotPos = (lrEnc - rrEnc)*rotScale/2.0;
                ySpeed = (lrSpeed + rrSpeed)*yScale/2.0;
            }
        }

        if (gyro != null)
        {
            heading = gyro.getAngle();
//            turnSpeed = gyro.getRate();
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }
    }   //prePeriodicTask

    public void postPeriodicTask(TrcRobot.RunMode runMode)
    {
    }   //postPeriodicTask

    public void preContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //preContinuousTask

    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}   //class TrcDriveBase
