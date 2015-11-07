package ftc3543.opmodes;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcRobot;
import trclib.TrcStateMachine;

public class AutoTriggerBeacon implements TrcRobot.AutoStrategy
{
    private FtcAuto autoMode = (FtcAuto)FtcOpMode.getInstance();
    private FtcRobot robot = autoMode.robot;
    private HalDashboard dashboard = HalDashboard.getInstance();

    private int alliance;
    private double delay;
    private TrcStateMachine sm;

    public AutoTriggerBeacon(int alliance, double delay)
    {
        this.alliance = alliance;
        this.delay = delay;
        sm = new TrcStateMachine("autoTriggerBeacon");
        sm.start();
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "TriggerBeacon: %s alliance, delay=%.1f",
                                alliance == autoMode.ALLIANCE_RED? "Red": "Blue", delay);

        if (sm.isReady())
        {
            int state = sm.getState();

            //
            // 1. drive forware about 120" until line detected
            // 2. if red turn left else turn right until line detected
            // 3. Follow the line until touch sensor is pressed.
            // 4. Read color and flip servo accordingly.
            // 5. Extend hanging hook
            // 6. wait 1 sec or so
            // 7. retract hanging hook
            //
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

}   //class AutoTriggerBeacon
