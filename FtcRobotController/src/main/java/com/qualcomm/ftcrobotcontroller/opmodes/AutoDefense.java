package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.FtcRobot;
import hallib.HalDashboard;
import trclib.TrcRobot;
import trclib.TrcStateMachine;

public class AutoDefense implements TrcRobot.AutoStrategy
{
    private FtcAuto autoRobot;
    private HalDashboard dashboard;
    private int alliance;
    private double delay;
    private double distance;

    public AutoDefense(int alliance, double delay, double distance)
    {
        autoRobot = (FtcAuto)FtcRobot.getInstance();
        dashboard = HalDashboard.getInstance();
        this.alliance = alliance;
        this.delay = delay;
        this.distance = distance;
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "Defense: %s alliance, delay=%.1f, distance=%.1f",
                                alliance == autoRobot.ALLIANCE_RED? "Red": "Blue",
                                delay, distance/12.0);

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
}   //class AutoDefense
