package com.qualcomm.ftcrobotcontroller.opmodes;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcRobot;
import trclib.TrcStateMachine;

public class AutoDefense implements TrcRobot.AutoStrategy
{
    private FtcAuto autoMode;
    private HalDashboard dashboard;
    private int alliance;
    private double delay;
    private double distance;
    private TrcStateMachine sm;

    public AutoDefense(int alliance, double delay, double distance)
    {
        autoMode = (FtcAuto)FtcOpMode.getInstance();
        dashboard = HalDashboard.getInstance();
        this.alliance = alliance;
        this.delay = delay;
        this.distance = distance;
        sm = new TrcStateMachine("autoDefense");
        sm.start();
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "Defense: %s alliance, delay=%.1f, distance=%.1f",
                                alliance == autoMode.ALLIANCE_RED? "Red": "Blue",
                                delay, distance/12.0);

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
}   //class AutoDefense
