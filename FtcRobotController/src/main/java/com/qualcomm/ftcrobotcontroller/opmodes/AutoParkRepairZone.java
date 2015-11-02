package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.FtcRobot;
import hallib.HalDashboard;
import trclib.TrcRobot;
import trclib.TrcStateMachine;

public class AutoParkRepairZone implements TrcRobot.AutoStrategy
{
    private FtcAuto autoRobot;
    private HalDashboard dashboard;
    private int alliance;
    private double delay;

    public AutoParkRepairZone(int alliance, double delay)
    {
        autoRobot = (FtcAuto)FtcRobot.getInstance();
        dashboard = HalDashboard.getInstance();
        this.alliance = alliance;
        this.delay = delay;
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkRepairZone: %s alliance, delay=%.1f",
                                alliance == autoRobot.ALLIANCE_RED? "Red": "Blue", delay);

        if (autoRobot.sm.isReady())
        {
            int state = autoRobot.sm.getState();

            switch (state)
            {
                case TrcStateMachine.STATE_STARTED:
                    break;

                default:
                    autoRobot.sm.stop();
                    break;
            }
        }

    }
}   //class AutoParkRepairZone
