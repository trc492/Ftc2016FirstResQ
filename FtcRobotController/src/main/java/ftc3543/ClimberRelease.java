package ftc3543;

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

    public void setPosition(double position)
    {
        servo.setPosition(position);
    }   //setPosition

}   //class ClimberRelease
