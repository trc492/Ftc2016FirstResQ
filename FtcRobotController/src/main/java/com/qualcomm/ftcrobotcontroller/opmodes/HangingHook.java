package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.HalServo;

public class HangingHook
{
    //
    // This component consists of a servo controlled hook.
    // It provides a method to extend and retract the hook.
    //
    private static final double HANGINGHOOK_RETRACT_POSITION = 0.0;
    private static final double HANGINGHOOK_EXTEND_POSITION = 180.0;

    private HalServo hookServo;

    public HangingHook()
    {
        hookServo = new HalServo("hangingHook");
    }   //HangingHook

    public void extend()
    {
        hookServo.setPosition(HANGINGHOOK_EXTEND_POSITION);
    }   //extend

    public void retract()
    {
        hookServo.setPosition(HANGINGHOOK_RETRACT_POSITION);
    }   //retract

}   //class HangingHook
