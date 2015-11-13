package ftc3543.opmodes;

import ftclib.FtcServo;

public class ButtonPusher
{
    //
    // This component consists of two servos controlling the left and right
    // button pusher.
    // It provides methods to activate either the left pusher, the right pusher
    // or not at all.
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

    public void retract()
    {
        leftPusher.setPosition(RobotInfo.PUSHER_RETRACT_LEFT);
        rightPusher.setPosition(RobotInfo.PUSHER_RETRACT_RIGHT);
    }   //retract

}   //class ButtonPusher
