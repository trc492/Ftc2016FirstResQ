package com.qualcomm.ftcrobotcontroller.opmodes;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcRobot;
import trclib.TrcStateMachine;

public class AutoParkMountain implements TrcRobot.AutoStrategy
{
    private FtcAuto autoMode;
    private HalDashboard dashboard;
    private int alliance;
    private double delay;
    private int mountainZone;
    private TrcStateMachine sm;

    public AutoParkMountain(int alliance, double delay, int mountainZone)
    {
        autoMode = (FtcAuto)FtcOpMode.getInstance();
        dashboard = HalDashboard.getInstance();
        this.alliance = alliance;
        this.delay = delay;
        this.mountainZone = mountainZone;
        sm = new TrcStateMachine("autoParkMountain");
        sm.start();
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkMountain: %s alliance, delay=%.1f, zone=%s",
                                alliance == autoMode.ALLIANCE_RED? "Red": "Blue",
                                delay,
                                mountainZone == autoMode.MOUNTAIN_FLOOR? "Floor":
                                mountainZone == autoMode.MOUNTAIN_LOW_ZONE? "Low":
                                mountainZone == autoMode.MOUNTAIN_MID_ZONE? "Mid": "High");

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
}   //class AutoParkMountain
