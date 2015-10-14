package hallib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import trclib.TrcRobot;
import trclib.TrcTaskMgr;

public abstract class FtcRobot extends LinearOpMode
{
    public abstract void robotInit();
    public abstract void startMode();
    public abstract void runPeriodic();
    public abstract void runContinuous();

    private final static long LOOP_PERIOD = 50;
    private TrcRobot.RunMode runMode = TrcRobot.RunMode.INVALID_MODE;

    //
    // Implements LinearOpMode
    //

    @Override
    public void runOpMode() throws InterruptedException
    {
        //
        // Determine run mode.
        //
        String opModeName = this.toString();
        if (opModeName.contains("FtcAuto"))
        {
            runMode = TrcRobot.RunMode.AUTO_MODE;
        }
        else if (opModeName.contains("FtcTeleOp"))
        {
            runMode = TrcRobot.RunMode.TELEOP_MODE;
        }
        else if (opModeName.contains("FtcTest"))
        {
            runMode = TrcRobot.RunMode.TEST_MODE;
        }
        else
        {
            throw new IllegalStateException("Invalid RunMode.");
        }

        HalDashboard dashboard = new HalDashboard(telemetry);

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
        while (true)
        {
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

            waitOneHardwareCycle();
        }
    }   //runOpMode

}   //class FtcRobot
