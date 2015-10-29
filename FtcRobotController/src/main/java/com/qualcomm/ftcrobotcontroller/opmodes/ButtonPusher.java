package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.FtcServo;

public class ButtonPusher
{
    //
    // This component consists of a servo controlled swing arm.
    // It provides methods to set the swing arm position to either
    // neutral, left or right.
    //
    private static final double PUSHER_LEFT_POSITION = 0.0;
    private static final double PUSHER_NEUTRAL_POSITION = 0.5;
    private static final double PUSHER_RIGHT_POSITION = 1.0;

    private FtcServo pusherServo;

    public ButtonPusher()
    {
        pusherServo = new FtcServo("buttonPusher");
    }   //ButtonPusher

    public void pushLeftButton()
    {
        pusherServo.setPosition(PUSHER_LEFT_POSITION);
    }   //pushLeftButton

    public void pushRightButton()
    {
        pusherServo.setPosition(PUSHER_RIGHT_POSITION);
    }   //pushRightButton

    public void pushNoButton()
    {
        pusherServo.setPosition(PUSHER_NEUTRAL_POSITION);
    }   //pushNoButton

}   //class ButtonPusher
