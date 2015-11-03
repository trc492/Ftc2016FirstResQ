package com.qualcomm.ftcrobotcontroller.opmodes;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcRobot;
import trclib.TrcStateMachine;

public class AutoTriggerBeacon implements TrcRobot.AutoStrategy
{
    private FtcAuto autoMode;
    private HalDashboard dashboard;
    private int alliance;
    private double delay;
    private TrcStateMachine sm;

    public AutoTriggerBeacon(int alliance, double delay)
    {
        autoMode = (FtcAuto)FtcOpMode.getInstance();
        dashboard = HalDashboard.getInstance();
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
