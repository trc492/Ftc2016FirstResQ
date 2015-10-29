package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.FtcServo;

public class ClimberRelease
{
    //
    // This component consists of a left and right swing arm.
    // It provides two methods: one is to deploy or undeploy
    // the left arm. The other is to deploy or undeploy the
    // right arm.
    //
    private static final double ARM_RETRACT_POSITION = 0.0;
    private static final double ARM_EXTEND_POSITION = 1.0;

    private FtcServo armServo;

    public ClimberRelease(String instanceName)
    {
        armServo = new FtcServo(instanceName);
    }   //ClimberRelease

    public void extend()
    {
        armServo.setPosition(ARM_EXTEND_POSITION);
    }   //extend

    public void retract()
    {
        armServo.setPosition(ARM_RETRACT_POSITION);
    }   //retract

}   //class ClimberRelease
