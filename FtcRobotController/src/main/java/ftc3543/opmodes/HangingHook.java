package ftc3543.opmodes;

import ftclib.FtcServo;

public class HangingHook
{
    //
    // This component consists of a servo controlled hook.
    // It provides a method to extend and retract the hook.
    //
    private FtcServo hookServo;

    public HangingHook()
    {
        hookServo = new FtcServo("hangingHook");
    }   //HangingHook

    public void extend()
    {
        hookServo.setPosition(RobotInfo.HANGINGHOOK_EXTEND_POSITION);
    }   //extend

    public void retract()
    {
        hookServo.setPosition(RobotInfo.HANGINGHOOK_RETRACT_POSITION);
    }   //retract

}   //class HangingHook
