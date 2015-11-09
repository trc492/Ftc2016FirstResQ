package ftc3543.opmodes;

import ftclib.FtcDcMotor;

public class TreadDrive
{
    //
    // This component consists of a motor driven tread.
    // It provides a method to drive the tread with
    // the given power.
    //
    private FtcDcMotor treadDriveMotor;

    public TreadDrive()
    {
        treadDriveMotor = new FtcDcMotor("treadDrive");
        treadDriveMotor.setInverted(true);
    }   //TreadDrive

    public void setPower(double power)
    {
        treadDriveMotor.setPower(power);
    }   //setPower

}   //class TreadDrive
