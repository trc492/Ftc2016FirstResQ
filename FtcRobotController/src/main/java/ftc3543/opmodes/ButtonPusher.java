package ftc3543.opmodes;

import ftclib.FtcServo;

public class ButtonPusher
{
    //
    // This component consists of a servo controlled swing arm.
    // It provides methods to set the swing arm position to either
    // neutral, left or right.
    //
    private FtcServo pusherServo;

    public ButtonPusher()
    {
        pusherServo = new FtcServo("buttonPusher");
    }   //ButtonPusher

    public void pushLeftButton()
    {
        pusherServo.setPosition(RobotInfo.PUSHER_LEFT_POSITION);
    }   //pushLeftButton

    public void pushRightButton()
    {
        pusherServo.setPosition(RobotInfo.PUSHER_RIGHT_POSITION);
    }   //pushRightButton

    public void pushNoButton()
    {
        pusherServo.setPosition(RobotInfo.PUSHER_NEUTRAL_POSITION);
    }   //pushNoButton

}   //class ButtonPusher