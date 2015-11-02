package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.FtcRobot;
import hallib.HalDashboard;
import trclib.TrcRobot;
import trclib.TrcStateMachine;

public class AutoParkMountain implements TrcRobot.AutoStrategy
{
    private FtcAuto autoRobot;
    private HalDashboard dashboard;
    private int alliance;
    private double delay;
    private int mountainZone;

    public AutoParkMountain(int alliance, double delay, int mountainZone)
    {
        autoRobot = (FtcAuto)FtcRobot.getInstance();
        dashboard = HalDashboard.getInstance();
        this.alliance = alliance;
        this.delay = delay;
        this.mountainZone = mountainZone;
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkMountain: %s alliance, delay=%.1f, zone=%s",
                                alliance == autoRobot.ALLIANCE_RED? "Red": "Blue",
                                delay,
                                mountainZone == autoRobot.MOUNTAIN_FLOOR? "Floor":
                                mountainZone == autoRobot.MOUNTAIN_LOW_ZONE? "Low":
                                mountainZone == autoRobot.MOUNTAIN_MID_ZONE? "Mid": "High");

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
}   //class AutoParkMountain
