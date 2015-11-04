package com.qualcomm.ftcrobotcontroller.opmodes;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoParkMountain implements TrcRobot.AutoStrategy
{
    private FtcAuto autoMode;
    private HalDashboard dashboard;
    private int alliance;
    private double delay;
    private int mountainZone;
    private TrcStateMachine sm;
    private TrcTimer timer;
    private TrcEvent event;

    public AutoParkMountain(int alliance, double delay, int mountainZone)
    {
        autoMode = (FtcAuto)FtcOpMode.getInstance();
        dashboard = HalDashboard.getInstance();
        this.alliance = alliance;
        this.delay = delay;
        this.mountainZone = mountainZone;
        sm = new TrcStateMachine("autoParkMountain");
        sm.start();
        timer = new TrcTimer("ParkMountainTimer");
        event = new TrcEvent("ParkMountainEvent");
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
                    if (delay == 0.0)
                    {
                        sm.setState(state + 1);
                    }
                    else
                    {
                        timer.set(delay, event);
                        sm.addEvent(event);
                        sm.waitForEvents(state + 1);
                    }
                    break;

                case TrcStateMachine.STATE_STARTED + 1:
                    autoMode.getRobot().pidDrive.setTarget(70.0, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 2:
                    if (alliance == autoMode.ALLIANCE_RED)
                    {
                        autoMode.getRobot().pidDrive.setTarget(0.0, -90.0, false, event, 0.0);
                    }
                    else
                    {
                        autoMode.getRobot().pidDrive.setTarget(0.0, 90.0, false, event, 0.0);
                    }
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 3:
                    autoMode.getRobot().pidDrive.setTarget(120.0, 0.0, false, event, 0.0);
                    autoMode.getRobot().chainsaw.setPower(1.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    autoMode.getRobot().chainsaw.setPower(0.0);
                    sm.stop();
                    break;
            }
        }
    }

}   //class AutoParkMountain
