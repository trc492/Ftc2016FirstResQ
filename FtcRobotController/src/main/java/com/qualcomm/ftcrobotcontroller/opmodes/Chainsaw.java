package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.HalSpeedController;

public class Chainsaw
{
    //
    // This component consists of a motor driven chain.
    // It provides a method to drive the chainsaw with
    // the given power.
    //
    private HalSpeedController chainsawMotor;

    public Chainsaw()
    {
        chainsawMotor = new HalSpeedController("chainsaw");
    }   //Chainsaw

    public void setPower(double power)
    {
        chainsawMotor.setPower(power);
    }   //setPower

}   //class Chainsaw
