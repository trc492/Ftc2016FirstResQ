package ftc3543.opmodes;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcRobot;
import trclib.TrcStateMachine;

public class AutoParkFloorGoal implements TrcRobot.AutoStrategy
{
    private FtcAuto autoMode = (FtcAuto)FtcOpMode.getInstance();
    private FtcRobot robot = autoMode.robot;
    private HalDashboard dashboard = HalDashboard.getInstance();

    private int alliance;
    private double delay;
    private TrcStateMachine sm;

    public AutoParkFloorGoal(int alliance, double delay)
    {
        this.alliance = alliance;
        this.delay = delay;
        sm = new TrcStateMachine("autoParkFloorGoal");
        sm.start();
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkFloorGoal: %s alliance, delay=%.1f",
                                alliance == autoMode.ALLIANCE_RED? "Red": "Blue", delay);

        if (sm.isReady())
        {
            int state = sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    break;

                default:
                    sm.stop();
                    break;
            }
        }
    }

}   //class AutoParkFloorGoal
