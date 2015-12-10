package ftclib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import hallib.HalDashboard;
import hallib.HalUtil;
import trclib.TrcDbgTrace;
import trclib.TrcRobot;
import trclib.TrcTaskMgr;

/**
 * This class implements a cooperative multi-tasking scheduler
 * extending LinearOpMode.
 */
public abstract class FtcOpMode extends LinearOpMode
{
    /**
     * This abstract method is called when the "Init" button on the
     * Driver Station phone is pressed. Typically, you put code to
     * initialze the robot here.
     */
    public abstract void robotInit();

    /**
     * This abstract method is called when the "Play" button on the
     * Driver Station phone is pressed. Typcially, you put code that
     * will prepare the robot for start of competition here. Most of
     * the time, there is nothing to do in this method because most of
     * the initialization is already done in robotInit().
     */
    public abstract void startMode();

    /**
     * This abstract method is called when competition mode is about
     * to end. Typically, you put code that will do clean up here.
     * Most of the time, there is nothing to do because when the program
     * ends, the robot will be stopped anyway.
     */
    public abstract void stopMode();

    /**
     * This abstract method is called periodically about 50 times a second.
     * This is where you put the bulk of your competition code.
     */
    public abstract void runPeriodic();

    /**
     * This abstract method is called periodically much faster than runPeriodic().
     * Typically, you put code that requires servicing at a higher frequency here.
     * Most of the time, there is nothing to do here because most robot actions
     * can be handled adequately with runPeriodic().
     */
    public abstract void runContinuous();

    private static final String moduleName = "FtcOpMode";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private static TrcDbgTrace opModeTrace = null;
    private static String opModeName = null;

    private final static String OPMODE_AUTO     = "FtcAuto";
    private final static String OPMODE_TELEOP   = "FtcTeleOp";
    private final static String OPMODE_TEST     = "FtcTest";

    private final static long LOOP_PERIOD = 20;
    private TrcRobot.RunMode runMode = TrcRobot.RunMode.INVALID_MODE;
    private static FtcOpMode instance = null;
    private double startTime = 0.0;

    /**
     * Constructor: Creates an instance of the object. It calls the constructor
     * of the LinearOpMode class and saves an instance of this class.
     */
    public FtcOpMode()
    {
        super();
        instance = this;
        //
        // Create task manager. There is only one global instance of task manager.
        //
        TrcTaskMgr taskMgr = new TrcTaskMgr();
    }   //FtcOpMode

    /**
     * This method returns the saved instance. This is a static method. So other
     * class can get to this class instance by calling getInstance(). This is very
     * useful for other classes that need to access the public fields such as
     * hardwareMap, gamepad1 and gamepad2.
     *
     * @return save instance of this class.
     */
    public static FtcOpMode getInstance()
    {
        return instance;
    }   //getInstance

    /**
     * This method returns a global debug trace object for tracing OpMode code.
     * If it doesn't exist yet, one is created. This is an easy way to quickly
     * get some debug output without a whole lot of setup overhead as the full
     * module-based debug tracing.
     *
     * @return global opMode trace object.
     */
    public static TrcDbgTrace getOpModeTraceInstance()
    {
        if (opModeTrace == null)
        {
            opModeTrace = new TrcDbgTrace(
                    opModeName, false, TrcDbgTrace.TraceLevel.API, TrcDbgTrace.MsgLevel.INFO);
        }

        return opModeTrace;
    }   //getOpModeTraceInstance

    /**
     * This method sets the OpMode trace configuration. The OpMode trace object was
     * created with default configuration of disabled method tracing, method tracing
     * level is set to API and message trace level set to INFO. Call this method if
     * you want to change the configuration.
     *
     * @param traceEnabled specifies true if enabling method tracing.
     * @param traceLevel specifies the method tracing level.
     * @param msgLevel specifies the message tracing level.
     */
    public static void setOpModeTraceConfig(
            boolean traceEnabled, TrcDbgTrace.TraceLevel traceLevel, TrcDbgTrace.MsgLevel msgLevel)
    {
        opModeTrace.setDbgTraceConfig(traceEnabled, traceLevel, msgLevel);
    }   //setOpModeTraceConfig

    //
    // Implements LinearOpMode
    //

    /**
     * This method is called when our OpMode is loaded and the "Init" button
     * on the Driver Station is pressed.
     *
     * @throws InterruptedException
     */
    @Override
    public void runOpMode() throws InterruptedException
    {
        final String funcName = "runOpMode";
        TrcTaskMgr taskMgr = TrcTaskMgr.getInstance();
        HalDashboard dashboard = HalDashboard.getInstance();

        if (debugEnabled)
        {
            if (dbgTrace == null)
            {
                dbgTrace = new TrcDbgTrace(
                        moduleName, false, TrcDbgTrace.TraceLevel.API, TrcDbgTrace.MsgLevel.INFO);
            }
        }
        //
        // Determine run mode.
        // Note that it means the OpMode must have "FtcAuto", "FtcTeleOp" or "FtcTest"
        // in its name.
        //
        String opModeFullName = this.toString();
        opModeName = "Invalid";

        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "opModeFullName=<%s>", opModeFullName);
        }

        if (opModeFullName.contains(OPMODE_AUTO))
        {
            runMode = TrcRobot.RunMode.AUTO_MODE;
            opModeName = "Auto";
        }
        else if (opModeFullName.contains(OPMODE_TELEOP))
        {
            runMode = TrcRobot.RunMode.TELEOP_MODE;
            opModeName = "TeleOp";
        }
        else if (opModeFullName.contains(OPMODE_TEST))
        {
            runMode = TrcRobot.RunMode.TEST_MODE;
            opModeName = "Test";
        }
        else
        {
            throw new IllegalStateException(
                    "Invalid OpMode (must be either FtcAuto, FtcTeleOp or FtcTest.");
        }

        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "runMode=%s", runMode.toString());
        }

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
        // Prepare for starting the run mode.
        //
        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "Running Start Mode Tasks ...");
        }
        taskMgr.executeTaskType(TrcTaskMgr.TaskType.START_TASK, runMode);

        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "Running startMode ...");
        }
        startMode();

        long nextPeriodTime = HalUtil.getCurrentTimeMillis();
        while (opModeIsActive())
        {
            dashboard.displayPrintf(
                    0, "%s: %.3f", opModeName, HalUtil.getCurrentTime() - startTime);

            if (debugEnabled)
            {
                dbgTrace.traceInfo(funcName, "Runing PreContinuous Tasks ...");
            }
            taskMgr.executeTaskType(TrcTaskMgr.TaskType.PRECONTINUOUS_TASK, runMode);

            if (debugEnabled)
            {
                dbgTrace.traceInfo(funcName, "Runing runContinuous ...");
            }
            runContinuous();

            if (debugEnabled)
            {
                dbgTrace.traceInfo(funcName, "Runing PostContinuous Tasks ...");
            }
            taskMgr.executeTaskType(TrcTaskMgr.TaskType.POSTCONTINUOUS_TASK, runMode);

            if (HalUtil.getCurrentTimeMillis() >= nextPeriodTime)
            {
                nextPeriodTime += LOOP_PERIOD;

                if (debugEnabled)
                {
                    dbgTrace.traceInfo(funcName, "Runing PrePeriodic Tasks ...");
                }
                taskMgr.executeTaskType(TrcTaskMgr.TaskType.PREPERIODIC_TASK, runMode);

                if (debugEnabled)
                {
                    dbgTrace.traceInfo(funcName, "Runing runPeriodic ...");
                }
                runPeriodic();

                if (debugEnabled)
                {
                    dbgTrace.traceInfo(funcName, "Runing PostPeriodic Tasks ...");
                }
                taskMgr.executeTaskType(TrcTaskMgr.TaskType.POSTPERIODIC_TASK, runMode);
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
        taskMgr.executeTaskType(TrcTaskMgr.TaskType.STOP_TASK, runMode);
    }   //runOpMode

}   //class FtcOpMode
