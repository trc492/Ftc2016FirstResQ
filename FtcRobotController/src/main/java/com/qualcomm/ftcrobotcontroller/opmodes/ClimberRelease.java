package com.qualcomm.ftcrobotcontroller.opmodes;

import ftclib.FtcServo;

public class ClimberRelease
{
    //
    // This component consists of a left and right swing arm.
    // It provides two methods: one is to deploy or undeploy
    // the left arm. The other is to deploy or undeploy the
    // right arm.
    //
    private FtcServo armServo;

    public ClimberRelease(String instanceName)
    {
        armServo = new FtcServo(instanceName);
    }   //ClimberRelease

    public void extend()
    {
        armServo.setPosition(RobotInfo.ARM_EXTEND_POSITION);
    }   //extend

    public void retract()
    {
        armServo.setPosition(RobotInfo.ARM_RETRACT_POSITION);
    }   //retract

}   //class ClimberRelease
