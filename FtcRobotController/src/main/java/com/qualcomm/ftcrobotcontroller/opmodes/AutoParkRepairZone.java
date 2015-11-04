package com.qualcomm.ftcrobotcontroller.opmodes;

import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcEvent;
import trclib.TrcRobot;
import trclib.TrcStateMachine;
import trclib.TrcTimer;

public class AutoParkRepairZone implements TrcRobot.AutoStrategy
{
    private FtcAuto autoMode;
    private HalDashboard dashboard;
    private int alliance;
    private double delay;
    private TrcStateMachine sm;
    private TrcTimer timer;
    private TrcEvent event;

    public AutoParkRepairZone(int alliance, double delay)
    {
        autoMode = (FtcAuto)FtcOpMode.getInstance();
        dashboard = HalDashboard.getInstance();
        this.alliance = alliance;
        this.delay = delay;
        sm = new TrcStateMachine("autoParkRepairZone");
        sm.start();
        timer = new TrcTimer("ParkRepairZoneTimer");
        event = new TrcEvent("ParkRepairZoneEvent");
    }

    public void autoPeriodic()
    {
        dashboard.displayPrintf(1, "ParkRepairZone: %s alliance, delay=%.1f",
                                alliance == autoMode.ALLIANCE_RED? "Red": "Blue", delay);

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
                    autoMode.getRobot().pidDrive.setTarget(100.0, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 2:
                    if (alliance == autoMode.ALLIANCE_RED)
                    {
                        autoMode.getRobot().pidDrive.setTarget(0.0, -45.0, false, event, 0.0);
                    }
                    else
                    {
                        autoMode.getRobot().pidDrive.setTarget(0.0, 45.0, false, event, 0.0);
                    }
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                case TrcStateMachine.STATE_STARTED + 3:
                    autoMode.getRobot().pidDrive.setTarget(24.0, 0.0, false, event, 0.0);
                    sm.addEvent(event);
                    sm.waitForEvents(state + 1);
                    break;

                default:
                    sm.stop();
                    break;
            }
        }
    }

}   //class AutoParkRepairZone
