package ftc3543;

import ftclib.FtcDcMotor;
import ftclib.FtcTouch;

public class TrackHook
{
    //
    // This component consists of a motor driven track.
    // It provides a method to drive the track hook with
    // the given power.
    //
    private FtcDcMotor trackHookMotor;
    private FtcTouch lowerLimitSwitch;
    private FtcTouch upperLimitSwitch;

    public TrackHook()
    {
        trackHookMotor = new FtcDcMotor("trackHook");
        trackHookMotor.setInverted(true);
        lowerLimitSwitch = new FtcTouch("trackLowerLimit");
        upperLimitSwitch = new FtcTouch("trackUpperLimit");
    }   //TrackHook

    public void setPower(double power)
    {
        if (power > 0.0 && !upperLimitSwitch.isActive() ||
            power < 0.0 && !lowerLimitSwitch.isActive())
        {
            trackHookMotor.setPower(power);
        }
        else
        {
            trackHookMotor.setPower(0.0);
        }
    }   //setPower

}   //class TrackHook
