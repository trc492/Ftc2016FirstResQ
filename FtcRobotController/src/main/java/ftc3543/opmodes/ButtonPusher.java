package ftc3543.opmodes;

import ftclib.FtcServo;

public class ButtonPusher
{
    //
    // This component consists of a servo controlled swing arm.
    // It provides methods to set the swing arm position to either
    // neutral, left or right.
    //
    private FtcServo leftPusher;
    private FtcServo rightPusher;

    public ButtonPusher()
    {
        leftPusher = new FtcServo("leftPusher");
        rightPusher = new FtcServo("rightPusher");
    }   //ButtonPusher

    public void pushLeftButton()
    {
        leftPusher.setPosition(RobotInfo.PUSHER_EXTEND_LEFT);
        rightPusher.setPosition(RobotInfo.PUSHER_RETRACT_RIGHT);
    }   //pushLeftButton

    public void pushRightButton()
    {
        leftPusher.setPosition(RobotInfo.PUSHER_RETRACT_LEFT);
        rightPusher.setPosition(RobotInfo.PUSHER_EXTEND_RIGHT);
    }   //pushRightButton

    public void pushNoButton()
    {
        leftPusher.setPosition(RobotInfo.PUSHER_RETRACT_LEFT);
        rightPusher.setPosition(RobotInfo.PUSHER_RETRACT_RIGHT);
    }   //pushNoButton

}   //class ButtonPusher
