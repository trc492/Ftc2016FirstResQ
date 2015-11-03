package com.qualcomm.ftcrobotcontroller.opmodes;

import ftclib.FtcDcMotor;

public class Chainsaw
{
    //
    // This component consists of a motor driven chain.
    // It provides a method to drive the chainsaw with
    // the given power.
    //
    private FtcDcMotor chainsawMotor;

    public Chainsaw()
    {
        chainsawMotor = new FtcDcMotor("chainsaw");
        chainsawMotor.setInverted(true);
    }   //Chainsaw

    public void setPower(double power)
    {
        chainsawMotor.setPower(power);
    }   //setPower

}   //class Chainsaw
