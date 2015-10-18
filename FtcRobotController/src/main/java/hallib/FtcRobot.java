package hallib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import trclib.TrcDbgTrace;
import trclib.TrcRobot;
import trclib.TrcTaskMgr;

public abstract class FtcRobot extends LinearOpMode
{
    private static final String moduleName = "FtcRobot";
    private static final boolean debugEnabled = true;
    private TrcDbgTrace dbgTrace = null;

    public abstract void robotInit();
    public abstract void startMode();
    public abstract void stopMode();
    public abstract void runPeriodic();
    public abstract void runContinuous();

    private final static long LOOP_PERIOD = 20;
    private TrcRobot.RunMode runMode = TrcRobot.RunMode.INVALID_MODE;

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
        String opMode = "Invalid";

        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "opModeName=<%s>", opModeName);
        }

        if (opModeName.contains("FtcAuto"))
        {
            runMode = TrcRobot.RunMode.AUTO_MODE;
            opMode = "Auto";
        }
        else if (opModeName.contains("FtcTeleOp"))
        {
            runMode = TrcRobot.RunMode.TELEOP_MODE;
            opMode = "TeleOp";
        }
        else if (opModeName.contains("FtcTest"))
        {
            runMode = TrcRobot.RunMode.TEST_MODE;
            opMode = "Test";
        }
        else
        {
            throw new IllegalStateException("Invalid RunMode.");
        }

        HalDashboard dashboard = HalDashboard.getInstance();
        if (dashboard == null)
        {
            dashboard = new HalDashboard(telemetry);
        }

        //
        // robotInit contains code to initialize the robot.
        //
        robotInit();
        //
        // Wait for the start of autonomous mode.
        //
        waitForStart();
        //
        // Prepare for starting autonomous.
        //
        startMode();

        long nextPeriodTime = System.currentTimeMillis();
        while (opModeIsActive())
        {
            dashboard.displayPrintf(0, "[%s] %f", opMode, getRuntime());

            if (System.currentTimeMillis() >= nextPeriodTime)
            {
                nextPeriodTime += LOOP_PERIOD;
                TrcTaskMgr.executeTaskType(
                        TrcTaskMgr.TaskType.PREPERIODIC_TASK,
                        runMode);
                runPeriodic();
                TrcTaskMgr.executeTaskType(
                        TrcTaskMgr.TaskType.POSTPERIODIC_TASK,
                        runMode);
            }
            TrcTaskMgr.executeTaskType(
                    TrcTaskMgr.TaskType.PRECONTINUOUS_TASK,
                    runMode);
            runContinuous();
            TrcTaskMgr.executeTaskType(
                    TrcTaskMgr.TaskType.POSTCONTINUOUS_TASK,
                    runMode);

            waitForNextHardwareCycle();
        }

        stopMode();
    }   //runOpMode

}   //class FtcRobot
