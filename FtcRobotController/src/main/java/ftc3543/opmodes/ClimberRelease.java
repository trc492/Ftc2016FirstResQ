package ftc3543.opmodes;

import ftclib.FtcServo;

public class ClimberRelease
{
    //
    // This component consists of a swing wing that extends or retracts.
    // It provides two methods: one to extend and one to retract the wing.
    //
    private FtcServo servo;

    public ClimberRelease(String instanceName, boolean inverted)
    {
        servo = new FtcServo(instanceName);
        servo.setReverse(inverted);
    }   //ClimberRelease

    public void extend()
    {
        servo.setPosition(RobotInfo.WING_EXTEND_POSITION);
    }   //extend

    public void retract()
    {
        servo.setPosition(RobotInfo.WING_RETRACT_POSITION);
    }   //retract

}   //class ClimberRelease
