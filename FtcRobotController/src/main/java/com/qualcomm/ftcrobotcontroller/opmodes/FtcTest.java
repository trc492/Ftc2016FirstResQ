package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.FtcMenu;
import hallib.FtcRobot;

public class FtcTest extends FtcRobot
{
    private static final int TEST_DRIVE_5SEC    = 0;
    private static final int TEST_DRIVE_8FT     = 1;
    private static final int TEST_TURN_360      = 2;
    private FtcMenu testMenu;

    @Override
    public void robotInit()
    {
        testMenu = new FtcMenu("Tests:", gamepad1);
        testMenu.addChoice("Drive 5 sec", TEST_DRIVE_5SEC);
        testMenu.addChoice("Drive forward 8 ft", TEST_DRIVE_8FT);
        testMenu.addChoice("Turn right 360 deg", TEST_TURN_360);
        double choice = testMenu.getChoice();
    }   //robotInit

    @Override
    public void startMode()
    {

    }   //startMode

    @Override
    public void stopMode()
    {

    }   //stopMode

    @Override
    public void runPeriodic()
    {

    }   //runPeriodic

    @Override
    public void runContinuous()
    {

    }   //runContinuous

}   //class FtcTest
