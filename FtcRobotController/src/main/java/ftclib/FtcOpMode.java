package ftclib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import hallib.HalDashboard;
import hallib.HalUtil;
import trclib.TrcDbgTrace;
import trclib.TrcRobot;
import trclib.TrcTaskMgr;

public abstract class FtcOpMode extends LinearOpMode
{
    private static final String moduleName = "FtcOpMode";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    public abstract void robotInit();
    public abstract void startMode();
    public abstract void stopMode();
    public abstract void runPeriodic();
    public abstract void runContinuous();

    private final static long LOOP_PERIOD = 20;
    private TrcRobot.RunMode runMode = TrcRobot.RunMode.INVALID_MODE;
    private static FtcOpMode instance = null;
    private double startTime = 0.0;

    public FtcOpMode()
    {
        super();
        instance = this;
    }   //FtcOpMode

    public static FtcOpMode getInstance()
    {
        return instance;
    }   //getInstance

    //
    // Implements LinearOpMode
    //

    @Override
    public void runOpMode() throws InterruptedException
    {
        final String funcName = "runOpMode";

        if (debugEnabled)
        {
            if (dbgTrace == null)
            {
                dbgTrace = new TrcDbgTrace(
                        moduleName,
                        false,
                        TrcDbgTrace.TraceLevel.API,
                        TrcDbgTrace.MsgLevel.INFO);
            }
        }
        //
        // Determine run mode.
        //
        String opModeName = this.toString();
        String runModeName = "Invalid";

        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "opModeName=<%s>", opModeName);
        }

        if (opModeName.contains("FtcAuto"))
        {
            runMode = TrcRobot.RunMode.AUTO_MODE;
            runModeName = "Auto";
        }
        else if (opModeName.contains("FtcTeleOp"))
        {
            runMode = TrcRobot.RunMode.TELEOP_MODE;
            runModeName = "TeleOp";
        }
        else if (opModeName.contains("FtcTest"))
        {
            runMode = TrcRobot.RunMode.TEST_MODE;
            runModeName = "Test";
        }
        else
        {
            throw new IllegalStateException("Invalid RunMode.");
        }

        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "runModeName=<%s>", runModeName);
        }

        TrcTaskMgr taskMgr = new TrcTaskMgr();
        HalDashboard dashboard = new HalDashboard();

        //
        // robotInit contains code to initialize the robot.
        //
        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "Runing robotInit ...");
        }
        robotInit();

        //
        // Wait for the start of autonomous mode.
        //
        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "Waiting to start ...");
        }
        waitForStart();
        startTime = HalUtil.getCurrentTime();

        //
        // Prepare for starting autonomous.
        //
        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "Running Start Mode Tasks ...");
        }
        taskMgr.executeTaskType(
                TrcTaskMgr.TaskType.START_TASK,
                runMode);

        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "Running startMode ...");
        }
        startMode();

        long nextPeriodTime = HalUtil.getCurrentTimeMillis();
        while (opModeIsActive())
        {
            dashboard.displayPrintf(0, "%s: %.3f", runModeName, HalUtil.getCurrentTime() - startTime);

            if (debugEnabled)
            {
                dbgTrace.traceInfo(funcName, "Runing PreContinuous Tasks ...");
            }
            taskMgr.executeTaskType(
                    TrcTaskMgr.TaskType.PRECONTINUOUS_TASK,
                    runMode);

            if (debugEnabled)
            {
                dbgTrace.traceInfo(funcName, "Runing runContinuous ...");
            }
            runContinuous();

            if (debugEnabled)
            {
                dbgTrace.traceInfo(funcName, "Runing PostContinuous Tasks ...");
            }
            taskMgr.executeTaskType(
                    TrcTaskMgr.TaskType.POSTCONTINUOUS_TASK,
                    runMode);

            if (HalUtil.getCurrentTimeMillis() >= nextPeriodTime)
            {
                nextPeriodTime += LOOP_PERIOD;

                if (debugEnabled)
                {
                    dbgTrace.traceInfo(funcName, "Runing PrePeriodic Tasks ...");
                }
                taskMgr.executeTaskType(
                        TrcTaskMgr.TaskType.PREPERIODIC_TASK,
                        runMode);

                if (debugEnabled)
                {
                    dbgTrace.traceInfo(funcName, "Runing runPeriodic ...");
                }
                runPeriodic();

                if (debugEnabled)
                {
                    dbgTrace.traceInfo(funcName, "Runing PostPeriodic Tasks ...");
                }
                taskMgr.executeTaskType(
                        TrcTaskMgr.TaskType.POSTPERIODIC_TASK,
                        runMode);
            }

            dashboard.refreshDisplay();
            waitForNextHardwareCycle();
        }

        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "Running stopMode ...");
        }
        stopMode();

        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "Running Stop Mode Tasks ...");
        }
        taskMgr.executeTaskType(
                TrcTaskMgr.TaskType.STOP_TASK,
                runMode);
    }   //runOpMode

}   //class FtcOpMode
